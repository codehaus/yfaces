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

package org.codehaus.yfaces;

import java.util.Map;

/**
 * Enumeration for easy access to Configuration properties.
 * 
 * @author Denny Strietzbaum
 */
public enum YFacesConfig {

	/** Enable/Disable useful HTML comments */
	ENABLE_HTML_DEBUG("yfaces.ycomponent.enableHtmlDebug", "false"),

	NAMESPACE_CONTEXT("yfaces.taglib.namespace.context", ""), //

	GEN_CMP_ID_SUFFIX("yfaces.component.generate.idSuffix", ""), //
	GEN_CMP_VARNAME_SUFFIX("yfaces.component.generate.varNameSuffix", "Var"), //
	CMP_ERROR_HANDLING("yfaces.component.errorhandling", "debug"), //
	;

	static Map properties = null;

	private String key;
	private String defaultValue;
	private Object value;

	private YFacesConfig(final String key) {
		this.key = key;
	}

	private YFacesConfig(final String key, final String defaultValue) {
		this.key = key;
		this.defaultValue = defaultValue;
	}

	private YFacesConfig(final String key, final boolean defaultValue) {
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

	public static Object getValue(final String key) {
		return properties.get(key);
	}

	public static void setValue(final String key, final Object value) {
		properties.put(key, value);
	}

	private String getString(final Map<String, String> properties) {
		String result = properties != null ? (String) properties.get(key) : null;
		if (result == null || result.trim().length() == 0) {
			result = defaultValue;
		}
		return result;
	}

	private boolean getBoolean(final Map<String, String> properties) {
		String value = properties != null ? getString(properties) : null;
		if (value == null || value.trim().length() == 0) {
			value = defaultValue;
		}
		final boolean result = "true".equals(value.toLowerCase());
		return result;
	}

}
