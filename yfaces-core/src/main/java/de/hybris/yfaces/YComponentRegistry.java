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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.hybris.yfaces.YComponentInfo.ERROR_STATE;
import de.hybris.yfaces.taglib.YFacesTaglib;

/**
 * A registry which holds meta information about registered YComponent. Components are registered
 * during startup. YComponent meta information are described as {@link YComponentInfo}.
 * 
 * @author Denny.Strietzbaum
 */
// 
// This registry is a very similar construct to FaceletFactory.
//
// FaceletFactory/Facelets
// - Describes a view fragment
// - A Facelet recognizes View changes and gets recreated by the FaceletFactory
// - FaceletFactory creates a Facelet on demand
// 
// YComponentRegistry/YComponentInfo
// - Describes a view fragment
// - YComponentInfo can't recognize view changes
// - YComponentRegistry gets all YComponentInfo registered during startup
// - YComponentInfo implements Validation rules
//
// A great deal would be if the Facelet implementation can be extended by an
// additional
// one, lets name it YComponentFacelet which combines the advantages of a
// Facelet with
// the additional functions of a YComponentInfo.
// 
// What are the pitfalls:
// - Facelets DefaultFaceletFactory always creates Facelets of type
// DefaultFacelet
// - DefaultFaceletFactory is final
// - DefaultFacelet is final and package protected
// - YComponentInfo can easily be integrated when getFacelet(URL) can be
// overwritten; but this one belongs to final DefaultFaceletFactory
// - FaceletFactory#getFacelet(String uri) gets only called once per request for
// the current view id -> useless
public class YComponentRegistry {

	public interface YComponentRegistryListener {

		public void addedYComponent(YComponentInfo cmpInfo);

		public void skippedYComponent(URL url, YComponentInfo cmpInfo);
	}

	private static final Logger log = Logger.getLogger(YComponentRegistry.class);

	// some attribute names
	private static final String ATTR_ID = "id";
	private static final String ATTR_VAR = "var";
	private static final String ATTR_IMPL_CLASS = "default";
	private static final String ATTR_SPEC_CLASS = "definition";
	private static final String ATTR_INJECTABLE = "injectable";

	// Searches for all component attributes as raw, unsplitted String
	// <yf:component xxx > whereas xxx is the result)
	public static final Pattern ALL_ATTRIBUTES = Pattern.compile("<"
			+ YFacesTaglib.COMPONENT_NAME_FULL + "(.*?)" + ">", Pattern.DOTALL);

	// Searches for a single Attribute within the attributes String
	private static final Pattern SINGLE_ATTRIBUTE = Pattern.compile(
			"\\s*(.*?)\\s*=\\s*\"\\s*(.*?)\\s*\"", Pattern.DOTALL);

	// #{xxx} whereas xxx is the result of first group
	private static final Pattern SINGLE_EL_ATTRIBUTE = Pattern.compile(
			"\\s*#\\{\\s*(.*?)\\s*\\}\\s*", Pattern.DOTALL);

	// ID to MetaComponent
	private Map<String, YComponentInfo> idToCmpMap = null;

	private Set<ERROR_STATE> treatAsWarning = null;

	private static YComponentRegistry singleton = new YComponentRegistry();

	public static YComponentRegistry getInstance() {
		return singleton;
	}

	public YComponentRegistry() {
		this.idToCmpMap = new LinkedHashMap<String, YComponentInfo>();
		this.treatAsWarning = EnumSet.of(ERROR_STATE.VIEW_ID_NOT_SPECIFIED,
				ERROR_STATE.SPEC_IS_MISSING);
	}

	/**
	 * Returns a {@link YComponentInfo} by it's ID.
	 * 
	 * @param id
	 *            ID
	 * @return {@link YComponentInfo}
	 */
	public YComponentInfo getComponent(final String id) {
		return idToCmpMap.get(id);
	}

	/**
	 * @return all registered {@link YComponentInfo}
	 */
	public Map<String, YComponentInfo> getAllComponents() {
		return this.idToCmpMap;
	}

	/**
	 * 
	 * @param url
	 * @param listener
	 */
	public void processURL(final URL url, final YComponentRegistryListener listener) {

		boolean added = false;
		YComponentInfo cmpInfo = null;

		// when URL matches valid component name pattern...
		if (isYComponentURL(url)) {
			// ...create the componentinfo
			cmpInfo = this.createYComponentInfo(url);

			if (cmpInfo != null) {
				added = addComponent(cmpInfo);
			}
		} else {
			log.debug("Skipped " + url);
		}

		// when a listener was passed...
		if (listener != null) {
			// ...call one of the two hookup methods
			if (added) {
				listener.addedYComponent(cmpInfo);
			} else {
				listener.skippedYComponent(url, cmpInfo);
			}
		}
	}

