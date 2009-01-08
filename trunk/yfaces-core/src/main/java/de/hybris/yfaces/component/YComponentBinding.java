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

import org.apache.log4j.Logger;

import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.YFacesException;

/**
 * A Binding for an {@link YComponent} instance.<br/>
 * <br/>
 * Within the view this binding gets fully resolved into a {@link YComponent}
 * instance.<br/>
 * When using it programatically {@link YComponentBinding#getValue()} must be
 * called.
 * 
 * @author Denny.Strietzbaum
 */
public class YComponentBinding<T extends YComponent> {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(YComponentBinding.class);

	private String frameBinding = null;
	private boolean resolved = false;

	protected String id = null;

	// transient members
	private transient T value = null;

	/**
	 * Constructor. Bounds this binding to a {@link YComponent} which is given
	 * by its ID.<br/>
	 * 
	 * @param id
	 *            component id
	 */
	protected YComponentBinding(final String id) {
		this.id = id;
	}

	protected YComponentBinding(final String id, final String frameBinding) {
		this.id = id;
		this.frameBinding = frameBinding;
	}

	/**
	 * Returns the component which is bound to this binding.<br/>
	 * 
	 * @return {@link YComponent}
	 */
	public T getValue() {
		// standard lookup
		if (this.value == null && this.id != null) {
			final YComponentInfo cmpd = YComponentRegistry.getInstance().getComponent(this.id);

			if (cmpd == null) {
				throw new YFacesException("There is no component with id '" + this.id
						+ "' registered.");
			}

			this.value = (T) cmpd.createDefaultComponent();
			((AbstractYComponent) value).setFrame(this.frameBinding);
		}

		this.resolved = true;

		return this.value;
	}

	/**
	 * Sets the {@link YComponent} for this binding.
	 */
	public void setValue(final T value) {
		this.value = value;
		if (value != null) {
			// inject the frame
			((AbstractYComponent) value).setFrame(this.frameBinding);

		}
		// resolved=false what be accurate here
		// is currently not implemented because setValue gets called with every
		// request
		// from HtmlYComponent from within processDecodes (=ApplyRequestValues)
	}

	/**
	 * A binding was resolved when {@link #getValue()} was called at least one
	 * times.<br/>
	 * A component of an unresolved binding never gets updated by the Frame.
	 * 
	 * @return true when resolved
	 */
	public boolean isResolved() {
		return this.resolved;
	}

}
