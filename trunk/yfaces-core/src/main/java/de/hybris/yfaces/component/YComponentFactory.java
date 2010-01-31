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
package de.hybris.yfaces.component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import de.hybris.yfaces.YFacesTaglib;

/**
 * Factory class for {@link YComponentInfoImpl} instances.
 * 
 * @author Denny Strietzbaum
 */
public class YComponentFactory {

	// some attribute names
	private static final String ATTR_ID = "id";
	private static final String ATTR_VAR = "var";
	private static final String ATTR_IMPL_CLASS = "impl";
	private static final String ATTR_SPEC_CLASS = "spec";
	private static final String ATTR_INJECTABLE = "injectable";

	// Searches for a single Attribute within the attributes String
	private static final Pattern SINGLE_ATTRIBUTE = Pattern.compile(
			"\\s*(.*?)\\s*=\\s*\"\\s*(.*?)\\s*\"", Pattern.DOTALL);

	// #{xxx} whereas xxx is the result of first group
	private static final Pattern SINGLE_EL_ATTRIBUTE = Pattern.compile(
			"\\s*#\\{\\s*(.*?)\\s*\\}\\s*", Pattern.DOTALL);

	// Searches for all component attributes as raw, unsplitted String
	// <yf:component xxx > whereas xxx is the result)
	private static Pattern ALL_ATTRIBUTES;

	public YComponentFactory(final String cmpTagName) {
		ALL_ATTRIBUTES = Pattern.compile("<" + cmpTagName + "(.*?)" + ">", Pattern.DOTALL);
	}

	public YComponentInfoImpl createComponentInfo(final URL url, final String namespace) {
		YComponentInfoImpl result = null;
		final Matcher tagNameMatcher = YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(url
				.toExternalForm());

		if (tagNameMatcher.matches()) {
			result = new YComponentInfoImpl();
			String content = null;
			try {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")), writer);
				content = writer.toString();

				// this.creationTime =
				// resource.openConnection().getLastModified();

				// component name
				result.setComponentName(tagNameMatcher.group(1));

				result.setURL(url);
				result.setNamespace(namespace);

				final boolean isComponent = this.initializeYComponentInfo(result, content);
				if (!isComponent) {
					result = null;
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public YComponentInfoImpl createComponentInfo(final String content) {
		YComponentInfoImpl result = new YComponentInfoImpl();
		final boolean isComponent = this.initializeYComponentInfo(result, content);
		if (!isComponent) {
			result = null;
		}
		return result;
	}

	/**
	 * Initializes passed {@link YComponentInfoImpl} instance. Uses and parses passed content for
	 * component relevant properties. Returns false when content doesn't contain a ycomponent
	 * declaration.
	 * 
	 * @param cmpInfo
	 *          {@link YComponentInfoImpl} instance which has to be configured
	 * @param content
	 *          content (e.g. from a URL)
	 * @return false when passed content contains a ycomponent declaration
	 */
	private boolean initializeYComponentInfo(final YComponentInfoImpl cmpInfo, final String content) {

		// component attributes
		final Map<String, String> attributes = this.getYComponentAttributes(content);
		final boolean isYComponent = attributes != null;

		if (isYComponent) {
			cmpInfo.setId(attributes.get(ATTR_ID));
			cmpInfo.setVariableName(attributes.get(ATTR_VAR));
			cmpInfo.setImplementation(attributes.get(ATTR_IMPL_CLASS));
			cmpInfo.setSpecification(attributes.get(ATTR_SPEC_CLASS));

			// old style
			for (final Map.Entry<String, String> entry : attributes.entrySet()) {
				final Matcher injectableMatcher = SINGLE_EL_ATTRIBUTE.matcher(entry.getValue());
				if (injectableMatcher.matches()) {
					cmpInfo.addProperty(entry.getKey());
				}
			}

			// add list of attributes given as "injectable" to injectable properties
			// this is the preferred way of declaring injectable attributes
			final String injectable = attributes.get(ATTR_INJECTABLE);
			if (injectable != null) {
				final String properties[] = injectable.trim().split("\\s*,\\s*");
				cmpInfo.addProperties(properties);
			}
		}

		return isYComponent;
	}

	/**
	 * Extracts configured ycomponent attributes.Expects a string which gets parsed. Returns null when
	 * String doesn't contain a ycomponent occurence. Returns a Map which maps
	 * attributename->attributevalue or an empty Map when no Attributes are configured.
	 * 
	 * @param content
	 *          String
	 */
	private Map<String, String> getYComponentAttributes(final String content) {
		Map<String, String> result = null;

		// extract all attributes (raw string)
		final Matcher allAttributesMatcher = ALL_ATTRIBUTES.matcher(content);
		if (allAttributesMatcher.find()) {
			final String _attributes = allAttributesMatcher.group(1);
			result = new HashMap<String, String>();

			// extract each single attribute
			final Matcher singleAttributeMatcher = SINGLE_ATTRIBUTE.matcher(_attributes);
			while (singleAttributeMatcher.find()) {
				// ...extract attributes name and value
				// String attrName = singleAttributeMatcher.group(1).toLowerCase();
				final String attrName = singleAttributeMatcher.group(1);
				final String attrValue = singleAttributeMatcher.group(2);
				result.put(attrName, attrValue);
			}
		}
		return result;
	}
}
