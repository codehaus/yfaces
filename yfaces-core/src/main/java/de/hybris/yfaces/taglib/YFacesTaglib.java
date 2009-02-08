/*
 * Copyright 2008 the original author or authors.
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

package de.hybris.yfaces.taglib;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.sun.facelets.tag.AbstractTagLibrary;

import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.YFacesConfig;
import de.hybris.yfaces.YComponentRegistry.YComponentInfoListener;
import de.hybris.yfaces.component.html.HtmlYComponent;
import de.hybris.yfaces.component.html.HtmlYComponentHandler;

/**
 * This is a Facelet based Taglibrary which registers various tags and components at Facelet system.
 * The used namespace is {@value #NAMESPACE}.
 * <p>
 * Registers YComponent views as usertags.<br/>
 * An YComponent view is a xhtml file which must match a special Pattern (*Cmp.xhtml). The
 * components search path can be configured as a comma separated list of single path elements under
 * context parameter {@value #PARAM_COMPONENT_DIRS}. Each path element must be given relatively to
 * the webapp root.When subdirectories shall be included the path element must end with '/**' The
 * default search path {@value #DEFAULT_COMPONENTS_DIR}is always available.
 * <p>
 * Registers {@link HtmlYComponent} as {@link UIComponent}.<br/>
 * This is a plain JSF Component which gets registered under the name {@value #COMPONENT_NAME}.
 * <p>
 * 
 * 
 * 
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesTaglib extends AbstractTagLibrary implements YComponentInfoListener {

	private static final Logger log = Logger.getLogger(YFacesTaglib.class);

	/** namespace for all tags and components which are getting registered */
	public static final String NAMESPACE = "http://hybris.com/jsf/yfaces";

	/** default search path for component view files */
	public static final String DEFAULT_COMPONENTS_DIR = "/components";

	/** tag name for {@link HtmlYComponent} */
	public static final String COMPONENT_NAME = "component";

	public static final String COMPONENT_NAME_FULL = "yf:component";

	/** deployment parameter for additional component search path elements */
	public static final String PARAM_COMPONENT_DIRS = "yfaces.taglib.DIR";

	// matches when a string represents a visible directory
	// (no dot after a slash; ends with a slash)
	private static Pattern UNHIDDEN_DIRECTORY_PATTERN = Pattern.compile("[^\\.]*/?");

	// matches when resource represents a component view
	public static final Pattern COMPONENT_RESOURCE_PATTERN = Pattern
			.compile(".*[/\\\\](.*)((?:Cmp)|(?:Tag))\\.xhtml");

	/**
	 * Constructor.
	 */
	public YFacesTaglib() {
		super(NAMESPACE);

		// search for component views and register all as usertags
		this.registerYComponents();

		// register some functions
		this.registerElFunctions();

		// register htmlycomponent
		this.addComponent(COMPONENT_NAME, HtmlYComponent.COMPONENT_TYPE, null,
				HtmlYComponentHandler.class);
	}

	public void addedYComponent(final YComponentInfo cmpInfo) {
		super.addUserTag(cmpInfo.getComponentName(), cmpInfo.getURL());
		if (log.isDebugEnabled()) {
			log.debug("Added component under yf namespace:" + cmpInfo.getId() + " ("
					+ cmpInfo.getURL() + ")");
		}
	}

	public void skippedYComponent(final URL url, final YComponentInfo cmpInfo) {
		if (cmpInfo != null) {
			log.info("Skipped " + url);
		} else {
			log.info("Skipped " + url);
			if (YFacesConfig.ALSO_REGISTER_NON_YCMP.getBoolean()) {
				final String name = YComponentRegistry.getInstance().getYComponentNameFromURL(url);
				if (name != null && name.trim().length() > 0) {
					log.info("Will be registered as plain tag (" + name + ")");
					super.addUserTag(name, url);
				}
			}
		}
	}

	/**
	 * Searches for possible component view files. Each valid component gets registered as usertag
	 * at the facelet subsystem and additional a {@link YComponentInfo} instance gets registered at
	 * {@link YComponentRegistry}
	 */
	private void registerYComponents() {

		// retrieve a prepared (no duplicates etc.) collection of search path elements
		Collection<String> paths = getComponentSearchPaths();
		log.debug("Search path for components:" + paths);

		// iterate over search paths...
		for (final String directory : paths) {
			log.debug("Searching " + directory + " for components...");
			final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
			final YComponentRegistry registry = YComponentRegistry.getInstance();

			// ...retrieve all available resources inside that path
			final Set<String> resources = ctx.getResourcePaths(directory);

			if (resources != null) {

				// iterate over resources...
				for (final String resource : resources) {

					// ...check whether name matches pattern for a YComponent view file
					if (COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {
						try {
							// fetch URL and register at component registry 
							final URL url = ctx.getResource(resource);
							registry.processURL(url, this);
						} catch (final MalformedURLException e) {
							log.error(e.getMessage());
							log.error("Error while fetching URL for resource " + resource);
						}
					}
				}
			}
		}
	}

	/**
	 * Prepares a collection of path elements which shall be searched for component view files. A
	 * raw search path is configured as value of deployment parameter {@link #PARAM_COMPONENT_DIRS}
	 * This value gets splitted, subdirectories are getting processed, duplicates are taken out.
	 * 
	 * @return {@link Collections} of search path elements
	 */
	private Collection<String> getComponentSearchPaths() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

		// configured raw value for search path elements
		String s = ctx.getInitParameter(PARAM_COMPONENT_DIRS);

		// split by ',' and remove any whitespaces
		final String[] searchDir = s.trim().split("\\s*,\\s*");

		// use a set of raw dirs to filter out configured duplicates
		Collection<String> searchDirSet = new LinkedHashSet<String>();
		searchDirSet.add(DEFAULT_COMPONENTS_DIR);
		searchDirSet.addAll(Arrays.asList(searchDir));

		Collection<String> result = new LinkedHashSet<String>();
		// iterate over each search element
		for (String dir : searchDirSet) {

			// when sub-elements shall be processed...  
			if (dir.endsWith("/**")) {
				// add them recursively
				dir = dir.substring(0, dir.length() - 3);
				List<String> subDirs = getComponentSearchPathsRecursive(dir);
				Collections.sort(subDirs);
				result.addAll(subDirs);
			} else {
				//otherwise just add this search path
				result.add(dir);
			}
		}

		return result;

	}

	/**
	 * Returns a List which contains directory resources. Starts from the base and recursively
	 * processes each sub-directory.
	 * 
	 * @param base
	 *            base path
	 * @return List of directory resources
	 */
	private List<String> getComponentSearchPathsRecursive(String base) {

		final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		Set<String> resources = ctx.getResourcePaths(base);

		// result contains at least the base
		List<String> result = new ArrayList<String>();
		result.add(base);

		// process each resource...
		for (String resource : resources) {

			if (UNHIDDEN_DIRECTORY_PATTERN.matcher(resource).matches()) {
				Collection<String> subDirs = getComponentSearchPathsRecursive(resource);
				result.addAll(subDirs);
			}
		}

		return result;
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
				+ "xmlns:ui=\"http://java.sun.com/jsf/facelets\" \n" + "xmlns:yf=\"" + NAMESPACE
				+ "\" >");
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

}
