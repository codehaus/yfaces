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
package ystorefoundationpackage.yfaces.component.product.tellfriend;

/**
 * @author lin.jiang
 * 
 */
public class TellAFriendContextImpl implements TellAFriendContext
{

	private String fromEmailAddress = null;
	private String toEmailAddress = null;
	private String productName = null;
	private String userName = null;
	private String comment = null;
	private String productLink = null;

	public String getFromEmailAddress()
	{
		return fromEmailAddress;
	}

	public void setFromEmailAddress(final String fromEmailAddress)
	{
		this.fromEmailAddress = fromEmailAddress;
	}

	public String getToEmailAddress()
	{
		return toEmailAddress;
	}

	public void setToEmailAddress(final String toEmailAddress)
	{
		this.toEmailAddress = toEmailAddress;
	}

	public String getProductName()
	{
		return productName;
	}

	public void setProductName(final String productName)
	{
		this.productName = productName;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(final String userName)
	{
		this.userName = userName;
	}

	public String getComment()
	{
		return this.comment;
	}

	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	public String getProductLink()
	{
		return this.productLink;
	}

	public void setProductLink(final String productLink)
	{
		this.productLink = productLink;
	}

}
