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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesConfig;

/**
 * Holds {@link YModel} specific meta information.
 * 
 * @author Denny Strietzbaum
 */
public class YComponentImpl implements YComponent {
	private static final Logger log = Logger.getLogger(YComponentImpl.class);

	// raw (unevaluated) attribute values
	private String id = null;
	private String cmpVar = null;
	private String errorHandling = null;

	private Set<String> pushProperties = Collections.emptySet();

	private String cmpName = null;
	private String uid = null;

	private String namespace = null;
	private URL url = null;
	private String location = null;

	// according configuration of model and default an impl class and optionally a spec can be loaded
	private Class<?> modelSpecClass = null;
	private Class<?> modelImplClass = null;

	private boolean isValid = false;
	private boolean isYComponent = false;

	private ModelProcessor modelProcessor = null;
	private YComponentConfigurationImpl cmpCfg = null;

	protected YComponentImpl() {
		this.pushProperties = Collections.emptySet();
		this.cmpCfg = new YComponentConfigurationImpl(this);
	}

	/**
	 * Constructor. Initializes this instance by parsing the url stream.
	 */
	public YComponentImpl(final String id, final String varName, final String modelSpecClass,
			final String modelClass) {
		this();
		cmpCfg.setId(id);
		cmpCfg.setVariableName(varName);
		cmpCfg.setModelSpecification(modelSpecClass);
		cmpCfg.setModelImplementation(modelClass);
	}

	public YComponentImpl(final String namespace, final URL url) {
		this();
		this.namespace = namespace;
		this.url = url;
	}

	public YComponentConfiguration getConfiguration() {
		return this.cmpCfg;
	}

	public String getId() {
		return this.id;
	}

	//	public void setId(final String id) {
	//		this.id = id;
	//	}

	public String getVariableName() {
		return this.cmpVar;
	}

	//	public void setVariableName(final String varName) {
	//		this.cmpVar = varName;
	//	}

	public String getErrorHandling() {
		return errorHandling;
	}

	//	public void setErrorHandling(final String errorHandling) {
	//		this.errorHandling = errorHandling;
	//	}

	public Collection<String> getPushProperties() {
		return this.pushProperties;
	}

	//	public void addPushProperty(final String property) {
	//		if (this.pushProperties == Collections.EMPTY_SET) {
	//			this.pushProperties = new TreeSet<String>();
	//		}
	//		this.pushProperties.add(property);
	//	}
	//
	//	public void setPushProperties(final String properties) {
	//		if (properties != null) {
	//			final String[] props = properties.trim().split("\\s*,\\s*");
	//			this.setPushProperties(Arrays.asList(props));
	//		}
	//	}
	//
	//	public void setPushProperties(final Collection<String> properties) {
	//		for (final String property : properties) {
	//			this.addPushProperty(property);
	//		}
	//	}

	public void setName(final String name) {
		this.cmpName = name;
	}

	public String getName() {
		return cmpName;
	}

	public String getViewLocation() {
		return location;
	}

	public void setViewLocation(final String location) {
		this.location = location;
	}

	@Override
	public String toString() {
		final String result = this.url.toExternalForm();
		return result;
	}

	public URL getViewURL() {
		return url;
	}

	protected void setViewURL(final URL url) {
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
		return this.url.toExternalForm().equals(((YComponentImpl) obj).url.toExternalForm());
	}

	@Override
	public int hashCode() {
		return this.url.toExternalForm().hashCode();
	}

	public boolean isValidated() {
		return isValid;
	}

	public void setValid(final boolean isValid) {
		this.isValid = isValid;
	}

	public boolean isYComponent() {
		return isYComponent;
	}

	public Class getModelSpecification() {
		return this.modelSpecClass;
	}

	protected void setModelSpecification(final Class spec) {
		this.modelSpecClass = spec;
	}

	public Class<?> getModelImplementation() {
		return this.modelImplClass;
	}

	protected void setModelImplementation(final Class impl) {
		this.modelImplClass = impl;
	}

	public YComponentValidator createValidator() {
		final YComponentValidator result = this.isYComponent ? new YComponentValidatorImpl(this)
				: new PojoComponentValidator(this);
		return result;
	}

