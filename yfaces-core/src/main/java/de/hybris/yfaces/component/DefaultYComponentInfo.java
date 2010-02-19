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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * Holds {@link YComponent} specific meta information.
 * 
 * @author Denny Strietzbaum
 */
public class DefaultYComponentInfo implements YComponentInfo {
	private static final Logger LOG = Logger.getLogger(DefaultYComponentInfo.class);

	// raw (unevaluated) attribute values
	private String id = null;
	private String cmpVar = null;
	private String specClassName = null;
	private String implClassName = null;

	// Properties which gets evaluated and injected; specified by renderer
	private Set<String> pushProperties = Collections.emptySet();

	private String cmpName = null;
	private String uid = null;

	private String namespace = null;
	private URL url = null;
	private String location = null;

	protected Class<?> implClass = null;
	protected Class<?> specClass = null;

	protected DefaultYComponentInfo() {
		this.pushProperties = Collections.emptySet();
	}

	/**
	 * Constructor. Initializes this instance by parsing the url stream.
	 */
	public DefaultYComponentInfo(final String id, final String varName, final String specClassname,
			final String implClassName) {
		this.id = id;
		this.cmpVar = varName;
		this.specClassName = specClassname;
		this.implClassName = implClassName;
		this.pushProperties = Collections.emptySet();
	}

	public DefaultYComponentInfo(final String namespace, final URL url) {
		this.namespace = namespace;
		this.url = url;
	}

	/**
	 * Returns the classname of the interface/ specification class.
	 * 
	 * @return classname
	 */
	public String getSpecification() {
		return this.specClassName;
	}

	/**
	 * Sets the classname of the interface/specification class.<br/>
	 * Nullifies an already (optional) created instance of the implementation class.<br/>
	 * Does no verification at all.<br/>
	 * 
	 * @param className
	 *          classname
	 */
	public void setSpecification(String className) {
		if (className != null && (className = className.trim()).length() == 0) {
			className = null;
		}

		if (className == null || !className.equals(this.specClassName)) {
			this.specClassName = className;
			this.specClass = null;
		}

	}

	/**
	 * Returns the classname of the implementation class.
	 * 
	 * @return classname
	 */
	public String getImplementation() {
		return this.implClassName;
	}

