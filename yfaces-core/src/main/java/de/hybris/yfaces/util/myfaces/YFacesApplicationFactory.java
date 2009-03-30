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

package de.hybris.yfaces.util.myfaces;

import javax.el.ELContextEvent;
import javax.el.ELContextListener;
import javax.el.ELResolver;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;

import org.apache.myfaces.application.ApplicationImpl;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.el.YFacesELContext;
import de.hybris.yfaces.el.YFacesResolverWrapper;

/**
 * A custom {@link ApplicationFactory} implementation which creates a
 * {@link YFacesApplication}.<br/>
 * <br/>
 * Note:<br/>
 * JSF provides no way to configure a custom {@link Application} class but a
 * {@link ApplicationFactory} instead. <br/>
 * 
 * @author Denny.Strietzbaum
 * 
 */
public class YFacesApplicationFactory extends ApplicationFactory {
	/**
	 * Own {@link Application} implementation which uses an
	 * {@link YFacesResolverWrapper} instead of the JSF created
	 * {@link ELResolver}.<br/>
	 * <br/>
	 * For easier handling this implementation depends on the myfaces
	 * implementation<br/>
	 * Whenever an independent solution is needed the source {@link Application}
	 * instance must be wrapped. <br/>
	 * 
	 * @author Denny.Strietzbaum
	 */
	public static class YFacesApplication extends ApplicationImpl {
		private ELResolver resolver = null;

		/**
		 * Constructor. Adds an {@link YFacesELContextListener}.
		 */
		public YFacesApplication() {
			this.addELContextListener(new YFacesELContextListener());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.apache.myfaces.application.ApplicationImpl#getELResolver()
		 */
		@Override
		public ELResolver getELResolver() {
			if (this.resolver == null) {
				this.resolver = new YFacesResolverWrapper(super.getELResolver());
			}
			return this.resolver;
		}
	}

	/**
	 * An {@link ELContextListener} which adds an {@link YFacesELContext}.
	 */
	public static class YFacesELContextListener implements ELContextListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.el.ELContextListener#contextCreated(javax.el.ELContextEvent)
		 */
		public void contextCreated(final ELContextEvent arg0) {
			arg0.getELContext().putContext(YFacesELContext.class, new YFacesELContext());
		}
	}

	/**
	 * Constructor.<br/>
	 * <br/>
	 * Following is taken out from the myfaces ApplicationFactoryImpl javadoc:<br/>
	 * <br/>
	 * Application is thread-safe (see Application javadoc)
	 * "Application represents a per-web-application singleton object..."
	 * FactoryFinder has a ClassLoader-Factory Map. Since each webapp has it's
	 * own ClassLoader, each webapp will have it's own private factory
	 * instances.
	 */
	public YFacesApplicationFactory() {
		this.application = new YFacesApplication();
	}

	private Application application = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.application.ApplicationFactory#getApplication()
	 */
	@Override
	public Application getApplication() {
		return this.application;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.application.ApplicationFactory#setApplication(javax.faces
	 * .application.Application)
	 */
	@Override
	public void setApplication(final Application application) {
		throw new YFacesException("Can't set another instance of " + Application.class.getName());
	}

}
