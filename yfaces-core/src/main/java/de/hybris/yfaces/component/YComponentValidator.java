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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

/**
 * @author Denny Strietzbaum
 */
public class YComponentValidator {
	private static final Logger LOG = Logger.getLogger(YComponentValidator.class);

	// various placeholders for error messages
	// placeholder: specification class
	private static final String PLACEHOLDER_SPECCLASS = "specClass";
	// placeholder: imlementation
	private static final String PLACEHOLDER_IMPLCLASS = "implClass";
	// placeholder: reserved properties (var, id, etc)
	private static final String PLACEHOLDER_PROPERTIES = "properties";

	private YComponentInfoImpl cmpInfo = null;

	public YComponentValidator(final YComponentInfo cmpInfo) {
		this.cmpInfo = (YComponentInfoImpl) cmpInfo;
	}

	/**
	 * Enumeration of possible error state. Supports placeholders whose values are extracted from a
	 * {@link YComponentValidator}
	 */
	public static enum ErrorState {

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
		IMPL_IS_NO_YCMP("Invalid implementation {" + PLACEHOLDER_IMPLCLASS + "}; not an instanceof "
				+ YComponent.class.getSimpleName()),

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

		public static String getFormattedErrorMessage(final Collection<ErrorState> errors,
				final YComponentInfo cmpInfo, final Class<?> customImplClass) {
			String result = null;
			if (!errors.isEmpty()) {
				result = cmpInfo.getId() != null ? cmpInfo.getId() : "";
				for (final ErrorState error : errors) {
					result = result + "," + error.getFormattedErrorMessage(cmpInfo);
				}
			}
			return result;

		}

		private String msg = null;

		private ErrorState(final String msg) {
			this.msg = msg;
		}

		/**
		 * Returns a formatted error message based on the passed {@link YComponentValidator}
		 * 
		 * @param cmpInfo
		 *          {@link YComponentValidator}
		 * @return String
		 */
		public String getFormattedErrorMessage(final YComponentInfo cmpInfo) {
			return this.getFormattedErrorMessage(cmpInfo, null);
		}

