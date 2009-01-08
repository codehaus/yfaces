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

package de.hybris.yfaces.session;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class AbstractUserSessionPropertyChangeLog implements UserSessionPropertyChangeLog {
	private static final Logger log = Logger.getLogger(AbstractUserSessionPropertyChangeLog.class);

	private final Set<String> logMap = new HashSet<String>();

	public boolean isPropertyChanged(final String key) {
		return logMap.contains(key);
	}

	public void setPropertyChanged(final String key, final boolean changed) {
		if (log.isDebugEnabled()) {
			log.debug("Set property changed: " + key + ": " + changed);
		}

		if (changed) {
			logMap.add(key);
		} else {
			logMap.remove(key);
		}
	}

	public void reset() {
		this.logMap.clear();
	}

	@Override
	public String toString() {
		final String result = super.toString() + " (" + this.logMap.toString() + ")";
		return result;
	}

}
