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

import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>EditCreditCardPaymentComponent</code> interface.
 */
public class DefaultEditCreditCardPaymentComponent extends AbstractEditPaymentComponent
{
	private List<? extends SelectItem> types = null;
	private List<? extends SelectItem> years = null;
	private List<? extends SelectItem> months = null;


	public List<? extends SelectItem> getAvailableTypes()
	{
		return this.types;
	}

	public void setAvailableTypes(final List<? extends SelectItem> creditCardTypes)
	{
		this.types = creditCardTypes;
	}

	public List<? extends SelectItem> getAvailableMonths()
	{
		return this.months;
	}

	public void setAvailableMonths(final List<? extends SelectItem> availableMonths)
	{
		this.months = availableMonths;
	}

	public List<? extends SelectItem> getAvailableYears()
	{
		return this.years;
	}

	public void setAvailableYears(final List<? extends SelectItem> availableYears)
	{
		this.years = availableYears;
	}

	public boolean isCreditCard()
	{
		return true;
	}

	public boolean isDebit()
	{
		return false;
	}


	@Override
	public void validate()
	{
		if (getAvailableTypes() == null)
		{
			setAvailableTypes(getDefaultTypes());
		}

		if (getAvailableYears() == null)
		{
			setAvailableYears(getDefaultYears());
		}

		if (getAvailableMonths() == null)
		{
			setAvailableMonths(getDefaultMonths());
		}

		if (getPaymentInfo() == null)
		{
			setPaymentInfo(getDefaultCreditCardPaymentInfo());
		}

	}


	private List<SelectItem> getDefaultTypes()
	{
		final List<SelectItem> result = new ArrayList<SelectItem>();

		final TypeService ts = YStorefoundation.getRequestContext().getPlatformServices().getTypeService();
		for (final HybrisEnumValue _ccType : CreditCardType.values())
		{
			final EnumerationValueModel ccType = ts.getEnumerationValue(_ccType);
			result.add(new SelectItem(_ccType, ccType.getName()));
		}

		return result;
	}

	private List<SelectItem> getDefaultYears()
	{
		final List<SelectItem> result = new ArrayList<SelectItem>();

		final int year = Utilities.getDefaultCalendar().get(Calendar.YEAR);
		for (int i = year; i < year + 10; i++)
		{
			result.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}

		return result;
	}

	private List<SelectItem> getDefaultMonths()
	{
		final List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 1; i <= 12; i++)
		{
			result.add(new SelectItem(String.valueOf(i), String.valueOf(i)));
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	private PaymentInfoModel getDefaultCreditCardPaymentInfo()
	{
		final CreditCardPaymentInfoModel paymentInfo = new CreditCardPaymentInfoModel();

		final Calendar calendar = Utilities.getDefaultCalendar();
		final UserModel user = YStorefoundation.getRequestContext().getSessionContext().getUser();

		// fill mandatory attributes 'code' and 'user'
		paymentInfo.setUser(user);
		paymentInfo.setCode(user.getUid() + " - CreditCard - " + (new Date()).toString()); // just to give it a kind of unique name...

		// preset some attributes for convenience 
		paymentInfo.setValidToMonth(String.valueOf(calendar.get(Calendar.MONTH)));
		paymentInfo.setValidToYear(String.valueOf(calendar.get(Calendar.YEAR)));
		paymentInfo.setCcOwner(user.getName());
		//paymentInfo.setType((CreditCardTypeBean) getAvailableTypes().get(0).getValue());
		paymentInfo.setType(CreditCardType.values()[0]);

		return paymentInfo;
	}




}
