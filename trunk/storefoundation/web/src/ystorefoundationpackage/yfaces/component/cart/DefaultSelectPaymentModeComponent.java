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

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PaymentModes;


/**
 * Implementation of the <code>SelectPaymentModeComponent</code> interface.
 */
public class DefaultSelectPaymentModeComponent extends AbstractYComponent implements SelectPaymentModeComponent
{

	/**
	 * This event gets fired when the user tries to choose the "Pay in Advance" as the payment mode.
	 */
	public static class ChooseAdvanceEvent extends DefaultYComponentEventListener<SelectPaymentModeComponent>
	{

		@Override
		public void actionListener(final YComponentEvent<SelectPaymentModeComponent> event)
		{
			final SelectPaymentModeComponent cmp = event.getComponent();
			final CartModel cart = cmp.getCart();
			YStorefoundation.getRequestContext().getOrderManagement().updateCart(cart, PaymentModes.ADVANCE.getCode(), null);
		}

	}

	/**
	 * This event gets fired when the user tries to choose "Invoice" as the payment mode.
	 */
	public static class ChooseInvoiceEvent extends DefaultYComponentEventListener<SelectPaymentModeComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<SelectPaymentModeComponent> event)
		{
			final SelectPaymentModeComponent cmp = event.getComponent();
			final CartModel cart = cmp.getCart();
			YStorefoundation.getRequestContext().getOrderManagement().updateCart(cart, PaymentModes.INVOICE.getCode(), null);
		}
	}

	private CartModel cart = null;

	private YComponentEventHandler<SelectPaymentModeComponent> ehChooseAdvance = null;
	private YComponentEventHandler<SelectPaymentModeComponent> ehChooseInvoice = null;


	/**
	 * Constructor.
	 */
	public DefaultSelectPaymentModeComponent()
	{
		super();
		this.ehChooseAdvance = super.createEventHandler(new ChooseAdvanceEvent());
		this.ehChooseInvoice = super.createEventHandler(new ChooseInvoiceEvent());
	}

	@Override
	public void validate()
	{
		if (getCart() == null)
		{
			final CartModel cart = YStorefoundation.getRequestContext().getSessionContext().getCart();
			setCart(cart);
		}
	}



	public CartModel getCart()
	{
		return this.cart;
	}

	public void setCart(final CartModel cart)
	{
		this.cart = cart;
	}



	public YComponentEventHandler<SelectPaymentModeComponent> getChooseAdvanceEvent()
	{
		return this.ehChooseAdvance;
	}

	public YComponentEventHandler<SelectPaymentModeComponent> getChooseInvoiceEvent()
	{
		return this.ehChooseInvoice;
	}

}
