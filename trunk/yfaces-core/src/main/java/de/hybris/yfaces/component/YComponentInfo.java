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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * Holds {@link YComponent} specific meta information.
 * 
 * @author Denny.Strietzbaum
 */
public class YComponentInfo {
	private static final Logger log = Logger.getLogger(YComponentInfo.class);

	// various placeholders for error messages
	// placeholder: specification class
	private static final String PLACEHOLDER_SPECCLASS = "specClass";
	// placeholder: imlementation
	private static final String PLACEHOLDER_IMPLCLASS = "implClass";
	// placeholder: reserved properties (var, id, etc)
	private static final String PLACEHOLDER_PROPERTIES = "properties";

	/**
	 * Enumeration of possible error state. Supports placeholders whose values are extracted from a
	 * {@link YComponentInfo}
	 */
	public static enum ERROR_STATE {

		/** When no component interface (specification) is provided */
		SPEC_IS_MISSING("No specification specified"),

		/** When specification is not an interface */
		SPEC_IS_NO_INTERFACE("Invalid specification {" + PLACEHOLDER_SPECCLASS + "} - no interface"),

		/** When specification is not an YComponent */
		SPEC_IS_NO_YCMP("Invalid specification {" + PLACEHOLDER_SPECCLASS + "} - no "
				+ YComponent.class.getSimpleName()),

		/** When specification is not loadable (classnotfound etc.) */
		SPEC_NOT_LOADABLE("Can't load specification class {" + PLACEHOLDER_SPECCLASS + "}"),

		/** When no component class (implementation) is provided */
		IMPL_IS_MISSING("No implementation specified"),

		/** Implementation is no implementation (but interface) */
		IMPL_IS_INTERFACE("Invalid implementation; got interface but no class"),

		/** When implementation is not an YComponent */
		IMPL_IS_NO_YCMP("Invalid implementation {" + PLACEHOLDER_IMPLCLASS
				+ "}; not an instanceof " + YComponent.class.getSimpleName()),

		/** When implementation doesn't match specification */
		IMPL_UNASSIGNABLE_TO_SPEC("Invalid implementation {" + PLACEHOLDER_IMPLCLASS
				+ "}; not an instance of {" + PLACEHOLDER_SPECCLASS + "}"),

		/** When implementation is not loadable (classnotfound etc.) */
		IMPL_NOT_LOADABLE("Can't load implementation class {" + PLACEHOLDER_IMPLCLASS + "}"),

		/** When no component id attribute is specified */
		VIEW_ID_NOT_SPECIFIED("No ID specified"),

		/** When no component var attribute is specified */
		VIEW_VAR_NOT_SPECIFIED("No VAR specified"),

		/** When a reserved attribute is marked as being injectable */
		VIEW_INJECTABLE_PROP_FORBIDDEN("Found reserved property; one of {" + PLACEHOLDER_PROPERTIES
				+ "}"), ;

		// Pattern for detecting placeholders
		private static final Pattern placeHolderPattern = Pattern.compile("\\{(.*?)\\}");

		public static String getFormattedErrorMessage(final Collection<ERROR_STATE> errors,
				final YComponentInfo cmpInfo, final Class customImplClass) {
			String result = null;
			if (!errors.isEmpty()) {
				result = cmpInfo.getId() != null ? cmpInfo.getId() : "";
				for (final ERROR_STATE error : errors) {
					result = result + "," + error.getFormattedErrorMessage(cmpInfo);
				}
			}
			return result;

		}

		private String msg = null;

		private ERROR_STATE(final String msg) {
			this.msg = msg;
		}

		/**
		 * Returns a formatted error message based on the passed {@link YComponentInfo}
		 * 
		 * @param cmpInfo
		 *            {@link YComponentInfo}
		 * @return String
		 */
		public String getFormattedErrorMessage(final YComponentInfo cmpInfo) {
			return this.getFormattedErrorMessage(cmpInfo, null);
		}

		/**
		 * Returns a formatted error message based on the passed {@link YComponentInfo}. An optional
		 * custom implementation is used instead of
		 * {@link YComponentInfo#getImplementationClassName()}
		 * 
		 * @param cmpInfo
		 *            {@link YComponentInfo}
		 * @param customImplClass
		 *            Class
		 * @return String
		 */
		public String getFormattedErrorMessage(final YComponentInfo cmpInfo,
				final Class customImplClass) {
			String result = "";
			final String msg = this.msg;

			final Matcher parameter = placeHolderPattern.matcher(msg);
			int start = 0;
			while (parameter.find()) {
				String param = parameter.group(1);
				if (param.equals(PLACEHOLDER_SPECCLASS)) {
					param = cmpInfo.getSpecificationClassName();
				} else if (param.equals(PLACEHOLDER_IMPLCLASS)) {
					param = (customImplClass == null) ? cmpInfo.getImplementationClassName()
							: customImplClass.getName();
				} else if (param.equals(PLACEHOLDER_PROPERTIES)) {
					param = Arrays.asList(RESERVED_PROPERTY_NAMES).toString();
				}

				result = result + msg.substring(start, parameter.start()) + param;
				start = parameter.end();
			}

			result = result + msg.substring(start);

			return result;
		}

	}

