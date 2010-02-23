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
package org.codehaus.yfaces.component;

import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.component.html.HtmlYComponent;


/**
 * A Binding for an {@link YModel} instance.<br/>
 * Generally JSF doesn't use a "binding instance" when a binding attribute is used but
 * injects/retrieves the bound value directly from the parent instance. <br/>
 * example: <code>&lt;h:outputText binding="#{managedBean.textComponent}"/&gt; </code><br/>
 * Whereas managedBean is a ManagedBean which has a getter and a setter for a {@link HtmlYComponent} <br/>
 * YFaces provides another technique and introduces a thin meta-class which describes such a binding
 * for a {@link YModel} instance more particularly. Instead of returning the concrete component
 * instance an instance of {@link YModelBinding} can be returned. For the view nothing changes,
 * the binding attribute is used exactly like before. However, the programmer has some more choices
 * now.
 * 
 * @author Denny Strietzbaum
 */
public class YModelBinding<T extends YModel> {

	//private static final Logger log = Logger.getLogger(YComponentBinding.class);

	private String frameBinding = null;
	private boolean resolved = false;

	private String id = null;
	private String ns = null;

	// transient members
	private transient T value = null;

	/**
	 * Constructor. Creates a general binding without any {@link YModel} information. A concrete
	 * {@link YModel} instance can be set later programatically. When no component instance is set
	 * JSF does it when this binding is resolved for the first time.<br/>
	 */
	public YModelBinding() {

	}

	/**
	 * Constructor. Bounds this binding to a {@link YModel} which is given by its ID.<br/>
	 * 
	 * @param id
	 *          component id
	 */
	public YModelBinding(final YComponentInfo cmpInfo) {
		this(cmpInfo, null);
	}

	protected YModelBinding(final YComponentInfo cmpInfo, final String frameBinding) {
		if (cmpInfo != null) {
			this.id = cmpInfo.getId();
			this.ns = cmpInfo.getNamespace();
		}
		this.frameBinding = frameBinding;
	}

	/**
	 * Returns the component which is bound to this binding.<br/>
	 * 
	 * @return {@link YModel}
	 */
	public T getValue() {
		// standard lookup
		if (this.value == null && this.id != null) {
			final YComponentInfo cmpInfo = YComponentRegistry.getInstance().getComponent(this.ns, this.id);

			if (cmpInfo == null) {
				throw new YFacesException("There is no component with id '" + this.id + "' registered.");
			}

			this.value = (T) cmpInfo.getModelProcessor().createModel();
			((AbstractYModel) value).setFrame(this.frameBinding);
		}

		this.resolved = true;

		return this.value;
	}

	/**
	 * Sets the {@link YModel} for this binding.
	 */
	public void setValue(final T value) {
		this.value = value;
		if (value != null) {
			// inject the frame
			((AbstractYModel) value).setFrame(this.frameBinding);

		}
		// resolved=false what be accurate here
		// is currently not implemented because setValue gets called with every
		// request
		// from HtmlYComponent from within processDecodes (=ApplyRequestValues)
	}

	/**
	 * A binding was resolved when {@link #getValue()} was called at least one times.<br/>
	 * A component of an unresolved binding never gets updated by the Frame.
	 * 
	 * @return true when resolved
	 */
	public boolean isResolved() {
		return this.resolved;
	}

}
