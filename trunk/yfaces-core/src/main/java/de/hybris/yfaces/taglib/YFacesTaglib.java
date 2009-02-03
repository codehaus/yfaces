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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * Registers all files within /components who're ending with (.*)Tag.xhtml as
 * usertag.
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesTaglib extends AbstractTagLibrary implements YComponentInfoListener {
	private static final Logger log = Logger.getLogger(YFacesTaglib.class);

	public static final String NAMESPACE = "http://hybris.com/jsf/yfaces";
	private static final String DEFAULT_COMPONENTS_DIR = "/components";

	private static final String CONTEXT_PARAM = "yfaces.taglib.DIR";

	public YFacesTaglib() {
		super(NAMESPACE);

		// register ycomponents
		this.registerYComponents();

		// register some functions
		this.registerElFunctions();

		// register htmlycomponent
		this.addComponent("component", HtmlYComponent.COMPONENT_TYPE, null,
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
	 * Internal.<br/>
	 * Load all available component source files as component into
	 * {@link YComponentRegistry}
	 */
	private void registerYComponents() {
		// retrieve all directories which shall be searched for components
		final String s = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(
				CONTEXT_PARAM);
		final Set<String> dirs = new LinkedHashSet<String>();
		dirs.add(DEFAULT_COMPONENTS_DIR);

		// split by delimiter and removing any kind of starting/ending
		// whitespace
		final String[] directories = s.trim().split("\\s*,\\s*");
		dirs.addAll(Arrays.asList(directories));

		// iterate over each lookup directory
		for (final String directory : dirs) {
			log.debug("Searching " + directory + " for components...");
			final ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
			final YComponentRegistry registry = YComponentRegistry.getInstance();

			// ...retrieve any resource inside that directory
			final Set<String> resources = ctx.getResourcePaths(directory);

			if (resources != null) {
				// ..go through each resource
				for (final String resource : resources) {
					try {
						// ...and try to handle this resource as component
						// source file
						final URL url = ctx.getResource(resource);

						registry.processURL(url, this);
					} catch (final MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
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
	 * {@link #DEFAULT_COMPONENTS_DIR} No component will be configured as no
	 * frame is available. The result is a path to a temporary file which must
	 * be included. Currently used for Selenium testing (test: allComponents)
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

	// private void test(final String url)
	// {
	// try
	// {
	// final InitialContext ctx = new InitialContext();
	// final NamingContext nameingCtx = (NamingContext)
	// ctx.lookup("java:comp/env");
	// nameingCtx.lookup(url);
	// final Object o = ctx.lookup(url);
	// System.out.println(o);
	// }
	// catch (final Exception e)
	// {
	// e.printStackTrace();
	// }
	//
	// }

}
