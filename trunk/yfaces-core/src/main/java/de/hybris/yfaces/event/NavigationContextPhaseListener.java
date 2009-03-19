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

package de.hybris.yfaces.event;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

import de.hybris.yfaces.application.YRequestContext;
import de.hybris.yfaces.component.NavigationContext;
import de.hybris.yfaces.component.NavigationContextImpl;

/**
 * This {@link PhaseListener} is mandatory for a properly work with the
 * {@link NavigationContext}.<br/>
 * <br/>
 * Implementation note: <br/>
 * This listener assumes a {@link NavigationContextImpl} as implementation.<br/>
 * On that implementation some callback methods are used:<br/>
 * - {@link NavigationContextImpl#startPageRequest(String)} -
 * {@link NavigationContextImpl#switchPage(String)} -
 * {@link NavigationContextImpl#finishPageRequest(String)}
 * 
 * @author Denny.Strietzbaum
 */

// Should this become renamed into a more general one like YFacesPhaseListener?
// since other stuff (clear errors) are done here too
public class NavigationContextPhaseListener implements PhaseListener {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(NavigationContextPhaseListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(final PhaseEvent phaseevent) {
		// if (phaseevent.getPhaseId() == PhaseId.RESTORE_VIEW)
		// {
		// REQUEST_PHASE phase =
		// ((NavigationContextImpl)NavigationContext.getCurrentContext()).getRequestPhase();
		// if (!phase.equals(REQUEST_PHASE.END_REQUEST))
		// {
		// while (!phase.equals(REQUEST_PHASE.END_REQUEST))
		// {
		// log.warn("Concurrent user request; go to sleep ...(" +
		// Thread.currentThread().getId() + ")");
		// try
		// {
		// Thread.sleep(2000);
		// phase =
		// ((NavigationContextImpl)NavigationContext.getCurrentContext()).getRequestPhase();
		// }
		// catch (Exception e)
		// {
		// e.printStackTrace();
		// }
		// }
		// log.warn("awake from sleep (" + Thread.currentThread().getId() +
		// ")");
		// }
		// }

		// if (phaseevent.getPhaseId() == PhaseId.INVOKE_APPLICATION)
		// {
		// FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put("FACES_REQUEST",
		// true);
		// }

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			final boolean facesRequest = getNavigationContext().isPostback();
			// boolean facesRequest =
			// FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("FACES_REQUEST")
			// != null;
			if (facesRequest) {
				// this viewid can be different (after a POST) from that one in
				// RESTORE_VIEW
				getNavigationContext().switchPage(getViewId());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(final PhaseEvent phaseevent) {
		if (phaseevent.getPhaseId() == PhaseId.RESTORE_VIEW) {
			getNavigationContext().startPageRequest(getViewId());
		}

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			getNavigationContext().finishPageRequest(getViewId());

			YRequestContext.getCurrentContext().getErrorHandler().clearErrorStack();
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
	 * Returns the {@link NavigationContext} which is used for this current
	 * cycle.
	 * 
	 * @return {@link NavigationContext}
	 */
	private NavigationContextImpl getNavigationContext() {
		// this could be the place to support different NavigationContexts
		// given by a URL query param
		return (NavigationContextImpl) YRequestContext.getCurrentContext().getNavigationContext();
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
