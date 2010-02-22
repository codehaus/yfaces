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

package org.codehaus.yfaces.component;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;

import org.codehaus.yfaces.YFacesException;


/**
 * @author Denny Strietzbaum
 * 
 */
public abstract class AbstractYComponentEventListener<T extends YComponent> implements
		YComponentEventListener<T> {

	private static final long serialVersionUID = 1L;

	// private static final Logger log = Logger.getLogger(AbstractYComponentEventListener.class);

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
				final ELContext elCtx = FacesContext.getCurrentInstance().getELContext();
				final ExpressionFactory elFac = FacesContext.getCurrentInstance().getApplication()
						.getExpressionFactory();
				final MethodExpression me = elFac.createMethodExpression(elCtx, this.action, null,
						new Class[] {});
				result = (String) me.invoke(elCtx, null);

			}
		} else {
			result = this.action();
		}
		return result;

	}

	protected void fireActionListener(final YComponentEvent<T> event) {
		if (this.actionListener != null) {
			this.invokeYComponentListener(this.actionListener, event);
		} else {
			this.actionListener(event);
		}
	}

	protected void fireValueChangeListener(final YComponentEvent<T> event) {
		if (this.valueChangeListener != null) {
			this.invokeYComponentListener(this.valueChangeListener, event);
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

	/**
	 * Checks the passed String for a valid MethodBinding expression.
	 * 
	 * @param binding
	 *          binding to check
	 * @throws YFacesException
	 *           when binding is not valid
	 */
	private void checkMethodBinding(final String binding) {
		final boolean isMethodBinding = binding.startsWith("#{");
		if (!isMethodBinding) {
			throw new YFacesException(binding + " is not a valid MethodBinding expression");
		}
	}

	private Object invokeYComponentListener(final String binding, final YComponentEvent<?> event) {
		MethodExpression me = null;

		final ExpressionFactory elFac = FacesContext.getCurrentInstance().getApplication()
				.getExpressionFactory();
		final ELContext elCtx = FacesContext.getCurrentInstance().getELContext();

		me = elFac.createMethodExpression(elCtx, binding, null, new Class[] { YComponentEvent.class });
		try {
			me.getMethodInfo(elCtx);
		} catch (final MethodNotFoundException e1) {
			try {
				me = elFac.createMethodExpression(elCtx, binding, null, new Class[] { event.getClass() });
				me.getMethodInfo(elCtx);
			} catch (final MethodNotFoundException e2) {
				final String error1 = "binding(" + YComponentEvent.class.getName() + ")";
				final String error2 = "binding(" + event.getClass() + ")";
				final String error = "Invalid " + YComponentEventListener.class.getName()
						+ "; there's neither a " + error1 + " nor a" + error2;
				throw new YFacesException(error);
			}
		}

		// invoke method
		final Object result = me.invoke(elCtx, new Object[] { event });

		return result;
	}

}
