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
package de.hybris.yfaces;

import javax.el.ELContext;

import de.hybris.yfaces.component.YComponentBinding;

/**
 * An own context class used for the current {@link ELContext}<br/>
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesELContext {
	private boolean resolveComponentBinding = true;

	/**
	 * Enables or disables resolving of {@link YComponentBinding} instances.<br/>
	 * When true every {@link YComponentBinding} gets treated as
	 * {@link YComponentBinding#getValue()} when an instance is requested or
	 * {@link YComponentBinding#setValue(de.hybris.yfaces.component.YComponent)}
	 * when an instance is set.
	 * 
	 * @param enabled
	 *            enable or disable
	 */
	public void setResolveYComponentBinding(final boolean enabled) {
		this.resolveComponentBinding = enabled;
	}

	/**
	 * @return true when resolving of {@link YComponentBinding} instances is
	 *         enabled
	 */
	public boolean isResolveYComponentBinding() {
		return this.resolveComponentBinding;
	}
}
