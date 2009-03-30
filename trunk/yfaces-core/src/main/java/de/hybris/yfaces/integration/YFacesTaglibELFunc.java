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

package de.hybris.yfaces.integration;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YFacesTaglibELFunc {

	/**
	 * Internal. Used for Selenium testing (allComponents.jsf)
	 * 
	 * @return generated xhtml fragment
	 * @throws Exception
	 */
	public static String getAllComponentsAsXHTMLFragment() throws Exception {
		return YFacesTaglib.getAllComponentsAsXHTMLFragment();
	}

}
