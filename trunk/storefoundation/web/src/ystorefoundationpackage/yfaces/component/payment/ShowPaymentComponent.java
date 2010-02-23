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
package ystorefoundationpackage.yfaces.component.payment;

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;



/**
 * This component displays the payment information in detail.
 */
public interface ShowPaymentComponent extends YComponent
{
	public void setCart(CartModel cart);

	public CartModel getCart();

	/**
	 * Returns the {@link PaymentInfoModel} this model holds.
	 * 
	 * @return the PaymentInfoModel this model represents.
	 */
	public PaymentInfoModel getPaymentInfo();

	/**
	 * Sets the {@link PaymentInfoModel} this model holds.
	 * 
	 * @param paymentInfo
	 *           the PaymentInfoModel this model should represent
	 */
	public void setPaymentInfo(PaymentInfoModel paymentInfo);

	public PaymentModeModel getPaymentMode();

	public void setPaymentMode(PaymentModeModel paymentMode);



	//events
	public YComponentEventHandler<ShowPaymentComponent> getCustomPaymentEvent();

	public YComponentEventHandler<ShowPaymentComponent> getDeletePaymentEvent();

	public YComponentEventHandler<ShowPaymentComponent> getEditPaymentEvent();

}
