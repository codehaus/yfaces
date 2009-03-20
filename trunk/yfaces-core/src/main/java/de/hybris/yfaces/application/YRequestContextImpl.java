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
import java.util.Collection;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.YManagedBean;
import de.hybris.yfaces.component.YFrame;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YRequestContextImpl extends YRequestContext {

	private static final Logger log = Logger.getLogger(YRequestContextImpl.class);
	private static final String IS_FLASHBACK = YRequestContext.class.getName() + "_isFlashback";

	public enum REQUEST_PHASE {
		START_REQUEST, FORWARD_REQUEST, END_REQUEST
	};

	private REQUEST_PHASE currentPhase = REQUEST_PHASE.END_REQUEST;

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

	@Override
	public YPageContext getPageContext() {
		return getConversationContext().getCurrentPage();
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
	public YConversationContextImpl getConversationContext() {
		final Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		YConversationContext result = (YConversationContext) map.get(YConversationContext.class
				.getName());
		if (result == null) {
			result = new YConversationContextImpl(null);
			map.put(YConversationContext.class.getName(), result);
		}
		return (YConversationContextImpl) result;
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

	/**
	 * Starts a new YPage request.<br/>
	 * 
	 * @param viewId
	 */
	public void startPageRequest(final String viewId) {
		this.currentPhase = REQUEST_PHASE.START_REQUEST;

		// detect method
		boolean isPostBack = YRequestContext.getCurrentContext().isPostback();
		boolean isFlash = YRequestContext.getCurrentContext().isFlashback();

		// restore context information (mbeans) when
		// a)POST (postback) or
		// b)GET with enabled flash
		if (isPostBack || isFlash) {
			// iterate over all context pages...
			Collection<YPageContext> pages = getConversationContext().getAllPages();
			for (final YPageContext page : pages) {
				// ...and notify page for a new request (re-inject all
				// frames/mbeans)
				for (YFrame frame : page.getFrames().values()) {
					((YManagedBean) frame).refreshBeanScope();
				}
			}

			// force a one-time survive after a GET (redirect)
			if (isFlash) {
				if (isPostBack) {
					throw new YFacesException("Illegal Navigationstate");
				}

				// must explicitly invoked for GET
				this.switchPage(viewId);
			}
		}
		// otherwise ...
		else {
			// ...reset context with new initialized page
			final String url = getViewURL(viewId, true);
			YPageContext newPage = new YPageContextImpl(getConversationContext(), viewId, url);
			getConversationContext().start(newPage);
		}
	}

	/**
	 * Gets invoked before the new Page is processed.<br/>
	 * Invocation happens:<br/>
	 * a) for a POST: after INVOKE_APPLICATION and before RENDER_RESPONSE<br/>
	 * b) for a GET (flash=true): after RESTORE_VIEW and before RENDER_RESPONSE<br/>
	 * 
	 * @param newViewId
	 */
	public void switchPage(final String newViewId) {
		this.currentPhase = REQUEST_PHASE.FORWARD_REQUEST;

		YConversationContextImpl navCtx = getConversationContext();

		// lookup whether newViewId matches on of context managed previous pages
		// (browser backbutton, regular "back" navigation, etc. )
		final YPageContext previousPage = navCtx.getPage(newViewId);

		// when no previous page is available (e.g. navigation to a new view)
		// ...
		if (previousPage == null) {

			final String viewUrl = getViewURL(newViewId, false);

			// ...and the context is prepared to have a next page...
			YPageContext forwardPage = navCtx.getNextPage();
			if (forwardPage != null) {
				((YPageContextImpl) forwardPage).setURL(viewUrl);
				((YPageContextImpl) forwardPage).setId(newViewId);
				navCtx.forward(forwardPage);
			}
			// ...otherwise reset NavigationContext
			else {
				// ...initialize new context and new YPage
				navCtx.start(new YPageContextImpl(navCtx, newViewId, viewUrl));
			}
		}
		// when a previous page is available...
		else {
			// ...navigate to this page
			navCtx.backward(previousPage);

			// ...and start update mechanism
			navCtx.update();
		}
	}

	/**
	 * Finishes the current Page request. The viewid has changed when an internal forward was
	 * accomplished <br/>
	 * This method gets called after RENDER_RESPONSE.
	 * 
	 * @param viewId
	 */
	public void finishPageRequest(final String viewId) {
		this.currentPhase = REQUEST_PHASE.END_REQUEST;

		if (log.isDebugEnabled()) {
			int i = 0;
			YPageContext page = getConversationContext().getCurrentPage();
			do {
				log.debug(getConversationContext().hashCode() + " Page(" + i++ + "):"
						+ page.toString());
			} while ((page = page.getPreviousPage()) != null);
		}
	}

	/**
	 * Returns a URI starting with a slash and relative to the webapps context root for the
	 * requested view.
	 * 
	 * @param viewId
	 *            view to generate the URL for
	 * @return String
	 */
	private String getViewURL(final String viewId, final boolean addCurrentQueryParams) {
		final FacesContext fc = FacesContext.getCurrentInstance();

		// request view url but without context path
		String result2 = fc.getApplication().getViewHandler().getActionURL(fc, viewId);
		result2 = result2.substring(fc.getExternalContext().getRequestContextPath().length());

		// optional append a query parameter string
		// HttpServletRequest#getQueryString isn't used here as it is not
		// available in an portlet
		// environment and, more important, may return an incorrect string when
		// urlrewriting is used.
		if (addCurrentQueryParams) {
			final Map<String, String[]> values = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterValuesMap();
			if (!values.isEmpty()) {
				String params = "?";
				for (final Map.Entry<String, String[]> entry : values.entrySet()) {
					for (final String value : entry.getValue()) {
						params = params + entry.getKey() + "=" + value + ";";
					}
				}
				result2 = result2 + params.substring(0, params.length() - 1);
			}
		}

		return result2;
	}

	public REQUEST_PHASE getRequestPhase() {
		return this.currentPhase;
	}

}
