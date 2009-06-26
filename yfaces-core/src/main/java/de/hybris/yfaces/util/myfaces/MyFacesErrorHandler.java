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
package de.hybris.yfaces.util.myfaces;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFaces;
import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.util.YFacesErrorHandler;

/**
 * This class is meant to be the myfaces error handler which must be declared as deployment
 * parameter in web.xml.
 * <p>
 * <code>
 * &lt;context-param&gt;<br/>
 * 		&lt;param-name&gt;org.apache.myfaces.ERROR_HANDLER&lt;/param-name&gt;<br/>
 * 		&lt;param-value&gt;de.hybris.yfaces.myfaces.MyFacesErrorHandler&lt;/param-value&gt;<br/>
 * 	&lt;/context-param&gt;<br/>
 * </code> After doing this {@link YFacesErrorHandler} gets invoked whenever an exception gets
 * thrown.
 * <p/>
 * This fully replaces the standard error handler or a custom one as provided by Facelets
 * <p>
 * Learn more about the error handling of myfaces here:
 * http://wiki.apache.org/myfaces/Handling_Server_Errors
 * 
 * @author Denny.Strietzbaum
 */
public class MyFacesErrorHandler {

	private static final Logger log = Logger.getLogger(MyFacesErrorHandler.class);

	/**
	 * MyFaces error handler. Just delegates to the configured {@link YFacesErrorHandler}
	 * 
	 * @param fc
	 *            {@link FacesContext}
	 * @param ex
	 *            {@link Exception} to deal with
	 */
	public void handleException(final FacesContext fc, final Exception ex) {

		log.error("Got notified of an unhandled exception: ", ex);

		Throwable cause = this.findReleventCause(ex);
		YFaces.getRequestContext().getErrorHandler().handleException(cause);
	}

	/**
	 * Internal.<br/>
	 * Tries to walk one level up in case the exception is not of type {@link YFacesException}
	 * 
	 * @param e
	 * @return {@link Throwable}
	 */
	private Throwable findReleventCause(Exception e) {
		final Throwable result = (e instanceof YFacesException) ? e : e.getCause();
		return result;
	}

}
