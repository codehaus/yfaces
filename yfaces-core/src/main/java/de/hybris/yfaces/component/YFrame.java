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

import java.util.Map;

import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

/**
 * @author Denny.Strietzbaum
 * 
 */
public interface YFrame {
	/**
	 * The ID for this frame.<br/>
	 * This is the ManagedBean ID as declared within faces-config.<br/>
	 * 
	 * @return ID for this frame.
	 */
	public String getId();

	/**
	 * An optional title for this Frame or 'null'.<br/>
	 * 
	 * @return title of the frame
	 */
	public String getTitle();

	/**
	 * Creates an {@link YComponentBinding} which is currently not bound to any particular component
	 * instance.<br/>
	 * 
	 * @param <T>
	 *            type of {@link YComponent}
	 * @return {@link YComponentBinding}
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding();

	/**
	 * Creates an {@link YComponentBinding} which is bound to a component instance given by its id.<br/>
	 * The ID must match one of the IDs of the component xhtml files.
	 * 
	 * @param <T>
	 *            type of {@link YComponent}
	 * @param componentId
	 *            component ID as declared in xhtml renderer
	 * @return {@link YComponentBinding}
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding(String componentId);

	/**
	 * Creates a {@link YComponentBinding} and already sets a concrete component instance.
	 * 
	 * @param <T>
	 *            type of {@link YComponent}
	 * @param component
	 *            {@link YComponent} to set
	 * @return {@link YComponentBinding}
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding(T component);

	/**
	 * Returns an attribute map which is bound to this frame.
	 * 
	 * @return Map
	 */
	public Map<String, Object> getAttributes();

	/**
	 * Returns the parent {@link YPageContext} which manages this Frame.
	 * 
	 * @return {@link YPageContext}
	 */
	public YPageContext getPage();

	/**
	 * Gets called from {@link YPageContext#update(UserSessionPropertyChangeLog)}<br/>
	 * 
	 * @param log
	 *            {@link UserSessionPropertyChangeLog} <br/>
	 * @see YConversationContext#update() for more information about when an update is invoked.
	 */
	public void update();

}
