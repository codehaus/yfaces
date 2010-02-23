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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Denny Strietzbaum
 */
public class YComponentValidatorImpl implements YComponentValidator {

	private static final Logger log = Logger.getLogger(YComponentValidator.class);

	private YComponentImpl cmpInfo = null;
	private YComponentConfiguration cmpCfg = null;

	public YComponentValidatorImpl(final YComponentImpl cmpInfo) {
		this.cmpInfo = cmpInfo;
		this.cmpCfg = cmpInfo.getConfiguration();
	}

	// all errors which are detected after a verification
	private Set<YValidationAspekt> foundErrors = null;
	private Set<YValidationAspekt> foundWarnings = null;

	private Set<YValidationAspekt> asWarnings = Collections.EMPTY_SET;

	public YComponent getYComponentInfo() {
		return this.cmpInfo;
	}

	public Set<YValidationAspekt> validateComponent(final YValidationAspekt... asWarningOnly) {
		this.asWarnings = EnumSet.noneOf(YValidationAspekt.class);
		for (final YValidationAspekt aspect : asWarningOnly) {
			this.asWarnings.add(aspect);
		}
		return validateComponent();
	}

	/**
	 * Verifies the component.<br/>
	 * Checks for specification, implementation, non-duplicate 'id' and 'var' values.<br/>
	 * 
	 */
	public Set<YValidationAspekt> validateComponent() {
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
		boolean isValid = assertNotEmpty(cmpCfg.getModelSpecification(),
				YValidationAspekt.SPEC_IS_MISSING);
		if (isValid) {
			final Class modelSpec = assertLoadingClass(cmpCfg.getModelSpecification(),
					YValidationAspekt.SPEC_NOT_LOADABLE);

			if (isValid = (modelSpec != null)) {

				if (!modelSpec.equals(cmpInfo.getModelSpecification())) {
					throw new IllegalStateException("Model specification class mismatch " + modelSpec
							+ " vs " + cmpInfo.getModelSpecification());
				}

				final Set<YValidationAspekt> _specErrors = this.validateSpecificationClass(modelSpec);

				for (final YValidationAspekt problem : _specErrors) {
					this.addValidationProblem(problem);
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
		boolean isValid = assertNotEmpty(cmpCfg.getModelImplementation(),
				YValidationAspekt.IMPL_IS_MISSING);
		if (isValid) {

			final Class modelImpl = assertLoadingClass(cmpCfg.getModelImplementation(),
					YValidationAspekt.IMPL_NOT_LOADABLE);

			if (isValid = (modelImpl != null)) {

				if (!modelImpl.equals(cmpInfo.getModelImplementation())) {
					throw new IllegalStateException("Model implementation class mismatch");
				}

				final Set<YValidationAspekt> _implErrors = this.validateImplementationClass(modelImpl);

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
		final boolean isValidId = assertNotEmpty(cmpCfg.getId(),
				YValidationAspekt.VIEW_ID_NOT_SPECIFIED);

		final boolean isValidVar = assertNotEmpty(cmpCfg.getVariableName(),
				YValidationAspekt.VIEW_VAR_NOT_SPECIFIED);

		final boolean isValid = isValidId && isValidVar;
		return isValid;

	}

	public Set<YValidationAspekt> validateSpecificationClass(final Class<?> specClass) {
		final Set<YValidationAspekt> result = EnumSet.noneOf(YValidationAspekt.class);

		// check for interface
		if (!specClass.isInterface()) {
			result.add(YValidationAspekt.SPEC_IS_NO_INTERFACE);
		}

		// check for YComponent type
		if (!YModel.class.isAssignableFrom(specClass)) {
			result.add(YValidationAspekt.SPEC_IS_NO_YCMP);
		}

		return result;
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
	public Set<YValidationAspekt> validateImplementationClass(final Class<?> implClass) {
		final Set<YValidationAspekt> result = EnumSet.noneOf(YValidationAspekt.class);

		if (implClass.isInterface()) {
			result.add(YValidationAspekt.IMPL_IS_INTERFACE);
		}

		if (!YModel.class.isAssignableFrom(implClass)) {
			result.add(YValidationAspekt.IMPL_IS_NO_YCMP);
		}

		final Class<?> specClass = cmpInfo.getModelSpecification();
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