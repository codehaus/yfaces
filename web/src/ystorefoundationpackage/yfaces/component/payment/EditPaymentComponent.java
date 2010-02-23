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

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;



/**
 * This component makes it possible for the user to edit the payment information.
 */
public interface EditPaymentComponent extends YModel
{

	/**
	 * Gets the {@link PaymentInfoModel} this model holds.
	 * 
	 * @return the PaymentInfoModel this model holds.
	 */
	public PaymentInfoModel getPaymentInfo();

	/**
	 * Sets the {@link PaymentInfoModel} this model holds.
	 * 
	 * @param paymentInfo
	 *           the PaymentInfoModel this model holds.
	 */
	public void setPaymentInfo(PaymentInfoModel paymentInfo);

	public YEventHandler<EditPaymentComponent> getSavePaymentInfoEvent();

	public YEventHandler<EditPaymentComponent> getCancelEditPaymentInfoEvent();

}
