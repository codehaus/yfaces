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

import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 */
public class ItemModelConverter implements Converter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
	{
		Object result = null;
		if (arg2 != null && arg2.trim().length() > 0)
		{
			final Item item = JaloSession.getCurrentSession().getItem(PK.parse(arg2));
			result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().get(item);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
	{
		String result = "";
		if (arg2 != null)
		{
			final Item item = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(arg2);
			result = item.getPK().toString();
		}
		return result;
	}



}
