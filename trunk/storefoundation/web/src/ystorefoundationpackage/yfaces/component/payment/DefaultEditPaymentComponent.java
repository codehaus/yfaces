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

import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Date;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>EditPaymentComponent</code> interface.
 */
public class DefaultEditPaymentComponent extends AbstractEditPaymentComponent
{


	@Override
	public void validate()
	{
		if (getPaymentInfo() == null)
		{
			setPaymentInfo(getDefaultDebitPaymentInfo());
		}

	}

	public boolean isCreditCard()
	{
		return false;
	}

	public boolean isDebit()
	{
		return true;
	}


	@SuppressWarnings("unchecked")
	private PaymentInfoModel getDefaultDebitPaymentInfo()
	{
		// create an empty PaymentInfoBean for a new DebitPaymentInfo
		final DebitPaymentInfoModel debitPaymentInfo = new DebitPaymentInfoModel();

		final UserModel user = YStorefoundation.getRequestContext().getSessionContext().getUser();

		// fill mandatory attributes 'code' and 'user'
		debitPaymentInfo.setUser(user);
		debitPaymentInfo.setCode(user.getUid() + " - Debit - " + (new Date()).toString()); // just to give it a kind of unique name...

		// preset the account owner's name for convenience
		debitPaymentInfo.setBaOwner(user.getName());

		return debitPaymentInfo;
	}



}
