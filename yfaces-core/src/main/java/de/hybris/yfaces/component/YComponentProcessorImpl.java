package de.hybris.yfaces.component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesException;

public class YComponentProcessorImpl implements YComponentProcessor {

	private static final Logger log = Logger.getLogger(YComponentProcessorImpl.class);

	private YComponentInfoImpl cmpInfo = null;

	public YComponentProcessorImpl(final YComponentInfoImpl cmpInfo) {
		this.cmpInfo = cmpInfo;
	}

	public YComponent createComponent() {
		YComponent result = null;
		try {
			result = (YComponent) cmpInfo.getModelImplClass().newInstance();
			this.initializeComponent(result);
		} catch (final Exception e) {
			throw new YFacesException("Can't create Component model " + cmpInfo.getModelImplementation(), e);
		}

		//TODO instance handling

		return result;
	}

	public void initializeComponent(final YComponent cmp) {
		if (cmp instanceof YComponent) {
			((AbstractYComponent) cmp).setYComponentInfo(cmpInfo);
		}
	}

	public void setProperty(final YComponent cmp, final String property, Object value) {

		final Method method = cmpInfo.getAllProperties().get(property);

		try {

			// JSF 1.2: do type coercion (e.g. String->Integer)
			value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
					.coerceToType(value, method.getParameterTypes()[0]);

			// invoke setter
			method.invoke(cmp, value);

		} catch (final Exception e) {
			if (e instanceof IllegalArgumentException) {
				log.error(cmpInfo.getId() + " Error converting " + value.getClass().getName() + " to "
						+ method.getParameterTypes()[0].getName());
			} else {
				if (e instanceof InvocationTargetException) {
					log.error(cmpInfo.getId() + " Error while executing setter for attribute '" + property
							+ "'");
				}
			}
			final String error = cmpInfo.getId() + " Error setting attribute '" + property + "' at "
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

			log.debug(cmpInfo.getId() + "injected Attribute " + property + " ("
					+ (_value.length() < 30 ? _value : _value.substring(0, 29).concat("...")) + ")" + suffix);
		}
	}

}
