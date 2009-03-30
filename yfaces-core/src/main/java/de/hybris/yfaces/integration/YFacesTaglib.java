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

package de.hybris.yfaces.integration;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sun.facelets.FaceletFactory;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.tag.AbstractTagLibrary;

import de.hybris.yfaces.YComponentFactory;
import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.component.html.HtmlYComponent;
import de.hybris.yfaces.component.html.HtmlYComponentHandler;

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
 * {@link YFacesTaglib#NAMESPACE_BASE}. However that namespace can be extended by either a custom
 * value or the folders name where the resource is located. To do so the namespace suffix must be
 * added as search path prefix.
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
 * and "components1" (incl. subfolders) is {@link YFacesTaglib#NAMESPACE_BASE}.<br/>
 * Namespace for "component2" is {@link YFacesTaglib#NAMESPACE_BASE} plus "/cmp".<br/>
 * Namespace for "component3" is {@link YFacesTaglib#NAMESPACE_BASE} plus name of folder the
 * component is located.<br/>
 * <p>
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesTaglib extends AbstractTagLibrary {

	private static final Logger log = Logger.getLogger(YFacesTaglib.class);

	/** namespace for all tags and components which are getting registered */
	public static final String NAMESPACE_BASE = "http://hybris.com/jsf/yfaces";

	/** default search path for component view files */
	public static final String DEFAULT_COMPONENTS_DIR = "/components";

	/** tag name for {@link HtmlYComponent} */
	public static final String COMPONENT_NAME = "component";

	public static final String COMPONENT_NAME_FULL = "yf:component";

	/** deployment parameter for additional component search path elements */
	public static final String PARAM_COMPONENT_DIRS = "yfaces.taglib.DIR";
	public static final String NAMESPACE_FROM_FOLDER = "$folder";

	// matches when a string represents a visible directory
	// (no dot after a slash; ends with a slash)
	private static final Pattern UNHIDDEN_DIRECTORY_PATTERN = Pattern.compile("[^\\.]*/?");

	// matches when resource represents a component view
	public static final Pattern COMPONENT_RESOURCE_PATTERN = Pattern
			.compile(".*[/\\\\](.*)((?:Cmp)|(?:Tag))\\.xhtml");

	// maps a namespace id to a taglib instance
	private Map<String, AbstractTagLibrary> namespaceToTaglibMap = null;
	// set of component resources
	private Set<YComponentInfo> componentSet = new HashSet<YComponentInfo>();

	/**
	 * Constructor. Gets invoked by Facelet framework.
	 */
	public YFacesTaglib() {
		super(NAMESPACE_BASE);

		this.namespaceToTaglibMap = new HashMap<String, AbstractTagLibrary>();
		this.namespaceToTaglibMap.put("", this);

		// search for component views and register all as usertags
		this.registerYComponents();

		// register some functions
		this.registerElFunctions();

		// register htmlycomponent
		this.addComponent(COMPONENT_NAME, HtmlYComponent.COMPONENT_TYPE, null,
				HtmlYComponentHandler.class);
	}

	/**
	 * Private Constructor. Internally used when multiple namespaces are configured.
	 * 
	 * @param namespace
	 *            namespace to use
	 */
	private YFacesTaglib(String namespace) {
		super(namespace);
		((DefaultFaceletFactory) FaceletFactory.getInstance()).getCompiler().addTagLibrary(this);
	}

	/**
	 * Registers configured components. Fetches the configured value of
	 * {@link #DEFAULT_COMPONENTS_DIR}, splits it to single elements and processes each search path
	 * element by looking for component resources.
	 */
	private void registerYComponents() {

		// fetch configured component search paths and split it by delimiter
		final String[] dirs = this.getAsArray(PARAM_COMPONENT_DIRS);

		// filter duplicates path elements
		Collection<String> dirSet = new LinkedHashSet<String>();
		dirSet.add(DEFAULT_COMPONENTS_DIR);
		dirSet.addAll(Arrays.asList(dirs));

		// collect each single component resources
		for (String rawSearchPath : dirSet) {
			collectComponentResources(rawSearchPath);
		}

		// sort collected component resources
		List<YComponentInfo> cmpInfoList = new ArrayList<YComponentInfo>(this.componentSet);
		Collections.sort(cmpInfoList, new Comparator<YComponentInfo>() {
			public int compare(YComponentInfo o1, YComponentInfo o2) {
				return o1.getURL().toExternalForm().compareTo(o2.getURL().toExternalForm());
			}
		});

		// create componentinfo for each component resource and add to registry
		YComponentFactory cmpFac = new YComponentFactory();
		for (YComponentInfo cmpInfo : cmpInfoList) {
			cmpInfo = cmpFac.createComponentInfo(cmpInfo.getURL(), cmpInfo.getNamespace());
			boolean added = YComponentRegistry.getInstance().addComponent(cmpInfo);

			if (added) {
				YFacesTaglib tagLib = getTagLib(cmpInfo);
				tagLib.addUserTag(cmpInfo.getComponentName(), cmpInfo.getURL());
				log.debug("Added " + cmpInfo.getURL());
			}
		}
	}

	/**
	 * Collect component resources. Expects a raw search path which contains information about
	 * recursive lookup and namespace. Does recursive lookup when path ends with "/**". Treats all
	 * before a colon as custom namespace. A special custom namespace is "$folder" which just takes
	 * the current resource location path as namespace. examples for a raw search path:
	 * <p/>
	 * <code>
	 * $folder:/demo/components,
	 * demo:/demo2/**,
	 * /demo3/**
	 * </code>
	 * 
	 * @param rawSearchPath
	 *            the component search path as raw string
	 */
	private void collectComponentResources(String rawSearchPath) {

		// detect recursive mode
		boolean recursive = rawSearchPath.endsWith("/**");
		if (recursive) {
			rawSearchPath = rawSearchPath.substring(0, rawSearchPath.length() - 3);
		}

		// detect namespace
		String namespaceURI = "";
		int nsDelimiterIndex = rawSearchPath.indexOf(":");
		if (nsDelimiterIndex > 0) {
			namespaceURI = rawSearchPath.substring(0, nsDelimiterIndex);
			rawSearchPath = rawSearchPath.substring(nsDelimiterIndex + 1);
		}

		// fetch component resources (recursive)
		this.collectComponentResources(namespaceURI, rawSearchPath, recursive);
	}

	/**
	 * Recursive way of finding all component resources.Create a {@link YComponentInfo} instances
	 * with appropriate namespaces and adda all to the {@link YComponentRegistry}. A namespace
	 * prefix may be passed which extends the default namespace: {@link #NAMESPACE_BASE}
	 * 
	 * @param extNamespace
	 *            namespace prefix; extends default namespace for all components within passed base
	 * @param base
	 *            base search path for component resources; starts with a slash and may (not must)
	 *            end with a slash; must be a folder, not a resource
	 * @param recursive
	 *            true when subfolders shall be searched too
	 */
	private void collectComponentResources(String extNamespace, String base, boolean recursive) {

		// fetch resources from passed base
		final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		Set<String> resources = ctx.getResourcePaths(base);

		// is null when base is not valid
		if (resources != null) {

			// process each resource...
			for (String resource : resources) {

				// when recursive lookup is enabled process subresources in case base is a folder 
				if (recursive && UNHIDDEN_DIRECTORY_PATTERN.matcher(resource).matches()) {
					collectComponentResources(extNamespace, resource, recursive);
				}

				// check whether resource name matches a component resource
				if (COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {
					try {
						URL url = ctx.getResource(resource);
						String ns = NAMESPACE_FROM_FOLDER.equals(extNamespace) ? base : "/"
								+ extNamespace;

						if (ns.endsWith("/")) {
							ns = ns.substring(0, ns.length() - 1);
						}
						ns = NAMESPACE_BASE + ns;
						YComponentInfo cmpInfo = new YComponentInfo(ns, url);
						this.componentSet.add(cmpInfo);
					} catch (MalformedURLException e) {
						log.error(e);
					}
				}
			}
		} else {
			log.error(base + " is not a valid resource path");
		}
	}

	private void registerElFunctions() {
		// Add functions
		for (final Method method : ELFunctions.class.getMethods()) {
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
	 * Internal (for testing).<br/>
	 * <br/>
	 * Creates a XHTML fragment which contains all components found in
	 * {@link #DEFAULT_COMPONENTS_DIR} No component will be configured as no frame is available. The
	 * result is a path to a temporary file which must be included. Currently used for Selenium
	 * testing (test: allComponents)
	 * 
	 * @return Path to temporary xhtml fragment file
	 * @throws Exception
	 */
	public static String getAllComponentsAsXHTMLFragment() throws Exception {
		final File result = File.createTempFile("allComponents", ".xhtml");
		final PrintWriter pr = new PrintWriter(new FileWriter(result));

		final Pattern pattern = Pattern.compile(DEFAULT_COMPONENTS_DIR + "/(.*)Tag\\.xhtml");
		final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		final ServletContext ctx = request.getSession().getServletContext();

		final Set resources = ctx.getResourcePaths(DEFAULT_COMPONENTS_DIR);

		pr.println("<ui:composition " + "xmlns=\"http://www.w3.org/1999/xhtml\" \n"
				+ "xmlns:ui=\"http://java.sun.com/jsf/facelets\" \n" + "xmlns:yf=\""
				+ NAMESPACE_BASE + "\" >");
		for (final Object obj : resources) {
			// not sure whether this set can contain any other stuff than
			// Strings
			if (obj instanceof String) {
				final String resource = (String) obj;
				final Matcher m = pattern.matcher(resource);
				if (m.matches()) {
					final String cmp = m.group(1);
					pr.println("<b>" + cmp + "Component<br/> &lt;yf:" + cmp + "&gt;</b><br/><br/>");
					pr.println("<yf:" + cmp + "/> <br/><hr/><br/>");
				} else {
					log.debug("Skipped component tag: " + resource + " (not valid)");
				}
			}
		}
		pr.println("<br/>eof</ui:composition>");
		pr.flush();
		pr.close();

		final String resultPath = "file:" + result.getAbsolutePath();

		System.out.println("generated testfile: " + resultPath);

		return resultPath;
	}

	/**
	 * Returns the value of an deployment parameter as array. Splits the value into an array, uses
	 * comma as delimiter and removes any whitespaces.
	 * 
	 * @param initParameter
	 *            name of deployment parameter
	 * @return array
	 */
	private String[] getAsArray(String initParameter) {
		// fetch conigured value
		String s = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(
				initParameter);

		// split by ',' and remove any whitespaces
		String[] result = s != null ? s.trim().split("\\s*,\\s*") : new String[] {};
		return result;
	}

	private YFacesTaglib getTagLib(YComponentInfo info) {
		String key = info.getNamespace();
		YFacesTaglib result = (YFacesTaglib) this.namespaceToTaglibMap.get(key);
		if (result == null) {
			this.namespaceToTaglibMap.put(key, result = new YFacesTaglib(key));
		}
		return result;
	}
}
