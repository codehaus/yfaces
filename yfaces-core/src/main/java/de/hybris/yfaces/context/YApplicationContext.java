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
package de.hybris.yfaces.context;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

import de.hybris.yfaces.YFacesException;

/**
 * A context object whose scope and lifetime is bound to {@link ServletContext}. In other words:
 * there's only one instance per webapplication available.<br/>
 * YFaces libraries must be loaded by a webapplication specific classloader (which generally is the
 * case when putting them into WEB-INF/lib)
 * 
 * @author Denny.Strietzbaum
 */
public class YApplicationContext {

	private static ApplicationContext appCtx = null;

	/**
	 * Constructor. An instance can be created only one times otherwise an exception is thrown.
	 * 
	 * @param ctx
	 *            Spring {@link ApplicationContext}
	 */
	public YApplicationContext(ApplicationContext ctx) {
		if (appCtx != null) {
			throw new YFacesException(this.getClass().getName() + " was already created");
		}
		appCtx = ctx;
	}

	protected static void setApplicationContext(ApplicationContext ctx) {
		appCtx = ctx;
	}

	/**
	 * Returns a Spring {@link ApplicationContext}
	 * 
	 * @return {@link ApplicationContext}
	 */
	public static ApplicationContext getApplicationContext() {
		return appCtx;
	}

}
