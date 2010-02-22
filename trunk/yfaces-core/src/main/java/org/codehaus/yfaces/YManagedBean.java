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

package org.codehaus.yfaces;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;


/**
 * Abstract Implementation of a generic ManagedBean. Autodetects the ManagedBean id according the
 * classname.<br/>
 * Automatically restores the ManagedBean scope after deserialization.<br/>
 * 
 * @author Denny Strietzbaum
 */
public class YManagedBean {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AbstractYFrame.class);

	// Cache for ManagedBean id's
	private static final Map<Class<YManagedBean>, String> mbeanIdMap = new HashMap<Class<YManagedBean>, String>();

	// custom attributes
	private Map<String, Object> attributes = null;

	// transient members
	// for logging only
	private transient String logId = null;

	// ID of this ManagedBean instance as defined in the faces-config.xml
	private transient String beanId = null;

	/**
	 * Constructor.
	 */
	public YManagedBean() {
		this.logId = this.getBeanId();

		log.debug("Creating instance (constructor) [" + logId + "]");
	}

	/**
	 * Returns a lazily created map of attributes for general usage.
	 * 
	 * @return Map
	 */
	public Map<String, Object> getAttributes() {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Object>();
		}
		return this.attributes;
	}

	/**
	 * Creates an expression String for this ManagedBean.<br/>
	 * Assuming a bean id "mybean" the result would be #{mybean}.
	 * 
	 * @return String
	 */
	public String createExpressionString() {
		return this.createExpressionString(getBeanId(), null);
	}

	/**
	 * Creates an expression String for a property or method of this ManagedBean.<br/>
	 * Assuming a bean id "mybean" whose property "myproperty" gets called the result would be
	 * #{mybean.property}.
	 * 
	 * @return String
	 */
	public String createExpressionString(final String property) {
		return this.createExpressionString(getBeanId(), property);
	}

	/**
	 * Internal. CReates an expression String based on a ManagedBean class and a property.
	 * 
	 * @param beanId
	 * @param property
	 */
	private String createExpressionString(final String beanId, final String property) {
		final String beanProperty = (property != null) ? "." + property : "";
		final String result = "#{" + beanId + beanProperty + "}";
		return result;
	}

	/**
	 * Returns the ID for this ManagedBean.<br/>
	 * This is the same one as configured within the JSF configuration file.<br/>
	 * Default implementation simply returns the simplified classname with first letter lowercase.<br/>
	 * Beans whose ID's doesn't work with that pattern must overwrite this method.
	 * 
	 * @return bean id as String
	 */
	public String getBeanId() {
		if (this.beanId == null) {
			this.beanId = getBeanId(this.getClass());
		}
		return this.beanId;
	}

	/**
	 * The YFrame identifier is the ManagedBean identifier for the faces-config. It is the name of the
	 * class, first char as lowercase.
	 * 
	 * @param clazz
	 *          Frame class
	 * @return YFrame ID
	 */
	private static String getBeanId(final Class<? extends YManagedBean> clazz) {
		String result = mbeanIdMap.get(clazz);
		if (result == null) {
			final String prefix = String.valueOf(clazz.getSimpleName().charAt(0)).toLowerCase();
			final String suffix = clazz.getSimpleName().substring(1);
			result = prefix + suffix;
			log.debug("Initial MBean ID detection for " + clazz.getSimpleName() + ": " + result);
			mbeanIdMap.put((Class) clazz, result);
		}
		return result;
	}

	public static <T extends YManagedBean> T getBean(final Class<T> clazz) {
		T result = null;
		try {
			// can't use the ValueExpression way as i don't know the beanid
			// however, its known after instantiation via #getBeanId()
			result = clazz.newInstance();
			result.refreshBeanScope();
		} catch (final Exception e) {
			throw new YFacesException("", e);
		}
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.YManagedBean#refreshBeanScope()
	 */
	public void refreshBeanScope() {
		final String beanId = this.getBeanId();
		final ELContext ctx = FacesContext.getCurrentInstance().getELContext();
		final ValueExpression ve = FacesContext.getCurrentInstance().getApplication()
				.getExpressionFactory().createValueExpression(ctx, "#{" + beanId + "}", YManagedBean.class);
		ve.setValue(ctx, this);
	}

	//	@Override
	//	public boolean equals(Object obj) {
	//		return (obj instanceof YManagedBean)
	//				&& ((YManagedBean) obj).getBeanId().equals(this.getBeanId());
	//	}

	//	@Override
	//	public int hashCode() {
	//		return this.getBeanId().hashCode();
	//	}

	@Override
	public String toString() {
		final String result = super.toString() + "(ManagedBean:" + getBeanId() + ")";
		return result;
	}

	//
	// DeSerialization
	//
	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		this.logId = this.getBeanId();
		if (log.isDebugEnabled()) {
			log.debug("DeSERIALIZE (restore) instance [" + logId + "] (" + this.hashCode() + ")");
		}

		this.refreshBeanScope();
	}

	//
	// Serialization
	//
	private void writeObject(final ObjectOutputStream aOutputStream) throws IOException {
		aOutputStream.defaultWriteObject();
		if (log.isDebugEnabled()) {
			log.debug("SERIALIZE (save) instance [" + logId + "] (" + this.hashCode() + ")");
		}
	}

}
