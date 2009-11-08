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

package de.hybris.yfaces.context;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.ServletContextListener;

import de.hybris.yfaces.YFaces;
import de.hybris.yfaces.YFacesApplication;
import de.hybris.yfaces.YFacesException;

/**
 * This {@link PhaseListener} is mandatory for a properly work with the {@link YConversationContext}
 * .<br/>
 * <br/>
 * Implementation note: <br/>
 * This listener assumes a {@link YConversationContextImpl} as implementation.<br/>
 * On that implementation some callback methods are used:<br/>
 * - {@link YConversationContextImpl#startPageRequest(String)} -
 * {@link YConversationContextImpl#switchPage(String)} -
 * {@link YConversationContextImpl#finishPageRequest(String)}
 * 
 * @author Denny Strietzbaum
 */

public class YRequestContextPhaseListener implements PhaseListener {

	private static final long serialVersionUID = 1L;

	//private static final Logger log = Logger.getLogger(YRequestContextPhaseListener.class);

	/**
	 * Constructor.
	 * <p>
	 * Creates and registers a {@link YFacesApplication} instance at currently installed
	 * {@link ApplicationFactory}.
	 * <p>
	 * Execution time of a {@link PhaseListener} constructor assures a well configured JSF
	 * environment. This wouldn't be the case when using a {@link ServletContextListener} for this
	 * task. E.g for myfaces such a listener will fail as myfaces configure the JSF environment via a
	 * listener <code>org.apache.myfaces.webapp.StartupServletContextListener</code> which is given in
	 * a tld file. Such listeners are (according spec.) invoked after web.xml listeners.
	 */
	public YRequestContextPhaseListener() {

		final ApplicationFactory appFac = (ApplicationFactory) FactoryFinder
				.getFactory(FactoryFinder.APPLICATION_FACTORY);
		final Application base = appFac.getApplication();

		// prevents a double registration of this Phaselistener
		if (base instanceof YFacesApplication) {
			throw new YFacesException(YFacesApplication.class.getName() + " already registered. "
					+ this.getClass().getSimpleName() + " is registered twice?");
		}

		appFac.setApplication(new YFacesApplication(base));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(final PhaseEvent phaseevent) {

		if (phaseevent.getPhaseId() == PhaseId.RESTORE_VIEW) {
			final YRequestContextImpl reqCtx = (YRequestContextImpl) YFaces.getRequestContext();
			final YSessionContext sesCtx = reqCtx.getSessionContext();
			final YPageContext pageContext = sesCtx.getConversationContext().getLastPage();
			reqCtx.setPageContext(pageContext);

			reqCtx.startInitialization();
		}

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			final YRequestContextImpl reqCtx = (YRequestContextImpl) YFaces.getRequestContext();
			final boolean facesRequest = reqCtx.isPostback();
			if (facesRequest) {
				// this viewid can be different (after a POST) from that one in
				// RESTORE_VIEW
				reqCtx.switchPage(getViewId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(final PhaseEvent phaseevent) {
		if (phaseevent.getPhaseId() == PhaseId.RESTORE_VIEW) {
			((YRequestContextImpl) YFaces.getRequestContext()).startPageRequest(getViewId());
		}

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			((YRequestContextImpl) YFaces.getRequestContext()).finishPageRequest(getViewId());

			YFaces.getRequestContext().getErrorHandler().reset();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	/**
	 * Returns the viewid for the current request cycle.<br/>
	 * This id can change between {@link PhaseId#INVOKE_APPLICATION} and
	 * {@link PhaseId#RENDER_RESPONSE}
	 * 
	 * @return viewid
	 */
	private String getViewId() {
		return FacesContext.getCurrentInstance().getViewRoot().getViewId();
	}

}
