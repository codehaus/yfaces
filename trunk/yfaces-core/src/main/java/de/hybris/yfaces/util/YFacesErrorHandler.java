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

package de.hybris.yfaces.util;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFaces;
import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.util.myfaces.MyFacesErrorHandler;

/**
 * Provides general error handling.
 * <p>
 * When myfaces are used an additional error handler {@link MyFacesErrorHandler} can be configured
 * at web.xml. <code>
 * <pre>
 * &lt;context-param&gt;
 *   &lt;param-name&gt;org.apache.myfaces.ERROR_HANDLER&lt;/param-name&gt;
 *   &lt;param-value&gt;de.hybris.yfaces.util.MyFacesErrorHandler&lt;/param-value&gt;
 * &lt;/context-param&gt;
 * </pre>
 * </code> This handler catches each exception which may occur in any lifecycle phase and delegates
 * to this handler by calling {@link #handleException(Throwable, String)} Default behavior is to log
 * the exception and redirect to an error page. Of course this can be invoked manually too. However,
 * there's a difference between call {@link #handleException(Throwable, String)} and let it be
 * called by the myfaces error handler. Calling manually may be problematic as the current lifecycle
 * always gets finished (and fully processed) whereas an exception which is thrown during a
 * lifecycle interrupts the processing and calls this error handler immediately.
 * 
 * @author Denny Strietzbaum
 */
public class YFacesErrorHandler {

	private static final Logger log = Logger.getLogger(YFacesErrorHandler.class);
	private static final String ERROR_STACK = YFacesErrorHandler.class.getName();

	/**
	 * Catches and handles the passed exception. Exception handling involves redirecting to an error
	 * page and hold an provide the error cause {@link #getErrorCause()}. until the next
	 * RENDER_RESPONSE phase has finished. The error cause gets lost with next RENDER_RESPONSE phase.
	 * (Nevertheless this is a redirect safe solution).
	 * <p/>
	 * The redirect page is requested from {@link #getErrorPage(FacesContext, Throwable)}. The error
	 * message is requested from {@link #getErrorMessage(FacesContext, Throwable)}. Both methods can
	 * be overridden for a more customized error handling.
	 * 
	 * @param fc
	 *          {@link FacesContext}
	 * @param ex
	 *          {@link Exception}
	 */

	public void handleException(final Throwable ex) {
		this.handleException(ex, null);
	}

	/**
	 * Raises an exception. This includes: error log (console) and redirecting to an error page.
	 * 
	 * @param ex
	 * @param redirectTo
	 */
	public void handleException(final Throwable ex, String redirectTo) {

		final FacesContext fc = FacesContext.getCurrentInstance();
		log.error(ex);
		final String errorMsg = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
		if (redirectTo == null) {
			redirectTo = this.getErrorPage(ex);
		}
		log.error("Redirecting to error page: " + redirectTo);

		if (redirectTo != null) {
			YFaces.getRequestContext().redirect(redirectTo);
			fc.getExternalContext().getSessionMap().put(ERROR_STACK, errorMsg);
		}
	}

	/**
	 * Returns the desired page which shall be redirected to. Parameters can be used to decide between
	 * multiple pages. A return of 'null' means that this error shouldn't be handled.
	 * 
	 * @param fc
	 * @param ex
	 * @return error page as relative path
	 */
	protected String getErrorPage(final Throwable ex) {
		String result = null;
		if (ex instanceof YFacesException) {
			result = "/index.jsf";
		}
		return result;
	}

	public String getErrorCause() {
		return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
				ERROR_STACK);
	}

	public void reset() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ERROR_STACK, null);
	}

}
