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

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.YManagedBean;
import de.hybris.yfaces.component.YFrame;
import de.hybris.yfaces.util.YFacesErrorHandler;

/**
 * A context object whose scope and lifetime is bound to {@link HttpServletRequest}.
 * 
 * @author Denny.Strietzbaum
 */
public abstract class YRequestContext {

	private static final Logger log = Logger.getLogger(YRequestContext.class);
	private static final String IS_FLASHBACK = YRequestContext.class.getName() + "_isFlashback";

	private YPageContext pageContext = null;

	public enum REQUEST_PHASE {
		/**
		 * Gets activated when RESTORE_VIEW is finished
		 */
		START_REQUEST,

		/**
		 * Gets never activated for a general GET request but under one of two circumstances:
		 * <ul>
		 * <li>with start of RENDER_RESPONSE and when request method is POST</li>
		 * <li>with ending if RESTORE_VIEW _and_ request method is GET _and_ flashback is enabled</li>
		 * </ul>
		 */
		FORWARD_REQUEST,

		/**
		 * Gets activated when RENDER_RESPONSE is finished
		 */
		END_REQUEST
	};

	private REQUEST_PHASE currentPhase = REQUEST_PHASE.END_REQUEST;

	private YSessionContext sessionContext = null;

	private YFacesErrorHandler errorHandler = null;

	private boolean isFlashback = false;

	private boolean isReqLifecycleInitialized = false;

	/**
	 * Constructor. Sets flashback property to true when previous {@link YRequestContext} instance
	 * was used to do a redirect with enabled flashback.
	 */
	public YRequestContext() {
		isFlashback = FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
				.remove(IS_FLASHBACK) != null;
	}

	// TODO: move to appCtx
	public YFacesErrorHandler getErrorHandler() {
		return errorHandler;
	}

