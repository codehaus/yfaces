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
package de.hybris.yfaces.util;

import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * A PhaseListener which is only useful for developing.<br/>
 * Evaluates every Phase of JSF lifecycle and prints some nice debug
 * information.<br/>
 * Outputs current jsf phase, recognizes redirects, various aborts like
 * validation errors (incl. their source) and other stuff.<br/>
 * To enable this listener it must be configured at faces configuration file and
 * the logger for this class must be set to debug level.<br/>
 * <br/>
 * 
 * @author Denny.Strietzbaum
 */
public class DebugPhaseListener implements PhaseListener {
	private static final long serialVersionUID = 1L;

	// log4j logger
	private static final Logger log = Logger.getLogger(DebugPhaseListener.class);

	private static int reqCount = 0;

	public void beforePhase(final PhaseEvent e) {
		if (log.isDebugEnabled()) {
			switch (e.getPhaseId().getOrdinal()) {
			case 1:
				reqCount++;
				log.debug(reqCount + ": ---------- START LIFECYCLE [" + getMethod()
						+ "] ---------- ");
				break;
			}
			log.debug(reqCount + ": ENTER JSF PHASE: " + e.getPhaseId());
		}
	}

	public void afterPhase(final PhaseEvent e) {
		if (log.isDebugEnabled()) {
			boolean isAbortedByRenderResponse = false;
			final boolean isAbortedByResponseComplete = FacesContext.getCurrentInstance()
					.getResponseComplete();

			switch (e.getPhaseId().getOrdinal()) {
			case 1:
				final UIViewRoot viewRoot = e.getFacesContext().getViewRoot();
				log.debug(reqCount + ": View:" + viewRoot.getViewId() + " (" + viewRoot.hashCode()
						+ ")");
				break;
			case 3:
				isAbortedByRenderResponse = FacesContext.getCurrentInstance().getRenderResponse();
				break;
			case 4:
				isAbortedByRenderResponse = FacesContext.getCurrentInstance().getRenderResponse();
				break;
			case 6:
				log.debug(reqCount + ": ---------- FINISHED LIFECYCLE [" + getMethod()
						+ "] ---------- ");
			}

			if (isAbortedByRenderResponse) {
				log.debug(reqCount
						+ ": ---------- ABORTED LIFECYCLE (renderResponse = true) ---------- ");
				log.debug(reqCount + "Validation- or Updateerror? (below a FacesMessage dump");
				for (final Iterator<FacesMessage> iter = FacesContext.getCurrentInstance()
						.getMessages(); iter.hasNext();) {
					final FacesMessage msg = iter.next();
					log.debug("- " + msg.getSeverity() + "; " + msg.getSummary() + "; "
							+ msg.getDetail());
				}
				;
			}

			if (isAbortedByResponseComplete) {
				log.debug(reqCount
						+ ": ---------- ABORTED LIFECYCLE (responseComplete = true) ---------- ");
				log.debug(reqCount + "Redirect?");
			}
		}

	}

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	private String getMethod() {
		String method = "";

		// StateManager#isPostback() can't be used since it isn't available in
		// RESTORE_VIEW
		final Object req = FacesContext.getCurrentInstance().getExternalContext().getRequest();
		if (req instanceof HttpServletRequest) {
			method = ((HttpServletRequest) req).getMethod().toUpperCase();
		}

		return method;
	}

}
