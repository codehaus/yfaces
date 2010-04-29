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

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Denny Strietzbaum
 */
public interface YComponentValidator {

	// various placeholders for error messages
	// placeholder: specification class
	static final String PLACEHOLDER_SPECCLASS = "specClass";
	// placeholder: imlementation
	static final String PLACEHOLDER_IMPLCLASS = "implClass";
	// placeholder: reserved properties (var, id, etc)
	static final String PLACEHOLDER_PROPERTIES = "properties";

	// reserved (forbidden) attributes;
	// their usage is not allowed as injectable component parameter
	static final String[] RESERVED_PROPERTY_NAMES = new String[] { "facet" };

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
				final YComponentContext cmpInfo, final Class<?> customImplClass) {
			String result = null;
			if (!errors.isEmpty()) {
				result = cmpInfo.getViewId() != null ? cmpInfo.getViewId() : "";
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
		public String getFormattedErrorMessage(final YComponentContext cmpInfo) {
			return this.getFormattedErrorMessage(cmpInfo, null);
		}

		/**
		 * Returns a formatted error message based on the passed {@link YComponentValidator}. An
		 * optional custom implementation is used instead of
		 * {@link YComponentValidator#getConfiguredModelImplementation()}
		 * 
		 * @param cmpInfo
		 *          {@link YComponentValidator}
		 * @param customImplClass
		 *          Class
		 * @return String
		 */
		public String getFormattedErrorMessage(final YComponentContext cmpInfo, final Class<?> customImplClass) {
			String result = "";
			final String msg = this.msg;

			final Matcher parameter = placeHolderPattern.matcher(msg);
			int start = 0;
			while (parameter.find()) {
				String param = parameter.group(1);
				if (param.equals(PLACEHOLDER_SPECCLASS)) {
					param = cmpInfo.getConfiguration().getModelSpecification();
				} else if (param.equals(PLACEHOLDER_IMPLCLASS)) {
					param = (customImplClass == null) ? cmpInfo.getConfiguration().getModelImplementation()
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

	Set<YValidationAspekt> validateComponent(final YValidationAspekt... asWarningOnly);

	public Set<YValidationAspekt> validateComponent();

	public Set<YValidationAspekt> getValidationErrors();

	public Set<YValidationAspekt> getValidationWarnings();

	/**
	 * Asserts the passed class whether it can be used with this component configuration.<br/>
	 * 
	 * Possible verification errors are:<br/> {@link YValidationAspekt#IMPL_IS_INTERFACE}<br/>
	 * {@link YValidationAspekt#IMPL_IS_NO_YCMP}<br/> {@link YValidationAspekt#IMPL_UNASSIGNABLE_TO_SPEC}<br/>
	 * 
	 * @param implClass
	 * @return result
	 */
	Set<YValidationAspekt> validateImplementationClass(final Class<?> implClass);

}