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
package ystorefoundationpackage.faces;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;


public class SfELResolver extends ELResolver
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SfELResolver.class);

	//	//separate ELResolver for items to allow bean resolving
	//	//before getAttribute() is called at the item
	//	private ELResolver beanElResolver = new BeanELResolver();



	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object getValue(final ELContext elcontext, final Object base, final Object property)
	{

		Object result = null;

		//RESOLVE PROPERTY

		//resolve property only when no base is available
		//(JSF 1.1 PropertyResolver)
		if (base == null)
		{
			if ("userSession".equals(property))
			{
				result = YStorefoundation.getRequestContext().getSessionContext();
			}

		}

		elcontext.setPropertyResolved(result != null);
		return result;
	}

	@Override
	public void setValue(final ELContext elcontext, final Object base, final Object property, final Object value)
	{
		//no variable setting needed
		if (base == null)
		{
			return;
		}
	}

	@Override
	public Class<?> getType(final ELContext elcontext, final Object obj, final Object obj1)
	{
		return null;
	}


	@Override
	public boolean isReadOnly(final ELContext elcontext, final Object obj, final Object obj1)
	{
		return false;
	}



	/*
	 * (non-Javadoc) This is a design-time method
	 * 
	 * @see javax.el.ELResolver#getCommonPropertyType(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Class<?> getCommonPropertyType(final ELContext elcontext, final Object base)
	{
		return null;
	}

	/*
	 * (non-Javadoc) This is a design-time method
	 * 
	 * @see javax.el.ELResolver#getFeatureDescriptors(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext elcontext, final Object base)
	{
		if (base == null)
		{
			return null;
		}
		return new ArrayList<FeatureDescriptor>(0).iterator();
	}



}
