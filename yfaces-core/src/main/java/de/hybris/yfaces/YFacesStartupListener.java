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

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * YFaces startup. Reads configuration and initializes runtime properties.
 * 
 * @author Denny Strietzbaum
 */
public class YFacesStartupListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(YFacesStartupListener.class);

	private static final String log4jCfg = "/WEB-INF/yfaces-log4j.properties";

	public void contextDestroyed(final ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void contextInitialized(final ServletContextEvent arg0) {

		final ServletContext ctx = arg0.getServletContext();
		this.configureLogging(ctx);

	}

	/**
	 * Configures log4j.
	 */
	protected void configureLogging(final ServletContext ctx) {
		try {
			final URL url = ctx.getResource(log4jCfg);
			if (url != null) {
				System.out.println(log4jCfg + " found; this overwrites any previous log4j configurations!");
				PropertyConfigurator.configure(url);
			}

		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
