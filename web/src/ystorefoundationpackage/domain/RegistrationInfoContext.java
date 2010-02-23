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

/**
 * Context information for email confirmation when the new user registers.
 * 
 */
public interface RegistrationInfoContext extends EmailNotificationContext
{

	/**
	 * @return the email address of the new user
	 */
	public String getEmailAddress();

	/**
	 * Sets the email address of the new user.
	 * 
	 * @param emailAddress
	 *           email address
	 */
	public void setEmailAddress(String emailAddress);

	/**
	 * @return login name of the user
	 */
	public String getLoginName();

	/**
	 * Sets the login name.
	 * 
	 * @param loginName
	 *           login name
	 */
	public void setLoginName(String loginName);

	/**
	 * @return the registration date
	 */
	public String getRegistrationDate();

}
