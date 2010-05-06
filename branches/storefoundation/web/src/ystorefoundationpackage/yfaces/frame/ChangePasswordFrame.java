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

import ystorefoundationpackage.yfaces.component.user.ChangePasswordComponent;

/**
 * Renders the page for the user to change the password.
 * 
 */
public class ChangePasswordFrame extends AbstractYComponentContainer {

	private static final long serialVersionUID = -3624214930177657263L;

	private ChangePasswordComponent changePasswordComponent = null;

	// constructor
	public ChangePasswordFrame() {
		super();
	}

	public ChangePasswordComponent getChangePasswordComponent() {
		return changePasswordComponent;
	}

	public void setChangePasswordComponent(ChangePasswordComponent cmp) {
		this.changePasswordComponent = cmp;
	}

}
