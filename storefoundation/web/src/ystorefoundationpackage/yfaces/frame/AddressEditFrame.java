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

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.yfaces.component.address.EditAddressComponent;


/**
 * Renders the page for the user to edit the address information.
 * 
 */
public class AddressEditFrame extends AbstractYFrame
{

	private final YModelBinding<EditAddressComponent> editAddressCmp = super
			.createComponentBinding(YComponent.EDIT_ADDRESS.viewId);

	public YModelBinding<EditAddressComponent> getEditAddressComponent()
	{
		return this.editAddressCmp;
	}

}
