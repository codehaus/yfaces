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
package org.codehaus.yfaces.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.codehaus.yfaces.YFacesException;


/**
 * Delegates a general, untyped {@link PropertyChangeListener} to a more specific listener given by
 * an interface definition.
 * 
 * @author Denny Strietzbaum
 */
public class PropertyChangeListenerWrapper implements PropertyChangeListener {
	private Object listener = null;
	private DelegatorDescriptor descriptor = null;

	private static class DelegatorDescriptor {
		private Class<?> delegateToDefinition = null;
		private transient Method method = null;

		private DelegatorDescriptor(final Class<?> definition) {
			this.delegateToDefinition = definition;
		}

		private Method getMethod() {
			if (this.method == null) {
				final Method[] methods = this.delegateToDefinition.getMethods();
				if (methods.length > 1) {
					throw new YFacesException("Ambigious number of methods");
				}
				this.method = methods[0];
			}
			return this.method;
		}
	}

	public PropertyChangeListenerWrapper(final Class<?> delegateDefinition, final Object listener) {
		this.listener = listener;
		// maybe use a static lookup map?
		this.descriptor = new DelegatorDescriptor(delegateDefinition);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		final Method method = this.descriptor.getMethod();
		try {
			method.invoke(listener, evt.getOldValue(), evt.getNewValue());
		} catch (final Exception e) {
			final String _method = method.getName();
			final Class<?>[] _ptypes = method.getParameterTypes();
			final String _listener = listener.getClass().getName() + "#" + _method + "("
					+ _ptypes[0].getName() + "," + _ptypes[1].getName() + ")";
			String error = null;

			if (e instanceof InvocationTargetException) {
				// listener could be invoked but throws an exception 
				error = "Error while processing listener: " + _listener;
			} else {
				// listener could not be invoked (IllegalArgumentException etc.)
				final String _passed1 = evt.getOldValue() != null ? evt.getOldValue().getClass().getName()
						: "null";
				final String _passed2 = evt.getNewValue() != null ? evt.getNewValue().getClass().getName()
						: "null";
				error = "Can't invoke listener: " + _listener + " with current arguments:" + _passed1 + ","
						+ _passed2;
			}
			throw new YFacesException(error, e);

		}
	}

	//
	// NOTE:
	// Doesn't matter whether we have equals/hashCode or not because sun wraps
	// each
	// PropertyChangeListener again into an EventListenerAggreagte which doesn't
	// care about duplicates
	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof PropertyChangeListenerWrapper)
				&& this.listener.equals(((PropertyChangeListenerWrapper) obj).listener);
	}

	@Override
	public int hashCode() {
		return listener.hashCode();
	}

}
