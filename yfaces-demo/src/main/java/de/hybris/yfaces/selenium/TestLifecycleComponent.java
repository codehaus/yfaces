package de.hybris.yfaces.selenium;

import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.session.UserSessionPropertyChangeLog;

public class TestLifecycleComponent extends AbstractYComponent {

	private String state = "unset";

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void postInitialize() {
		this.state = "postInitialize";
	}

	public void update(UserSessionPropertyChangeLog log) {
		this.state = "update";
	}

}
