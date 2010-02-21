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
	public static enum YValidationAspekt {

		/** When no component interface (specification) is provided */
		SPEC_IS_MISSING("No model specification specified"),

		/** When specification is not loadable (classnotfound etc.) */
		SPEC_NOT_LOADABLE("Can't load model specification class {" + PLACEHOLDER_SPECCLASS + "}"),

		/** When a specification is used but specification is not an interface */
		SPEC_IS_NO_INTERFACE("Invalid specification {" + PLACEHOLDER_SPECCLASS + "} - no interface"),

		/** When a specification is used, but specification is not an YComponent */
		SPEC_IS_NO_YCMP("Invalid specification {" + PLACEHOLDER_SPECCLASS + "} - no "
				+ YComponent.class.getSimpleName()),

		/** When no component class (implementation) is provided */
		IMPL_IS_MISSING("No default model implementation specified"),

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

		public static String getFormattedErrorMessage(final Collection<YValidationAspekt> errors,
				final YComponentInfo cmpInfo, final Class<?> customImplClass) {
			String result = null;
			if (!errors.isEmpty()) {
				result = cmpInfo.getId() != null ? cmpInfo.getId() : "";
				for (final YValidationAspekt error : errors) {
					result = result + "," + error.getFormattedErrorMessage(cmpInfo);
				}
			}
			return result;

		}

		private String msg = null;

		private YValidationAspekt(final String msg) {
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
		 * {@link YComponentValidator#getModelImplementation()}
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
					param = cmpInfo.getModelSpecification();
				} else if (param.equals(PLACEHOLDER_IMPLCLASS)) {
					param = (customImplClass == null) ? cmpInfo.getModelImplementation() : customImplClass
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
	private Set<YValidationAspekt> foundErrors = null;
	private Set<YValidationAspekt> foundWarnings = null;

	private Set<YValidationAspekt> asWarnings = Collections.EMPTY_SET;

	public YComponentInfo getYComponentInfo() {
		return this.cmpInfo;
	}

	public Set<YValidationAspekt> verifyComponent(final YValidationAspekt... asWarningOnly) {
		this.asWarnings = EnumSet.noneOf(YValidationAspekt.class);
		for (final YValidationAspekt aspect : asWarningOnly) {
			this.asWarnings.add(aspect);
		}
		return verifyComponent();
	}

	/**
	 * Verifies the component.<br/>
	 * Checks for specification, implementation, non-duplicate 'id' and 'var' values.<br/>
	 * 
	 */
	public Set<YValidationAspekt> verifyComponent() {
		this.foundErrors = EnumSet.noneOf(YValidationAspekt.class);
		this.foundWarnings = EnumSet.noneOf(YValidationAspekt.class);

		assertModelSpecification();
		assertModelImplementation();

		assertIdAndVarName();
		assertProperties();

		return this.foundErrors;
	}

	public Set<YValidationAspekt> getValidationErrors() {
		return this.foundErrors;
	}

	public Set<YValidationAspekt> getValidationWarnings() {
		return this.foundWarnings;
	}

	private boolean assertModelSpecification() {
		boolean isValid = assertNotEmpty(cmpInfo.getModelSpecification(),
				YValidationAspekt.SPEC_IS_MISSING);
		if (isValid) {
			final Class modelSpec = assertLoadingClass(cmpInfo.getModelSpecification(),
					YValidationAspekt.SPEC_NOT_LOADABLE);

			if (isValid = (modelSpec != null)) {

				if (!modelSpec.equals(cmpInfo.getModelSpecClass())) {
					throw new IllegalStateException("Model specification class mismatch " + modelSpec
							+ " vs " + cmpInfo.getModelSpecClass());
				}

				// check for interface
				if (!modelSpec.isInterface()) {
					this.addValidationProblem(YValidationAspekt.SPEC_IS_NO_INTERFACE);
					isValid = false;
				}

				// check for YComponent type
				if (!YComponent.class.isAssignableFrom(modelSpec)) {
					this.addValidationProblem(YValidationAspekt.SPEC_IS_NO_YCMP);
					isValid = false;
				}
			}
		}
		return isValid;
	}

	/**
	 * Asserts whether an implementation classname is available.<br/>
	 * Does not assert whether class can be loaded.
	 * 
	 * @return true when assertion succeeds
	 */
	private boolean assertModelImplementation() {
		boolean isValid = assertNotEmpty(cmpInfo.getModelImplementation(),
				YValidationAspekt.IMPL_IS_MISSING);
		if (isValid) {

			final Class modelImpl = assertLoadingClass(cmpInfo.getModelImplementation(),
					YValidationAspekt.IMPL_NOT_LOADABLE);

			if (isValid = (modelImpl != null)) {

				if (!modelImpl.equals(cmpInfo.getModelImplClass())) {
					throw new IllegalStateException("Model implementation class mismatch");
				}

				final Set<YValidationAspekt> _implErrors = this.assertImplementationClass(modelImpl);

				for (final YValidationAspekt problem : _implErrors) {
					this.addValidationProblem(problem);
				}

			}
		}
		return isValid;
	}

	/**
	 * Asserts whether an ID and VAR value is available.
	 * 
	 * @return true when assertion succeeds
	 */
	private boolean assertIdAndVarName() {
		final boolean isValidId = assertNotEmpty(cmpInfo.getId(),
				YValidationAspekt.VIEW_ID_NOT_SPECIFIED);

		final boolean isValidVar = assertNotEmpty(cmpInfo.getVariableName(),
				YValidationAspekt.VIEW_VAR_NOT_SPECIFIED);

		final boolean isValid = isValidId && isValidVar;
		return isValid;

	}

	/**
	 * Asserts the passed class whether it can be used with this component configuration.<br/>
	 * 
	 * Possible verification errors are:<br/> {@link YValidationAspekt#IMPL_IS_INTERFACE}<br/>
	 * {@link YValidationAspekt#IMPL_IS_NO_YCMP}<br/> {@link YValidationAspekt#IMPL_UNASSIGNABLE_TO_SPEC}<br/>
	 * 
	 * @param implClass
	 * @return result
	 */
	public Set<YValidationAspekt> assertImplementationClass(final Class<?> implClass) {
		final Set<YValidationAspekt> result = EnumSet.noneOf(YValidationAspekt.class);

		if (implClass.isInterface()) {
			result.add(YValidationAspekt.IMPL_IS_INTERFACE);
		}

		if (!YComponent.class.isAssignableFrom(implClass)) {
			result.add(YValidationAspekt.IMPL_IS_NO_YCMP);
		}

		final Class<?> specClass = cmpInfo.getModelSpecClass();
		if (specClass != null && !specClass.isAssignableFrom(implClass)) {
			result.add(YValidationAspekt.IMPL_UNASSIGNABLE_TO_SPEC);
		}
		return result;
	}

	private boolean assertProperties() {
		for (final String s : RESERVED_PROPERTY_NAMES) {
			// if (this.writableProperties.containsKey(s))
			// this.errors.add(ERROR_STATE.FORBIDDEN_PROPERTY);

			if (cmpInfo.getPushProperties().contains(s)) {
				this.addValidationProblem(YValidationAspekt.VIEW_INJECTABLE_PROP_FORBIDDEN);
			}
		}
		return this.foundErrors.isEmpty();
	}

	private void addValidationProblem(final YValidationAspekt problem) {
		if (asWarnings.contains(problem)) {
			this.foundWarnings.add(problem);
		} else {
			this.foundErrors.add(problem);
		}
	}

	private boolean assertNotEmpty(final String stringToCheck, final YValidationAspekt errorType) {
		final boolean isValid = !isEmpty(stringToCheck);
		if (!isValid) {
			this.addValidationProblem(errorType);
		}
		return isValid;
	}

	private boolean isEmpty(final String value) {
		return value == null || value.trim().length() == 0;
	}

	private <T> Class<T> assertLoadingClass(final String classname, final YValidationAspekt problem) {
		Class<T> result = null;
		try {
			//result = (Class<T>) Class.forName(classname);
			result = (Class<T>) Thread.currentThread().getContextClassLoader().loadClass(classname);
		} catch (final Exception e) {
			this.addValidationProblem(problem);
		}
		return result;
	}

}