	public boolean addComponent(final YComponentInfo cmpInfo) {
		boolean result = false;
		final String id = cmpInfo.getId();

		if (id != null) {
			Set<ERROR_STATE> errors = cmpInfo.verifyComponent();
			Set<ERROR_STATE> warnings = Collections.EMPTY_SET;

			// when errors are thrown...
			if (!errors.isEmpty()) {
				// ...remove all who're declared as warning
				errors = new HashSet<ERROR_STATE>(errors);
				warnings = new HashSet<ERROR_STATE>(this.treatAsWarning);
				// only keep warnings which are thrown as error
				warnings.retainAll(errors);
				// cleanup errors from warnings
				errors.removeAll(warnings);
			}

			// we shouldn't have any errors now
			if (errors.isEmpty()) {
				// don't add components whose ID is already in use
				if (this.idToCmpMap.containsKey(id)) {
					log.error("Error adding component: " + cmpInfo.getURL());
					log.error("Duplicate component ID: " + id + " (" + cmpInfo.getURL() + ")");
				} else {
					this.idToCmpMap.put(cmpInfo.getId(), cmpInfo);
					result = true;
					if (!warnings.isEmpty()) {
						log.debug("Added component " + cmpInfo.getComponentName()
								+ " with warnings:");
						log.debug(ERROR_STATE.getFormattedErrorMessage(warnings, cmpInfo, null));
					} else {
						log.debug("Added component " + cmpInfo.getComponentName());
					}
				}
			} else {
				log.error("Error adding component: " + cmpInfo.getURL());
				log.error(ERROR_STATE.getFormattedErrorMessage(errors, cmpInfo, null));
			}
		} else {
			log.error("Error adding component (no id): " + cmpInfo.getURL());
		}
		return result;
	}

	/**
	 * Checks whether passed URL matches a name Pattern for a YComponent Renderer. This is the case
	 * when externalized URL ends with "Tag.xhtml"
	 * 
	 * @param url
	 * @return true when passed url matches component pattern
	 */
	public boolean isYComponentURL(final URL url) {
		// final Matcher tagNameMatcher =
		// TAGNAME_FROM_URL.matcher(url.toExternalForm());
		// final boolean isYComponent = tagNameMatcher.matches();
		// return isYComponent;
		return getYComponentNameFromURL(url) != null;
	}

	public String getYComponentNameFromURL(final URL url) {
		String result = null;
		final Matcher tagNameMatcher = YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(url
				.toExternalForm());
		if (tagNameMatcher.matches()) {
			result = tagNameMatcher.group(1);
		}
		return result;
	}

	/**
	 * Detects available properties from a given resource. Parses the content and extracts needed
	 * values. Does no verification or class instantiations.
	 * 
	 * @param url
	 *            resource url
	 */
	public YComponentInfo createYComponentInfo(final URL url) {
		YComponentInfo result = null;

		// extract tagname
		final Matcher tagNameMatcher = YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(url
				.toExternalForm());
		if (tagNameMatcher.matches()) {
			String content = null;
			try {
				final StringWriter writer = new StringWriter();
				IOUtils.copy(new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")),
						writer);
				content = writer.toString();

				// this.creationTime =
				// resource.openConnection().getLastModified();
			} catch (final Exception e) {
				e.printStackTrace();
			}

			result = createYComponentInfo(content);
			if (result != null) {
				result.setComponentName(tagNameMatcher.group(1));
				result.setURL(url);
			}
		}
		return result;
	}

	public YComponentInfo createYComponentInfo(final String content) {
		YComponentInfo result = null;
		final Map<String, String> attributes = this.getYComponentAttributes(content);
		if (attributes != null) {
			result = new YComponentInfo();
			this.initializeYComponentInfo(result, attributes);
		}
		return result;
	}

	/**
	 * Detects available properties from a given resource. Parses the content and extracts needed
	 * values. Does no verification or class instantiations.
	 * 
	 * @param content
	 *            String
	 */
	private Map<String, String> getYComponentAttributes(final String content) {
		Map<String, String> result = null;

		// extract all attributes (raw string)
		final Matcher allAttributesMatcher = ALL_ATTRIBUTES.matcher(content);
		if (allAttributesMatcher.find()) {
			final String _attributes = allAttributesMatcher.group(1);
			result = findAttributes(_attributes);
		}
		return result;
	}

	private void initializeYComponentInfo(final YComponentInfo cmpInfo,
			final Map<String, String> attributes) {
		cmpInfo.setId(attributes.get(ATTR_ID));
		cmpInfo.setVarName(attributes.get(ATTR_VAR));
		cmpInfo.setImplementationClassName(attributes.get(ATTR_IMPL_CLASS));
		cmpInfo.setSpecificationClassName(attributes.get(ATTR_SPEC_CLASS));

		// old style
		for (final Map.Entry<String, String> entry : attributes.entrySet()) {
			final Matcher injectableMatcher = SINGLE_EL_ATTRIBUTE.matcher(entry.getValue());
			if (injectableMatcher.matches()) {
				cmpInfo.addInjectableProperty(entry.getKey());
			}
		}

		// add list of attributes given as "injectable" to injectable properties
		// this is the preferred way of declaring injectable attributes
		final String injectable = attributes.get(ATTR_INJECTABLE);
		if (injectable != null) {
			final String properties[] = injectable.trim().split("\\s*,\\s*");
			cmpInfo.addInjectableProperties(properties);
		}

	}

	private Map<String, String> findAttributes(final String attributes) {
		final Map<String, String> result = new HashMap<String, String>();

		// extract each single attribute
		final Matcher singleAttributeMatcher = SINGLE_ATTRIBUTE.matcher(attributes);
		while (singleAttributeMatcher.find()) {
			// ...extract attributes name and value
			// String attrName = singleAttributeMatcher.group(1).toLowerCase();
			String attrName = singleAttributeMatcher.group(1);
			String attrValue = singleAttributeMatcher.group(2);
			result.put(attrName, attrValue);
		}
		return result;
	}

}
