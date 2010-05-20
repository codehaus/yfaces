package org.codehaus.yfaces.selenium.component;

import java.io.Serializable;

public class PojoTestModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value = null;

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
