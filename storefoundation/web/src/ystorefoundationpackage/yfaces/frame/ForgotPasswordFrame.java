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

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

import ystorefoundationpackage.yfaces.component.user.ForgotPasswordComponent;


/**
 * Renders the page for the user who forgets the password.
 * 
 */
public class ForgotPasswordFrame extends AbstractYFrame
{
	private YComponentBinding<ForgotPasswordComponent> forgotPwCmp = null;

	public ForgotPasswordFrame()
	{
		super();
		this.forgotPwCmp = super.createComponentBinding();
	}

	public YComponentBinding<ForgotPasswordComponent> getForgotPasswordComponent()
	{
		return this.forgotPwCmp;
	}

}
