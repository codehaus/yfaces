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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import de.hybris.yfaces.YFacesTaglib;

/**
 * Factory class for {@link DefaultYComponentInfo} instances.
 * 
 * @author Denny Strietzbaum
 */
public class YComponentInfoFactory {

	// some attribute names
	private static final String ATTR_ID = "id";
	private static final String ATTR_VAR = "var";
	private static final String ATTR_IMPL_CLASS = "impl";
	private static final String ATTR_SPEC_CLASS = "spec";
	private static final String ATTR_INJECTABLE = "injectable";

	// Searches for a single Attribute within the attributes String
	private static final Pattern SINGLE_ATTRIBUTE = Pattern.compile(
			"\\s*(.*?)\\s*=\\s*\"\\s*(.*?)\\s*\"", Pattern.DOTALL);

	//	// #{xxx} whereas xxx is the result of first group
	private static final Pattern SINGLE_EL_ATTRIBUTE = Pattern.compile(
			"\\s*#\\{\\s*(.*?)\\s*\\}\\s*", Pattern.DOTALL);

	private static Pattern NAMESPACE_PREFIX = Pattern.compile("xmlns:(.*?)\\s*=\\s*\""
			+ YFacesTaglib.YFACES_NAMESPACE + "\"");

	// matches when resource represents a component view
	public static final Pattern COMPONENT_RESOURCE_PATTERN = Pattern
			.compile(".*[/\\\\](.*)((?:Cmp)|(?:Tag))\\.xhtml");

	public YComponentInfoFactory() {
	}

	public YComponentInfo createComponentInfo(final URL url, final String namespace) {
		DefaultYComponentInfo result = null;

		// get component name
		final String cmpName = this.getComponentName(url);

		if (cmpName != null) {

			// get component content
			String content = null;
			try {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")), writer);
				content = writer.toString();
			} catch (final IOException e) {
				e.printStackTrace();
			}

			result = this.createComponentInfo(content);

			if (result != null) {
				// this.creationTime = resource.openConnection().getLastModified();

				// component name
				result.setComponentName(cmpName);
				result.setURL(url);
				result.setNamespace(namespace);
			}
		}
		return result;
	}

	public DefaultYComponentInfo createComponentInfo(final String content) {

		DefaultYComponentInfo result = null;

		// get namespace prefix used for HtmlYCOmponent
		final String nsPrefix = this.getYComponentNamespacePrefix(content);

		if (nsPrefix != null) {
			final Map<String, String> attributes = this.getYComponentAttributes(nsPrefix, content);

			if (attributes != null) {

				result = new DefaultYComponentInfo();

				// this.creationTime =
				// resource.openConnection().getLastModified();

				// component name
				result.setId(attributes.get(ATTR_ID));
				result.setVariableName(attributes.get(ATTR_VAR));
				result.setImplementation(attributes.get(ATTR_IMPL_CLASS));
				result.setSpecification(attributes.get(ATTR_SPEC_CLASS));
				final Collection<String> injectable = this.getComponentProperties(attributes);
				result.setProperties(injectable);
			}
		}
		return result;
	}

	/**
	 * Extracts name of component. Returns null when component url doesn't match a valid component
	 * pattern.
	 * 
	 * @param url
	 *          component resource
	 * @return name of component
	 */
	private String getComponentName(final URL url) {
		final Matcher tagNameMatcher = COMPONENT_RESOURCE_PATTERN.matcher(url.toExternalForm());

		final String result = (tagNameMatcher.matches()) ? tagNameMatcher.group(1) : null;

		return result;

	}

	private String getYComponentNamespacePrefix(final String content) {
		final Matcher m = NAMESPACE_PREFIX.matcher(content);

		final String result = (m.find()) ? m.group(1) : null;
		return result;
	}

	/**
	 * Extracts configured ycomponent attributes.Expects a string which gets parsed. Returns null when
	 * String doesn't contain a ycomponent occurence. Returns a Map which maps
	 * attributename->attributevalue or an empty Map when no Attributes are configured.
	 * 
	 * @param content
	 *          String
	 */
	private Map<String, String> getYComponentAttributes(final String nsPrefix, final String content) {
		Map<String, String> result = null;

		final Pattern attributesPattern = Pattern.compile("<" + nsPrefix + ":"
				+ YFacesTaglib.YCOMPONENT_NAME + "(.*?)" + ">", Pattern.DOTALL);

		// extract all attributes (raw string)
		final Matcher allAttributesMatcher = attributesPattern.matcher(content);
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

	private Collection<String> getComponentProperties(final Map<String, String> attributes) {
		final Set<String> result = new HashSet<String>();

		// old style
		for (final Map.Entry<String, String> entry : attributes.entrySet()) {
			final Matcher injectableMatcher = SINGLE_EL_ATTRIBUTE.matcher(entry.getValue());
			if (injectableMatcher.matches()) {
				result.add(entry.getKey());
			}
		}

		final String injectable = attributes.get(ATTR_INJECTABLE);
		if (injectable != null) {
			final String _properties[] = injectable.trim().split("\\s*,\\s*");
			result.addAll(Arrays.asList(_properties));
		}
		return result;

	}
}
