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

import de.hybris.platform.cms.CmsService;
import de.hybris.platform.cms.model.ParagraphContentModel;
import de.hybris.platform.cms.model.WebsiteModel;

import ystorefoundationpackage.domain.YStorefoundation;


public class ConvertCmsPageModel extends ConvertItemModel<ParagraphContentModel>
{

	@Override
	public String convertObjectToString()
	{
		final ParagraphContentModel content = getConvertedObject();
		final String result = super.getValidIdOrPK(content.getCode(), content);
		return result;
	}

	@Override
	public ParagraphContentModel convertStringToObject()
	{
		ParagraphContentModel result = null;
		final String id = getConvertedString();

		//first try: assume 'code'
		final CmsService cmsService = YStorefoundation.getRequestContext().getPlatformServices().getCmsService();
		final WebsiteModel ws = cmsService.getSessionStore().getWebsite();
		result = cmsService.findParagraphPageBy(ws, id);

		//second try: assume PK
		if (result == null)
		{
			result = super.convertStringToObject();
		}
		return result;
	}

}
