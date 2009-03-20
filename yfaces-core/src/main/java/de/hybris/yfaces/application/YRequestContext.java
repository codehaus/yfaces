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
import javax.faces.render.ResponseStateManager;

/**
 * @author Denny.Strietzbaum
 * 
 */
public abstract class YRequestContext {

	public static YRequestContext getCurrentContext() {
		return (YRequestContext) YApplicationContext.getApplicationContext().getBean(
				YRequestContext.class.getName());
	}

	/**
	 * Returns the {@link UserSession}
	 * 
	 * @return the UserSession
	 */
	public abstract YSessionContext getSessionContext();

	public abstract YFacesErrorHandler getErrorHandler();

	public abstract NavigationContext getNavigationContext();

	// add Constructor at NavigationContext which accepts YSessionContext
	// add getSessionContext at NavgiationContext
	// let getNavigationContext part of YFacesContextImpl; ask SpringCOntext for YSessionContext instance when creating NavContext
	// comment out getUserSession / getNavigationContext; (or let it be for convenience at first but use new delegate mechanism) <- no think better remove it
	// add getPageContext (getNavigationContext.getCurrentPage)
	// 
	// rename each into *Context

	/**
	 * Redirects to the current URL.<br/>
	 * This creates a non-faces request and is useful to ensure that no data is cached within the
	 * component tree.<br/>
	 */
	public abstract void redirect(boolean isFlash);

	public abstract void redirect(YPageContext page, boolean isFlash);

	/**
	 * Redirect to the passed URL.<br>
	 * If URL is relative, the Applicationpath will be added first.
	 * 
	 * @param url
	 *            target URL.
	 */
	public abstract void redirect(String url, boolean isFlash);

	/**
	 * Redirects to the passed URL.
	 * 
	 * @param url
	 */
	public abstract void redirect(String url);

	/**
	 * Similar to a postback but only occurs for a GET request
	 */
	public abstract boolean isFlashback();

	/**
	 * Shortcut for {@link ResponseStateManager#isPostback(FacesContext)}
	 * 
	 * @return true when current request is a jsf postback
	 */
	public abstract boolean isPostback();
}
