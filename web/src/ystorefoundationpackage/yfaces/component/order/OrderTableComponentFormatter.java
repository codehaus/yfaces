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
package ystorefoundationpackage.yfaces.component.order;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import ystorefoundationpackage.domain.ContentManagement;
import ystorefoundationpackage.domain.PriceLeverage;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 *
 */
// this class may 
// a) become merged into a formatter service
// b) may returned as formatter class (interface) from within a formatter service
public class OrderTableComponentFormatter
{
	public List<String> getFormattedDiscounts(final AbstractOrderModel order)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final List<String> result = new ArrayList<String>();

		final List<PriceLeverage> discounts = reqCtx.getOrderManagement().getDisounts(order);
		for (final PriceLeverage leverage : discounts)
		{
			String _formattedValue = null;
			String _formattedAppliedValue = null;

			if (leverage.isAbsolute())
			{
				final CurrencyModel currency = leverage.getAppliedCurrency();
				final NumberFormat nf = reqCtx.getContentManagement().getCurrencyNumberFormat(currency);
				_formattedValue = nf.format(leverage.getValue());
				_formattedAppliedValue = nf.format(-1.0 * leverage.getAppliedValue());
			}
			else
			{
				final NumberFormat nf = reqCtx.getContentManagement().getNumberFormat();
				_formattedValue = nf.format(leverage.getAppliedValue()) + " %";
				_formattedAppliedValue = nf.format(-1.0 * leverage.getAppliedValue()) + " %";
			}
			final String _formatted = leverage.getName() + " " + _formattedValue + ": " + _formattedAppliedValue;
			result.add(_formatted);
		}
		return result;
	}

	public List<String> getFormattedTaxes(final AbstractOrderModel order)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final ContentManagement cm = reqCtx.getContentManagement();

		final List<String> result = new ArrayList<String>();

		final List<PriceLeverage> taxes = reqCtx.getOrderManagement().getTaxes(order);
		for (final PriceLeverage leverage : taxes)
		{
			final String _formattedValue = cm.getNumberFormat().format(leverage.getValue()) + " %";
			final String _formattedAppliedValue = cm.getCurrencyNumberFormat().format(leverage.getAppliedValue());

			final String _formatted = leverage.getName() + " " + _formattedValue + ": " + _formattedAppliedValue;
			result.add(_formatted);
		}

		return result;
	}

	public String getFormattedPaymentTotal(final AbstractOrderModel order)
	{
		final String result = (order.getPaymentCost().doubleValue() > 0) ? createFormattedPrice(order, order.getPaymentCost()
				.doubleValue()) : "";
		return result;
	}

	public String getFormattedSubTotal(final AbstractOrderModel order)
	{
		return createFormattedPrice(order, order.getSubtotal().doubleValue());
	}

	public String getFormattedTotal(final AbstractOrderModel order)
	{
		return createFormattedPrice(order, order.getTotalPrice().doubleValue());
	}

	public String getFormattedDelivery(final AbstractOrderModel order)
	{
		return createFormattedPrice(order, order.getDeliveryCost().doubleValue());
	}


	private String createFormattedPrice(final AbstractOrderModel order, final double price)
	{
		final CurrencyModel cur = order.getCurrency();
		return YStorefoundation.getRequestContext().getContentManagement().getCurrencyNumberFormat(cur).format(price);
	}

}
