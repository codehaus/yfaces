package org.codehaus.yfaces;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.context.YApplicationContext;
import org.codehaus.yfaces.context.YRequestContext;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;


public class SpringYRequestContextBuilder implements YRequestContextBuilder {

	private static final Logger log = Logger.getLogger(SpringYRequestContextBuilder.class);

	private static final String PARAM_YFACES_CTX = "yfaces-context";
	private static final String DEFAULT_YFACES_CTX = "/META-INF/yfaces-context.xml";

	private static ApplicationContext appCtx = null;

	public YRequestContext buildYRequestContext(final ServletRequest request) {
		if (appCtx == null) {
			appCtx = this.createYApplicationContext(((HttpServletRequest) request).getSession()
					.getServletContext());
		}
		final YRequestContext ctx = (YRequestContext) appCtx.getBean(YRequestContext.class
				.getSimpleName());

		return ctx;
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
	 *          {@link ServletContextEvent}
	 * @return {@link ApplicationContext}
	 */
	protected WebApplicationContext createYApplicationContext(final ServletContext servletCtx) {
		final ConfigurableWebApplicationContext result = new XmlWebApplicationContext();
		result.setServletContext(servletCtx);

		final URL defaultConfig = SpringYRequestContextBuilder.class.getResource(DEFAULT_YFACES_CTX);
		String[] configs = new String[] { defaultConfig.toExternalForm() };

		try {
			final String yfacesCtx = servletCtx.getInitParameter(PARAM_YFACES_CTX);
			if (yfacesCtx != null) {
				final URL customConfig = servletCtx.getResource(yfacesCtx);
				configs = new String[] { configs[0], customConfig.toExternalForm() };
			}
			log.debug("Using spring configuration:" + Arrays.asList(configs));
			result.setConfigLocations(configs);
			result.refresh();
		} catch (final MalformedURLException ex) {
			ex.printStackTrace();
		}

		return result;
	}

}
