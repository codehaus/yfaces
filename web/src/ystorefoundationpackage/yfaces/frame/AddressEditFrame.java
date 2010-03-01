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

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.yfaces.component.address.EditAddressComponent;

/**
 * Renders the page for the user to edit the address information.
 * 
 */
public class AddressEditFrame extends AbstractYFrame {
	private EditAddressComponent editAddressCmp = null;

	public EditAddressComponent getEditAddressComponent() {
		if (this.editAddressCmp == null) {
			this.editAddressCmp = (EditAddressComponent) createDefaultYModel(YComponent.EDIT_ADDRESS.viewId);
		}
		return this.editAddressCmp;
	}

	public void setEditAddressComponent(EditAddressComponent cmp) {
		this.editAddressCmp = cmp;
	}

}
