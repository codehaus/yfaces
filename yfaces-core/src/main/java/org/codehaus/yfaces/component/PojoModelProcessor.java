package org.codehaus.yfaces.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.el.ExpressionFactory;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;

public class PojoModelProcessor<T> implements ModelProcessor<T> {

	private static final Logger log = Logger.getLogger(YModelProcessor.class);

	protected YComponentImpl cmpInfo = null;

	public PojoModelProcessor(final YComponentImpl cmpInfo) {
		this.cmpInfo = cmpInfo;
	}

	public T createModel() {
		T result = null;
		try {
			result = (T) cmpInfo.getModelImplementation().newInstance();
			this.setYComponent(result);
		} catch (final Exception e) {
			throw new YFacesException("Can't create Component model " + cmpInfo.getModelImplementation(),
					e);
		}

		return result;
	}

	public void setYComponent(final T model) {

	}

	public void setYFrame(final T model, final YFrame frame, final String frameProperty) {

	}

	public void validateModel(final T model) {
		// NOP
	}

	public void setProperty(final T cmp, final String property, Object value) {

		final Method method = cmpInfo.getAllProperties().get(property);

		try {

			// JSF 1.2: do type coercion (e.g. String->Integer)
			final ExpressionFactory ef = FacesContext.getCurrentInstance().getApplication()
					.getExpressionFactory();
			value = ef.coerceToType(value, method.getParameterTypes()[0]);

			// invoke setter
			method.invoke(cmp, value);

		} catch (final Exception e) {
			if (e instanceof IllegalArgumentException) {
				log.error(cmpInfo.getViewId() + " Error converting " + value.getClass().getName() + " to "
						+ method.getParameterTypes()[0].getName());
			} else {
				if (e instanceof InvocationTargetException) {
					log.error(cmpInfo.getViewId() + " Error while executing setter for attribute '"
							+ property + "'");
				}
			}
			final String error = cmpInfo.getViewId() + " Error setting attribute '" + property + "' at "
					+ cmp.getClass().getSimpleName() + "(" + method + ")";
			throw new YFacesException(error, e);
		}

		// some nice debug output for bughunting
		if (log.isDebugEnabled()) {
			final String _value = (value != null) ? value.toString() : "null";
			String suffix = "";
			if (value instanceof Collection<?>) {
				suffix = "(count:" + ((Collection<?>) value).size() + ")";
			}

			log.debug(cmpInfo.getViewId() + "injected Attribute " + property + " ("
					+ (_value.length() < 30 ? _value : _value.substring(0, 29).concat("...")) + ")" + suffix);
		}
	}
}
