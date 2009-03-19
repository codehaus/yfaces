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

import org.springframework.context.ApplicationContext;

import de.hybris.yfaces.component.NavigationContext;

/**
 * @author Denny.Strietzbaum
 * 
 */
public abstract class YFacesContext {

	private static ApplicationContext appCtx = null;

	static void setApplicationContext(ApplicationContext ctx) {
		appCtx = ctx;
	}

	public static YFacesContext getCurrentContext() {
		return (YFacesContext) appCtx.getBean(YFacesContext.class.getName());
	}

	/**
	 * Returns the {@link UserSession}
	 * 
	 * @return the UserSession
	 */
	public abstract YSessionContext getUserSession();

	public abstract YFacesErrorHandler getErrorHandler();

	public abstract NavigationContext getNavigationContext();

	// add Constructor at NavigationContext which accepts YSessionContext
	// add getSessionContext at NavgiationContext
	// let getNavigationContext part of YFacesContextImpl; ask SpringCOntext for YSessionContext instance when creating NavContext
	// comment out getUserSession / getNavigationContext; (or let it be for convenience at first but use new delegate mechanism) <- no think better remove it
	// add getPageContext (getNavigationContext.getCurrentPage)
	// 
	// rename each into *Context

}
