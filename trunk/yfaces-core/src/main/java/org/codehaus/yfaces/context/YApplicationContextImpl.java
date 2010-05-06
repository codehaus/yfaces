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

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.codehaus.yfaces.component.YComponentHandlerRegistry;

/**
 * A context object whose scope and lifetime is bound to {@link ServletContext}. In other words:
 * there's only one instance per webapplication available.<br/>
 * YFaces libraries must be loaded by a webapplication specific classloader (which generally is the
 * case when putting them into WEB-INF/lib)
 * 
 * @author Denny Strietzbaum
 */
public class YApplicationContextImpl implements YApplicationContext {

	private final YComponentHandlerRegistry cmpHandlers;
	private final Map<Class, String> cmpContainerIdMap;

	public YApplicationContextImpl() {
		this.cmpHandlers = new YComponentHandlerRegistry();
		this.cmpContainerIdMap = new HashMap<Class, String>();
	}

	public YComponentHandlerRegistry getComponentHandlers() {
		return this.cmpHandlers;
	}

	public String getComponentContainerId(final Class cmpContainerClass) {
		return this.cmpContainerIdMap.get(cmpContainerClass);
	}

	public void setComponentContainerId(final Class cmpContainerClass, final String id) {
		this.cmpContainerIdMap.put(cmpContainerClass, id);
	}

	/**
	 * A map of attributes backed by the lifetime of this scope. Fetching a value for a key is the
	 * same like {@link ServletContext#getAttribute(String)}. Same with setting a value.
	 * 
	 * @return {@link Map}
	 */
	public Map<String, Object> getAttributes() {
		return FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
	}

}