	/**
	 * Sets the classname of the implementation class.
	 * 
	 * @param className
	 */
	public void setImplementation(String className) {
		if (className != null && (className = className.trim()).length() == 0) {
			className = null;
		}

		if (className == null || !className.equals(this.specClassName)) {
			this.implClassName = className;
			this.implClass = null;
		}

	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getVariableName() {
		return this.cmpVar;
	}

	public void setVariableName(final String varName) {
		this.cmpVar = varName;
	}

	public Collection<String> getPushProperties() {
		return this.pushProperties;
	}

	public void addPushProperty(final String property) {
		if (this.pushProperties == Collections.EMPTY_SET) {
			this.pushProperties = new TreeSet<String>();
		}
		this.pushProperties.add(property);
	}

	public void setPushProperties(final String properties) {
		if (properties != null) {
			final String[] props = properties.trim().split("\\s*,\\s*");
			this.setPushProperties(Arrays.asList(props));
		}
	}

	public void setPushProperties(final Collection<String> properties) {
		for (final String property : properties) {
			this.addPushProperty(property);
		}
	}

	public void setName(final String name) {
		this.cmpName = name;
	}

	public String getName() {
		return cmpName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(final String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		final String result = this.url.toExternalForm();
		return result;
	}

	public URL getURL() {
		return url;
	}

	protected void setURL(final URL url) {
		this.url = url;
	}

	public String getNamespace() {
		return this.namespace;
	}

	protected void setNamespace(final String ns) {
		this.namespace = ns;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(final String uid) {
		this.uid = uid;
	}

	@Override
	public boolean equals(final Object obj) {
		return this.url.toExternalForm().equals(((DefaultYComponentInfo) obj).url.toExternalForm());
	}

	@Override
	public int hashCode() {
		return this.url.toExternalForm().hashCode();
	}

	/**
	 * Returns the implementation class.
	 * 
	 * @return
	 */
	public Class<?> getImplementationClass() {

		// lazy check whether class is already loaded 
		if (this.implClass == null) {
			try {
				// if not just load without any validity check
				this.implClass = Thread.currentThread().getContextClassLoader().loadClass(
						this.implClassName);
			} catch (final Exception e) {
				throw new YFacesException("Error loading component imlementation class: "
						+ this.implClassName, e);
			}
		}
		return this.implClass;
	}

	/**
	 * Creates and returns an {@link YComponent} instance based on this information.
	 * 
	 * @return {@link YComponent}
	 */
	public YComponent createComponent() {
		YComponent result = null;
		try {
			result = (YComponent) this.getImplementationClass().newInstance();
			//((AbstractYComponent) result).setId(this.id);
			((AbstractYComponent) result).setYComponentInfo(this);
		} catch (final Exception e) {
			throw new YFacesException("Can't create " + YComponent.class.getName() + " instance ("
					+ implClass + ")", e);
		}
		return result;
	}

	public YComponentValidator createValidator() {
		return new YComponentValidator(this);
	}

	// Class to Methods lookup map.
	private static Map<String, Map<String, Method>> classToPropertiesMap = new HashMap<String, Map<String, Method>>();

	// runtime detected properties
	// Properties which can be injected; specified by component class
	private Map<String, Method> availableCmpProperties = null;

	public Map<String, Method> getAllProperties() {
		// refresh injectable properties
		this.availableCmpProperties = classToPropertiesMap.get(this.implClassName);
		if (this.availableCmpProperties == null) {
			this.availableCmpProperties = this
					.findAllWriteProperties(implClass, AbstractYComponent.class);
			classToPropertiesMap.put(implClassName, this.availableCmpProperties);
		}
		return this.availableCmpProperties;
	}

	//	public void pushProperty(final YComponent cmp, final String property, Object value) {
	//		final Method method = getAllProperties().get(property);
	//
	//		try {
	//
	//			// JSF 1.2: do type coercion (e.g. String->Integer)
	//			value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
	//					.coerceToType(value, method.getParameterTypes()[0]);
	//
	//			// invoke setter
	//			method.invoke(cmp, value);
	//
	//		} catch (final Exception e) {
	//			if (e instanceof IllegalArgumentException) {
	//				LOG.error(this.id + " Error converting " + value.getClass().getName() + " to "
	//						+ method.getParameterTypes()[0].getName());
	//			} else {
	//				if (e instanceof InvocationTargetException) {
	//					LOG.error(id + " Error while executing setter for attribute '" + property + "'");
	//				}
	//			}
	//			final String error = id + " Error setting attribute '" + property + "' at "
	//					+ cmp.getClass().getSimpleName() + "(" + method + ")";
	//			throw new YFacesException(error, e);
	//		}
	//
	//		// some nice debug output for bughunting
	//		if (LOG.isDebugEnabled()) {
	//			final String _value = (value != null) ? value.toString() : "null";
	//			String suffix = "";
	//			if (value instanceof Collection<?>) {
	//				suffix = "(count:" + ((Collection<?>) value).size() + ")";
	//			}
	//
	//			LOG.debug(id + "injected Attribute " + property + " ("
	//					+ (_value.length() < 30 ? _value : _value.substring(0, 29).concat("...")) + ")" + suffix);
	//		}
	//
	//	}

	private Map<String, Method> findAllWriteProperties(final Class<?> startClass,
			final Class<?> endClass) {

		final Map<String, Method> result = new HashMap<String, Method>();
		try {
			// find setter for attributes
			final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(startClass,
					AbstractYComponent.class).getPropertyDescriptors();

			for (final PropertyDescriptor descriptor : descriptors) {
				final String name = descriptor.getName();
				final Method writeMethod = descriptor.getWriteMethod();

				if (writeMethod != null) {
					result.put(name, writeMethod);
				}

				if (LOG.isDebugEnabled()) {
					if (writeMethod != null) {
						LOG.debug(this.id + " add property " + descriptor.getName() + " ("
								+ descriptor.getWriteMethod() + ")");
					} else {
						LOG.debug(this.id + " skip property " + name + " (read only)");
					}
				}
			}
		} catch (final IntrospectionException e) {
			e.printStackTrace();
		}
		return result;
	}

}