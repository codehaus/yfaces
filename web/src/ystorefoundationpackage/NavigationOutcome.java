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
package ystorefoundationpackage;

/**
 * Enumeration of outcome id's from available (configured) navigation cases.
 */
public enum NavigationOutcome
{
	CART_PAGE("cartPage"), //
	LOGIN_PAGE("loginPage"), //
	TELLAFRIEND_PAGE("tellAFriendPage"), //
	WELCOME_PAGE("welcomePage"), //
	FORGOT_PW_PAGE("forgotPasswordPage"), //
	;

	public final String id;

	private NavigationOutcome(final String outcomeId)
	{
		this.id = outcomeId;
	}

}
