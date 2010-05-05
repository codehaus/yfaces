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
package org.codehaus.yfaces.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.codehaus.yfaces.YFacesTaglib;

/**
 * Factory class for {@link YComponentHandlerImpl} instances.
 * 
 * @author Denny Strietzbaum
 */
public class YComponentHandlerFactory {

	// Searches for a single Attribute within the attributes String
	private static final Pattern SINGLE_ATTRIBUTE = Pattern.compile(
			"\\s*(.*?)\\s*=\\s*\"\\s*(.*?)\\s*\"", Pattern.DOTALL);

	//	// #{xxx} whereas xxx is the result of first group
	//	private static final Pattern SINGLE_EL_ATTRIBUTE = Pattern.compile(
	//			"\\s*#\\{\\s*(.*?)\\s*\\}\\s*", Pattern.DOTALL);

	// extracts the namespace prefix from a namespace declaration
	// e.g. xmlns:yf="http://yfaces.codehaus.org/taglib" returns as group 'yf'
	private static Pattern NAMESPACE_PREFIX = Pattern.compile("xmlns:(.*?)\\s*=\\s*\""
			+ YFacesTaglib.YFACES_NAMESPACE + "\"");

	// matches when resource represents a component view
	//	private static final Pattern COMPONENT_RESOURCE_PATTERN = Pattern
	//			.compile(".*[/\\\\](.*)((?:Cmp)|(?:Tag))\\.xhtml");
	private static final Pattern COMPONENT_RESOURCE_PATTERN = Pattern
			.compile(".*[/\\\\](.*)\\.xhtml");

	private Map<String, Pattern> cmpAttributesPatternMap = null;

	private String base = null;

	public YComponentHandlerFactory() {
		this("");
	}

	public YComponentHandlerFactory(String base) {
		this.cmpAttributesPatternMap = new HashMap<String, Pattern>();
		this.base = base;
		if (!base.startsWith("/")) {
			base = "/" + base;
		}
	}

	public YComponentHandler createHandler(final URL url, final String namespace) {

		YComponentHandler result = null;

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

			result = this.createHandler(content);

			if (result != null) {

				final YComponentHandlerImpl impl = (YComponentHandlerImpl) result;

				// component name
				impl.setName(cmpName);
				impl.setViewURL(url);
				impl.setNamespace(namespace);
				impl.setViewLocation("/" + url.getFile().substring(this.base.length()));
			}
		}
		return result;
	}

	public YComponentHandler createHandler(final String content) {

		YComponentHandlerImpl result = null;

		// get namespace prefix used for HtmlYCOmponent
		final String nsPrefix = this.getYComponentNamespacePrefix(content);

		if (nsPrefix != null) {
			final Map<String, String> attributes = this.getYComponentAttributes(nsPrefix, content);

			if (attributes != null) {

				result = new YComponentHandlerImpl();

				// component name
				final YComponentConfigImpl cmpCfg = (YComponentConfigImpl) result.getConfiguration();
				cmpCfg.setId(attributes.get(YComponentConfig.ID_ATTRIBUTE));
				cmpCfg.setVariableName(attributes.get(YComponentConfig.VAR_ATTRIBUTE));
				cmpCfg.setModelImplementation(attributes.get(YComponentConfig.MODEL_IMPL_ATTRIBUTE));
				cmpCfg.setModelSpecification(attributes.get(YComponentConfig.MODEL_SPEC_ATTRIBUTE));
				cmpCfg.setErrorHandling(attributes.get(YComponentConfig.ERROR_ATTRIBUTE));
				cmpCfg.setPushProperties(attributes.get(YComponentConfig.PASS_TO_MODEL_ATTRIBUTE));

				result.initialize();
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

		// avoid multiple compilation of same Pattern
		Pattern attributesPattern = this.cmpAttributesPatternMap.get(nsPrefix);
		if (attributesPattern == null) {
			attributesPattern = Pattern.compile("<" + nsPrefix + ":" + YFacesTaglib.YCOMPONENT_NAME
					+ "(.*?)" + ">", Pattern.DOTALL);
			this.cmpAttributesPatternMap.put(nsPrefix, attributesPattern);
		}

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

}
