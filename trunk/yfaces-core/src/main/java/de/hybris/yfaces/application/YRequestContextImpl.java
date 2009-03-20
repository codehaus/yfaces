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

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YRequestContextImpl extends YRequestContext {

	private static final Logger log = Logger.getLogger(YRequestContextImpl.class);

	private YSessionContext userSession = null;
	private YFacesErrorHandler errorHandler = null;

	private boolean isFlashback = false;

	public YRequestContextImpl() {
		isFlashback = FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.remove(IS_FLASHBACK) != null;
	}

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
	public YSessionContext getSessionContext() {
		return userSession;
	}

	/**
	 * @param userSession
	 *            the userSession to set
	 */
	public void setSessionContext(YSessionContext userSession) {
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

	/**
	 * Redirects to the current URL.<br/>
	 * This creates a non-faces request which destroys the current {@link UIViewRoot}
	 */
	@Override
	public void redirect(final boolean isFlash) {
		final String url = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestServletPath();
		redirect(url, isFlash);
	}

	@Override
	public void redirect(final YPageContext page, final boolean isFlash) {
		this.redirect(page.getURL(), isFlash);
	}

	@Override
	public void redirect(final String url) {
		this.redirect(url, false);
	}

	/**
	 * Redirects to the passed url.<br>
	 * When flash is enabled some yfaces specific content keeps alive.<br/>
	 * <br/>
	 * Url can be absolute or relative.<br/>
	 * URLs starting with 'http' are used absolute.<br/>
	 * URLs starting with a slash '/' are handled relative to the webapp root.<br/>
	 * All other URLs are handled relative to the current request URI.
	 * 
	 * @param url
	 *            target URL.
	 */
	@Override
	public void redirect(String url, final boolean enableFlashback) {
		if (url == null) {
			throw new YFacesException("No URL specified", new NullPointerException());
		}

		final FacesContext fctx = FacesContext.getCurrentInstance();
		final ExternalContext ectx = fctx.getExternalContext();

		// when url is not absolute...
		if (!url.startsWith("http")) {
			// spec. accepts absolute as well as relative url
			// relative path without leading slash is interpreted relatively to
			// current request URI
			// relative path with leading slash is interpreted relatively to
			// context root

			// but here a leading slash is interpreted relatively to
			// webapplication root
			if (url.startsWith("/")) {
				url = ectx.getRequestContextPath() + url;
			}
		}
		log.info("Redirecting to " + url);

		try {
			ectx.redirect(ectx.encodeResourceURL(url));
			fctx.responseComplete();

		} catch (final IOException e) {
			throw new YFacesException("Can't redirect to " + url, e);
		}
		//this.setFlash(isFlash);
		if (enableFlashback) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
					IS_FLASHBACK, Boolean.TRUE);
		}
	}

	private static final String IS_FLASHBACK = RequestCycle.class.getName() + "_isFlashback";

	@Override
	public boolean isFlashback() {
		return this.isFlashback;
	}

	@Override
	public boolean isPostback() {
		// true when a _JSF_ form was submitted 
		// (javax.faces.ViewState parameter is present at request map)
		return FacesContext.getCurrentInstance().getRenderKit().getResponseStateManager()
				.isPostback(FacesContext.getCurrentInstance());
	}

}
