package de.hybris.yfaces.selenium;

import de.hybris.yfaces.component.AbstractYComponent;

public class TestLifecycleComponent extends AbstractYComponent {

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
