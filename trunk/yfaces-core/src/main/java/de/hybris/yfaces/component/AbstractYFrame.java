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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YManagedBean;
import de.hybris.yfaces.application.YRequestContext;
import de.hybris.yfaces.application.YPageContext;

/**
 * Abstract base class for every YFrame.<br/>
 * Each {@link YPageContext} manages one or more {@link YFrame} instances.<br/>
 * Each {@link YFrame} manages one ore more {@link YComponent} instances. <br/>
 * A YFrame is a ManagedBean and must declared in a faces configuration file.<br/>
 * 
 * @author Denny.Strietzbaum
 * 
 */
public abstract class AbstractYFrame extends YManagedBean implements YFrame, Serializable {
	private static final Logger log = Logger.getLogger(AbstractYFrame.class);

	private List<YComponentBinding> componentBindings = null;

	/**
	 * Constructor.
	 */
	public AbstractYFrame() {
		super();
		this.componentBindings = new ArrayList<YComponentBinding>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.mbean.YFrame#updateComponents(de.hybris
	 * .platform.webfoundation.PropertyChangeLog)
	 */
	public void update() {
		for (final YComponentBinding binding : this.componentBindings) {
			if (binding.isResolved() && binding.getValue() != null) {
				log.debug("Updating component: " + binding.getValue().getClass().getSimpleName());
				binding.getValue().update();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.mbean.YFrame#getFrameId()
	 */
	public String getId() {
		return super.getBeanId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YFrame#createComponentBinding()
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding() {
		return this.createComponentBinding(null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YFrame#createComponentBinding(de.hybris.yfaces.YComponent )
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding(final T value) {
		return this.createComponentBinding(null, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YFrame#createComponentBinding(java.lang.String)
	 */
	public <T extends YComponent> YComponentBinding<T> createComponentBinding(
			final String componentId) {
		return this.createComponentBinding(componentId, null);
	}

	private <T extends YComponent> YComponentBinding<T> createComponentBinding(final String id,
			final T value) {
		final YComponentBinding<T> result = new YComponentBinding(id, super
				.createExpressionString());
		result.setValue(value);
		this.componentBindings.add(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storefoundation.yfaces.YFrame#getTitle()
	 */
	public String getTitle() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storefoundation.yfaces.YFrame#getPage()
	 */
	public YPageContext getPage() {
		return YRequestContext.getCurrentContext().getNavigationContext().getCurrentPage();
	}

	/**
	 * Creates an {@link YComponentEventListener} for this {@link YFrame} based on the passed
	 * method.<br/>
	 * 
	 * @param frameMethod
	 *            method of this frame which shall listen to
	 * @return {@link YComponentEventListener}
	 */
	public YComponentEventListener createComponentEventListener(final String frameMethod) {
		final YComponentEventListener result = new DefaultYComponentEventListener();
		result.setActionListener(super.createExpressionString(frameMethod));
		return result;
	}

}
