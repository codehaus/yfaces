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
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * Default {@link UserSession} implementation.
 * 
 * @author Denny.Strietzbaum
 */
public class UserSessionImpl implements UserSession {
	private static final Logger log = Logger.getLogger(UserSessionImpl.class);

	private final Map<String, Object> properties = new HashMap<String, Object>();

	// custom user attributes
	private Map<String, Object> attributes = null;

	// logging for changed properties
	private UserSessionPropertyChangeLog propertyChangeLog = null;

	// handler for event listeners
	private PropertyChangeSupport propertyChangeSupport = null;

	// registered usersessionpropertychangelistener interfaces
	private final Map<String, Class<UserSessionPropertyChangeListener>> interfaceMap = new HashMap<String, Class<UserSessionPropertyChangeListener>>();

	/**
	 * Constructor.
	 */
	public UserSessionImpl() {
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeLog = new AbstractUserSessionPropertyChangeLog();
		this.attributes = new HashMap<String, Object>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#getAttributes()
	 */
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#getProperty(java.lang.String
	 * )
	 */
	public <T> T getProperty(final String key) {
		return (T) properties.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#getPropertyChangeLog()
	 */
	public UserSessionPropertyChangeLog getPropertyChangeLog() {
		return this.propertyChangeLog;
	}

	/**
	 * Sets a new {@link UserSessionPropertyChangeLog}.
	 * 
	 * @param log
	 *            {@link UserSessionPropertyChangeLog}
	 */
	protected void setPropertyChangeLog(final UserSessionPropertyChangeLog log) {
		this.propertyChangeLog = log;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#setProperty(java.lang.String
	 * , java.lang.Object)
	 */
	public void setProperty(final String property, final Object value) {
		this.setProperty(property, value, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#setProperty(java.lang.String
	 * , java.lang.Object, boolean)
	 */
	public void setProperty(final String property, final Object value, final boolean enableEvents) {
		final Object oldValue = this.properties.get(property);

		// if we ever introduce some kind of veto concept we might need to
		// change the order of setting the property and firing the change event
		// here...
		this.properties.put(property, value);

		if (enableEvents) {
			this.firePropertyChange(property, oldValue, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#firePropertyChange(java.
	 * lang.String, java.lang.Object, java.lang.Object)
	 */
	public void firePropertyChange(final String property, final Object oldValue,
			final Object newValue) {
		final boolean changed = isDifferent(oldValue, newValue);

		if (log.isDebugEnabled()) {
			final String value = newValue != null ? String.valueOf(newValue.hashCode()) : "[null]";
			final String msg = "property " + property.toUpperCase() + " was set ("
					+ (changed ? "changed to " + value : "unchanged") + ") ";
			log.debug(msg);
		}

		if (changed) {
			this.propertyChangeLog.setPropertyChanged(property, true);
			this.propertyChangeSupport.firePropertyChange(property, oldValue, newValue);
		}
	}

	/**
	 * Adds an interface class to the definition list of possible property
	 * listeners.
	 * 
	 * @param property
	 *            property which this interface is assigned to
	 * @param interfaceClass
	 *            interface class
	 */
	protected void registerUserSessionPropertyChangeListener(final String property,
			final Class<? extends UserSessionPropertyChangeListener> interfaceClass) {
		if (!interfaceClass.isInterface()) {
			throw new YFacesException("Expected an interface, but got " + interfaceClass);
		}

		this.interfaceMap.put(property, (Class) interfaceClass);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#addPropertyChangeListener
	 * (de.hybris.platform.webfoundation.UserSession
	 * .UserSessionPropertyChangeListener)
	 */
	public void addPropertyChangeListener(final UserSessionPropertyChangeListener listener) {
		for (final Map.Entry<String, Class<UserSessionPropertyChangeListener>> entry : this.interfaceMap
				.entrySet()) {
			final Class<UserSessionPropertyChangeListener> specClass = entry.getValue();
			if (specClass.isAssignableFrom(listener.getClass())) {
				log.debug("Adding " + listener.getClass().getName() + " as "
						+ specClass.getSimpleName());
				this.propertyChangeSupport.addPropertyChangeListener(entry.getKey(),
						new PropertyChangeListenerWrapper(specClass, listener));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#addPropertyChangeListener
	 * (java.lang.String, java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final String property,
			final PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.UserSession#addPropertyChangeListener
	 * (java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Internal. Compares two values for equality.
	 * 
	 * @param value1
	 *            value1
	 * @param value2
	 *            value1
	 * @return true when values aren't equal
	 */
	private boolean isDifferent(final Object value1, final Object value2) {
		return (value1 != null) ? !value1.equals(value2) : (value2 != null);
	}

}
