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
package ystorefoundationpackage.yfaces.frame;

import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YModelBinding;

import ystorefoundationpackage.yfaces.component.user.DefaultLoginComponent;
import ystorefoundationpackage.yfaces.component.user.LoginComponent;


/**
 * Renders the login page.
 * 
 */
public class LoginFrame extends AbstractYFrame
{
	private YModelBinding<LoginComponent> loginCmp = null;

	public LoginFrame()
	{
		super();
		this.loginCmp = super.createComponentBinding(this.createLoginComponent());
	}

	public YModelBinding<LoginComponent> getLoginComponent()
	{
		return this.loginCmp;
	}

	private LoginComponent createLoginComponent()
	{
		final LoginComponent result = new DefaultLoginComponent();
		//result.setSuccessForward(OUTCOME.WELCOME_PAGE);
		return result;
	}
}
