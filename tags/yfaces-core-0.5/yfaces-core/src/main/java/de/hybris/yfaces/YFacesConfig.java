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

package de.hybris.yfaces;

import java.util.Map;

/**
 * Enumeration for easy access to Configuration properties.
 * 
 * @author Denny.Strietzbaum
 */
public enum YFacesConfig {

	/** Enable/Disable useful HTML comments */
	ENABLE_HTML_DEBUG("yfaces.ycomponent.enableHtmlDebug", "false"),

	/** when true registers non-ycomponents as tag too */
	ALSO_REGISTER_NON_YCMP("yfaces.tags.registerNonYComponents", "true"), ;

	static Map<String, String> properties = null;

	private String key;
	private String defaultValue;

	private YFacesConfig(final String key) {
		this.key = key;
	}

	private YFacesConfig(String key, String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	private YFacesConfig(String key, boolean defaultValue) {
		this(key, String.valueOf(defaultValue));
	}

	/**
	 * Returns the configuration value for this key as String.
	 * 
	 * @return String
	 */
	public String getString() {
		return getString(properties);
	}

	/**
	 * Returns the configuration value for this key as Boolean.
	 * 
	 * @return Boolean
	 */
	public Boolean getBoolean() {
		return getBoolean(properties);
	}

	private String getString(Map<String, String> properties) {
		String result = properties != null ? (String) properties.get(key) : null;
		if (result == null || result.trim().length() == 0) {
			result = defaultValue;
		}
		return result;
	}

	private boolean getBoolean(Map<String, String> properties) {
		String value = properties != null ? getString(properties) : null;
		if (value == null || value.trim().length() == 0) {
			value = defaultValue;
		}
		boolean result = "true".equals(value.toLowerCase());
		return result;
	}

}