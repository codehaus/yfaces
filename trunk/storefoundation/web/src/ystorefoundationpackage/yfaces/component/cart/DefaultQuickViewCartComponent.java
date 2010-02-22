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

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>QuickViewCartComponent</code> interface.
 */
public class DefaultQuickViewCartComponent extends AbstractYComponent implements QuickViewCartComponent
{
	private static final long serialVersionUID = 8712436172843689173L;

	private static final String LOCKEY_EMPTY = "quickViewCartCmp.empty";
	private static final String LOCKEY_ONE = "quickViewCartCmp.oneProduct";
	private static final String LOCKEY_MANY = "quickViewCartCmp.manyProducts";

	private CartModel cart = null;
	private String localizationKey = null;
	private Long count = null;
	private String total;



	public CartModel getCart()
	{
		return this.cart;
	}

	public Long getCount()
	{
		return this.count;
	}

	public void setCount(final Long count)
	{
		this.count = count;
	}


	public String getTotal()
	{
		return this.total;
	}



	public String getLocalizationKey()
	{
		return this.localizationKey;
	}

	public void setLocalizationKey(final String key)
	{
		this.localizationKey = key;
	}



	public void setCart(final CartModel cart)
	{
		this.cart = cart;
	}


	@Override
	public void validate()
	{
		if (this.getCart() == null)
		{
			this.setCart(YStorefoundation.getRequestContext().getSessionContext().getCart());
		}

		if (this.getCount() == null)
		{
			this.setCount(getCountInternal());
		}

		this.total = getTotalInternal();

		if (this.getLocalizationKey() == null)
		{
			final long count = this.getCount().longValue();
			this.setLocalizationKey((count == 0) ? LOCKEY_EMPTY : (count == 1) ? LOCKEY_ONE : LOCKEY_MANY);
		}
	}


	private String getTotalInternal()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		//update currency
		final CurrencyModel currency = reqCtx.getSessionContext().getCurrency();
		this.cart.setCurrency(currency);
		// final NumberFormat nf = Webfoundation.getInstance().getServices().getC2LService().getNumberFormat(currency);

		final String total = reqCtx.getContentManagement().getCurrencyNumberFormat().format(this.cart.getSubtotal());
		return total;
	}

	private Long getCountInternal()
	{
		final CartModel cart = getCart();
		long result = 0;
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			result = result + entry.getQuantity().longValue();
		}
		return Long.valueOf(result);
	}


}
