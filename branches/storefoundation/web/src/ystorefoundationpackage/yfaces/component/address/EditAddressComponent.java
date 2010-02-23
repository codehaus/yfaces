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

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;




/**
 * The user can edit the address with this component.
 */
public interface EditAddressComponent extends YModel
{

	//model
	AddressModel getAddress();

	void setAddress(AddressModel address);

	/**
	 * Get available Countries for editing or creating a new Address
	 * 
	 * @return List of SelectItems
	 */
	List<? extends SelectItem> getAvailableCountries();

	/**
	 * Set available Countries for editing or creating a new Address
	 * 
	 * @param countries
	 */
	void setAvailableCountries(List<? extends SelectItem> countries);


	YEventHandler<EditAddressComponent> getSaveAddressEvent();

	YEventHandler<EditAddressComponent> getCancelEditAddressEvent();

}
