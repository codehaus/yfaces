/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2009 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package ystorefoundationpackage.datatable.ext.cell;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ystorefoundationpackage.datatable.DataTableException;


/**
 * 
 * 
 */
public class BeanPropertyCellConverter<V> implements DataTableCellConverter<V>
{
	private String beanProperty = null;
	private transient final Map<Class, Method> methodMap = new HashMap();

	public BeanPropertyCellConverter(final String property)
	{
		this.beanProperty = property;
	}

	public V getConvertedCellValue(final Object sourceCellValue)
	{
		V result = null;
		try
		{
			final Method method = getMethod(sourceCellValue.getClass());
			result = (V) method.invoke(sourceCellValue);
		}
		catch (final Exception e)
		{
			throw new DataTableException("Can't access beanproperty '" + this.beanProperty, e);
		}

		return result;
	}

	private Method getMethod(final Class clazz)
	{
		Method result = this.methodMap.get(clazz);
		if (result == null)
		{
			try
			{
				final PropertyDescriptor d = new PropertyDescriptor(beanProperty, clazz, beanProperty, null);
				result = d.getReadMethod();
			}
			catch (final Exception e)
			{
				throw new DataTableException("Can't access beanproperty '" + beanProperty + "' at class " + clazz.getName(), e);
			}
			this.methodMap.put(clazz, result);
		}
		return result;
	}


}
