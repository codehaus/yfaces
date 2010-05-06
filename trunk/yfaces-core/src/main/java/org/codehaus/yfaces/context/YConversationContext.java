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
public interface YConversationContext {

	/**
	 * Returns the ID of this context.
	 * 
	 * @return id
	 */
	public String getId();

	/**
	 * A map of attributes backed by the lifetime of this scope.
	 * 
	 * @return {@link Map}
	 */
	public Map<String, Object> getAttributes();

	/**
	 * Returns the current displayed Page.<br/>
	 * May or may not have one ore more previous pages<br/>
	 * (depends on whether {@link #getOrCreateNextPage()} was called before current request)<br/>
	 * <br/>
	 * 
	 * @return the current {@link YPageContext}
	 */
	public YPageContext getLastPage();

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
	public YPageContext getOrCreateNextPage();

	/**
	 * Returns all available {@link YPageContext} instances for this conversation. Collection elements
	 * are ordered by creation time.
	 * 
	 * @return all {@link YPageContext} instances
	 */
	public Collection<YPageContext> getAllPages();

	/**
	 * Returns the current {@link YSessionContext}
	 * 
	 * @return {@link YSessionContext}
	 */
	public YSessionContext getSessionContext();

}
