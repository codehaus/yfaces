package de.hybris.yfaces;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

import org.apache.log4j.Logger;

import de.hybris.yfaces.context.YRequestContext;

/**
 * @author Denny Strietzbaum
 */
public class YFacesRequestListener implements ServletRequestListener {

	private static final String REQUEST_BUILDER = "yfaces-requestbuilder";

	private static final Logger log = Logger.getLogger(YFacesRequestListener.class);

	private static YRequestContextBuilder builder = null;

	public void requestInitialized(final ServletRequestEvent arg0) {

		// first call needs a builder initialization
		if (builder == null) {

			// get configured builder class from web.xml
			final String builderName = arg0.getServletContext().getInitParameter(REQUEST_BUILDER);

			// if configured...
			if (builderName != null) {
				try {
					// ... instantiate
					final Class builderClass = Thread.currentThread().getContextClassLoader().loadClass(
							builderName.trim());
					builder = (YRequestContextBuilder) builderClass.newInstance();
				} catch (final Exception e) {
					throw new YFacesException("Error creating "
							+ YRequestContextBuilder.class.getSimpleName(), e);
				}
				log.debug("Use " + YRequestContextBuilder.class.getSimpleName() + ": "
						+ builder.getClass().getName());
				// ... otherwise take a default one
			} else {
				builder = new DefaultYRequestContextBuilder();
			}
		}

		// take builder to create and set a YRequestContext
		final YRequestContext ctx = builder.buildYRequestContext(arg0.getServletRequest());
		YFaces.setRequestContext(ctx);
	}

	public void requestDestroyed(final ServletRequestEvent arg0) {
		YFaces.removeRequestContext();
	}

}
