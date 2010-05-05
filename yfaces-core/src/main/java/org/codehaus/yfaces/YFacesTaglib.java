/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.yfaces;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.YComponentHandlerFactory;
import org.codehaus.yfaces.component.YComponentHandlerImpl;
import org.codehaus.yfaces.component.YComponentValidator;
import org.codehaus.yfaces.component.YComponentValidator.YValidationAspekt;
import org.codehaus.yfaces.component.html.HtmlYComponent;
import org.codehaus.yfaces.component.html.HtmlYComponentHandler;

import com.sun.facelets.FaceletFactory;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.tag.AbstractTagLibrary;

/**
 * A Facelet based Taglibrary which registers YComponent resources.
 * <p>
 * A component resource has the following properties:
 * <ul>
 * <li>resource name ends with *Cmp.xhtml or *Tag.xhtml</li>
 * <li>resource content contains a ycomponent declaration (&lt;yf;component...&gt;)</li>
 * <li>resource is located under at least one configured path given as value of web.xml deployment
 * descriptor {@value #DEFAULT_COMPONENTS_DIR}</li>
 * </ul>
 * Each component resource gets registered under it's component name.The search path for component
 * resources is configured as comma separated list.This is configured as value of deployment
 * parameter {@link #DEFAULT_COMPONENTS_DIR}. When a search path element ends with /** subfolders
 * are included. By default each resource gets registered under a default namespace
 * {@link YFacesTaglib2#namespace}. However that namespace can be extended by either a custom value
 * or the folders name where the resource is located. To do so the namespace suffix must be added as
 * search path prefix.
 * <p>
 * Example: <code><pre>
 * &ltcontext-param&gt
 *  &ltparam-name&gtyfaces.taglib.DIR&lt/param-name&gt 
 *  &ltparam-value&gt
 *  /components,
 *  /components1/**,
 *  cmp:/components2
 *  $folder:/components3/**
 *  &lt/param-value&gt
 *  &lt/context-param&gt;
 * </pre></code>
 * folder "components" and "components2" will be searched for. Additionally folder and all
 * subfolders of "components1" and "components3" will be searched for. Namespace for "components"
 * and "components1" (incl. subfolders) is {@link YFacesTaglib2#namespace}.<br/>
 * Namespace for "component2" is {@link YFacesTaglib2#namespace} plus "/cmp".<br/>
 * Namespace for "component3" is {@link YFacesTaglib2#namespace} plus name of folder the component
 * is located.<br/>
 * <p>
 * 
 * @author Denny Strietzbaum
 */
public class YFacesTaglib extends AbstractTagLibrary {

	private static final Logger log = Logger.getLogger(YFacesTaglib.class);

	public static final String YFACES_NAMESPACE = "http://yfaces.codehaus.org/taglib";

	/** tag name for {@link HtmlYComponent} */
	public static final String YCOMPONENT_NAME = "component";

	/** default search path for component view files */
	private static final String DEFAULT_COMPONENTS_DIR = "/components";
	private static final String DEFAULT_FACELETS_DIR = "/tags";

	/** deployment parameter for additional component search path elements */
	private static final String PARAM_COMPONENT_DIRS = "yfaces.taglib.DIR";
	private static final String NAMESPACE_FROM_FOLDER = "$folder:";

	// matches when resource represents a component view
	private static final Pattern TAGFILE_RESOURCE_PATTERN = Pattern.compile(".*[/\\\\](.*)\\.xhtml");

	// maps a namespace id to a taglib instance
	private Map<String, AbstractTagLibrary> namespaceToTaglibMap = null;

	private String namespaceContext = null;

	/**
	 * Constructor. Gets invoked by Facelet framework.
	 */
	public YFacesTaglib() {
		super(YFACES_NAMESPACE);

		// maps each namespace to a Facelet TagLibrary instance
		// initially add this TagLibrary instance under yfaces default namespace 
		this.namespaceToTaglibMap = new HashMap<String, AbstractTagLibrary>();
		this.namespaceToTaglibMap.put(YFACES_NAMESPACE, this);

		// a nameespace context extends namespace definitions of custom tags 
		// e.g. http://yfaces.codehaus.org/taglib/myapptags to
		//      http://yfaces.codehaus.org/taglib/myappname/myapptags to
		this.namespaceContext = YFacesConfig.NAMESPACE_CONTEXT.getString();
		if (this.namespaceContext.length() > 0 && !this.namespaceContext.startsWith("/")) {
			this.namespaceContext = "/" + namespaceContext;
		}

		// search for ycomponent view files and register them as usertags
		this.registerYComponents();

		// search for general view files and register them as usertag
		this.registerTags();

		// register some functions
		this.registerElFunctions();

		// register htmlycomponent under default yfaces namespace
		this.addComponent(YCOMPONENT_NAME, HtmlYComponent.COMPONENT_TYPE, null,
				HtmlYComponentHandler.class);
	}

