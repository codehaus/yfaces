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

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;



/**
 * This component lists all payment ways of the user.
 */
public interface ListPaymentComponent extends YComponent
{
	public List<PaymentInfoModel> getPaymentInfoList();

	public void setPaymentInfoList(List<PaymentInfoModel> paymentInfoList);

	public ShowPaymentComponent getShowPaymentComponent();

	public ShowPaymentComponent getShowPaymentComponentTemplate();

	public YComponentEventHandler<ListPaymentComponent> getCreateCreditCardEvent();

	public YComponentEventHandler<ListPaymentComponent> getCreateDebitEvent();

}
