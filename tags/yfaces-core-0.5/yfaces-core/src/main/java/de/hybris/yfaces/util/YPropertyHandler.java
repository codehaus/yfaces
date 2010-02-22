package de.hybris.yfaces.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class YPropertyHandler extends PropertyChangeSupport {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(YPropertyHandler.class);

	private final Set<String> propChangeLog = new HashSet<String>();
	private final Map<String, Object> propMap = new HashMap<String, Object>();

	public YPropertyHandler() {
		super("");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#getProperty(java.lang.String )
	 */
	public <T> T getProperty(String key) {
		return (T) propMap.get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#setProperty(java.lang.String ,
	 * java.lang.Object)
	 */
	public void setProperty(String property, Object value) {
		this.setProperty(property, value, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#setProperty(java.lang.String ,
	 * java.lang.Object, boolean)
	 */
	public void setProperty(String property, Object value, boolean enableEvents) {
		Object oldValue = this.propMap.get(property);

		// if we ever introduce some kind of veto concept we might need to
		// change the order of setting the property and firing the change event
		// here...
		this.propMap.put(property, value);

		if (enableEvents) {
			this.firePropertyChange(property, oldValue, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#firePropertyChange(java. lang.String,
	 * java.lang.Object, java.lang.Object)
	 */
	@Override
	public void firePropertyChange(String property, Object oldValue, Object newValue) {
		boolean changed = isDifferent(oldValue, newValue);

		if (log.isDebugEnabled()) {
			String value = newValue != null ? String.valueOf(newValue.hashCode()) : "[null]";
			String msg = "property " + property.toUpperCase() + " was set ("
					+ (changed ? "changed to " + value : "unchanged") + ") ";
			log.debug(msg);
		}

		if (changed) {
			this.propChangeLog.add(property);
			super.firePropertyChange(property, oldValue, newValue);
		}
	}

	public boolean isPropertyChanged(String key) {
		return propChangeLog.contains(key);
	}

	public void setPropertyChanged(String key, boolean changed) {
		if (log.isDebugEnabled()) {
			log.debug("Set property changed: " + key + ": " + changed);
		}

		if (changed) {
			propChangeLog.add(key);
		} else {
			propChangeLog.remove(key);
		}
	}

	public void resetPropertyChanged() {
		this.propChangeLog.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.UserSession#addPropertyChangeListener
	 * (de.hybris.platform.webfoundation.UserSession .UserSessionPropertyChangeListener)
	 */
	public void addPropertyChangeListener(String propertyName, Class<?> spec, Object listener) {
		PropertyChangeListener pListener = new PropertyChangeListenerWrapper(spec, listener);
		super.addPropertyChangeListener(propertyName, pListener);
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
	private boolean isDifferent(Object value1, Object value2) {
		return (value1 != null) ? !value1.equals(value2) : (value2 != null);
	}

}