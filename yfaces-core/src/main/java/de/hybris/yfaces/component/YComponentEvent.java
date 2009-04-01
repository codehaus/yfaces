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
package de.hybris.yfaces.component;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.event.FacesEvent;

/**
 * Common event which gets thrown by a {@link YComponent}<br/>
 * <br/>
 * Or more detailed:<br/>
 * This type of event gets created and thrown from within a {@link YComponentEventHandler}<br/>
 * The {@link YComponentEventHandler} takes the FacesEvent which was thrown by JSF, wraps it into
 * this event and notifies each registered {@link YComponentEventListener}<br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface YComponentEvent<T extends YComponent> {
	/**
	 * Returns the nearest enclosing {@link UIForm} of the {@link UICommand} who fired this action.<br/>
	 * 
	 * @return {@link UIForm}
	 */
	public UIForm getActionUIForm();

	/**
	 * Returns the {@link UICommand} who fired this action.<br/>
	 * 
	 * @return {@link UICommand}
	 */
	public UIComponent getActionUIComponent();

	/**
	 * The {@link YComponent} which was responsible for this event.
	 * 
	 * @return {@link YComponent}
	 */
	public T getActionComponent();

	/**
	 * The {@link FacesEvent} which was thrown by JSF.<br/>
	 * 
	 * @return {@link FacesEvent}
	 */
	public FacesEvent getFacesEvent();

}
