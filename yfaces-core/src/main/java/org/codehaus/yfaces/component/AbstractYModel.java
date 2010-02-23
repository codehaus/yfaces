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

package org.codehaus.yfaces.component;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;

/**
 * @author Denny Strietzbaum
 * 
 */
public abstract class AbstractYModel implements YModel {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(AbstractYModel.class);

	private Map<String, Object> attributes = null;
	private String frameBinding = null;

	private String id = null;
	private String ns = null;

	private String uid = null;

	private transient String logId = this.getClass().getSimpleName();
	private transient YComponent cmpInfo = null;

	public AbstractYModel() {
		this.uid = this.getClass().getName() + String.valueOf(Math.random());
	}

	public Map<String, Object> getAttributes() {
		if (this.attributes == null) {
			this.attributes = new HashMap<String, Object>();
		}
		return this.attributes;
	}

	public YComponent getComponent() {
		if (this.cmpInfo == null) {
			this.cmpInfo = YComponentRegistry.getInstance().getComponent(ns, id);
		}
		return this.cmpInfo;
	}

	void setYComponentInfo(final YComponent info) {
		this.id = info.getViewId();
		this.ns = info.getNamespace();
		this.cmpInfo = info;
	}

	/**
	 * Creates an {@link YEventHandler} whose default {@link YEventListener} does nothing.
	 * 
	 * @return {@link YEventHandler}
	 */
	public <T extends YModel> YEventHandler<T> createEventHandler() {
		return this.createEventHandler(new DefaultYEventListener<T>());
	}

	/**
	 * Creates an {@link YEventHandler} whose default {@link YEventListener} is the passed one.
	 * 
	 * @param listener
	 *          {@link YEventListener} default listener
	 * @return {@link YEventHandler}
	 */
	public <T extends YModel> YEventHandler<T> createEventHandler(final YEventListener<T> listener) {
		final YEventHandler<T> result = new YEventHandlerImpl<T>(listener);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.YComponent#postInitialize()
	 */
	public void validate() {
	};

	public void refresh() {

	}

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
			final ValueExpression ve = fc.getApplication().getExpressionFactory().createValueExpression(
					fc.getELContext(), this.frameBinding, Object.class);

			final Object value = ve.getValue(fc.getELContext());
			if (value instanceof YFrame) {
				result = (YFrame) value;
			}
		}
		return result;
	}

	public <T extends YModel> T newInstance(final String id) {
		return this.newInstance(null, id);
	}

	public <T extends YModel> T newInstance(final String ns, final String id) {

		final YComponent cmpInfo = YComponentRegistry.getInstance().getComponent(ns, id);
		return (T) cmpInfo.getModelProcessor().createModel();
	}

	public <T extends YModel> T newInstance(final T template) {
		T result = null;
		final Class<T> clazz = (Class) template.getClass();
		try {
			final Constructor<T> c = clazz.getConstructor(YModel.class);
			result = c.newInstance(template);
		} catch (final Exception e) {
			throw new YFacesException(clazz + " can't be created; missing Constructor which accepts "
					+ YModel.class);
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
		return (obj.getClass().equals(this.getClass())) && ((AbstractYModel) obj).uid.equals(this.uid);
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
