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

import org.codehaus.yfaces.component.YModel;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;


public interface TellAFriendComponent extends YModel
{

	//event: send email

	//model
	public ProductModel getProduct();

	public UserModel getUser();

	public String getEmailAddress();

	public void setEmailAddress(String emailAddress);

	public String getComment();

	public void setComment(String comment);

}
