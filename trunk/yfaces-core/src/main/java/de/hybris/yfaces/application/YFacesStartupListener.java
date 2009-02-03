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

package de.hybris.yfaces.application;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

public class YFacesStartupListener implements ServletContextListener {

	private static final Logger log = Logger.getLogger(YFacesStartupListener.class);

	private static final String PARAM_YFACES_CTX = "yfaces-context";
	private static final String log4jCfg = "/WEB-INF/yfaces.properties";

	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void contextInitialized(ServletContextEvent arg0) {
		this.configureSpring(arg0);
		this.configureLogging(arg0);
	}

	private void configureSpring(ServletContextEvent arg0) {
		ConfigurableWebApplicationContext ctx = new XmlWebApplicationContext();
		ctx.setServletContext(arg0.getServletContext());
		try {
			String yfacesCtx = arg0.getServletContext().getInitParameter(PARAM_YFACES_CTX);
			URL url = null;
			if (yfacesCtx != null) {
				url = arg0.getServletContext().getResource(yfacesCtx);
			} else {
				url = YFacesStartupListener.class.getResource("/META-INF/yfaces-context.xml");
			}
			log.debug("Using spring configuration:" + url);
			ctx.setConfigLocation(url.toExternalForm());
			ctx.refresh();
			YFacesContext.setApplicationContext(ctx);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void configureLogging(ServletContextEvent arg0) {
		ConfigurableWebApplicationContext ctx = new XmlWebApplicationContext();
		try {
			URL url = arg0.getServletContext().getResource(log4jCfg);
			PropertyConfigurator.configure(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
