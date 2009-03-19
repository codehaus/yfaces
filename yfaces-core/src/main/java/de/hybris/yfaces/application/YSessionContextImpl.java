package de.hybris.yfaces.application;

import java.util.HashMap;
import java.util.Map;

public class YSessionContextImpl implements YSessionContext {

	private Map<String, Object> attributes = new HashMap<String, Object>();

	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	public void update() {
		// TODO Auto-generated method stub

	}

}
