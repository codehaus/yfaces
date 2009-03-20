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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.application.YConversationContext;
import de.hybris.yfaces.application.YRequestContext;

/**
 * @author Denny.Strietzbaum
 * 
 */
public abstract class AbstractYComponentEventListener<T extends YComponent> implements
		YComponentEventListener<T> {

	private static final Logger log = Logger.getLogger(AbstractYComponentEventListener.class);

	private String action = null;
	private String actionListener = null;
	private String valueChangeListener = null;

	public abstract String action();

	public abstract void actionListener(YComponentEvent<T> event);

	public abstract void valueChangeListener(YComponentEvent<T> event);

	protected String fireAction() {
		String result = null;
		if (this.action != null) {
			final boolean isMethodBinding = this.action.startsWith("#{");
			result = this.action;
			if (isMethodBinding) {
				final MethodBinding mb = FacesContext.getCurrentInstance().getApplication()
						.createMethodBinding(this.action, (Class[]) null);
				result = (String) mb.invoke(FacesContext.getCurrentInstance(), null);
			}
		} else {
			result = this.action();
		}
		return result;

	}

	protected void fireActionListener(final YComponentEvent<T> event) {
		if (this.actionListener != null) {
			this.invokeYComponentBinding(this.actionListener, event);
		} else {
			this.actionListener(event);
		}
	}

	protected void fireValueChangeListener(final YComponentEvent<T> event) {
		if (this.valueChangeListener != null) {
			this.invokeYComponentBinding(this.valueChangeListener, event);
		} else {
			this.valueChangeListener(event);
		}
	}

	public void setAction(final String binding) {
		this.action = binding;
	}

	public void setActionListener(final String binding) {
		this.checkMethodBinding(binding);
		this.actionListener = binding;
	}

	public void setValueChangeListener(final String binding) {
		this.checkMethodBinding(binding);
		this.valueChangeListener = binding;
	}

	public String getAction() {
		return this.action;
	}

	public String getActionListener() {
		return this.actionListener;
	}

	public String getValueChangeListener() {
		return this.valueChangeListener;
	}

	public YConversationContext getNavigationContext() {
		return YRequestContext.getCurrentContext().getConversationContext();
	}

	/**
	 * Checks the passed String for a valid MethodBinding expression.
	 * 
	 * @param binding
	 *            binding to check
	 * @throws YFacesException
	 *             when binding is not valid
	 */
	private void checkMethodBinding(final String binding) {
		final boolean isMethodBinding = binding.startsWith("#{");
		if (!isMethodBinding) {
			throw new YFacesException(binding + " is not a valid MethodBinding expression");
		}
	}

	// private Object invokeYComponentBinding(String binding, YComponentEvent
	// event)
	// {
	// MethodExpression mb = null;
	//		
	// //collect classes which can be used as method parameters
	// List<Class> paramTypes = new ArrayList<Class>();
	// paramTypes.add(YComponentEvent.class);
	// paramTypes.add(event.getClass());
	//		
	// //detect a valid binding which accepts one of the parameters
	// for (Class paramType : paramTypes)
	// {
	// FacesContext fc = FacesContext.getCurrentInstance();
	// fc.getApplication().getExpressionFactory().createMethodExpression(
	// fc.getELContext(), binding, Object.class, new Class[]{paramType});
	// }
	//		
	// //throw exception when no binding can be created
	// if (mb == null)
	// throw new YFacesException("Invalid MethodBinding: " + binding +
	// "; tried parameters: " + paramTypes);
	//		
	// //otherwise invoke binding
	// Object result = null;
	// try
	// {
	// result = mb.invoke(FacesContext.getCurrentInstance().getELContext(), new
	// Object[]{event});
	// }
	// catch (Exception e)
	// {
	// throw new YFacesException("Error invoking methodbinding '" + binding +
	// "'", e);
	// }
	//		
	// return result;
	// }

	private Object invokeYComponentBinding(final String binding, final YComponentEvent event) {
		MethodBinding mb = null;

		// collect classes which can be used as method parameters
		final List<Class> paramTypes = new ArrayList<Class>();
		paramTypes.add(YComponentEvent.class);
		paramTypes.add(event.getClass());

		// detect a valid binding which accepts one of the parameters
		for (final Class paramType : paramTypes) {
			mb = FacesContext.getCurrentInstance().getApplication().createMethodBinding(binding,
					new Class[] { paramType });
			try {
				mb.getType(FacesContext.getCurrentInstance());
				log.debug("Invoke MethodBinding " + binding + "(" + paramType.getName() + ")");
				break;
			} catch (final Exception e) {
				mb = null;
			}
		}

		// throw exception when no binding can be created
		if (mb == null) {
			throw new YFacesException("Invalid MethodBinding: " + binding + "; tried parameters: "
					+ paramTypes);
		}

		// otherwise invoke binding
		final Object result = mb.invoke(FacesContext.getCurrentInstance(), new Object[] { event });

		return result;
	}

}
