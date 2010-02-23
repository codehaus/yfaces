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
import org.codehaus.yfaces.component.YComponentBinding;

import ystorefoundationpackage.yfaces.component.user.RegistrationComponent;


/**
 * Renders the page for the new user to register in the store foundation.
 * 
 */
public class RegistrationFrame extends AbstractYFrame
{
	private static final long serialVersionUID = 57824365972845L;

	private final YComponentBinding<RegistrationComponent> registrationCmp = super.createComponentBinding();

	public YComponentBinding<RegistrationComponent> getRegistrationComponent()
	{
		return this.registrationCmp;
	}

}
