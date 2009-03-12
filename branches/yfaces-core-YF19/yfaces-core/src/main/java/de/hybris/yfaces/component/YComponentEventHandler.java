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

import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

/**
 * An event handler for a {@link YComponent}.<br/>
 * Accepts JSF events ({@link FacesEvent}) and notifies each listener.<br/>
 * Event delegating methods:<br/>
 * ValueChangeEvent: {@link #valueChangeListener(ValueChangeEvent)} ActionEvent:
 * {@link #actionListener(ActionEvent)} Action outcome: {@link #action()} <br/>
 * Appropriate listener methods are:<br/>
 * {@link YComponentEventListener#valueChangeListener(YComponentEvent)}<br/>
 * {@link YComponentEventListener#actionListener(YComponentEvent)}<br/>
 * {@link YComponentEventListener#action()}<br/>
 * <br/>
 * Caught events are wrapped into a {@link YComponentEvent} which gets passed to
 * an appropriate listener. <br/>
 * <br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface YComponentEventHandler<T extends YComponent> extends Serializable {

	/**
	 * Adds a custom {@link YComponentEventListener}.<br/>
	 * This listener gets processed after the default one and after all previous
	 * set listeners.<br/>
	 * 
	 * @param listener
	 *            listener to add.
	 */
	public void addCustomListener(YComponentEventListener listener);

	/**
	 * Returns the default {@link YComponentEventListener}.<br/>
	 * 
	 * @return {@link YComponentEventListener}
	 */
	public YComponentEventListener getListener();

	/**
	 * Sets the default {@link YComponentEventListener}
	 * 
	 * @param listener
	 *            listener to set.
	 */
	public void setListener(YComponentEventListener listener);

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
	 *            true when enabled
	 */
	public void setEnabled(boolean enabled);

	/**
	 * Sets the name of this handler
	 * 
	 * @param key
	 *            name to set
	 */
	public void setName(CharSequence key);

	/**
	 * @return name of this handler
	 */
	public String getName();

}
