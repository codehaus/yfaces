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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A registry which holds meta information about registered YComponent. Components are registered
 * during startup. YComponent meta information are described as {@link DefaultYComponentInfo}.
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

	private static final Logger log = Logger.getLogger(YComponentRegistry.class);

	// ID to MetaComponent
	private Map<String, YComponentInfo> idToCmpMap = null;

	private static YComponentRegistry singleton = new YComponentRegistry();

	public static YComponentRegistry getInstance() {
		return singleton;
	}

	public YComponentRegistry() {
		this.idToCmpMap = new LinkedHashMap<String, YComponentInfo>();
	}

	/**
	 * Returns a {@link YComponentInfo} by it's ID.
	 * 
	 * @param id
	 *          ID
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
	 * Adds passed {@link DefaultYComponentInfo} to the registry.
	 * 
	 * @param cmpInfo
	 * @return true when successful
	 */
	public boolean addComponent(final YComponentInfo cmpInfo) {

		boolean result = false;

		if (cmpInfo != null) {

			final String id = cmpInfo.getId();

			if (id != null) {

				if (!this.idToCmpMap.containsKey(id)) {
					this.idToCmpMap.put(cmpInfo.getId(), cmpInfo);
					result = true;
				} else {
					log.error("Error adding component: " + cmpInfo.getURL());
					log.error("Duplicate component ID: " + id + " (" + cmpInfo.getURL() + ")");
				}
			} else {
				log.error("Error adding component: " + cmpInfo.getURL() + " (no ID)");
			}
		}
		return result;
	}

}
