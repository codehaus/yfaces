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
package ystorefoundationpackage.yfaces.component.misc;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.codehaus.yfaces.component.YModel;



/**
 * This components renders the advertisement banners.
 */
public interface AdBannerComponent extends YModel
{

	/**
	 * Helper interface for <code>AdBannerComponent</code>.
	 */
	interface AdBannerMetaData extends Serializable
	{
		String getMimeType();

		int getWidth();

		int getHeight();

		Map<String, Object> getParameters();

		Object getSource();

		Object getTarget();
	}

	List<AdBannerMetaData> getBannerList();

	AdBannerMetaData getBannerById(String id);

	AdBannerMetaData addBanner(String id, Object bannerSource, Object bannerTarget);

}
