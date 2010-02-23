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
package ystorefoundationpackage.domain.impl;

import ystorefoundationpackage.domain.RegistrationInfoContext;

/**
 * 
 * 
 */
public class RegistrationInfoContextImpl extends AbstractEmailNotificationContextImpl implements RegistrationInfoContext
{

	private String emailAddress = null;
	private String loginName = null;

	public String getEmailAddress()
	{
		return this.emailAddress;
	}

	public String getRegistrationDate()
	{
		return null;
	}

	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

	public String getLoginName()
	{
		return loginName;
	}

	public void setLoginName(final String loginName)
	{
		this.loginName = loginName;
	}

}
