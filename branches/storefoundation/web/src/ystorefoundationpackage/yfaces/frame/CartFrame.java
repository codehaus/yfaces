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

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.yfaces.component.cart.CartTableComponent;
import ystorefoundationpackage.yfaces.component.user.DefaultLoginComponent;
import ystorefoundationpackage.yfaces.component.user.LoginComponent;


/**
 * Renders the detail information of the cart.
 */
public class CartFrame extends AbstractYFrame
{
	private static final long serialVersionUID = 57824365959445L;

	private YModelBinding<LoginComponent> loginCmp = null;
	private YModelBinding<CartTableComponent> cartTableCmp = null;

	public CartFrame()
	{
		super();
		this.loginCmp = super.createComponentBinding(this.createLoginComponent());
		this.cartTableCmp = super.createComponentBinding();
	}

	public YModelBinding<LoginComponent> getLoginComponent()
	{
		return this.loginCmp;
	}

	public YModelBinding<CartTableComponent> getCartTableComponent()
	{
		return this.cartTableCmp;
	}

	private LoginComponent createLoginComponent()
	{
		final LoginComponent result = new DefaultLoginComponent();
		result.setErrorForward(NavigationOutcome.LOGIN_PAGE.id);
		result.setSuccessForward(NavigationOutcome.CART_PAGE.id);
		return result;
	}
}
