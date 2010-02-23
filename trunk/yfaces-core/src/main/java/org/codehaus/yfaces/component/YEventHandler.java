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

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

/**
 * An event handler for a {@link YModel}.
 * <p>
 * Technically this handler catches a JSF event of type {@link FacesEvent}, wraps that event into a
 * {@link YEvent} and notifies all {@link YEventListener} which are registered at
 * this handler.
 * <p>
 * To assure this handler gets notified when a {@link FacesEvent} is thrown this handler must be
 * added as listener to appropriate {@link UIComponent} properties.
 * <p>
 * Example:<br/>
 * An YComponent is available under 'mycomponent' and returns an eventhandler under the propery
 * 'submitForm'. <code>&lth:commandButton action="#{mycomponent.submitForm.action}"
 * actionListener="#{mycomponent.submitForm.actionListener}"&gt;</code>
 * <p>
 * 
 * @author Denny Strietzbaum
 */
public interface YEventHandler<T extends YModel> extends Serializable {

	/**
	 * Adds a custom {@link YEventListener}.<br/>
	 * This listener gets processed after the default one and after all previous set listeners.<br/>
	 * 
	 * @param listener
	 *          listener to add.
	 */
	public void addCustomListener(YEventListener<T> listener);

	/**
	 * Returns the default {@link YEventListener}.<br/>
	 * 
	 * @return {@link YEventListener}
	 */
	public YEventListener<T> getListener();

	/**
	 * Sets the default {@link YEventListener}
	 * 
	 * @param listener
	 *          listener to set.
	 */
	public void setListener(YEventListener<T> listener);

	/**
	 * Listens to an incoming JSF FacesEvent and notifies all listeners.<br/>
	 * 
	 * @return JSF outcome
	 */
	public String action();

	/**
	 * Listens to an incoming JSF FacesEvent and notifies all listeners.<br/>
	 */
	public void actionListener(ActionEvent event);

	/**
	 * Listens to an incoming JSF FacesEvent and notifies all listeners.<br/>
	 */
	public void valueChangeListener(ValueChangeEvent event);

	/**
	 * Checks whether this handler is enabled or disabled
	 * 
	 * @return true when enabled
	 */
	public boolean isEnabled();

	/**
	 * Sets this handler enabled or disabled
	 * 
	 * @param enabled
	 *          true when enabled
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Sets the name of this handler
	 * 
	 * @param key
	 *          name to set
	 */
	public void setName(CharSequence key);

	/**
	 * @return name of this handler
	 */
	public String getName();

}
