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
package de.hybris.yfaces.component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.ResponseStateManager;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.application.YFacesContext;
import de.hybris.yfaces.el.YFacesResolverWrapper;
import de.hybris.yfaces.session.UserSessionPropertyChangeLog;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class NavigationContextImpl extends NavigationContext {
	private static final Logger log = Logger.getLogger(NavigationContextImpl.class);

	public enum REQUEST_PHASE {
		START_REQUEST, FORWARD_REQUEST, END_REQUEST
	};

	// the current pagecontext
	private YPage currentPage = null;
	private REQUEST_PHASE currentPhase = REQUEST_PHASE.END_REQUEST;

	private int resetCounter = 0;

	private String id = null;

	// context attributes
	private final Map<String, Object> attributes = new HashMap<String, Object>();
	private boolean isFlash = false;

	// holds a queue of navigable pages
	private Map<String, YPage> contextPages = new LinkedHashMap<String, YPage>();

	// next page
	// gets added to queue of context pages with the next request
	private YPage nextContextPage = null;

	public NavigationContextImpl(final String idPrefix) {
		this.id = this.calculateNewId();
	}

	/**
	 * @return Attributes bound to this context.
	 */
	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * @return the current {@link YPage}
	 */
	@Override
	public YPage getCurrentPage() {
		return this.currentPage;
	}

	@Override
	public YPage getNextPage() {
		if (this.nextContextPage == null) {
			this.nextContextPage = new YPageImpl(this, null, null);
		}
		return this.nextContextPage;
	}

	/**
	 * Enabling 'flash' allows a one-time GET request without destroying the
	 * current active {@link NavigationContext}.<br/>
	 * Especially useful when doing a redirect, but all other GETs are fine too.<br/>
	 * 
	 * @param flash
	 */
	private void setFlash(final boolean flash) {
		this.isFlash = flash;
	}

	/**
	 * Starts a new YPage request.<br/>
	 * 
	 * @param viewId
	 */
	public void startPageRequest(final String viewId) {
		this.currentPhase = REQUEST_PHASE.START_REQUEST;

		// detect method
		final boolean isPostBack = this.isPostback();

		// restore context information (mbeans) when
		// a)POST (postback) or
		// b)GET with enabled flash
		if (isPostBack || isFlash) {
			// iterate over all context pages...
			for (final YPage page : this.contextPages.values()) {
				// ...and notify page for a new request (re-inject all
				// frames/mbeans)
				((YPageImpl) page).startPageRequest();
			}

			// force a one-time survive after a GET (redirect)
			if (isFlash) {
				if (isPostBack) {
					throw new YFacesException("Illegal Navigationstate");
				}

				this.isFlash = false;

				// must explicitly invoked for GET
				this.switchPage(viewId);
			}
		}
		// otherwise ...
		else {
			// ...reset context with new initialized page
			final String url = getViewURL(viewId, true);
			this.resetToPage(new YPageImpl(this, viewId, url));
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

		// lookup whether newViewId matches on of context managed previous pages
		// (browser backbutton, regular "back" navigation, etc. )
		final YPage previousPage = this.getPage(newViewId);

		// when no previous page is available (e.g. navigation to a new view)
		// ...
		if (previousPage == null) {
			// ...and the context is prepared to have a next page...
			if (this.nextContextPage != null) {
				// ...take that "next page" and append it to the queue of
				// current pages
				final String viewUrl = getViewURL(newViewId, false);
				((YPageImpl) this.nextContextPage).setId(newViewId);
				((YPageImpl) this.nextContextPage).setURL(viewUrl);
				this.addPage(this.nextContextPage);
				this.currentPage = this.nextContextPage;
				this.nextContextPage = null;

			}
			// ...otherwise reset NavigationContext
			else {
				// ...initialize new context and new YPage
				final String viewUrl = getViewURL(newViewId, false);
				this.resetToPage(new YPageImpl(this, newViewId, viewUrl));
			}
		}
		// when a previous page is available...
		else {
			// ...navigate to this page
			this.navigateToPage(previousPage);

			// ...and start update mechanism
			this.update();
		}
	}

	/**
	 * Finishes the current Page request. The viewid has changed when an
	 * internal forward was accomplished <br/>
	 * This method gets called after RENDER_RESPONSE.
	 * 
	 * @param viewId
	 */
	public void finishPageRequest(final String viewId) {
		this.currentPhase = REQUEST_PHASE.END_REQUEST;

		if (log.isDebugEnabled()) {
			int i = 0;
			YPage page = this.getCurrentPage();
			do {
				log.debug(this.id + " Page(" + i++ + "):" + page.toString());
			} while ((page = page.getPreviousPage()) != null);
		}
	}

	/**
	 * Starts updating this context.<br/>
	 * The default update process is:<br/>
	 * For each {@link YPage}, call
	 * {@link YPage#update(UserSessionPropertyChangeLog)}<br/>
	 * For each {@link YFrame} of current update {@link YPage} call
	 * {@link YFrame#update(UserSessionPropertyChangeLog)}<br/>
	 * For each {@link YComponent} of current update {@link YFrame} call
	 * {@link YComponent#update(UserSessionPropertyChangeLog)}<br/>
	 * <br/>
	 * An update is invoked for:<br/>
	 * - a Navigationrequest which requests the current Page<br/>
	 * - a Navigationrequest which requests a previous Page<br/>
	 * <br/>
	 * An update is not invoked for:<br/>
	 * - a Navigationrequest which requests a new Page<br/>
	 * - any other requests (non valid Navigationrequests)<br/>
	 * <br/>
	 */
	@Override
	public void update() {
		final UserSessionPropertyChangeLog log = YFacesContext.getCurrentContext().getUserSession()
				.getPropertyChangeLog();
		for (final YPage page : this.contextPages.values()) {
			page.update(log);
		}
		log.reset();
	}

	/**
	 * Redirects to the current URL.<br/>
	 * This creates a non-faces request which destroys the current
	 * {@link UIViewRoot}
	 */
	@Override
	public void redirect(final boolean isFlash) {
		final String url = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestServletPath();
		redirect(url, isFlash);
	}

	@Override
	public void redirect(final YPage page, final boolean isFlash) {
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
	public void redirect(String url, final boolean isFlash) {
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
		this.setFlash(isFlash);
	}

	/**
	 * Internal. Notifies the context that a Frame is right now requested.
	 * 
	 * @param frame
	 * 
	 * @see YFacesResolverWrapper
	 */
	public void handleFrameRequest(final YFrame frame) {
		// frames are getting added when:
		// a) method is get
		// b) method is post and START_REQUEST phase has finished
		// e.g. nothing is done when the Frame was requested from within an
		// action/actionlistener
		final boolean doNothing = this.isPostback()
				&& (this.currentPhase.equals(REQUEST_PHASE.START_REQUEST));

		if (!doNothing) {
			this.getCurrentPage().addFrame(frame);
		}
	}

	public REQUEST_PHASE getRequestPhase() {
		return this.currentPhase;
	}

	/**
	 * Shortcut for {@link ResponseStateManager#isPostback(FacesContext)}
	 * 
	 * @return true when current request is a jsf postback
	 */
	public boolean isPostback() {
		// JSF postback: true only when a JSF form was submitted or in other
		// words
		// when a javax.faces.ViewState parameter is present at the request map
		return FacesContext.getCurrentInstance().getRenderKit().getResponseStateManager()
				.isPostback(FacesContext.getCurrentInstance());
	}

	/**
	 * Returns a URI starting with a slash and relative to the webapps context
	 * root for the requested view.
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

	/**
	 * Resets the context and sets the passed Page as new, initial one.
	 * 
	 * @param page
	 */
	private void resetToPage(final YPage page) {
		this.attributes.clear();
		this.id = this.calculateNewId();
		this.currentPage = page;
		this.contextPages = new LinkedHashMap<String, YPage>();
		this.contextPages.put(page.getId(), page);
		this.nextContextPage = null;

		log.debug("Reseting to initial new Page (" + page.getId() + ")");
	}

	/**
	 * Navigates to the passed page. Sets the passed page as current one. Throws
	 * away all following pages (if any) and preserves all previous ones.
	 * 
	 * @param page
	 *            page to navigate to
	 */
	private void navigateToPage(final YPage page) {
		if (!this.contextPages.containsKey(page.getId())) {
			throw new YFacesException("Can't navigate to page " + page.getId() + " (not found)");
		}

		if (log.isDebugEnabled()) {
			log.debug("Navigating to already existing page (" + page.getId() + ")");
		}

		this.currentPage = page;
		this.nextContextPage = null;

		// find requested page within queued context pages
		final Map<String, YPage> updatedNavigationPages = new LinkedHashMap<String, YPage>();
		for (final Map.Entry<String, YPage> entry : this.contextPages.entrySet()) {
			updatedNavigationPages.put(entry.getKey(), entry.getValue());
			if (entry.getKey().equals(this.currentPage.getId())) {
				break;
			}
		}
		this.contextPages = updatedNavigationPages;
	}

	/**
	 * Adds the passed page to top of the queue of already managed pages.
	 * 
	 * @param page
	 *            {@link YPage} to add.
	 */
	public void addPage(final YPage page) {
		final YPage previousPage = page.getPreviousPage();
		final YPage currentPage = this.getCurrentPage();

		// in case added page has already a previous page, then it must be same
		// as current page
		if (previousPage != null && !previousPage.equals(currentPage)) {
			final String msg = "Can't add page (" + page.getId() + ") as previous page ("
					+ previousPage.getId() + ") is not compatible with current page ("
					+ currentPage.getId() + ")";
			throw new YFacesException(msg);
		}

		// set current page as previous page of added page
		((YPageImpl) page).setPreviousPage(currentPage);

		// add page to queue
		this.contextPages.put(page.getId(), page);
	}

	/**
	 * Returns a {@link YPage} by its pageId. The page must be available within
	 * the queue of managed pages otherwise null is returned.
	 * 
	 * @param pageId
	 *            pageId
	 * @return {@link YPage}
	 */
	public YPage getPage(final String pageId) {
		return this.contextPages.get(pageId);
	}

	private String calculateNewId() {
		return "#" + String.valueOf(this.resetCounter++);
	}

}
