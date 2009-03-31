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

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

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
 * @author Denny.Strietzbaum
 */

public class YRequestContextPhaseListener implements PhaseListener {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(YRequestContextPhaseListener.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(final PhaseEvent phaseevent) {

		if (phaseevent.getPhaseId() == PhaseId.RESTORE_VIEW) {
			YRequestContextImpl reqCtx = (YRequestContextImpl) YRequestContext.getCurrentContext();
			YSessionContext sesCtx = reqCtx.getSessionContext();
			YPageContext pageContext = sesCtx.getConversationContext().getLastPage();
			reqCtx.setPageContext(pageContext);
		}

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			final boolean facesRequest = YRequestContext.getCurrentContext().isPostback();
			if (facesRequest) {
				// this viewid can be different (after a POST) from that one in
				// RESTORE_VIEW
				((YRequestContextImpl) YRequestContext.getCurrentContext()).switchPage(getViewId());
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
			((YRequestContextImpl) YRequestContext.getCurrentContext())
					.startPageRequest(getViewId());
		}

		if (phaseevent.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			((YRequestContextImpl) YRequestContext.getCurrentContext())
					.finishPageRequest(getViewId());

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
