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
package de.hybris.yfaces.session;

import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Holds (similar to a HttpSession) session based user information.<br/>
 * Additionally provides a Listener concept which allows listeners for property
 * change events.<br/>
 * <br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface UserSession {
	/** Just a marker interface. */
	public interface UserSessionPropertyChangeListener {
	};

	/**
	 * Returns a Map of Attributes which are bound to the current User.<br/>
	 * When a new User is set all attributes are getting cleared.
	 * 
	 * @return Map
	 */
	public Map<String, Object> getAttributes();

	/**
	 * Returns a property for a given key.
	 * 
	 * @param <T>
	 *            type of result
	 * @param key
	 *            property key
	 * @return property value
	 */
	public <T> T getProperty(String key);

	/**
	 * Sets a property value and notifies appropriate listeners.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            property value
	 */
	public void setProperty(String key, Object value);

	/**
	 * Sets a property value and notifies appropriate listeners when enabled.
	 * 
	 * @param key
	 *            property key
	 * @param value
	 *            property value
	 * @param enableEvents
	 *            enable listeners
	 */
	public void setProperty(String key, Object value, boolean enableEvents);

	/**
	 * Adds a property change listener. The listener must be an instance of
	 * {@link UserSessionPropertyChangeListener}.
	 * 
	 * @param listener
	 *            {@link UserSessionPropertyChangeListener}
	 */
	public void addPropertyChangeListener(UserSessionPropertyChangeListener listener);

	/**
	 * Adds a property change listener.<br/>
	 * The listener follows java bean conventions.
	 * 
	 * @param listener
	 *            {@link PropertyChangeListener}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Adds a property change listener.<br/>
	 * The listener follows java bean conventions.
	 * 
	 * @param property
	 *            property to listen to
	 * @param listener
	 *            {@link PropertyChangeListener}
	 */
	public void addPropertyChangeListener(String property, PropertyChangeListener listener);

	/**
	 * Fires a property.<br/>
	 * This process involves first invoking appropriate listeners and second
	 * updating the {@link UserSessionPropertyChangeLog}.
	 * 
	 * @param property
	 *            property whose value has changed
	 * @param oldValue
	 *            the old value
	 * @param newValue
	 *            the new value
	 */
	public void firePropertyChange(String property, Object oldValue, Object newValue);

	/**
	 * Returns a log protocol about changed properties. The time when the
	 * protocol gets reseted depends on the implementation. In general for a
	 * webcontainer this should be done with every new request.
	 * 
	 * @return {@link UserSessionPropertyChangeLog}
	 */
	public UserSessionPropertyChangeLog getPropertyChangeLog();

}
