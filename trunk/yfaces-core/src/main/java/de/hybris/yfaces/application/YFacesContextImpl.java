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

import java.util.Map;

import javax.faces.context.FacesContext;

import de.hybris.yfaces.component.NavigationContext;
import de.hybris.yfaces.component.NavigationContextImpl;
import de.hybris.yfaces.session.UserSession;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YFacesContextImpl extends YFacesContext {

	private UserSession userSession = null;
	private YFacesErrorHandler errorHandler = null;

	/**
	 * @return the errorHandler
	 */
	@Override
	public YFacesErrorHandler getErrorHandler() {
		return errorHandler;
	}

	/**
	 * @param errorHandler
	 *            the errorHandler to set
	 */
	public void setErrorHandler(final YFacesErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * @return the userSession
	 */
	@Override
	public UserSession getUserSession() {
		return userSession;
	}

	/**
	 * @param userSession
	 *            the userSession to set
	 */
	public void setUserSession(final UserSession userSession) {
		this.userSession = userSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YFacesContext#getNavigationContext()
	 */
	@Override
	public NavigationContext getNavigationContext() {
		// return NavigationContext.getCurrentContext();
		final Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		NavigationContext result = (NavigationContext) map.get(NavigationContext.class.getName());
		if (result == null) {
			result = new NavigationContextImpl(null);
			map.put(NavigationContext.class.getName(), result);
		}
		return result;

	}

}
