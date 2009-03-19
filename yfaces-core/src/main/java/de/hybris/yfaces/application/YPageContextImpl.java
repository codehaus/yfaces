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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.YManagedBean;
import de.hybris.yfaces.component.NavigationContext;
import de.hybris.yfaces.component.YFrame;

/**
 * Implementation of {@link YPageContext}.
 * 
 * @author Denny.Strietzbaum
 */
public class YPageContextImpl implements YPageContext {
	private static final Logger log = Logger.getLogger(YPageContextImpl.class);

	private String pageId = null;
	private String url = null;
	private String navigationId = null;

	private final Map<Object, Object> attributes = new HashMap<Object, Object>();

	private YPageContext previousPage = null;

	private NavigationContext navigationContext = null;

	// all Frames within this page
	private final Map<String, YFrame> frames = new HashMap<String, YFrame>();

	public YPageContextImpl(final NavigationContext ctx, final String pageId, final String url) {
		this(ctx, pageId, url, null);
	}

	private YPageContextImpl(final NavigationContext ctx, final String pageId, final String url,
			final YPageContext previous) {
		if (ctx == null) {
			throw new YFacesException("No NavigationContext specified", new NullPointerException());
		}

		this.pageId = pageId;
		this.navigationContext = ctx;
		this.url = url;
		this.previousPage = previous;

		log.debug("Created new YPage (" + pageId + ") as root view");
	}

	// pattern for the url
	private static final Pattern urlPattern = Pattern.compile(".*/(.*?)\\..*");

	public String getNavigationId() {
		if (this.navigationId == null) {
			// get the path of the url without the host name and/or port,
			// for example, if the url looks like
			// "http://www.example.com:8080/path/index.html",
			// the result is "index"
			final Matcher m = urlPattern.matcher(this.getURL());
			if (m.matches()) {
				this.navigationId = m.group(1);
			}
			if (this.navigationId == null) {
				log.error("no navigation id found for [" + getId() + "]");
			}
		}
		return this.navigationId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YPage#addFrame(de.hybris.yfaces.YFrame)
	 */
	public void addFrame(final YFrame frame) {
		this.frames.put(frame.getClass().getName(), frame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YPage#getFrame(java.lang.Class)
	 */
	public <T extends YFrame> T getFrame(final Class<T> frameClass) {
		T result = (T) this.frames.get(frameClass.getName());

		if (result == null) {
			result = (T) YManagedBean.getBean((Class) frameClass);
			this.addFrame(result);
		}

		return result;
	}

	//	/**
	//	 * Initiates a new Page request. This put back all request scoped ManagedBeans into JSF context.
	//	 */
	//	protected void startPageRequest() {
	//		for (final YFrame frame : getFrames().values()) {
	//			((YManagedBean) frame).refreshBeanScope();
	//		}
	//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getPageId()
	 */
	public String getId() {
		return this.pageId;
	}

	public void setId(final String pageId) {
		this.pageId = pageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getURL()
	 */
	public String getURL() {
		return this.url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getAttributes()
	 */
	public Map<Object, Object> getAttributes() {
		return this.attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getNavigationContext()
	 */
	public NavigationContext getNavigationContext() {
		return this.navigationContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getFrames()
	 */
	public Map<String, YFrame> getFrames() {
		return this.frames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#getPreviousPage()
	 */
	public YPageContext getPreviousPage() {
		return this.previousPage;
	}

	public void setPreviousPage(final YPageContext previousPage) {
		this.previousPage = previousPage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.YPage#updateFrames()
	 */
	public void update() {
		for (final YFrame frame : getFrames().values()) {
			log.debug("Updating Frame: " + frame.getId());
			frame.update();
		}
	}

	/**
	 * Sets the URL for this page.
	 * 
	 * @param url
	 *            url to set.
	 */
	public void setURL(final String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		final List<String> idList = new ArrayList<String>();
		for (final YFrame frame : this.frames.values()) {
			idList.add(frame.getId());
		}
		final String frames = Arrays.toString(idList.toArray());

		final String result = getId() + ": " + frames;

		return result;
	}

}