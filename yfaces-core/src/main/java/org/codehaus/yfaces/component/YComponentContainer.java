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
package org.codehaus.yfaces.component;

import java.util.Map;

import org.codehaus.yfaces.context.YPageContext;

/**
 * @author Denny Strietzbaum
 * 
 */
public interface YComponentContainer {
	/**
	 * The ID for this frame.<br/>
	 * This is the ManagedBean ID as declared within faces-config.<br/>
	 * 
	 * @return ID for this frame.
	 */
	String getId();

	/**
	 * An optional title for this Frame or 'null'.<br/>
	 * 
	 * @return title of the frame
	 */
	String getTitle();

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
	 * Refreshes this frame.A refresh is processed when this frame was already created and is used
	 * again (POST or GET (flashback enabled) to same page). A refresh always is performed before any
	 * components attribute injection has started.
	 */
	public void refresh();

}
