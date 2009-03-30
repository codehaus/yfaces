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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YFrame;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YConversationContextImpl implements YConversationContext {
	private static final Logger log = Logger.getLogger(YConversationContextImpl.class);

	// the current pagecontext
	private YPageContext currentPage = null;

	private int resetCounter = 0;

	private String id = null;

	// context attributes
	private final Map<String, Object> attributes = new HashMap<String, Object>();

	// holds a queue of navigable pages
	private LinkedHashMap<String, YPageContext> contextPages = new LinkedHashMap<String, YPageContext>();

	// next page
	// gets added to queue of context pages with the next request
	private YPageContext nextContextPage = null;

	public YConversationContextImpl(final String idPrefix) {
		this.id = this.calculateNewId();

		//new
		YPageContext startPage = new YPageContextImpl(this, null, null);
		this.currentPage = startPage;
		this.contextPages = new LinkedHashMap<String, YPageContext>();
		this.contextPages.put(startPage.getId(), startPage);
		this.nextContextPage = null;
	}

	public String getId() {
		return this.id;
	}

	/**
	 * @return Attributes bound to this context.
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	//	/**
	//	 * @return the current {@link YPageContext}
	//	 */
	//	public YPageContext getCurrentPage() {
	//		//return this.currentPage;
	//
	//		return YRequestContext.getCurrentContext().getPageContext();
	//	}

	public YPageContext getOrCreateNextPage() {
		if (this.nextContextPage == null) {
			this.nextContextPage = new YPageContextImpl(this, null, null);
		}
		return this.nextContextPage;
	}

	public YPageContext getNextPage() {
		return this.nextContextPage;
	}

	public Collection<YPageContext> getAllPages() {
		return this.contextPages.values();
	}

	/**
	 * Starts updating this context.<br/>
	 * The default update process is:<br/>
	 * For each {@link YPageContext}, call {@link YPageContext#update(UserSessionPropertyChangeLog)}<br/>
	 * For each {@link YFrame} of current update {@link YPageContext} call
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
	public void update() {
		for (final YPageContext page : this.contextPages.values()) {
			page.update();
		}
		YRequestContext.getCurrentContext().getSessionContext().update();
	}

	/**
	 * Resets the context and sets the passed Page as new, initial one.
	 * 
	 * @param page
	 */
	public void start(final YPageContext page) {
		this.attributes.clear();
		this.id = this.calculateNewId();

		this.currentPage = page;
		((YRequestContextImpl) YRequestContext.getCurrentContext()).setPageContext(page);

		this.contextPages = new LinkedHashMap<String, YPageContext>();
		this.contextPages.put(page.getId(), page);
		this.nextContextPage = null;

		log.debug("Reseting to initial new Page (" + page.getId() + ")");
	}

	public void forward(YPageContext page) {
		// ...take that "next page" and append it to the queue of current pages
		this.addPage(page);

		this.currentPage = page;
		((YRequestContextImpl) YRequestContext.getCurrentContext()).setPageContext(page);

		this.nextContextPage = null;
	}

	/**
	 * Navigates to the passed page. Sets the passed page as current one. Throws away all following
	 * pages (if any) and preserves all previous ones.
	 * 
	 * @param page
	 *            page to navigate to
	 */
	public void backward(final YPageContext page) {
		if (!this.contextPages.containsKey(page.getId())) {
			throw new YFacesException("Can't navigate to page " + page.getId() + " (not found)");
		}

		if (log.isDebugEnabled()) {
			log.debug("Navigating to already existing page (" + page.getId() + ")");
		}

		this.currentPage = page;
		((YRequestContextImpl) YRequestContext.getCurrentContext()).setPageContext(page);

		this.nextContextPage = null;

		// find requested page within queued context pages
		final LinkedHashMap<String, YPageContext> updatedNavigationPages = new LinkedHashMap<String, YPageContext>();
		for (final Map.Entry<String, YPageContext> entry : this.contextPages.entrySet()) {
			updatedNavigationPages.put(entry.getKey(), entry.getValue());
			if (entry.getKey().equals(page.getId())) {
				break;
			}
		}
		this.contextPages = updatedNavigationPages;
	}

	/**
	 * Adds the passed page to top of the queue of already managed pages.
	 * 
	 * @param page
	 *            {@link YPageContext} to add.
	 */
	public void addPage(final YPageContext page) {
		final YPageContext previousPage = page.getPreviousPage();

		//final YPageContext currentPage = this.getCurrentPage();
		YPageContext currentPage = YRequestContext.getCurrentContext().getPageContext();

		// in case added page has already a previous page, then it must be same
		// as current page
		if (previousPage != null && !previousPage.equals(currentPage)) {
			final String msg = "Can't add page (" + page.getId() + ") as previous page ("
					+ previousPage.getId() + ") is not compatible with current page ("
					+ currentPage.getId() + ")";
			throw new YFacesException(msg);
		}

		// set current page as previous page of added page
		((YPageContextImpl) page).setPreviousPage(currentPage);

		// add page to queue
		this.contextPages.put(page.getId(), page);
	}

	/**
	 * Returns a {@link YPageContext} by its pageId. The page must be available within the queue of
	 * managed pages otherwise null is returned.
	 * 
	 * @param pageId
	 *            pageId
	 * @return {@link YPageContext}
	 */
	public YPageContext getPage(final String pageId) {
		return this.contextPages.get(pageId);
	}

	public YPageContext getLastPage() {
		return this.currentPage;
	}

	private String calculateNewId() {
		return "#" + String.valueOf(this.resetCounter++);
	}

}
