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

import ystorefoundationpackage.yfaces.component.user.DefaultLoginComponent;
import ystorefoundationpackage.yfaces.component.user.LoginComponent;

/**
 * Renders the login page.
 * 
 */
public class LoginFrame extends AbstractYFrame {
	private LoginComponent loginComponent = null;

	public LoginFrame() {
		super();
	}

	public LoginComponent getLoginComponent() {
		if (this.loginComponent == null) {
			this.loginComponent = new DefaultLoginComponent();
		}
		return loginComponent;
	}

	public void setLoginComponent(LoginComponent loginComponent) {
		this.loginComponent = loginComponent;
	}

}
