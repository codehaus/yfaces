package org.codehaus.yfaces.component;

import java.util.EnumSet;
import java.util.Set;

public class PojoComponentValidator extends YComponentValidatorImpl {

	public PojoComponentValidator(final YComponentInfoImpl cmpInfo) {
		super(cmpInfo);
	}

	@Override
	public Set<YValidationAspekt> validateSpecificationClass(final Class<?> specClass) {
		final Set<YValidationAspekt> result = EnumSet.noneOf(YValidationAspekt.class);

		// check for interface
		if (!specClass.isInterface()) {
			result.add(YValidationAspekt.SPEC_IS_NO_INTERFACE);
		}

		return result;
	}

	@Override
	public Set<YValidationAspekt> validateImplementationClass(final Class<?> implClass) {
		final Set<YValidationAspekt> result = EnumSet.noneOf(YValidationAspekt.class);

		if (implClass.isInterface()) {
			result.add(YValidationAspekt.IMPL_IS_INTERFACE);
		}

		final Class<?> specClass = ((YComponentInfoImpl) getYComponentInfo()).getModelSpecClass();
		if (specClass != null && !specClass.isAssignableFrom(implClass)) {
			result.add(YValidationAspekt.IMPL_UNASSIGNABLE_TO_SPEC);
		}
		return result;
	}

}
