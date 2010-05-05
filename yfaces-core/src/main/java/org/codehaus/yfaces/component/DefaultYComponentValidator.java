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

import java.util.Set;

/**
 * @author Denny Strietzbaum
 */
public class DefaultYComponentValidator extends BaseYComponentValidator {

	public DefaultYComponentValidator(final YComponentHandler cmpInfo) {
		super(cmpInfo);
	}

	@Override
	public Set<YValidationAspekt> validateSpecificationClass(final Class<?> specClass) {

		final Set<YValidationAspekt> result = super.validateSpecificationClass(specClass);

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
	@Override
	public Set<YValidationAspekt> validateImplementationClass(final Class<?> implClass) {

		final Set<YValidationAspekt> result = super.validateImplementationClass(implClass);

		if (!YModel.class.isAssignableFrom(implClass)) {
			result.add(YValidationAspekt.IMPL_IS_NO_YCMP);
		}

		return result;
	}

}