	public ModelProcessor getModelProcessor() {
		return this.modelProcessor;
	}

	/**
	 * Initializes missing values when possible. Goes no validation, swallows any error which may
	 * occur.
	 */
	public void initialize() {

		//configure id 
		this.id = cmpCfg.getId();
		if (isEmpty(this.id)) {
			this.id = this.uid;
			if (log.isDebugEnabled()) {
				log.debug(this.cmpName + ": set missing '" + YComponentConfiguration.ID_ATTRIBUTE + "' to "
						+ this.uid);
			}
		}

		// configure varname
		this.cmpVar = cmpCfg.getVariableName();

		//configure errorhandling
		this.errorHandling = cmpCfg.getErrorHandling();
		if (isEmpty(this.errorHandling)) {
			this.errorHandling = YFacesConfig.CMP_ERROR_HANDLING.getString();
			if (log.isDebugEnabled()) {
				log.debug(this.cmpName + ": set missing '" + YComponentConfiguration.ERROR_ATTRIBUTE
						+ "' to " + this.errorHandling);
			}
		}

		// configure push properties
		final String props = cmpCfg.getPushProperties();
		if (!isEmpty(props)) {
			final String[] propsList = cmpCfg.getPushProperties().trim().split("\\s*,\\s*");
			this.pushProperties = new LinkedHashSet<String>(Arrays.asList(propsList));
		}

		// load spec and impl class
		final ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try {
			if (cmpCfg.getModelSpecification() != null) {
				this.modelSpecClass = loader.loadClass(cmpCfg.getModelSpecification());
				if (modelSpecClass != null && YModel.class.isAssignableFrom(modelSpecClass)) {
					this.isYComponent = true;
				}
			}
		} catch (final Exception e) {
			//NOP, gets handled by a Validator
		}
		try {
			if (cmpCfg.getModelImplementation() != null) {
				this.modelImplClass = loader.loadClass(cmpCfg.getModelImplementation());

				if (modelSpecClass == null && modelImplClass != null
						&& YModel.class.isAssignableFrom(modelImplClass)) {
					this.isYComponent = true;
				}
			}
		} catch (final Exception e) {
			//NOP, gets handled by a Validator
		}
		if (log.isDebugEnabled()) {
			log.debug(this.cmpName + ": model is declared as "
					+ (isYComponent ? YModel.class.getSimpleName() : "PoJo component"));
		}

		this.modelProcessor = this.isYComponent ? new YModelProcessor(this) : new PojoModelProcessor(
				this);

	}

	private boolean isEmpty(final String value) {
		return value == null || value.trim().length() == 0;
	}

	// Class to Methods lookup map.
	private static Map<Class, Map<String, Method>> classToPropertiesMap = new HashMap<Class, Map<String, Method>>();

	// runtime detected properties
	// Properties which can be injected; specified by component class
	private Map<String, Method> availableCmpProperties = null;

	public Map<String, Method> getAllProperties() {
		// refresh injectable properties
		this.availableCmpProperties = classToPropertiesMap.get(this.modelImplClass);
		if (this.availableCmpProperties == null) {
			this.availableCmpProperties = this.findAllWriteProperties(modelImplClass,
					AbstractYModel.class);
			classToPropertiesMap.put(modelImplClass, this.availableCmpProperties);
		}
		return this.availableCmpProperties;
	}

	private Map<String, Method> findAllWriteProperties(final Class<?> startClass,
			final Class<?> endClass) {

		final Map<String, Method> result = new HashMap<String, Method>();
		try {
			// find setter for attributes
			final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(startClass, Object.class)
					.getPropertyDescriptors();

			for (final PropertyDescriptor descriptor : descriptors) {
				final String name = descriptor.getName();
				final Method writeMethod = descriptor.getWriteMethod();

				if (writeMethod != null) {
					result.put(name, writeMethod);
				}

				if (log.isDebugEnabled()) {
					if (writeMethod != null) {
						log.debug(this.id + " add property " + descriptor.getName() + " ("
								+ descriptor.getWriteMethod() + ")");
					} else {
						log.debug(this.id + " skip property " + name + " (read only)");
					}
				}
			}
		} catch (final IntrospectionException e) {
			e.printStackTrace();
		}
		return result;
	}

}