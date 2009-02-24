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

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.myfaces.MyFacesErrorHandler;

/**
 * Provides general error handling. When the {@link MyFacesErrorHandler} is registered at web.xml
 * this handler gets invoked automatically at {@link #handleException(FacesContext, Exception)}.
 * Additionally a manual error handling can be invoked at every time.
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesErrorHandler {

	private static final Logger log = Logger.getLogger(YFacesErrorHandler.class);
	private static final String ERROR_STACK = YFacesErrorHandler.class.getName();

	/**
	 * Catches and handles the passed exception. Exception handling involves redirecting to an error
	 * page and hold an provide the error cause {@link #getErrorSource()}. until the next
	 * RENDER_RESPONSE phase has finished. The error cause gets lost with next RENDER_RESPONSE
	 * phase. (Nevertheless this is a redirect safe solution).
	 * <p/>
	 * The redirect page is requested from {@link #getErrorPage(FacesContext, Throwable)}. The error
	 * message is requested from {@link #getErrorMessage(FacesContext, Throwable)}. Both methods can
	 * be overridden for a more customized error handling.
	 * 
	 * @param fc
	 *            {@link FacesContext}
	 * @param ex
	 *            {@link Exception}
	 */
	public void handleException(final FacesContext fc, final Exception ex) {
		final Throwable cause = this.findReleventCause(ex);

		log.error("Got unhandled exception", ex);

		final String target = this.getErrorPage(fc, cause);
		if (target != null) {
			final String errorMsg = getErrorMessage(fc, cause);
			log.error("Redirecting to errorpage: " + target + "(" + errorMsg + ")");
			YFacesContext.getCurrentContext().getNavigationContext().redirect(target);
			fc.getExternalContext().getSessionMap().put(ERROR_STACK, errorMsg);
		}
	}

	public void handleException(final FacesContext fc, final String msg) {
		try {
			this.handleException(fc, new YFacesException(msg));
		} catch (final Exception e) {
			log.fatal("Unhandled error");
			e.printStackTrace();
		}
	}

	/**
	 * Returns the desired page which shall be redirected to. Parameters can be used to decide
	 * between multiple pages. A return of 'null' means that this error shouldn't be handled.
	 * 
	 * @param fc
	 * @param ex
	 * @return error page as relative path
	 */
	protected String getErrorPage(final FacesContext fc, final Throwable ex) {
		String result = null;
		if (ex instanceof YFacesException) {
			result = "/index.jsf";
		}
		return result;
	}

	/**
	 * Returns the error message for the passed exception.
	 * 
	 * @param fc
	 * @param ex
	 * @return error message
	 */
	protected String getErrorMessage(final FacesContext fc, final Throwable ex) {
		final String result = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
		return result;
	}

	public String getErrorSource() {
		return (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
				ERROR_STACK);
	}

	public void clearErrorStack() {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ERROR_STACK,
				null);
	}

	/**
	 * Internal.<br/>
	 * Tries to walk one level up in case the exception is not of type {@link YFacesException}
	 * 
	 * @param e
	 * @return {@link Throwable}
	 */
	private Throwable findReleventCause(final Exception e) {
		final Throwable result = (e instanceof YFacesException) ? e : e.getCause();
		return result;
	}

}
