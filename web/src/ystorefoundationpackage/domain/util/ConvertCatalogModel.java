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

import de.hybris.platform.catalog.model.CatalogModel;

import ystorefoundationpackage.domain.YStorefoundation;


public class ConvertCatalogModel extends ConvertItemModel<CatalogModel>
{

	@Override
	public String convertObjectToString()
	{
		final CatalogModel catalog = getConvertedObject();
		final String result = super.getValidIdOrPK(catalog.getId(), catalog);
		return result;
	}

	@Override
	public CatalogModel convertStringToObject()
	{
		//first try: assume catalog id
		final String id = getConvertedString();
		CatalogModel result = YStorefoundation.getRequestContext().getPlatformServices().getCatalogService().getCatalog(id);

		//second try: assume PK (regardless any PK flags)
		if (result == null)
		{
			result = super.convertStringToObject();
		}

		return result;
	}

}
