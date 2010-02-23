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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;

import org.codehaus.yfaces.component.AbstractYModel;


/**
 * Implementation of the <code>AdBannerComponent</code> interface.
 */
public class DefaultAdBannerComponent extends AbstractYModel implements AdBannerComponent
{
	private Map<String, AdBannerMetaData> bannerMap = null;

	/**
	 * Implementation of the <code>AdBannerMetaData</code> interface.
	 */
	public class DefaultAdBannerMetaData implements AdBannerMetaData
	{
		private final int width = -1;
		private final int height = -1;
		private Map<String, Object> params = null;
		private String mime = null;
		private Object source = null;
		private Object target = null;

		public DefaultAdBannerMetaData()
		{
			this.params = new HashMap<String, Object>();
		}

		public int getHeight()
		{
			return this.height;
		}

		public String getMimeType()
		{
			return this.mime;
		}

		public Map<String, Object> getParameters()
		{
			return this.params;
		}

		public Object getSource()
		{
			return this.source;
		}

		public Object getTarget()
		{
			return this.target;
		}

		public int getWidth()
		{
			return this.width;
		}

	}

	public DefaultAdBannerComponent()
	{
		super();
		this.bannerMap = new LinkedHashMap<String, AdBannerMetaData>();
	}

	public AdBannerMetaData addBanner(final String id, final Object bannerSource, final Object bannerTarget)
	{
		final DefaultAdBannerMetaData banner = new DefaultAdBannerMetaData();
		banner.source = bannerSource;
		banner.target = bannerTarget;

		if (bannerSource instanceof String)
		{
			final String type = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType((String) bannerSource);
			banner.mime = type;
		}

		this.bannerMap.put(id, banner);
		return banner;
	}

	public List<AdBannerMetaData> getBannerList()
	{
		return new ArrayList(this.bannerMap.values());
	}

	public AdBannerMetaData getBannerById(final String id)
	{
		return this.bannerMap.get(id);
	}

	@Override
	public void validate()
	{
		// TODO Auto-generated method stub

	}

}
