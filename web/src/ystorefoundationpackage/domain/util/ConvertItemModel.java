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
package ystorefoundationpackage.domain.util;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;

import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.AbstractObjectConverter;
import ystorefoundationpackage.domain.YStorefoundation;


public class ConvertItemModel<T extends ItemModel> extends AbstractObjectConverter<T>
{

	private static final Logger log = Logger.getLogger(ConvertItemModel.class);

	/** Forces PK conversion from Object to String **/
	private static final String PARAM_FORCE_PK_ID = "pkConversion";

	private boolean pKConversionEnabled = false;

	@Override
	public String convertObjectToString()
	{
		final Object object = getConvertedObject();
		String result = null;
		if (object instanceof ItemModel)
		{
			result = ((ItemModel) object).getPk().toString();
		}
		return result;
	}

	@Override
	public T convertStringToObject()
	{
		T result = null;
		final String id = getConvertedString();
		final Item item = getItemByPK(id);
		if (item != null)
		{
			result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().get(item);
		}
		return result;
	}

	protected <B extends Item> B getItemByPK(final String id)
	{
		B result = null;
		try
		{
			final PK pk = PK.parse(id);
			result = JaloSession.getCurrentSession().getItem(pk);
		}
		catch (final Exception e)
		{
			log.error(id + " is not a valid PK");
		}
		return result;
	}

	protected boolean isPKConversionEnabled()
	{
		return this.pKConversionEnabled;
	}

	@Override
	public void setProperties(final Map properties)
	{
		super.setProperties(properties);

		final Boolean _forcePK = (Boolean) properties.get(PARAM_FORCE_PK_ID);
		this.pKConversionEnabled = _forcePK != null ? _forcePK.booleanValue() : false;
	}

	/**
	 * @param id
	 *           ID which is used when valid
	 * @param item
	 *           PK which is used as fall back
	 * @return a valid ID
	 */
	protected String getValidIdOrPK(final String id, final Object item)
	{
		String result = id;
		if (isPKConversionEnabled())
		{
			result = this.convertObjectToPK(item);
		}
		else
		{
			if (id == null || id.trim().length() == 0)
			{
				log.error("Can't use converted ID due to invalid resultstring (null or zero); use PK as fallback");
				result = convertObjectToPK(item);
			}
		}
		return result;
	}

	private String convertObjectToPK(final Object object)
	{
		String result = null;
		if (object instanceof ItemModel)
		{
			result = ((ItemModel) object).getPk().toString();
		}
		else
		{
			log.error("Currently only instances of " + ItemModel.class.getName() + " are supported for PK conversion");
		}
		return result;
	}

}