		/**
		 * Returns a formatted error message based on the passed {@link YComponentValidator}. An
		 * optional custom implementation is used instead of
		 * {@link YComponentValidator#getImplementation()}
		 * 
		 * @param cmpInfo
		 *          {@link YComponentValidator}
		 * @param customImplClass
		 *          Class
		 * @return String
		 */
		public String getFormattedErrorMessage(final YComponentInfo cmpInfo,
				final Class<?> customImplClass) {
			String result = "";
			final String msg = this.msg;

			final Matcher parameter = placeHolderPattern.matcher(msg);
			int start = 0;
			while (parameter.find()) {
				String param = parameter.group(1);
				if (param.equals(PLACEHOLDER_SPECCLASS)) {
					param = cmpInfo.getSpecification();
				} else if (param.equals(PLACEHOLDER_IMPLCLASS)) {
					param = (customImplClass == null) ? cmpInfo.getImplementation() : customImplClass
							.getName();
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

	// all errors which are detected after a verification
	private Set<ErrorState> errors = null;

	public YComponentInfo getYComponentInfo() {
		return this.cmpInfo;
	}

	/**
	 * Verifies the component.<br/>
	 * Checks for specification, implementation, non-duplicate 'id' and 'var' values.<br/>
	 * 
	 */
	public Set<ErrorState> verifyComponent() {
		if (this.errors == null) {
			this.errors = EnumSet.noneOf(ErrorState.class);
			assertImplementationClassName();
			assertSpecificationClassName();
			assertIdAndVarName();

			this.assertSpecificationClass();
			this.assertImplementationClass();

			this.assertProperties();

			//			if (this.implClass != null) {
			//				this.refreshWritableProperties();
			//			}
		}
		return Collections.unmodifiableSet(this.errors);
	}

	private boolean assertSpecificationClassName() {
		final String specClassName = cmpInfo.getSpecification();
		if (specClassName == null || specClassName.trim().length() == 0) {
			this.errors.add(ErrorState.SPEC_IS_MISSING);
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
		final String implClassName = cmpInfo.getImplementation();
		if (implClassName == null || implClassName.trim().length() == 0) {
			this.errors.add(ErrorState.IMPL_IS_MISSING);
		}
		return this.errors.isEmpty();
	}

	/**
	 * Asserts whether an ID and VAR value is available.
	 * 
	 * @return true when assertion succeeds
	 */
	private boolean assertIdAndVarName() {
		final String id = cmpInfo.getId();
		final String cmpVar = cmpInfo.getVariableName();

		if (id == null || id.trim().length() == 0) {
			this.errors.add(ErrorState.VIEW_ID_NOT_SPECIFIED);
		}

		if (cmpVar == null || cmpVar.trim().length() == 0) {
			this.errors.add(ErrorState.VIEW_VAR_NOT_SPECIFIED);
		}

		return this.errors.isEmpty();
	}

	private boolean assertSpecificationClass() {
		Class<?> specClass = cmpInfo.specClass;
		if (specClass == null && !this.errors.contains(ErrorState.SPEC_IS_MISSING)) {
			specClass = getClass(cmpInfo.getSpecification(), ErrorState.SPEC_NOT_LOADABLE);
			cmpInfo.specClass = specClass;
		}

		if (specClass != null) {
			if (!specClass.isInterface()) {
				this.errors.add(ErrorState.SPEC_IS_NO_INTERFACE);
			}

			if (!YComponent.class.isAssignableFrom(specClass)) {
				this.errors.add(ErrorState.SPEC_IS_NO_YCMP);
			}
		}
		return this.errors.isEmpty();
	}

	private void assertImplementationClass() {
		Class<?> implClass = cmpInfo.implClass;
		if (implClass == null && !this.errors.contains(ErrorState.IMPL_IS_MISSING)) {
			implClass = getClass(cmpInfo.getImplementation(), ErrorState.IMPL_NOT_LOADABLE);
			cmpInfo.implClass = implClass;
		}

		if (implClass != null) {
			final Set<ErrorState> _implErrors = this.assertCustomImplementationClass(implClass);

			if (!_implErrors.isEmpty()) {
				this.errors.addAll(_implErrors);
			}
		}
	}

	/**
	 * Asserts the passed class whether it can be used with this component configuration.<br/>
	 * 
	 * Possible verification errors are:<br/> {@link ErrorState#IMPL_IS_INTERFACE}<br/>
	 * {@link ErrorState#IMPL_IS_NO_YCMP}<br/> {@link ErrorState#IMPL_UNASSIGNABLE_TO_SPEC}<br/>
	 * 
	 * @param implClass
	 * @return result
	 */
	public Set<ErrorState> assertCustomImplementationClass(final Class<?> implClass) {
		final Set<ErrorState> result = EnumSet.noneOf(ErrorState.class);

		if (implClass.isInterface()) {
			result.add(ErrorState.IMPL_IS_INTERFACE);
		}

		if (!YComponent.class.isAssignableFrom(implClass)) {
			result.add(ErrorState.IMPL_IS_NO_YCMP);
		}

		final Class<?> specClass = cmpInfo.specClass;
		if (specClass != null && !specClass.isAssignableFrom(implClass)) {
			result.add(ErrorState.IMPL_UNASSIGNABLE_TO_SPEC);
		}
		return result;

	}

	private boolean assertProperties() {
		for (final String s : RESERVED_PROPERTY_NAMES) {
			// if (this.writableProperties.containsKey(s))
			// this.errors.add(ERROR_STATE.FORBIDDEN_PROPERTY);

			if (cmpInfo.getPushProperties().contains(s)) {
				this.errors.add(ErrorState.VIEW_INJECTABLE_PROP_FORBIDDEN);
			}
		}
		return this.errors.isEmpty();
	}

	private <T> Class<T> getClass(final String classname, final ErrorState catchError) {
		Class<T> result = null;
		try {
			//result = (Class<T>) Class.forName(classname);
			result = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(classname);
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

}