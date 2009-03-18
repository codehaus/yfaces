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

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.component.html.HtmlYComponent;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YComponentEventImpl<T extends YComponent> implements YComponentEvent {
	private static final Logger log = Logger.getLogger(YComponentEventImpl.class);
	private static final String YCOMPONENT_ACTION_PARAMETER = YComponent.class.getSimpleName();

	// current ActionEvent, ActionForm and ActionComponent (autodetected)
	private transient FacesEvent facesEventSource = null;
	private transient UIForm actionUIForm = null;
	private transient UIComponent actionUICmp = null;
	private transient HtmlYComponent actionHtmlHCmp = null;

	private transient T yCmpSource = null;

	private transient String logId = null;

	public YComponentEventImpl(final FacesEvent source) {
		this.logId = this.getClass().getSimpleName();
		this.facesEventSource = source;
	}

	/**
	 * Initializes this controller. This includes operations like detect
	 * actionform (parentform), actioncomponent, retrieve the model, ... <br/>
	 * This method must be called by all actioneventlisteners.
	 * 
	 * @param event
	 *            {@link ActionEvent}
	 */
	protected void initialize(final FacesEvent event) {
		this.facesEventSource = event;

		if (event == null) {
			throw new YFacesException(logId
					+ ":can't initialize controller; No ActionEvent specified",
					new NullPointerException());
		}

		if (getActionComponent() == null) {
			throw new YFacesException(logId + ": can't find a YComponent");
		}

		if (log.isDebugEnabled()) {
			final YComponent cmp = getActionComponent();
			String ctxPath = "event:" + this.getClass().getSimpleName() + "; componentId:"
					+ cmp.getId();

			if (cmp.getFrame() != null) {
				ctxPath = ctxPath + "; frameId: " + cmp.getFrame().getId() + "; pageId:"
						+ cmp.getFrame().getPage().getId();
			} else {
				ctxPath = ctxPath + "; [not bound to any frame]";
			}
			log.debug(ctxPath);

		}
	}

	/**
	 * Returns the {@link UICommand} who fired this action.<br/>
	 * 
	 * @return {@link UICommand}
	 */
	public UIComponent getActionUIComponent() {
		if (this.actionUICmp == null) {
			if (this.facesEventSource == null) {
				throw new YFacesException(logId
						+ ":can't get UIComponent; Controller not initialized (no ActionEvent)");
			}

			this.actionUICmp = this.facesEventSource.getComponent();
		}
		return this.actionUICmp;
	}

	/**
	 * Returns the nearest enclosing {@link UIForm} of the {@link UICommand} who
	 * fired this action.<br/>
	 * 
	 * @return {@link UIForm}
	 */
	public UIForm getActionUIForm() {
		if (this.actionUIForm == null) {
			if (this.facesEventSource == null) {
				throw new YFacesException(logId
						+ ":can't get UIForm; Controller not initialized (no ActionEvent)");
			}

			if ((this.actionUIForm = this.findParentForm(this.facesEventSource.getComponent())) == null) {
				throw new YFacesException(logId + ":can't find parent UIForm");
			}
		}

		return this.actionUIForm;
	}

	/**
	 * Returns the nearest enclosing {@link HtmlYComponent} of the
	 * {@link UIComponent} (may be of type command or select) who fired this
	 * action.<br/>
	 * 
	 * @return {@link HtmlYComponent}
	 */
	public HtmlYComponent getActionHtmlYComponent() {
		if (this.actionHtmlHCmp == null) {
			UIComponent _cmp = getActionUIComponent();
			while (!((_cmp = _cmp.getParent()) instanceof HtmlYComponent)) {
				if (_cmp instanceof UIViewRoot) {
					_cmp = null;
					break;
				}
			}
			this.actionHtmlHCmp = (HtmlYComponent) _cmp;
		}
		return this.actionHtmlHCmp;
	}

	/**
	 * Finds the closest parent {@link UIForm}
	 * 
	 * @param c
	 *            Childcomponent of the Parentform.
	 * @return Parentform
	 */
	private UIForm findParentForm(final UIComponent c) {
		UIComponent result = c;
		while ((!(result instanceof UIForm)) && (result != null)) {
			result = result.getParent();
		}
		return (UIForm) result;
	}

	@SuppressWarnings("unchecked")
	private <T2 extends YComponent> T2 findComponent(final String attribute) {
		T2 result = null;

		if (this.isInitialized()) {
			// lookup for parent HybrisComponent which holds the model
			HtmlYComponent cmp = null;
			if (result == null && (cmp = getActionHtmlYComponent()) != null) {
				result = (T2) cmp.getYComponent();
			}
		} else {
			throw new YFacesException(
					logId
							+ " found no Component (got no FacesEvent; missing Action- or ValueChangeListener?)");
		}

		// //try request attributes
		// if (result == null)
		// {
		// log.warn(logId +
		// ": is not initialized; try request for attribute-lookup as fallback");
		// result =
		// (T)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(attribute);
		// }

		// finally throw exception when no component was found
		if (result == null) {
			throw new YFacesException(logId + ": can't find a " + YComponent.class.getSimpleName());
		}

		return result;
	}

	private boolean isInitialized() {
		return (this.facesEventSource != null);
	}

	public T getActionComponent() {
		if (this.yCmpSource == null) {
			this.yCmpSource = (T) findComponent(YCOMPONENT_ACTION_PARAMETER);
		}
		return this.yCmpSource;
	}

	public FacesEvent getFacesEvent() {
		return this.facesEventSource;
	}

}
