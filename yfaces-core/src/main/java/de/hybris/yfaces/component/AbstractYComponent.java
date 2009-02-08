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

package de.hybris.yfaces.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.session.UserSessionPropertyChangeLog;

/**
 * @author Denny.Strietzbaum
 * 
 */
public abstract class AbstractYComponent implements YComponent {
	private static final Logger log = Logger.getLogger(AbstractYComponent.class);

	private Map<String, Object> attributes = null;
	private String frameBinding = null;

	// private String styleClass = null;

	private String id = null;// "[component]" + Math.random() ;
	private String uid = null;

	private transient String logId = this.getClass().getSimpleName();

	public AbstractYComponent() {
		this.uid = this.getClass().getName() + String.valueOf(Math.random());
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	// public String getStyleClass()
	// {
	// return this.styleClass;
	// }
	//
	// public void setStyleClass(String styleClass)
	// {
	// this.styleClass = styleClass;
	// }

	public Map<String, Object> getAttributes() {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Object>();
		}
		return this.attributes;
	}

	/**
	 * Creates an {@link YComponentEventHandler} whose default
	 * {@link YComponentEventListener} does nothing.
	 * 
	 * @return {@link YComponentEventHandler}
	 */
	public YComponentEventHandler createEventHandler() {
		return this.createEventHandler(new DefaultYComponentEventListener<YComponent>());
	}

	/**
	 * Creates an {@link YComponentEventHandler} whose default
	 * {@link YComponentEventListener} is the passed one.
	 * 
	 * @param listener
	 *            {@link YComponentEventListener} default listener
	 * @return {@link YComponentEventHandler}
	 */
	public YComponentEventHandler createEventHandler(final YComponentEventListener listener) {
		final YComponentEventHandler result = new YComponentEventHandlerImpl(listener);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.YComponent#postInitialize()
	 */
	public void postInitialize() {
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.faces.components.YComponent#update(de.hybris.
	 * platform.webfoundation.PropertyChangeLog)
	 */
	public void update(final UserSessionPropertyChangeLog log) {
	};

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		return super.hashCode() + " uid: " + this.uid + " " + result.toString();
	}

	public void setFrame(final String frameBinding) {
		this.frameBinding = frameBinding;
	}

	public YFrame getFrame() {
		YFrame result = null;
		if (frameBinding != null) {
			final FacesContext fc = FacesContext.getCurrentInstance();
			final ValueExpression ve = fc.getApplication().getExpressionFactory()
					.createValueExpression(fc.getELContext(), this.frameBinding, Object.class);
			result = (YFrame) ve.getValue(fc.getELContext());
		}
		return result;
	}

	public <T extends YComponent> T newInstance(final String id) {
		return (T) YComponentRegistry.getInstance().getComponent(id).createDefaultComponent();
	}

	public <T extends YComponent> T newInstance(final T template) {
		return this.newInstance(template, template.getClass());
	}

	private <T extends YComponent> T newInstance(final T template, final Class clazz) {
		T result = null;
		try {
			final Constructor c = clazz.getConstructor(YComponent.class);
			result = (T) c.newInstance(template);
		} catch (final Exception e) {
			throw new YFacesException(clazz
					+ " can't be created; missing Constructor which accepts " + YComponent.class);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		return (obj.getClass().equals(this.getClass()))
				&& ((AbstractYComponent) obj).uid.equals(this.uid);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.uid.hashCode();
	}

	// SERIALIZATION
	// NOTE: (regarding myfaces implementation)
	// Although an instance gets serialized one times, it gets deserialized two
	// times
	// first during restoring treestructure and second during restoring
	// componentstate
	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
		in.defaultReadObject();
		this.logId = this.getClass().getSimpleName();
		// ((SfUserSession)Webfoundation.getInstance().getUserSession()).getEventHandlerForComponents().addToAllPossible(this);

		if (log.isDebugEnabled()) {
			log.debug("DeSERIALIZE (restore) component [" + logId + "] (" + this.hashCode() + ")");
		}
	}

	private void writeObject(final ObjectOutputStream aOutputStream) throws IOException {
		aOutputStream.defaultWriteObject();
		if (log.isDebugEnabled()) {
			log.debug("SERIALIZE (save) component [" + logId + "] (" + this.hashCode() + ")");
		}
	}

	// //EXTERNALIZATION
	// public void readExternal(ObjectInput in) throws IOException,
	// ClassNotFoundException
	// {
	// this.attributes = (Map)in.readObject();
	// this.eventMap =(Map)in.readObject();
	// this.templateMap = (Map)in.readObject();
	// this.uid = (String)in.readObject();
	// this.frameBinding = (String)in.readObject();
	//		
	// }
	//
	// public void writeExternal(ObjectOutput out) throws IOException
	// {
	// out.writeObject(this.attributes);
	// out.writeObject(this.eventMap);
	// out.writeObject(this.templateMap);
	// out.writeObject(this.uid);
	// out.writeObject(this.frameBinding);
	// }

}