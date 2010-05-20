package org.codehaus.yfaces.selenium.component;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;

public class TestComponent extends AbstractYModel {

	private static final Logger log = Logger.getLogger(TestComponent.class);

	private String value = null;
	private int isRefreshed = 0;
	private int isValidated = 0;

	public TestComponent() {
		log.info("Creating " + TestComponent.class.getSimpleName());
	}

	@Override
	public void refresh() {
		super.refresh();
		this.isRefreshed++;
	}

	@Override
	public void validate() {
		super.validate();
		this.isValidated++;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public int getRefreshed() {
		return isRefreshed;
	}

	public int getValidated() {
		return isValidated;
	}

}
