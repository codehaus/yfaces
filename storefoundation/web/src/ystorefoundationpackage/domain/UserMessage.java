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
package ystorefoundationpackage.domain;

import java.io.Serializable;



/**
 * 
 * 
 */
public class UserMessage implements Serializable
{
	private String summary = null;
	private String detail = null;
	private String type = null;

	public UserMessage(final String type)
	{
		this.type = type;
	}

	public UserMessage(final String type, final String summary, final String detail)
	{
		this.summary = summary;
		this.detail = detail;
		this.type = type;
	}

	public String getType()
	{
		return this.type;
	}

	public String getSummary()
	{
		return this.summary;
	}

	public String getDetail()
	{
		return this.detail;
	}

	public void setSummary(final String summary)
	{
		this.setSummary(summary, (Object[]) null);
	}

	public void setSummary(final String summary, final Object... params)
	{
		this.summary = YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(summary, params);
	}

	public void setUnlocalizedSummary(final String summary)
	{
		this.summary = summary;
	}


	@Override
	public String toString()
	{
		return type + ":" + summary + detail;
	}

}