	protected void setErrorHandler(YFacesErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Returns the current {@link YRequestContext} instance.
	 * 
	 * @return {@link YRequestContext}
	 */
	public static YRequestContext getCurrentContext() {
		return (YRequestContext) YApplicationContext.getApplicationContext().getBean(
				YRequestContext.class.getSimpleName());
	}

	/**
	 * @return {@link YPageContext}
	 */
	public YPageContext getPageContext() {
		return this.pageContext;
	}

	/**
	 * Sets the {@link YPageContext}
	 * 
	 * @param pageContext
	 *            {@link YPageContext} to set
	 */
	protected void setPageContext(YPageContext pageContext) {
		this.pageContext = pageContext;
	}

	/**
	 * @return {@link YSessionContext}
	 */
	public YSessionContext getSessionContext() {
		return sessionContext;
	}

	protected void setSessionContext(YSessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * Shortcut to {@link #redirect(String, boolean)} with disabled flashback.
	 * 
	 * @param url
	 *            target url
	 */
	public void redirect(String url) {
		this.redirect(url, false);
	}

	/**
	 * Shortcut to {@link #redirect(String, boolean)} whereas URL is the servletpath.
	 * 
	 * @param isFlash
	 *            whether flashback shall be enabled
	 * @see HttpServletRequest#getServletPath()
	 */
	public void redirect(boolean isFlash) {
		final String url = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestServletPath();
		redirect(url, isFlash);

	}

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
	public void redirect(YPageContext page, boolean isFlash) {
		this.redirect(page.getURL(), isFlash);
	}

	/**
	 * Redirects to the passed URL.
	 * 
	 * @param url
	 *            target url.
	 * @param isFlash
	 *            true when flash shall be enabled
	 */
	public void redirect(String url, boolean enableFlashback) {
		if (url == null) {
			throw new YFacesException("No URL specified", new NullPointerException());
		}

		final FacesContext fctx = FacesContext.getCurrentInstance();
		String contextPath = fctx.getExternalContext().getRequestContextPath();

		try {
			String target = null;
			if (!url.startsWith(contextPath + "/")) {
				target = fctx.getApplication().getViewHandler().getResourceURL(fctx, url);
			} else {
				target = fctx.getExternalContext().encodeResourceURL(url);
			}

			// using the ViewHandler's getResourceURL(...) automatically handles issues
			// with an existing/non-existing context path and relative/absolute URLs 
			// -> for relative URL's: only jsessionid is appended (when needed)
			// -> for absolute URL's: when not already present the requestcontextpath gets added first
			log.info("Redirecting to " + target + " (" + url + ")");
			fctx.getExternalContext().redirect(target);
			fctx.responseComplete();

		} catch (IOException e) {
			throw new YFacesException("Can't redirect to " + url, e);
		}
		//this.setFlash(isFlash);
		if (enableFlashback) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
					IS_FLASHBACK, Boolean.TRUE);
		}
	}

	/**
	 * Similar to a postback but can only be true when current request is of type GET and flashback
	 * was enabled in previous request.
	 */
	public boolean isFlashback() {
		return this.isFlashback;
	}

	/**
	 * Shortcut for {@link ResponseStateManager#isPostback(FacesContext)}
	 * 
	 * @return true when current request is a jsf postback
	 */
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
	void startPageRequest(String viewId) {
		this.currentPhase = REQUEST_PHASE.START_REQUEST;

		// detect method
		boolean isPostBack = YRequestContext.getCurrentContext().isPostback();
		boolean isFlash = YRequestContext.getCurrentContext().isFlashback();

		YConversationContext convCtx = getPageContext().getConversationContext();

		// restore context information (mbeans) when
		// a)POST (postback) or
		// b)GET with enabled flash
		if (isPostBack || isFlash) {
			// iterate over all context pages...
			Collection<YPageContext> pages = convCtx.getAllPages();
			for (YPageContext page : pages) {
				// ...and notify page for a new request (re-inject all
				// frames/mbeans)
				for (YFrame frame : page.getFrames()) {
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
			String url = getViewURL(viewId, true);
			YPageContext newPage = new YPageContext(convCtx, viewId, url);
			convCtx.start(newPage);
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
	void switchPage(String newViewId) {
		this.currentPhase = REQUEST_PHASE.FORWARD_REQUEST;

		YConversationContext convCtx = getPageContext().getConversationContext();

		// lookup whether newViewId matches on of context managed previous pages
		// (browser backbutton, regular "back" navigation, etc. )
		YPageContext previousPage = convCtx.getPage(newViewId);

		// when no previous page is available (e.g. navigation to a new view)
		// ...
		if (previousPage == null) {

			final String viewUrl = getViewURL(newViewId, false);

			// ...and the context is prepared to have a next page...
			YPageContext forwardPage = convCtx.getNextPage();
			if (forwardPage != null) {
				forwardPage.setURL(viewUrl);
				forwardPage.setId(newViewId);
				convCtx.forward(forwardPage);
			}
			// ...otherwise reset Conversation
			else {
				// ...initialize new context and new YPage
				convCtx.start(new YPageContext(convCtx, newViewId, viewUrl));
			}
		}
		// when a previous page is available (or current page)...
		else {
			// ...navigate to this page
			convCtx.backward(previousPage);

			// ...and start update mechanism
			this.sessionContext.refresh();
		}
	}

	/**
	 * Finishes the current Page request. The viewid has changed when an internal forward was
	 * accomplished <br/>
	 * This method gets called after RENDER_RESPONSE.
	 * 
	 * @param viewId
	 */
	void finishPageRequest(String viewId) {
		this.currentPhase = REQUEST_PHASE.END_REQUEST;

		if (log.isDebugEnabled()) {
			int i = 0;
			YPageContext page = getPageContext();
			YConversationContext convCtx = page.getConversationContext();
			String id = convCtx.getId();
			do {
				log.debug(id + " Page[" + i++ + "]: " + page.toString());
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
	private String getViewURL(String viewId, boolean addCurrentQueryParams) {
		FacesContext fc = FacesContext.getCurrentInstance();

		// request view url but without context path
		String result = fc.getApplication().getViewHandler().getActionURL(fc, viewId);
		result = result.substring(fc.getExternalContext().getRequestContextPath().length());

		// optional append a query parameter string
		// HttpServletRequest#getQueryString isn't used here as it is not
		// available in an portlet
		// environment and, more important, may return an incorrect string when
		// urlrewriting is used.
		if (addCurrentQueryParams) {
			Map<String, String[]> values = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterValuesMap();
			if (!values.isEmpty()) {
				String params = "?";
				for (Map.Entry<String, String[]> entry : values.entrySet()) {
					for (String value : entry.getValue()) {
						params = params + entry.getKey() + "=" + value + ";";
					}
				}
				result = result + params.substring(0, params.length() - 1);
			}
		}

		return result;
	}

	public REQUEST_PHASE getRequestPhase() {
		return this.currentPhase;
	}

	/**
	 * Listener which can be used for some custom initialization. Method gets invoked after all
	 * YFaces specific dependencies are created and properly injected. It's guaranteed that
	 * {@link YRequestContext#getCurrentContext()} returns a valid instance whose properties like
	 * {@link YSessionContext}, {@link YPageContext}, {@link YConversationContext} etc. are fully
	 * available.
	 */
	protected void init() {
		if (isReqLifecycleInitialized) {
			log.error("YFaces request lifecycle already initialized");
		}
		this.isReqLifecycleInitialized = true;
	}

}
