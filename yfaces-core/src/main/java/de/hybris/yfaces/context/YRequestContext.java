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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import de.hybris.yfaces.util.YFacesErrorHandler;

/**
 * A context object whose scope and lifetime is bound to {@link HttpServletRequest}.
 * 
 * @author Denny.Strietzbaum
 */
public interface YRequestContext {

	// TODO: move to appCtx
	public YFacesErrorHandler getErrorHandler();

	/**
	 * @return {@link YPageContext}
	 */
	public YPageContext getPageContext();

	/**
	 * @return {@link YSessionContext}
	 */
	public YSessionContext getSessionContext();

	/**
	 * A map of attributes backed by the lifetime of this scope. Fetching a value for a key is the
	 * same like {@link HttpServletRequest#getAttribute(String)}. Same with setting a value.
	 * 
	 * @return {@link Map}
	 */
	public Map<String, Object> getAttributes();

	/**
	 * Shortcut to {@link #redirect(String, boolean)} with disabled flashback.
	 * 
	 * @param url
	 *            target url
	 */
	public void redirect(String url);

	/**
	 * Shortcut to {@link #redirect(String, boolean)} whereas URL is the servletpath.
	 * 
	 * @param isFlash
	 *            whether flashback shall be enabled
	 * @see HttpServletRequest#getServletPath()
	 */
	public void redirect(boolean isFlash);

	/**
	 * Shortcut to {@link #redirect(String, boolean)} whereas URL is taken from passed
	 * {@link YPageContext}
	 * 
	 * @param page
	 *            {@link YPageContext} to redirect to
	 * @param isFlash
	 *            whether flashback shall be enabled
	 * @see YPageContext#getURL()
	 */
	public void redirect(YPageContext page, boolean isFlash);

	/**
	 * Redirects to the passed URL.
	 * 
	 * @param url
	 *            target url.
	 * @param isFlash
	 *            true when flash shall be enabled
	 */
	public void redirect(String url, boolean enableFlashback);

}
