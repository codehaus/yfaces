/*
 * Copyright 2008 the original author or authors.
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

public enum YFacesConfig {

	/** Enable/Disable useful HTML comments */
	ENABLE_HTML_DEBUG("yfaces.ycomponent.enableHtmlDebug", "false"),

	/** when true registers non-ycomponents as tag too */
	ALSO_REGISTER_NON_YCMP("yfaces.tags.registerNonYComponents", "true"), ;

	static Map properties = null;

	private String key;
	private String defaultValue;

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

	public String getString() {
		return getString(properties);
	}

	public Boolean getBoolean() {
		return getBoolean(properties);
	}

	private String getString(final Map properties) {
		String result = properties != null ? (String) properties.get(key) : null;
		if (result == null || result.trim().length() == 0) {
			result = defaultValue;
		}
		return result;
	}

	private boolean getBoolean(final Map properties) {
		String value = properties != null ? getString(properties) : null;
		if (value == null || value.trim().length() == 0) {
			value = defaultValue;
		}
		final boolean result = "true".equals(value.toLowerCase());
		return result;
	}

}
