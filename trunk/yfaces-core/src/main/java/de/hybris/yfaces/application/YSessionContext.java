package de.hybris.yfaces.application;

import java.util.Map;

public interface YSessionContext {

	public void update();

	public Map<String, Object> getAttributes();
}
