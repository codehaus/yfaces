package org.codehaus.yfaces;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.codehaus.yfaces.context.YRequestContext;


/**
 * Request listener which builds yfaces context for current request. Building strategy is configured
 * as deployment parameter "yfaces-requestbuilder" whose value must be a class literal of type
 * {@link YRequestContextBuilder}.
 * 
 * @author Denny Strietzbaum
 */
public class YFacesRequestListener implements ServletRequestListener {

	private static Logger LOG = Logger.getLogger(YFacesRequestListener.class);

	// name of deployment parameter for an YRequestContextBuilder 
	private static final String REQUEST_BUILDER = "yfaces-requestbuilder";

	// default log4j properties
	private static final String log4jCfg = "/WEB-INF/log4j.properties";

	// core properties are taken as fallback for a log4j configuration file
	private static final String yfacesCfg = "/WEB-INF/yfaces.properties";

	private static YRequestContextBuilder builder = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequestListener#requestInitialized(javax.servlet.ServletRequestEvent)
	 */
	public void requestInitialized(final ServletRequestEvent arg0) {

		// first call needs a builder initialization
		// conditional block gets never executed a second time 
		if (builder == null) {

			// setup yfaces configuration
			this.configureYConfig(arg0.getServletContext());

			// setup logging configuration
			this.configureLogging(arg0.getServletContext());
			// initialize Logger again
			LOG = Logger.getLogger(YFacesRequestListener.class);

			// get configured builder class from web.xml
			final String builderName = arg0.getServletContext().getInitParameter(REQUEST_BUILDER);

			// if configured...
			if (builderName != null) {
				try {
					// ... instantiate
					final Class<?> builderClass = Thread.currentThread().getContextClassLoader().loadClass(
							builderName.trim());
					builder = (YRequestContextBuilder) builderClass.newInstance();
				} catch (final Exception e) {
					throw new YFacesException("Error creating "
							+ YRequestContextBuilder.class.getSimpleName(), e);
				}
				LOG.debug("Use " + YRequestContextBuilder.class.getSimpleName() + ": "
						+ builder.getClass().getName());
				// ... otherwise take a default one
			} else {
				builder = new DefaultYRequestContextBuilder();
			}
		}

		// take builder to create and set a YRequestContext
		final YRequestContext ctx = builder.buildYRequestContext(arg0.getServletRequest());
		// context is pushed into a ThreadLocal
		YFaces.setRequestContext(ctx);
	}

	public void requestDestroyed(final ServletRequestEvent arg0) {
		YFaces.removeRequestContext();
	}

	protected void configureYConfig(final ServletContext ctx) {
		URL cfgUrl = null;
		try {
			cfgUrl = ctx.getResource(yfacesCfg);
			final Properties p = new Properties();
			p.load(cfgUrl.openStream());
			YFacesConfig.properties = p;

		} catch (final MalformedURLException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			LOG.error("Error reading " + yfacesCfg, e);
		}

	}

	/**
	 * Configures log4j.
	 */
	protected void configureLogging(final ServletContext ctx) {

		URL logCfg = null;
		try {
			// try default log-config
			logCfg = ctx.getResource(log4jCfg);

			// if no success, try fallback log-config 
			if (logCfg == null) {
				logCfg = ctx.getResource(yfacesCfg);
			}

			// configure Logger
			if (logCfg != null) {
				PropertyConfigurator.configure(logCfg);
				LOG.debug("Successfully loaded " + log4jCfg);
			}
		} catch (final MalformedURLException e) {
			// should never happens
			e.printStackTrace();
		}
	}

}
