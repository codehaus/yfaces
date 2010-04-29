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

import java.util.ArrayList;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFaces;
import org.codehaus.yfaces.YManagedBean;
import org.codehaus.yfaces.context.YPageContext;

/**
 * Abstract base class for every YFrame.<br/>
 * Each {@link YPageContext} manages one or more {@link YFrame} instances.<br/>
 * Each {@link YFrame} manages one ore more {@link YModel} instances. <br/>
 * A YFrame is a ManagedBean and must declared in a faces configuration file.<br/>
 * 
 * @author Denny Strietzbaum
 * 
 */
public abstract class AbstractYFrame extends YManagedBean implements YFrame {

	//
	// Developer notes:
	// Never hold instances if YModel here (transient is allowed)
	// Reason: When client-side state saving is enabled, YModels are getting serialized in UiTree
	// after deserializing them, these models are decoupled from the instances held here
	//
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AbstractYFrame.class);

	private List<ValueExpression> modelBindings = null;

	/**
	 * Constructor.
	 */
	public AbstractYFrame() {
		super();
		this.modelBindings = new ArrayList<ValueExpression>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.component.YFrame#refresh()
	 */
	public void refresh() {
		for (final ValueExpression ve : this.modelBindings) {
			log.debug("Refreshing " + ve);
			AbstractYModel model = null;
			try {

				// NOTES:
				// refresh should only invoke VE get and call refers
				// YFrame set should be called in HtmlYComponent in processDecodes
				// problrm is: identic model gets serialized in htmlycompoent and stays in yframe
				// -> after deserialization its not identic anymore
				// soluion: model must store full framebinding

				model = (AbstractYModel) ve.getValue(FacesContext.getCurrentInstance().getELContext());
				model.refresh();
			} catch (final Exception e) {
				log.error("Error refreshing component: " + model.getClass().getSimpleName() + " ("
						+ this.getClass().getSimpleName() + ")", e);
			}
		}

	};

	protected void addModelBinding(final ValueExpression ve) {
		this.modelBindings.add(ve);
	}

	protected <T extends YModel> T createDefaultYModel(final String cmpId) {
		return (T) YFaces.getYComponentRegistry().getComponent(cmpId).getModelProcessor().createModel();
	}

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
		return YFaces.getRequestContext().getPageContext();
	}

	/**
	 * Creates an {@link YEventListener} for this {@link YFrame} based on the passed method.<br/>
	 * 
	 * @param frameMethod
	 *          method of this frame which shall listen to
	 * @return {@link YEventListener}
	 */
	public <T extends YModel> YEventListener<T> createComponentEventListener(final String frameMethod) {
		final YEventListener<T> result = new DefaultYEventListener<T>();
		result.setActionListener(super.createExpressionString(frameMethod));
		return result;
	}

}
