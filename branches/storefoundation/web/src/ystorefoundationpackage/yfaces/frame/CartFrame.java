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

import org.codehaus.yfaces.component.AbstractYComponentContainer;

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.yfaces.component.cart.CartTableComponent;
import ystorefoundationpackage.yfaces.component.user.DefaultLoginComponent;
import ystorefoundationpackage.yfaces.component.user.LoginComponent;

/**
 * Renders the detail information of the cart.
 */
public class CartFrame extends AbstractYComponentContainer {
	private static final long serialVersionUID = 57824365959445L;

	private LoginComponent loginCmp = null;
	private CartTableComponent cartTableCmp = null;

	public CartFrame() {
		super();
	}

	public LoginComponent getLoginComponent() {
		if (this.loginCmp == null) {
			this.loginCmp = this.createLoginComponent();
		}
		return this.loginCmp;
	}

	public void setLoginComponent(LoginComponent cmp) {
		this.loginCmp = cmp;
	}

	public CartTableComponent getCartTableComponent() {
		return this.cartTableCmp;
	}

	public void setCartTableComponent(CartTableComponent cmp) {
		this.cartTableCmp = cmp;
	}

	private LoginComponent createLoginComponent() {
		final LoginComponent result = new DefaultLoginComponent();
		result.setErrorForward(NavigationOutcome.LOGIN_PAGE.id);
		result.setSuccessForward(NavigationOutcome.CART_PAGE.id);
		return result;
	}
}
