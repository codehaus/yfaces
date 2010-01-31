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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * Holds {@link YComponent} specific meta information.
 * 
 * @author Denny Strietzbaum
 */
public class YComponentInfoImpl implements YComponentInfo {
	private static final Logger LOG = Logger.getLogger(YComponentInfoImpl.class);

	// raw (unevaluated) attribute values
	private String id = null;
	private String cmpVar = null;
	private String specClassName = null;
	private String implClassName = null;

	// Properties which gets evaluated and injected; specified by renderer
	private Set<String> viewProperties = Collections.emptySet();

	private String cmpName = null;

	private String namespace = null;
	private URL url = null;

	protected Class<?> implClass = null;
	protected Class<?> specClass = null;

	protected YComponentInfoImpl() {
		this.viewProperties = Collections.emptySet();
	}

	/**
	 * Constructor. Initializes this instance by parsing the url stream.
	 */
	public YComponentInfoImpl(final String id, final String varName, final String specClassname,
			final String implClassName) {
		this.id = id;
		this.cmpVar = varName;
		this.specClassName = specClassname;
		this.implClassName = implClassName;
		this.viewProperties = Collections.emptySet();
	}

	public YComponentInfoImpl(final String namespace, final URL url) {
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
	protected void setSpecification(final String className) {
		this.specClassName = className;
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
	protected void setImplementation(final String className) {
		this.implClassName = className;
	}

	public String getId() {
		return this.id;
	}

	protected void setId(final String id) {
		this.id = id;
	}

	public String getVariableName() {
		return this.cmpVar;
	}

	protected void setVariableName(final String varName) {
		this.cmpVar = varName;
	}

	public Collection<String> getPushProperties() {
		return this.viewProperties;
	}

	protected void addProperty(final String property) {
		if (this.viewProperties == Collections.EMPTY_SET) {
			this.viewProperties = new HashSet<String>();
		}
		this.viewProperties.add(property);
	}

	protected void addProperties(final String... properties) {
		for (final String property : properties) {
			this.addProperty(property);
		}
	}

	protected void setComponentName(final String name) {
		this.cmpName = name;
	}

	public String getComponentName() {
		return cmpName;
	}

	@Override
	public String toString() {
		//		final String result = "id:" + this.id + "; spec:" + this.specClassName + "; impl:"
		//				+ this.implClassName + "; var:" + this.cmpVar + "; injects:"
		//				+ this.injectableAttributes;
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

	@Override
	public boolean equals(final Object obj) {
		return this.url.toExternalForm().equals(((YComponentInfoImpl) obj).url.toExternalForm());
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
	protected Class<?> getImplementationClass() {

		// lazy check whether class is already loaded 
		if (this.implClass == null) {
			try {
				// if not just load without any validity check
				final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(
						this.implClassName);
				this.implClass = (Class<?>) clazz.newInstance();
			} catch (final Exception e) {
				throw new YFacesException("Error loading component imlementation class: "
						+ this.implClassName);
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
			((AbstractYComponent) result).setId(this.id);
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
			this.availableCmpProperties = this.findWriteableProperties(implClass,
					AbstractYComponent.class);
			classToPropertiesMap.put(implClassName, this.availableCmpProperties);
		}
		return this.availableCmpProperties;
	}

	public void pushProperty(final YComponent cmp, final String property, Object value) {
		final Method method = getAllProperties().get(property);

		try {

			// JSF 1.2: do type coercion (e.g. String->Integer)
			value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
					.coerceToType(value, method.getParameterTypes()[0]);

			// invoke setter
			method.invoke(cmp, value);

		} catch (final Exception e) {
			if (e instanceof IllegalArgumentException) {
				LOG.error(this.id + " Error converting " + value.getClass().getName() + " to "
						+ method.getParameterTypes()[0].getName());
			} else {
				if (e instanceof InvocationTargetException) {
					LOG.error(id + " Error while executing setter for attribute '" + property + "'");
				}
			}
			final String error = id + " Error setting attribute '" + property + "' at "
					+ cmp.getClass().getSimpleName() + "(" + method + ")";
			throw new YFacesException(error, e);
		}

		// some nice debug output for bughunting
		if (LOG.isDebugEnabled()) {
			final String _value = (value != null) ? value.toString() : "null";
			String suffix = "";
			if (value instanceof Collection) {
				suffix = "(count:" + ((Collection<?>) value).size() + ")";
			}

			LOG.debug(id + "injected Attribute " + property + " ("
					+ (_value.length() < 30 ? _value : _value.substring(0, 29).concat("...")) + ")" + suffix);
		}

	}

	private Map<String, Method> findWriteableProperties(final Class<?> startClass,
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