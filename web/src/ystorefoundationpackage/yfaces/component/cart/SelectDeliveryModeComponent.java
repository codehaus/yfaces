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
package ystorefoundationpackage.yfaces.component.cart;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * The user can select one delivery mode for the order with this component.
 */
public interface SelectDeliveryModeComponent extends YModel
{

	DeliveryModeModel getSelectedDeliveryMode();

	void setSelectedDeliveryMode(DeliveryModeModel selected);

	void setAvailableDeliveryModes(List<DeliveryModeModel> list);

	List<DeliveryModeModel> getAvailableDeliveryModes();

	void setSupportedDeliveryModes(List<DeliveryModeModel> list);

	List<DeliveryModeModel> getSupportedDeliveryModes();

	CartModel getCart();

	void setCart(CartModel cart);

	SfSelectItemGroup<DeliveryModeModel> getDeliveryModeSelector();

	YEventHandler<SelectDeliveryModeComponent> getSelectDeliveryModeEvent();
}
