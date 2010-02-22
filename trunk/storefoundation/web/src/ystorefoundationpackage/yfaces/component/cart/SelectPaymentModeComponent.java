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
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;



/**
 * The user can select one payment mode for the order with this component.
 */
public interface SelectPaymentModeComponent extends YComponent
{
	CartModel getCart();

	void setCart(CartModel cart);


	YComponentEventHandler<SelectPaymentModeComponent> getChooseAdvanceEvent();

	YComponentEventHandler<SelectPaymentModeComponent> getChooseInvoiceEvent();

}