	// reserved (forbidden) attributes;
	// their usage is not allowed as injectable component parameter
	private static final String[] RESERVED_PROPERTY_NAMES = new String[] { "facet" };

	// Class to Methods lookup map.
	private static Map<String, Map<String, Method>> classToPropertiesMap = new HashMap<String, Map<String, Method>>();

	//
	// Members
	//

	// raw (unevaluated) attribute values
	private String id = null;
	private String cmpVar = null;
	private String specClassName = null;
	private String implClassName = null;

	// Properties which gets evaluated and injected; specified by renderer
	private Set<String> injectableAttributes = Collections.EMPTY_SET;

	// Properties which can be injected; specified by component class
	private Map<String, Method> writableProperties = null;

	// instance of specification class
	private Class specClass = null;

	// instance of implementation class
	private Class implClass = null;

	private String cmpName = null;

	private String namespace = null;
	private URL url = null;

	// all errors which are detected after a verification
	private Set<ERROR_STATE> errors = null;

	protected YComponentInfo() {
		this.injectableAttributes = Collections.EMPTY_SET;
	}

	/**
	 * Constructor. Initializes this instance by parsing the url stream.
	 */
	public YComponentInfo(final String id, final String varName, final String specClassname,
			final String implClassName) {
		this.id = id;
		this.cmpVar = varName;
		this.specClassName = specClassname;
		this.implClassName = implClassName;
		this.injectableAttributes = Collections.EMPTY_SET;
	}

	public YComponentInfo(String namespace, URL url) {
		this.namespace = namespace;
		this.url = url;
	}

	/**
	 * Verifies the component.<br/>
	 * Checks for specification, implementation, non-duplicate 'id' and 'var' values.<br/>
	 * 
	 */
	public Set<ERROR_STATE> verifyComponent() {
		if (this.errors == null) {
			this.errors = EnumSet.noneOf(ERROR_STATE.class);
			assertImplementationClassName();
			assertSpecificationClassName();
			assertIdAndVarName();

			this.assertSpecificationClass();
			this.assertImplementationClass();

			this.assertProperties();

			if (this.implClass != null) {
				this.refreshWritableProperties();
			}
		}
		return Collections.unmodifiableSet(this.errors);
	}

	/**
	 * Returns the classname of the interface/ specification class.
	 * 
	 * @return classname
	 */
	public String getSpecificationClassName() {
		return this.specClassName;
	}

	/**
	 * Sets the classname of the interface/specification class.<br/>
	 * Nullifies an already (optional) created instance of the implementation class.<br/>
	 * Does no verification at all.<br/>
	 * 
	 * @param className
	 *            classname
	 */
	protected void setSpecificationClassName(final String className) {
		final String newClass = (className == null || className.trim().length() == 0) ? null
				: className;
		if (newClass != null && !newClass.equals(this.specClassName)) {
			this.specClassName = newClass;
			this.implClass = null;
		}
	}

	/**
	 * Returns the classname of the implementation class.
	 * 
	 * @return classname
	 */
	public String getImplementationClassName() {
		return this.implClassName;
	}

	/**
	 * Sets the classname of the implementation class.
	 * 
	 * @param className
	 */
	protected void setImplementationClassName(final String className) {
		final String newClass = (className == null || className.trim().length() == 0) ? null
				: className;
		if (newClass != null && !newClass.equals(this.implClassName)) {
			this.implClassName = newClass;
			this.implClass = null;
		}
	}

	public String getId() {
		return this.id;
	}

	protected void setId(final String id) {
		this.id = id;
	}

	public String getVarName() {
		return this.cmpVar;
	}

	protected void setVarName(final String varName) {
		this.cmpVar = varName;
	}

	public Class getSpecificationClass() {
		return this.specClass;
	}

	public Class getImplementationClass() {
		return this.implClass;
	}

	public Map<String, Method> getAllComponentProperties() {
		return this.writableProperties;
	}

	public Collection<String> getInjectableProperties() {
		return this.injectableAttributes;
	}

	protected void addInjectableProperty(final String property) {
		if (this.injectableAttributes == Collections.EMPTY_SET) {
			this.injectableAttributes = new HashSet<String>();
		}
		this.injectableAttributes.add(property);
	}

	protected void addInjectableProperties(final String... properties) {
		for (final String property : properties) {
			this.addInjectableProperty(property);
		}
	}

