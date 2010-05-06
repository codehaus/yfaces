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
package org.codehaus.yfaces.context;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFaces;
import org.codehaus.yfaces.YFacesException;

/**
 * A context object whose scope and lifetime is bound to a Conversation. A conversation lays between
 * {@link YApplicationContext} and {@link YSessionContext} and is always greater than a
 * {@link YPageContext}.
 * <p>
 * A conversation is started when {@link #getOrCreateNextPage()} is called. In that case a
 * {@link YPageContext} is returned (or initially created) which can be modified and initialized
 * with a specific state.This preconfigured context gets activated with next request
 * <p>
 * The conversation "rollbacks" to a previous state whenever a user interacts in a way that a page
 * is displayed which is element of the pages stack internally hold by this conversation.The
 * conversation goes on when the user navigates to the page which was configured with
 * {@link #getOrCreateNextPage()}. The conversation stops (throws away all state) when the user
 * navigates to a page which is not element of this conversation (means: which is neither a previous
 * page nor current displayed page nor the prepared next page)
 * 
 * @author Denny Strietzbaum
 */
public class YConversationContextImpl implements YConversationContext {

	private static final Logger log = Logger.getLogger(YConversationContext.class);

	// the current pagecontext
	private YPageContextImpl currentPage = null;

	private int resetCounter = 0;

	private String id = null;

	// context attributes
	private final Map<String, Object> attributes = new HashMap<String, Object>();

	// holds a queue of navigable pages
	private LinkedHashMap<String, YPageContext> contextPages = new LinkedHashMap<String, YPageContext>();

	// next page
	// gets added to queue of context pages with the next request
	private YPageContextImpl nextContextPage = null;

	/**
	 * Constructor.
	 */
	public YConversationContextImpl() {
		this.id = this.calculateNewId();

		final YPageContextImpl startPage = new YPageContextImpl(this, null, null);
		this.currentPage = startPage;
		this.contextPages = new LinkedHashMap<String, YPageContext>();
		this.contextPages.put(startPage.getId(), startPage);
		this.nextContextPage = null;
	}

	/**
	 * Returns the ID of this context.
	 * 
	 * @return id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * A map of attributes backed by the lifetime of this scope.
	 * 
	 * @return {@link Map}
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/**
	 * Returns the current displayed Page.<br/>
	 * May or may not have one ore more previous pages<br/>
	 * (depends on whether {@link #getOrCreateNextPage()} was called before current request)<br/>
	 * <br/>
	 * 
	 * @return the current {@link YPageContext}
	 */
	public YPageContextImpl getLastPage() {
		return this.currentPage;
	}

	/**
	 * Returns the {@link YPageContext} which becomes the current one with next request.<br/>
	 * Calling this method first lazily creates a new instance.<br/>
	 * Every other call returns the same instance as long as the current request is processed.<br/>
	 * When a new requests starts this instance gets added to the stack of available context pages.<br/>
	 * <br/>
	 * When this method isn't called the context is reseted with the next request.
	 * 
	 * @return {@link YPageContext}
	 */
	public YPageContext getOrCreateNextPage() {
		if (this.nextContextPage == null) {
			this.nextContextPage = new YPageContextImpl(this, null, null);
		}
		return this.nextContextPage;
	}

	/**
	 * Returns the next {@link YPageContext} if any.
	 * 
	 * @return {@link YPageContext}
	 */
	protected YPageContext getNextPage() {
		return this.nextContextPage;
	}

	/**
	 * Returns all available {@link YPageContext} instances for this conversation. Collection elements
	 * are ordered by creation time.
	 * 
	 * @return all {@link YPageContext} instances
	 */
	public Collection<YPageContext> getAllPages() {
		return this.contextPages.values();
	}

	/**
	 * Returns the current {@link YSessionContext}
	 * 
	 * @return {@link YSessionContext}
	 */
	public YSessionContext getSessionContext() {
		return YFaces.getRequestContext().getSessionContext();
	}

	/**
	 * Starts updating the current {@link YPageContext}. If a conversation is running and more than
	 * one pages are available only the active {@link YPageContext} gets refreshed. Incoming request
	 * must be of type POST or GET as flashback. Refresh isn't called when conversation is left (When
	 * requested page is not element element of this conversation)
	 * <p>
	 * This method gets not invoked for general GET requests and it gets not invoked for POST requests
	 * on Pages that are not element of this conversation.
	 */
	protected void refresh() {
		this.currentPage.refresh();
	}

	/**
	 * Resets the context and sets the passed Page as new, initial one.
	 * 
	 * @param page
	 *          {@link YPageContext} as start page
	 */
	void start(final YPageContextImpl page) {
		this.attributes.clear();
		this.id = this.calculateNewId();

		this.currentPage = page;
		((YRequestContextImpl) YFaces.getRequestContext()).setPageContext(page);

		this.contextPages = new LinkedHashMap<String, YPageContext>();
		this.contextPages.put(page.getId(), page);
		this.nextContextPage = null;

		log.debug("Reseting to initial new Page (" + page.getId() + ")");
	}

	/**
	 * Proceeds conversation with adding a new {@link YPageContext} to the queue of current pages.A
	 * possible {@link YPageContext} created previously with {@link #getOrCreateNextPage()} gets
	 * reseted.
	 * 
	 * @param page
	 *          {@link YPageContext}
	 */
	void forward(final YPageContextImpl page) {
		// ...take that "next page" and append it to the queue of current pages
		this.addPage(page);

		this.currentPage = page;
		((YRequestContextImpl) YFaces.getRequestContext()).setPageContext(page);

		this.nextContextPage = null;
	}

	/**
	 * "Rollback" the conversation. Passed {@link YPageContext} must be element of this conversations
	 * page stack. Passed page is set as current one and the conversations page stack gets updated
	 * (following pages are removed)
	 * 
	 * @param page
	 *          page to navigate to
	 */
	void backward(final YPageContextImpl page) {

		// availability check
		if (!this.contextPages.containsKey(page.getId())) {
			throw new YFacesException("Can't navigate to page " + page.getId() + " (not found)");
		}

		if (log.isDebugEnabled()) {
			log.debug("Navigating to already existing page (" + page.getId() + ")");
		}

		// update some members and request context
		this.nextContextPage = null;
		this.currentPage = page;
		((YRequestContextImpl) YFaces.getRequestContext()).setPageContext(page);

		// update pages stack
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
	 *          {@link YPageContext} to add.
	 */
	protected void addPage(final YPageContextImpl page) {
		final YPageContext previousPage = page.getPreviousPage();

		final YPageContext currentPage = YFaces.getRequestContext().getPageContext();

		// in case added page has already a previous page, then it must be same
		// as current page
		if (previousPage != null && !previousPage.equals(currentPage)) {
			final String msg = "Can't add page (" + page.getId() + ") as previous page ("
					+ previousPage.getId() + ") is not compatible with current page (" + currentPage.getId()
					+ ")";
			throw new YFacesException(msg);
		}

		// set current page as previous page of added page
		page.setPreviousPage(currentPage);

		// add page to stack
		this.contextPages.put(page.getId(), page);
	}

	/**
	 * Returns a {@link YPageContext} by its pageId. The page must be available within the queue of
	 * managed pages otherwise null is returned.
	 * 
	 * @param pageId
	 *          pageId
	 * @return {@link YPageContext}
	 */
	protected YPageContext getPage(final String pageId) {
		return this.contextPages.get(pageId);
	}

	private String calculateNewId() {
		return "#" + String.valueOf(this.resetCounter++);
	}

}
