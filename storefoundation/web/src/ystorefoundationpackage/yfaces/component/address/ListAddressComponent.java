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
package ystorefoundationpackage.yfaces.component.address;

import de.hybris.platform.core.model.user.AddressModel;

import java.util.List;

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;



/**
 * This component lists all addresses for the user.
 */
public interface ListAddressComponent extends YComponent
{
	List<AddressModel> getAddressList();

	void setAddressList(List<AddressModel> addressList);


	//provide a template component for show
	ShowAddressComponent getShowAddressComponent();

	ShowAddressComponent getShowAddressComponentTemplate();

	//	void setShowAddressComponentTemplate(ShowAddressComponent showAddressComponent);

	YComponentEventHandler<ListAddressComponent> getCreateAddressEvent();

}