	/**
	 * Private Constructor. Internally used when multiple namespaces are configured.
	 * 
	 * @param namespace
	 *          namespace to use
	 */
	private YFacesTaglib(final String namespace) {
		super(namespace);
		((DefaultFaceletFactory) FaceletFactory.getInstance()).getCompiler().addTagLibrary(this);
	}

	/**
	 * Finds and registers all custom tag files.
	 */
	private void registerTags() {

		final Collection<String> pathEntries = this.getPathEntries(null, DEFAULT_FACELETS_DIR);
		// create resource collectors for each path entry
		final Collection<ResourceCollector> resCollectors = this.getResourceCollectors(pathEntries);

		// for each ResourceCollector...
		for (final ResourceCollector resCollector : resCollectors) {
			final String namespace = resCollector.getNamespace();

			//...and each of their file-based resources...
			for (final URL url : resCollector.getFileResources()) {

				// register component file at a Facelet Taglib as UserTag
				final YFacesTaglib tagLib = getOrCreateTagLib(namespace);
				final Matcher tagNameMatcher = TAGFILE_RESOURCE_PATTERN.matcher(url.getFile());
				if (tagNameMatcher.matches()) {
					final String name = tagNameMatcher.group(1);
					tagLib.addUserTag(name, url);
				}
			}
		}

	}

