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
package org.codehaus.yfaces;

import javax.el.ELContext;

import org.codehaus.yfaces.component.YModelBinding;
import org.codehaus.yfaces.component.html.HtmlYComponent;


/**
 * When an {@link ELContext} gets created this YFaces specific context is added into it. The
 * {@link YFacesELContextListener} listens to {@link ELContext} creation and injects an instance of
 * this context into it. The {@link YFacesELResolver} extracts this context from {@link ELContext}
 * and asks each time when a {@link YModelBinding} shall be resolved whether that binding has to
 * be resolved automatically. If so the result of {@link YModelBinding#getValue() } is used for
 * further resolving instead of the {@link YModelBinding} instance.
 * 
 * @see YFacesELContextListener
 * @see YFacesELResolver
 * @see HtmlYComponent
 * 
 * @author Denny Strietzbaum
 */
public class YFacesELContext {
	private boolean resolveComponentBinding = true;

	/**
	 * Sets auto-resolving of {@link YModelBinding} instances.
	 * 
	 * @param enabled
	 *          true when auto-resolving shall be enabled
	 */
	public void setResolveYComponentBinding(final boolean enabled) {
		this.resolveComponentBinding = enabled;
	}

	/**
	 * Whether auto-resolving of {@link YModelBinding} instances is enabled.
	 * 
	 * @return true when enabled
	 */
	public boolean isResolveYComponentBinding() {
		return this.resolveComponentBinding;
	}
}