	/**
	 * Creates and returns an {@link YComponent} instance based on this information.
	 * 
	 * @return {@link YComponent}
	 */
	public YComponent createDefaultComponent() {
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

	private void refreshWritableProperties() {
		// refresh injectable properties
		this.writableProperties = classToPropertiesMap.get(this.implClassName);
		if (this.writableProperties == null) {
			this.writableProperties = this.findWriteableProperties(implClass,
					AbstractYComponent.class);
			classToPropertiesMap.put(this.implClassName, this.writableProperties);
		}
	}

	private Map<String, Method> findWriteableProperties(final Class startClass, final Class endClass) {

		final Map<String, Method> result = new HashMap<String, Method>();
		try {
			// find setter for attributes
			PropertyDescriptor[] descriptors = Introspector.getBeanInfo(startClass,
					AbstractYComponent.class).getPropertyDescriptors();

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

	private boolean assertSpecificationClassName() {
		if (specClassName == null || specClassName.trim().length() == 0) {
			this.errors.add(ERROR_STATE.SPEC_IS_MISSING);
		}
		return this.errors.isEmpty();
	}

	/**
	 * Asserts whether an implementation classname is available.<br/>
	 * Does not assert whether class can be loaded.
	 * 
	 * @return true when assertion succeeds
	 */
	private boolean assertImplementationClassName() {
		if (implClassName == null || implClassName.trim().length() == 0) {
			this.errors.add(ERROR_STATE.IMPL_IS_MISSING);
		}
		return this.errors.isEmpty();
	}

	/**
	 * Asserts whether an ID and VAR value is available.
	 * 
	 * @return true when assertion succeeds
	 */
	private boolean assertIdAndVarName() {
		if (id == null || id.trim().length() == 0) {
			this.errors.add(ERROR_STATE.VIEW_ID_NOT_SPECIFIED);
		}

		if (cmpVar == null || cmpVar.trim().length() == 0) {
			this.errors.add(ERROR_STATE.VIEW_VAR_NOT_SPECIFIED);
		}

		return this.errors.isEmpty();
	}

	private boolean assertSpecificationClass() {
		if (this.specClass == null && !this.errors.contains(ERROR_STATE.SPEC_IS_MISSING)) {
			this.specClass = getClass(this.specClassName, ERROR_STATE.SPEC_NOT_LOADABLE);
		}

		if (specClass != null) {
			if (!specClass.isInterface()) {
				this.errors.add(ERROR_STATE.SPEC_IS_NO_INTERFACE);
			}

			if (!YComponent.class.isAssignableFrom(specClass)) {
				this.errors.add(ERROR_STATE.SPEC_IS_NO_YCMP);
			}
		}
		return this.errors.isEmpty();
	}

	private void assertImplementationClass() {
		if (this.implClass == null && !this.errors.contains(ERROR_STATE.IMPL_IS_MISSING)) {
			this.implClass = getClass(this.implClassName, ERROR_STATE.IMPL_NOT_LOADABLE);
		}

		if (implClass != null) {
			final Set<ERROR_STATE> _implErrors = this.assertCustomImplementationClass(implClass);

			if (!_implErrors.isEmpty()) {
				this.implClass = null;
				this.errors.addAll(_implErrors);
			}
		}
	}

	/**
	 * Asserts the passed class whether it can be used with this component configuration.<br/>
	 * 
	 * Possible verification errors are:<br/> {@link ERROR_STATE#IMPL_IS_INTERFACE}<br/>
	 * {@link ERROR_STATE#IMPL_IS_NO_YCMP}<br/> {@link ERROR_STATE#IMPL_UNASSIGNABLE_TO_SPEC}<br/>
	 * 
	 * @param implClass
	 * @return result
	 */
	public Set<ERROR_STATE> assertCustomImplementationClass(final Class implClass) {
		final Set<ERROR_STATE> result = EnumSet.noneOf(ERROR_STATE.class);

		if (implClass.isInterface()) {
			result.add(ERROR_STATE.IMPL_IS_INTERFACE);
		}

		if (!YComponent.class.isAssignableFrom(implClass)) {
			result.add(ERROR_STATE.IMPL_IS_NO_YCMP);
		}

		if (specClass != null && !specClass.isAssignableFrom(implClass)) {
			result.add(ERROR_STATE.IMPL_UNASSIGNABLE_TO_SPEC);
		}
		return result;

	}

	private boolean assertProperties() {
		for (final String s : RESERVED_PROPERTY_NAMES) {
			// if (this.writableProperties.containsKey(s))
			// this.errors.add(ERROR_STATE.FORBIDDEN_PROPERTY);

			if (this.injectableAttributes.contains(s)) {
				this.errors.add(ERROR_STATE.VIEW_INJECTABLE_PROP_FORBIDDEN);
			}
		}
		return this.errors.isEmpty();
	}

	private <T> Class<T> getClass(final String classname, final ERROR_STATE catchError) {
		Class<T> result = null;
		try {
			result = (Class<T>) Class.forName(classname);
			this.errors.remove(catchError);
		} catch (final Exception e) {
			if (catchError != null) {
				this.errors.add(catchError);
			} else {
				throw new YFacesException("Can't load class " + classname, e);
			}
		}
		return result;
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
		String result = this.url.toExternalForm();
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

	protected void setNamespace(String ns) {
		this.namespace = ns;
	}

	@Override
	public boolean equals(Object obj) {
		return this.url.toExternalForm().equals(((YComponentInfo) obj).url.toExternalForm());
	}

	@Override
	public int hashCode() {
		return this.url.toExternalForm().hashCode();
	}

}