	/**
	 * Finds and registers all YComponent view files.
	 */
	private void registerYComponents() {

		// get all component paths (configured and initial ones)
		final Collection<String> pathEntries = this.getPathEntries(PARAM_COMPONENT_DIRS,
				DEFAULT_COMPONENTS_DIR);

		// create resource collectors for each path entry
		final Collection<ResourceCollector> resCollectors = this.getResourceCollectors(pathEntries);

		String base = "";
		try {
			final URL _url = FacesContext.getCurrentInstance().getExternalContext().getResource("/");
			base = _url.getFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		final YComponentHandlerFactory cmpFac = new YComponentHandlerFactory(base);

		final Map<String, Integer> dupIdCount = new HashMap<String, Integer>();
		final String idSuffix = YFacesConfig.GEN_CMP_ID_SUFFIX.getString();

		// for each ResourceCollector...
		for (final ResourceCollector resCollector : resCollectors) {
			final String namespace = resCollector.getNamespace();

			log.debug("Creating namespace: " + namespace);

			//...and each of their file-based resources...
			for (final URL url : resCollector.getFileResources()) {

				//...create component meta information
				final YComponentHandlerImpl cmpInfo = (YComponentHandlerImpl) cmpFac.createHandler(url, namespace);

				//...which is successful when it's really a YComponent and not only a simple file
				if (cmpInfo != null) {

					// take 'name' for 'id' if necessary
					final String _uid = cmpInfo.getName() + idSuffix;

					// create a short UID for every YComponentInfo
					final Integer count = dupIdCount.get(_uid);
					if (count == null) {
						cmpInfo.setUid(_uid);
						dupIdCount.put(_uid, Integer.valueOf(1));
					} else {
						cmpInfo.setUid(_uid + count);
						dupIdCount.put(_uid, Integer.valueOf(count.intValue() + 1));
					}

					cmpInfo.initialize();

					// validate that YComponent
					// reduce level for "missing specification" from ERROR to WARNING 
					final YComponentValidator cmpValidator = cmpInfo.createValidator();
					cmpValidator.validateComponent(YValidationAspekt.SPEC_IS_MISSING);
					final Set<YValidationAspekt> errors = cmpValidator.getValidationErrors();
					final Set<YValidationAspekt> warnings = cmpValidator.getValidationWarnings();

					// add that Component to a registry
					final boolean added = YFaces.getYComponentRegistry().addComponent(cmpInfo);

					// and if registry does not complain
					if (added) {
						// register component file at a Facelet Taglib as UserTag
						final YFacesTaglib tagLib = getOrCreateTagLib(cmpInfo.getNamespace());
						tagLib.addUserTag(cmpInfo.getName(), cmpInfo.getViewURL());
						if (warnings.isEmpty()) {
							log.debug("Successfully added component: " + cmpInfo.getViewURL());
						} else {
							log.debug("Added component with warnings: " + cmpInfo.getViewURL());
							log.debug(YValidationAspekt.getFormattedErrorMessage(warnings, cmpInfo, null));
						}
					}

					// if errors were found print them out 
					if (!errors.isEmpty()) {
						log.error("Adding component: " + cmpInfo.getViewURL() + " with error(s)");
						log.error(YValidationAspekt.getFormattedErrorMessage(errors, cmpInfo, null));
					}
				}
			}
		}
	}

	private void registerElFunctions() {
		// Add functions
		for (final Method method : YFacesTaglibELFunc.class.getMethods()) {
			if (Modifier.isStatic(method.getModifiers())) {
				this.addFunction(method.getName(), method);
				if (log.isDebugEnabled()) {
					log.debug("Added function: " + method.getName() + "("
							+ StringUtils.join(method.getParameterTypes(), ",") + ")");
				}
			}
		}
	}

	/**
	 * Returns a Collection of path entries which is read from a configuration source. Single path
	 * entries are not validated, they are just a result of a string literal split.
	 * 
	 * @param initParamName
	 *          name of init-parameter of web.xml which is read
	 * @param defaultEntry
	 *          a default path which gets added in every case
	 * @return Collection of path entries
	 */
	private Collection<String> getPathEntries(final String initParamName, final String defaultEntry) {

		final Set<String> pathEntries = new LinkedHashSet<String>();
		pathEntries.add(defaultEntry);

		if (initParamName != null) {

			// get configured component path as one raw literal
			final String s = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(
					initParamName);

			// extract all path elements: split by ',' remove any whitespaces
			final String[] pathEntry = s != null ? s.trim().split("\\s*,\\s*") : new String[] {};

			// add entries
			pathEntries.addAll(Arrays.asList(pathEntry));
		}

		return pathEntries;
	}

	/**
	 * Creates a Collection of {@link ResourceCollector} for each available path entry. Support path
	 * entry format:
	 * <ul>
	 * <li>
	 * 1. $folder:path/to/resources</li>
	 * <li>2. path/to/resources/**</li>
	 * </ul>
	 * 1. '$folder' is used as dynamic variable which holds name of each directory subresource of
	 * path/to/resource. Each subresources within '$folder' is registered under a namespace which
	 * contains '$folder'. <br/>
	 * <br/>
	 * 2. '/**' just means search in subdirectories
	 * 
	 * @return Collection of {@link ResourceCollector}
	 */
	private Collection<ResourceCollector> getResourceCollectors(final Collection<String> pathEntries) {

		final ResourceCollector defaultCollector = new ResourceCollector(YFACES_NAMESPACE);
		final List<ResourceCollector> result = new ArrayList<ResourceCollector>();
		result.add(defaultCollector);

		// iterate over each path entry...
		for (String path : pathEntries) {

			//...recognize 'recursive' mode by looking at the path trail for a '/**' 
			final boolean recursive = path.endsWith("/**");
			if (recursive) {
				path = path.substring(0, path.length() - 3);
			}

			//...recognize 'dynamic namespace' mode (take subfolder as new namespace)
			if (path.startsWith(NAMESPACE_FROM_FOLDER)) {
				path = path.substring(NAMESPACE_FROM_FOLDER.length());

				// add root components to default namespace (non-recursive)
				defaultCollector.addResources(path, false);

				// a custom collector to find directories
				final ResourceCollector resCol = new ResourceCollector(YFACES_NAMESPACE);
				resCol.addResources(path, false);

				// each directory represents it's own  namespace ...
				for (final String resource : resCol.getDirLocations()) {
					final ResourceCollector resources = new ResourceCollector(YFACES_NAMESPACE
							+ this.namespaceContext + resource);
					// ... which itself becomes a new ResourceCollector 
					resources.addResources(resource, recursive);

					// ... and gets added for later processing in case it contains some resources
					if (!resources.getFileResources().isEmpty()) {
						result.add(resources);
					}
				}
			} else {
				// basic resource detection
				defaultCollector.addResources(path, recursive);
			}
		}

		// finally sort ResourceCollectors ordered by their namespace
		Collections.sort(result, new Comparator<ResourceCollector>() {
			public int compare(final ResourceCollector o1, final ResourceCollector o2) {
				return o1.getNamespace().compareTo(o2.getNamespace());
			}
		});

		return result;
	}

	/**
	 * Gets or creates a new taglib for passed namespace prefix.
	 * 
	 * @param namespace
	 * @return
	 */
	private YFacesTaglib getOrCreateTagLib(final String namespace) {
		YFacesTaglib result = (YFacesTaglib) this.namespaceToTaglibMap.get(namespace);
		if (result == null) {
			this.namespaceToTaglibMap.put(namespace, result = new YFacesTaglib(namespace));
		}
		return result;
	}
}
