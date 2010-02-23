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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.util.Collection;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.DefaultPriceLeverage;
import ystorefoundationpackage.domain.PriceLeverage;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Provides a general {@link PriceLeverage} implementation for either {@link DiscountValue} or {@link TaxValue}.
 */
public class PriceLeverageImpl extends DefaultPriceLeverage
{
	public PriceLeverageImpl(final DiscountValue discountValue)
	{
		super();
		final String discountCode = discountValue.getCode();

		if (discountCode == null)
		{
			throw new StorefoundationException("No Discount specified", new NullPointerException());
		}

		final Collection<DiscountModel> discounts = JaloBridge.getInstance().getDiscounts(discountCode);

		if (discounts.isEmpty())
		{
			throw new StorefoundationException("No Discounts found for code " + discountCode);
		}

		if (discounts.size() > 1)
		{
			throw new StorefoundationException("Multiple Discounts found for code " + discountCode);
		}

		final DiscountModel discount = discounts.iterator().next();

		final CurrencyModel appliedCurrency = getCurrencyModel(discountValue.getCurrencyIsoCode());
		final String discountName = discount.getName() != null ? discount.getName() : discount.getCode();

		super.setValues(discountCode, discountValue.getValue(), discountValue.isAbsolute());
		super.setName(discountName);
		super.setAppliedValue(discountValue.getAppliedValue());
		super.setAppliedCurrency(appliedCurrency);
		super.setType(PriceLeverage.TYPE_DISCOUNT);
	}

	public PriceLeverageImpl(final TaxValue taxValue)
	{
		super();
		final String taxCode = taxValue.getCode() != null ? taxValue.getCode() : "";
		if (taxCode == null)
		{
			throw new StorefoundationException("No Tax specified", new NullPointerException());
		}

		//just take the unlocalized first		
		String taxName = taxValue.getCode();

		// now get the Tax object (if any) to get a nice localized name etc.
		final Collection<TaxModel> taxes = JaloBridge.getInstance().getTaxes(taxCode);
		if (taxes.size() > 1)
		{
			throw new StorefoundationException("Multiple Taxes found for code " + taxCode);
		}
		if (taxes.size() == 1)
		{
			//final Tax tax = taxes.iterator().next();
			final TaxModel tax = taxes.iterator().next();

			taxName = tax.getName() != null ? tax.getName() : tax.getCode();
		}

		//and the currency
		final String _currency = taxValue.getCurrencyIsoCode();
		final CurrencyModel appliedCurrency = _currency != null ? getCurrencyModel(taxValue.getCurrencyIsoCode()) : null;

		super.setValues(taxCode, taxValue.getValue(), taxValue.isAbsolute());
		super.setName(taxName);
		super.setAppliedValue(taxValue.getAppliedValue());
		super.setAppliedCurrency(appliedCurrency);
		super.setType(PriceLeverage.TYPE_TAX);
	}

	private CurrencyModel getCurrencyModel(final String isocode)
	{
		final CurrencyModel result = YStorefoundation.getRequestContext().getPlatformServices().getI18NService().getCurrency(
				isocode);
		return result;
	}
}