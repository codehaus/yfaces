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
package de.hybris.yfaces;

import javax.el.ELContext;
import javax.el.ELContextEvent;
import javax.el.ELContextListener;

/**
 * An {@link ELContextListener} which adds an {@link YFacesELContext} whenever a new
 * {@link ELContext} gets created..
 */
public class YFacesELContextListener implements ELContextListener {
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELContextListener#contextCreated(javax.el.ELContextEvent)
	 */
	public void contextCreated(ELContextEvent arg0) {
		arg0.getELContext().putContext(YFacesELContext.class, new YFacesELContext());
	}
}
