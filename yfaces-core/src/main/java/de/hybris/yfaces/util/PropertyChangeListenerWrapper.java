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
package de.hybris.yfaces.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;

import de.hybris.yfaces.YFacesException;

/**
 * Delegates a general, untyped {@link PropertyChangeListener} to a more specific listener given by
 * an interface definition.
 * 
 * @author Denny.Strietzbaum
 */
public class PropertyChangeListenerWrapper implements PropertyChangeListener {
	private Object listener = null;
	private DelegatorDescriptor descriptor = null;

	private static class DelegatorDescriptor {
		private Class<?> delegateToDefinition = null;
		private transient Method method = null;

		private DelegatorDescriptor(Class<?> definition) {
			this.delegateToDefinition = definition;
		}

		private Method getMethod() {
			if (this.method == null) {
				Method[] methods = this.delegateToDefinition.getMethods();
				if (methods.length > 1) {
					throw new YFacesException("Ambigious number of methods");
				}
				this.method = methods[0];
			}
			return this.method;
		}
	}

	public PropertyChangeListenerWrapper(Class<?> delegateDefinition, Object listener) {
		this.listener = listener;
		// maybe use a static lookup map?
		this.descriptor = new DelegatorDescriptor(delegateDefinition);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		Method method = this.descriptor.getMethod();
		try {
			method.invoke(listener, evt.getOldValue(), evt.getNewValue());
		} catch (Exception e) {
			throw new YFacesException("Can't reach eventlistener " + listener.getClass().getName()
					+ " (" + method.getName() + ")", e);
		}
	}

	//
	// NOTE:
	// Doesn't matter whether we have equals/hashCode or not because sun wraps
	// each
	// PropertyChangeListener again into an EventListenerAggreagte which doesn't
	// care about duplicates
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof PropertyChangeListenerWrapper)
				&& this.listener.equals(((PropertyChangeListenerWrapper) obj).listener);
	}

	@Override
	public int hashCode() {
		return listener.hashCode();
	}

}
