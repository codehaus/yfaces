package org.codehaus.yfaces.selenium.component;

import org.codehaus.yfaces.component.AbstractYModel;

public class TestErrorHandlingCmp extends AbstractYModel {

	private String errorMessage = null;

	@Override
	public void validate() {
		if ("validate".equals(errorMessage)) {
			throw new RuntimeException("Forced error while doing validate");
		}
	}

	@Override
	public void refresh() {
		if ("refresh".equals(errorMessage)) {
			throw new RuntimeException("Forced error while doing refresh");
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String error) {
		this.errorMessage = error;
		if ("now".equals(error)) {
			throw new RuntimeException("Forced error while setting attribute");
		}
	}

}
