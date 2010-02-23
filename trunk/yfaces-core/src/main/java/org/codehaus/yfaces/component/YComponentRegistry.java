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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.YFacesTaglib;


/**
 * A registry which holds meta information about registered YComponent. Components are registered
 * during startup. YComponent meta information are described as {@link YComponentImpl}.
 * 
 * @author Denny Strietzbaum
 */
// 
// This registry is a very similar construct to FaceletFactory.
//
// FaceletFactory/Facelets
// - Describes a view fragment
// - A Facelet recognizes View changes and gets recreated by the FaceletFactory
// - FaceletFactory creates a Facelet on demand
// 
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

	private static final Logger log = Logger.getLogger(YComponentRegistry.class);

	// namespace + id are mapped to YComponentInfo
	private Map<String, YComponent> defaultNsCmpMap = null;
	private Map<String, Map<String, YComponent>> cmpMap = null;

	private Map<String, YComponent> locationToCmpMap = null;

	// for now a singleton, should be integrated intoe the yfaces-framework as part of application
	private static YComponentRegistry singleton = new YComponentRegistry();

	public static YComponentRegistry getInstance() {
		return singleton;
	}

	public YComponentRegistry() {
		this.locationToCmpMap = new HashMap<String, YComponent>();

		this.defaultNsCmpMap = new LinkedHashMap<String, YComponent>();
		this.cmpMap = new HashMap<String, Map<String, YComponent>>();
		this.cmpMap.put(YFacesTaglib.YFACES_NAMESPACE, defaultNsCmpMap);
	}

	/**
	 * Looks for a registered {@link YComponent} by ID and default namespace.
	 * 
	 * @param id
	 *          ID of requested {@link YComponent}
	 * @return {@link YComponent} or null
	 */
	public YComponent getComponent(final String id) {
		return defaultNsCmpMap.get(id);
	}

	/**
	 * Looks for a registered {@link YComponent} by namespace and ID.
	 * 
	 * @param namespace
	 *          namespace (or null for default namespace) of requested {@link YComponent}
	 * @param id
	 *          id of requested {@link YComponent}
	 * @return {@link YComponent} or null
	 */
	public YComponent getComponent(String namespace, final String id) {

		if (namespace == null) {
			namespace = YFacesTaglib.YFACES_NAMESPACE;
		}
		final Map<String, YComponent> idToCmpMap = this.cmpMap.get(namespace);
		final YComponent result = idToCmpMap != null ? idToCmpMap.get(id) : null;
		return result;
	}

	/**
	 * Looks for a {@link YComponent} by it's unique location.
	 * 
	 * @param location
	 *          location of requested {@link YComponent}
	 * @return {@link YComponent} or null
	 */
	public YComponent getComponentByPath(final String location) {
		return locationToCmpMap.get(location);
	}

	/**
	 * Registers a new YComponent as {@link YComponent} to this registry.
	 * 
	 * @param cmpInfo
	 *          {@link YComponent}
	 * @return true when successfully registered
	 */
	public boolean addComponent(final YComponent cmpInfo) {

		boolean result = false;

		if (cmpInfo != null) {

			final String id = cmpInfo.getViewId();
			final String ns = cmpInfo.getNamespace();

			// an ID should always be available (at least as fallback of 'name')
			if (id == null) {
				throw new YFacesException("No component ID: " + cmpInfo.getViewURL());
			}

			// same with namespace
			if (ns == null) {
				throw new YFacesException("No component namespace: " + cmpInfo.getViewURL());
			}

			// get namespace -> component mapping
			Map<String, YComponent> map = this.cmpMap.get(ns);

			// create if necessary
			if (map == null) {
				cmpMap.put(ns, map = new LinkedHashMap<String, YComponent>());
			}

			// assure another component is not already mapped with same id
			// (can happen when custom IDs are used)
			if (!map.containsKey(id)) {
				// ... add to namesapce ,ap
				map.put(cmpInfo.getViewId(), cmpInfo);
				// ... add to global map
				this.locationToCmpMap.put(cmpInfo.getViewLocation(), cmpInfo);
				result = true;

				// otherwise log as error
			} else {
				log.error("Error adding component: " + cmpInfo.getViewURL());
				log.error("A component with same ID is already registered (" + map.get(id).getViewURL() + ")");
			}
		}
		return result;
	}

}
