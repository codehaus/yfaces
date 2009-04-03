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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.YManagedBean;
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YFrame;

/**
 * A context object whose scope and lifetime is bound to a Page. A page lays between
 * {@link YApplicationContext} and {@link YSessionContext} but is always lesser than a
 * {@link YConversationContext}. A page represents the full rendered view and lives as long as
 * operations are made at that view. An easy summary: this instance lives as long as the customer
 * deals with the same browser URL (incl. query parameters)
 * <p>
 * Access is provided to every {@link YFrame} (and all {@link YComponent} instances) which are used
 * to build up the current view. When this page is part of a conversation it may have one ore more
 * previous (not visible) pages.
 * 
 * @author Denny.Strietzbaum
 */
public class YPageContext {

	private static final Logger log = Logger.getLogger(YPageContext.class);

	// pattern for the url
	private static final Pattern urlPattern = Pattern.compile(".*/(.*?)\\..*");

	private String pageId = null;
	private String url = null;
	private String navigationId = null;

	private final Map<Object, Object> attributes = new HashMap<Object, Object>();

	private YPageContext previousPage = null;

	private YConversationContext navigationContext = null;

	// all Frames within this page
	private final Map<String, YFrame> frames = new HashMap<String, YFrame>();

	/**
	 * Constructor.
	 * 
	 * @param ctx
	 *            {@link YConversationContext}
	 * @param pageId
	 *            id of page
	 * @param url
	 *            url of page
	 */
	public YPageContext(YConversationContext ctx, String pageId, String url) {
		if (ctx == null) {
			throw new YFacesException("No NavigationContext specified", new NullPointerException());
		}

		this.pageId = pageId;
		this.navigationContext = ctx;
		this.url = url;

	}

	/**
	 * Returns the page-id.
	 * 
	 * @return page-id
	 */
	public String getId() {
		return this.pageId;
	}

	/**
	 * Sets the page-id.
	 * 
	 * @param pageId
	 *            page-id to set
	 */
	protected void setId(String pageId) {
		this.pageId = pageId;
	}

	/**
	 * Returns the URL where this page is located (absolute or relative to the webapp root). A
	 * relative result always starts with a leading slash '/'. In difference to the viewroot-id this
	 * result may include query parameters.
	 * 
	 * @return URL
	 */
	public String getURL() {
		return this.url;
	}

	/**
	 * Sets the URL for this page.
	 * 
	 * @param url
	 *            url to set.
	 */
	protected void setURL(String url) {
		this.url = url;
	}

	/**
	 * Returns the navigation id for this page. This is an ID which can be used within faces-config
	 * as navigation target. The navigation id is never null.
	 * 
	 * @return navigationId
	 */
	public String getNavigationId() {
		if (this.navigationId == null) {
			// get the path of the url without the host name and/or port,
			// for example, if the url looks like
			// "http://www.example.com:8080/path/index.html",
			// the result is "index"
			Matcher m = urlPattern.matcher(this.getURL());
			if (m.matches()) {
				this.navigationId = m.group(1);
			}
			if (this.navigationId == null) {
				log.error("no navigation id found for [" + getId() + "]");
			}
		}
		return this.navigationId;
	}

	/**
	 * Custom attributes for free usage.
	 * 
	 * @return {@link Map}
	 */
	public Map<Object, Object> getAttributes() {
		return this.attributes;

	}

	/**
	 * Returns the {@link YConversationContext}.
	 * 
	 * @return {@link YConversationContext}
	 */
	public YConversationContext getConversationContext() {
		return this.navigationContext;
	}

	/**
	 * Returns all frames which are managed by this page.
	 * 
	 * @return Frames
	 */
	public Collection<YFrame> getFrames() {
		return this.frames.values();
	}

	/**
	 * Returns (and initially creates when necessary) a frame instance according the passed frame
	 * class.Every frame which shall be managed by this page must be requested (created) by this
	 * method.
	 * 
	 * @param frameClass
	 *            requested frame
	 * 
	 * @see AbstractYFrame#getBeanId()
	 */
	public <T extends YFrame> T getOrCreateFrame(Class<T> frameClass) {
		T result = (T) this.frames.get(frameClass.getName());

		// when no instance is available, request is delegated to YManagedBean which itself creates a
		// instance of the class, asks for the beanId and register it at JSF scope
		if (result == null) {
			result = (T) YManagedBean.getBean((Class) frameClass);
			this.addFrame(result);
		}
		return result;
	}

	//currently only used for YFacesELResolver
	public void addFrame(YFrame frame) {
		this.frames.put(frame.getClass().getName(), frame);
	}

	/**
	 * Returns the previous YPage.<br/>
	 * This is the case when this YPage is element of a navigation route (Wizard or Conversation)
	 * otherwise it is null. <br/>
	 * A page becomes ancestor of another page after calling
	 * {@link YConversationContext#getOrCreateNextPage()} <br/>
	 * 
	 * @return previous YPage
	 */
	public YPageContext getPreviousPage() {
		return this.previousPage;
	}

	/**
	 * Sets a previous page for this page.
	 * 
	 * @param previousPage
	 *            {@link YPageContext}
	 */
	void setPreviousPage(YPageContext previousPage) {
		this.previousPage = previousPage;
	}

	/**
	 * Starts updating all {@link YFrame} instances of this page.
	 */
	protected void refresh() {
		for (final YFrame frame : getFrames()) {
			frame.refresh();
		}
	}

	@Override
	public String toString() {
		List<String> idList = new ArrayList<String>();
		for (YFrame frame : this.frames.values()) {
			idList.add(frame.getId());
		}
		String frames = Arrays.toString(idList.toArray());

		String result = getId() + ": " + frames;

		return result;
	}

}
