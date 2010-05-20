package org.codehaus.yfaces.selenium.component;

import org.codehaus.yfaces.component.AbstractYModel;

public class TestLifecycleComponent extends AbstractYModel {

	private String state = "unset";

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public void validate() {
		this.state = "postInitialize";
	}

	@Override
	public void refresh() {
		this.state = "update";
	}

}
