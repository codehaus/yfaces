package de.hybris.yfaces.integration;

import javax.el.ELContextEvent;
import javax.el.ELContextListener;

/**
 * An {@link ELContextListener} which adds an {@link YFacesELContext}.
 */
public class YFacesELContextListener implements ELContextListener {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELContextListener#contextCreated(javax.el.ELContextEvent)
	 */
	public void contextCreated(final ELContextEvent arg0) {
		arg0.getELContext().putContext(YFacesELContext.class, new YFacesELContext());
	}
}
