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

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * A context object whose scope and lifetime is bound to {@link HttpSession}.
 * 
 * @author Denny Strietzbaum
 */
public interface YSessionContext {

	/**
	 * A map of attributes backed by the lifetime of this scope. Fetching a value for a key is the
	 * same like {@link HttpSession#getAttribute(String)}. Same with setting a value.
	 * 
	 * @return {@link Map}
	 */
	Map<String, Object> getAttributes();

	YConversationContext getConversationContext();

	YApplicationContext getApplicationContext();

}
