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

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import de.hybris.platform.core.model.user.AddressModel;



/**
 * This component displays the user address.
 */
public interface ShowAddressComponent extends YComponent
{

	/**
	 * The Address which should be used.
	 * 
	 * @return Address which should be used.
	 */
	AddressModel getAddress();

	/**
	 * Sets the working address.
	 */
	void setAddress(AddressModel address);

	Object getOwner();

	void setOwner(Object owner);

	/**
	 * Checks whether the target is of type User and the current address is the users deliveraddress.
	 * 
	 * @return true when the current address is the deliveryaddress
	 */
	Boolean getDefaultDeliveryAddress();

	Boolean getDefaultPaymentAddress();

	void setDefaultDeliveryAddress(Boolean isDefaultDelivery);

	void setDefaultPaymentAddress(Boolean isDefaultPayment);

	YComponentEventHandler<ShowAddressComponent> getEditAddressEvent();

	YComponentEventHandler<ShowAddressComponent> getDeleteAddressEvent();

	YComponentEventHandler<ShowAddressComponent> getCustomAddressEvent();

	YComponentEventHandler<ShowAddressComponent> getChooseAddressAsDeliveryEvent();

	YComponentEventHandler<ShowAddressComponent> getChooseAddressAsPaymentEvent();


}
