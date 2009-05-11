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

package de.hybris.yfaces;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import de.hybris.yfaces.context.YApplicationContext;

/**
 * YFaces startup. Reads configuration and initializes runtime properties.
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesStartupListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(YFacesStartupListener.class);

	private static final String PARAM_YFACES_CTX = "yfaces-context";
	private static final String DEFAULT_YFACES_CTX = "/META-INF/yfaces-context.xml";

	private static final String log4jCfg = "/WEB-INF/yfaces-log4j.properties";

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void contextInitialized(ServletContextEvent arg0) {
		ApplicationContext ctx = this.createYFacesApplicationContext(arg0.getServletContext());
		this.setYFacesApplicationContext(ctx);
		this.configureLogging(arg0);
	}

	/**
	 * Creates a spring based {@link ApplicationContext}. The result is used for construction of an
	 * yfaces specific {@link YApplicationContext}.
	 * <p>
	 * Default behavior is to use two different context files which are merged. First one is located
	 * under {@link #DEFAULT_YFACES_CTX}. This is just a simple but well pre-configured yfaces
	 * context. Location of second one can be configured as deployment parameter
	 * {@link #PARAM_YFACES_CTX}.
	 * <p>
	 * If there's already a {@link ApplicationContext} available from elsewhere just override this
	 * method.
	 * 
	 * @param servletCtx
	 *            {@link ServletContextEvent}
	 * @return {@link ApplicationContext}
	 */
	protected WebApplicationContext createYFacesApplicationContext(ServletContext servletCtx) {
		ConfigurableWebApplicationContext result = new XmlWebApplicationContext();
		result.setServletContext(servletCtx);

		URL defaultConfig = YFacesStartupListener.class.getResource(DEFAULT_YFACES_CTX);
		String[] configs = new String[] { defaultConfig.toExternalForm() };

		try {
			String yfacesCtx = servletCtx.getInitParameter(PARAM_YFACES_CTX);
			if (yfacesCtx != null) {
				URL customConfig = servletCtx.getResource(yfacesCtx);
				configs = new String[] { configs[0], customConfig.toExternalForm() };
			}
			log.debug("Using spring configuration:" + Arrays.asList(configs));
			result.setConfigLocations(configs);
			result.refresh();
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private void setYFacesApplicationContext(ApplicationContext ctx) {
		// constructor can be called one times
		// applicationconext is managed internally as static singleton 
		new YApplicationContext(ctx);
	}

	/**
	 * Configures log4j.
	 */
	private void configureLogging(ServletContextEvent arg0) {
		try {
			URL url = arg0.getServletContext().getResource(log4jCfg);
			if (url != null) {
				System.out.println(log4jCfg
						+ " found; this overwrites any previous log4j configurations!");
				PropertyConfigurator.configure(url);
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
