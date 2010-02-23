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
package ystorefoundationpackage.yfaces.component.user;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component sends the user a new password by Email if he/she forgets it.
 */
public interface ForgotPasswordComponent extends YModel
{
	public String getEmail();

	public void setEmail(String mail);

	public String getLogin();

	public void setLogin(String name);

	public YEventHandler<ForgotPasswordComponent> getSendPasswordEvent();
}
