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

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 */
public class EnumerationValueModelConverter extends ItemModelConverter
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.ItemModelConverter#getAsString(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
	{
		return super.getAsString(arg0, arg1, arg2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.ItemModelConverter#getAsObject(javax.faces.context.FacesContext,
	 * javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
	{
		Object result = null;
		if (arg2 != null && arg2.trim().length() > 0)
		{
			final Item item = JaloSession.getCurrentSession().getItem(PK.parse(arg2));
			final PlatformServices ps = YStorefoundation.getRequestContext().getPlatformServices();

			// returns a returns a HybrisEnumValue
			result = ps.getModelService().get(item);

			// but we need an EnumerationValueModel
			result = ps.getTypeService().getEnumerationValue((HybrisEnumValue) result);
		}
		return result;
	}


}
