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
package ystorefoundationpackage.domain.util;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

import java.util.Comparator;


public class ComparePaymentInfoByCode implements Comparator<PaymentInfoModel>
{

	public int compare(final PaymentInfoModel o1, final PaymentInfoModel o2)
	{
		if (o1 != null && o2 != null)
		{
			final String code1 = o1.getCode();
			final String code2 = o2.getCode();

			return !code1.equals(code2) ? code1.compareTo(code2) : o1.getPk().compareTo(o2.getPk());
		}
		else
		{
			return -1;
		}
	}

}
