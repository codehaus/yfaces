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
package de.hybris.yfaces.util;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * {@link Converter} implementation which evaluates the 'converter' Attribute of enclosing
 * {@link UIParameter} components.<br/>
 * <br/>
 * example:<br/>
 * <code>
 * &lt;h:outputFormat value="formated {0} output {1}" converter="parameterConverter"&gt;<br/>
 * 	&lt;f:param value="#{bean.value1}" converter="converterId"/&gt;<br/>
 * 	&lt;f:param value="#{bean.value2}" converter="#{bean.myConverter}"/&gt;<br/>
 * 	&lt;h:outputFormat/&gt;<br/>
 * </code> <br/>
 * Without the given paramaterConverter all passed paramater values wouldn't be converted by their
 * given converter.<br/>
 * 
 * @author Denny.Strietzbaum
 */
public class ParameterConverter implements Converter {

	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {
		throw new UnsupportedOperationException();
	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		for (UIComponent child : arg1.getChildren()) {
			if (child instanceof UIParameter) {
				Object converter = child.getAttributes().get("converter");
				Converter _converter = null;

				if (converter instanceof Converter) {
					_converter = (Converter) converter;
				}

				if (converter instanceof String) {
					_converter = FacesContext.getCurrentInstance().getApplication()
							.createConverter((String) converter);
				}

				if (_converter != null) {
					String newValue = _converter.getAsString(FacesContext.getCurrentInstance(),
							child, ((UIParameter) child).getValue());
					((UIParameter) child).setValue(newValue);
				}
			}
		}
		return (String) arg2;
	}
}