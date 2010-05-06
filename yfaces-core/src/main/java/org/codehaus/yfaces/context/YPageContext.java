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
import java.util.Map;

import org.codehaus.yfaces.component.YComponentContainer;
import org.codehaus.yfaces.component.YModel;

/**
 * A context object whose scope and lifetime is bound to a Page. A page lays between
 * {@link YApplicationContext} and {@link YSessionContext} but is always lesser than a
 * {@link YConversationContext}. A page represents the full rendered view and lives as long as
 * operations are made at that view. An easy summary: this instance lives as long as the customer
 * deals with the same browser URL (incl. query parameters)
 * <p>
 * Access is provided to every {@link YComponentContainer} (and all {@link YModel} instances) which
 * are used to build up the current view. When this page is part of a conversation it may have one
 * ore more previous (not visible) pages.
 * 
 * @author Denny Strietzbaum
 */
public interface YPageContext {

	/**
	 * Returns the page-id.
	 * 
	 * @return page-id
	 */
	String getId();

	/**
	 * Returns the URL where this page is located (absolute or relative to the webapp root). A
	 * relative result always starts with a leading slash '/'. In difference to the viewroot-id this
	 * result may include query parameters.
	 * 
	 * @return URL
	 */
	String getURL();

	/**
	 * Returns the navigation id for this page. This is an ID which can be used within faces-config as
	 * navigation target. The navigation id is never null.
	 * 
	 * @return navigationId
	 */
	String getNavigationId();

	/**
	 * A map of attributes backed by the lifetime of this scope.
	 * 
	 * @return {@link Map}
	 */
	Map<Object, Object> getAttributes();

	/**
	 * Returns the {@link YConversationContext}.
	 * 
	 * @return {@link YConversationContext}
	 */
	YConversationContext getConversationContext();

	/**
	 * Returns all frames which are managed by this page.
	 * 
	 * @return Frames
	 */
	Collection<YComponentContainer> getFrames();

	/**
	 * Returns the previous YPage.<br/>
	 * This is the case when this YPage is element of a navigation route (Wizard or Conversation)
	 * otherwise it is null. <br/>
	 * A page becomes ancestor of another page after calling
	 * {@link YConversationContext#getOrCreateNextPage()} <br/>
	 * 
	 * @return previous YPage
	 */
	YPageContext getPreviousPage();

	<T extends YComponentContainer> T getOrCreateFrame(final Class<T> frameClass);

}
