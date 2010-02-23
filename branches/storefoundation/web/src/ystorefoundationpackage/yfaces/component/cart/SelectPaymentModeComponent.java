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

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.order.CartModel;



/**
 * The user can select one payment mode for the order with this component.
 */
public interface SelectPaymentModeComponent extends YModel
{
	CartModel getCart();

	void setCart(CartModel cart);


	YEventHandler<SelectPaymentModeComponent> getChooseAdvanceEvent();

	YEventHandler<SelectPaymentModeComponent> getChooseInvoiceEvent();

}
