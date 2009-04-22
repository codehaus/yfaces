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

package de.hybris.yfaces.component;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;

import de.hybris.yfaces.YFacesException;

/**
 * @author Denny.Strietzbaum
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
			boolean isMethodBinding = this.action.startsWith("#{");
			result = this.action;
			if (isMethodBinding) {
				//				final MethodBinding mb = FacesContext.getCurrentInstance().getApplication()
				//						.createMethodBinding(this.action, (Class[]) null);
				//				result = (String) mb.invoke(FacesContext.getCurrentInstance(), null);
				ELContext elCtx = FacesContext.getCurrentInstance().getELContext();
				ExpressionFactory elFac = FacesContext.getCurrentInstance().getApplication()
						.getExpressionFactory();
				MethodExpression me = elFac.createMethodExpression(elCtx, this.action, null,
						new Class[] {});
				result = (String) me.invoke(elCtx, null);

			}
		} else {
			result = this.action();
		}
		return result;

	}

	protected void fireActionListener(YComponentEvent<T> event) {
		if (this.actionListener != null) {
			this.invokeYComponentListener(this.actionListener, event);
		} else {
			this.actionListener(event);
		}
	}

	protected void fireValueChangeListener(YComponentEvent<T> event) {
		if (this.valueChangeListener != null) {
			this.invokeYComponentListener(this.valueChangeListener, event);
		} else {
			this.valueChangeListener(event);
		}
	}

	public void setAction(String binding) {
		this.action = binding;
	}

	public void setActionListener(String binding) {
		this.checkMethodBinding(binding);
		this.actionListener = binding;
	}

	public void setValueChangeListener(String binding) {
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
	 *            binding to check
	 * @throws YFacesException
	 *             when binding is not valid
	 */
	private void checkMethodBinding(String binding) {
		final boolean isMethodBinding = binding.startsWith("#{");
		if (!isMethodBinding) {
			throw new YFacesException(binding + " is not a valid MethodBinding expression");
		}
	}

	private Object invokeYComponentListener(String binding, YComponentEvent<?> event) {
		MethodExpression me = null;

		ExpressionFactory elFac = FacesContext.getCurrentInstance().getApplication()
				.getExpressionFactory();
		ELContext elCtx = FacesContext.getCurrentInstance().getELContext();

		me = elFac.createMethodExpression(elCtx, binding, null,
				new Class[] { YComponentEvent.class });
		try {
			me.getMethodInfo(elCtx);
		} catch (MethodNotFoundException e1) {
			try {
				me = elFac.createMethodExpression(elCtx, binding, null, new Class[] { event
						.getClass() });
				me.getMethodInfo(elCtx);
			} catch (MethodNotFoundException e2) {
				String error1 = "binding(" + YComponentEvent.class.getName() + ")";
				String error2 = "binding(" + event.getClass() + ")";
				String error = "Invalid " + YComponentEventListener.class.getName()
						+ "; there's neither a " + error1 + " nor a" + error2;
				throw new YFacesException(error);
			}
		}

		// invoke method
		Object result = me.invoke(elCtx, new Object[] { event });

		return result;
	}

	//	private Object invokeYComponentListener(String binding, YComponentEvent<?> event) {
	//		MethodBinding mb = null;
	//
	//		// collect classes which can be used as method parameters
	//		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
	//		paramTypes.add(YComponentEvent.class);
	//		paramTypes.add(event.getClass());
	//
	//		// detect a valid binding which accepts one of the parameters
	//		for (Class<?> paramType : paramTypes) {
	//			mb = FacesContext.getCurrentInstance().getApplication().createMethodBinding(binding,
	//					new Class[] { paramType });
	//			try {
	//				mb.getType(FacesContext.getCurrentInstance());
	//				log.debug("Invoke MethodBinding " + binding + "(" + paramType.getName() + ")");
	//				break;
	//			} catch (Exception e) {
	//				mb = null;
	//			}
	//		}
	//
	//		// throw exception when no binding can be created
	//		if (mb == null) {
	//			throw new YFacesException("Invalid MethodBinding: " + binding + "; tried parameters: "
	//					+ paramTypes);
	//		}
	//
	//		// otherwise invoke binding
	//		final Object result = mb.invoke(FacesContext.getCurrentInstance(), new Object[] { event });
	//
	//		return result;
	//	}

}
