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

import de.hybris.yfaces.component.NavigationContext;
import de.hybris.yfaces.component.YFrame;

/**
 * A YPage contains of one or more {@link YFrame}. A page is that thing which is locateable by a
 * client.<br/>
 * (Either directly via URL or indirectly via navigationid)<br/>
 * <br/>
 * The superior instance of a YPage is the {@link NavigationContext} which is able to manage state
 * and navigation route between multiple pages <br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface YPage {
	/**
	 * @return a
	 */
	public String getId();

	/**
	 * Returns the URL where this page is located.<br/>
	 * Result may either be absolute or relative to the webapp root.<br/>
	 * A relative result always starts with a leading slash '/'.
	 * <p/>
	 * In difference to the viewrootid this result may include query parameters.
	 * 
	 * @return URL
	 */
	public String getURL();

	/**
	 * Returns the navigation id of the page which is located.<br/>
	 * The navigation id is never null.
	 * 
	 * @return navigationId
	 */
	public String getNavigationId();

	/**
	 * Returns a page scoped attribute map.
	 * 
	 * @return attribute map.
	 */
	public Map<Object, Object> getAttributes();

	/**
	 * Returns the superior {@link NavigationContext}.
	 * 
	 * @return {@link NavigationContext}
	 */
	public NavigationContext getNavigationContext();

	/**
	 * Returns the enclosing Frames.
	 * 
	 * @return Frames
	 */
	public Map<String, YFrame> getFrames();

	/**
	 * Adds an {@link YFrame} to this page.<br/>
	 * 
	 * @param frame
	 */
	public void addFrame(YFrame frame);

	/**
	 * @param frameClass
	 */
	public <T extends YFrame> T getFrame(Class<T> frameClass);

	public void update();

	/**
	 * Returns the previous YPage.<br/>
	 * This is the case when this YPage is element of a navigation route (Wizard or Conversation)
	 * otherwise it is null. <br/>
	 * A page becomes ancestor of another page after calling {@link NavigationContext#getNextPage()} <br/>
	 * 
	 * @return previous YPage
	 */
	public YPage getPreviousPage();
}
