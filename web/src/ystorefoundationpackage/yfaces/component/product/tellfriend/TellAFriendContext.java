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
public interface TellAFriendContext
{

	public String getFromEmailAddress();

	public void setFromEmailAddress(String fromEmailAddress);

	public String getToEmailAddress();

	public void setToEmailAddress(String toEmailAddress);

	public String getUserName();

	public void setUserName(String userName);

	public String getProductName();

	public void setProductName(String productName);

	public String getComment();

	public void setComment(String comment);

	public String getProductLink();

	public void setProductLink(String productLink);

	//TODO picture of the product

}
