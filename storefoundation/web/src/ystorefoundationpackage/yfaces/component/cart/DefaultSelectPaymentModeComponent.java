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

import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.order.CartModel;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PaymentModes;


/**
 * Implementation of the <code>SelectPaymentModeComponent</code> interface.
 */
public class DefaultSelectPaymentModeComponent extends AbstractYModel implements SelectPaymentModeComponent
{

	/**
	 * This event gets fired when the user tries to choose the "Pay in Advance" as the payment mode.
	 */
	public static class ChooseAdvanceEvent extends DefaultYEventListener<SelectPaymentModeComponent>
	{

		@Override
		public void actionListener(final YEvent<SelectPaymentModeComponent> event)
		{
			final SelectPaymentModeComponent cmp = event.getComponent();
			final CartModel cart = cmp.getCart();
			YStorefoundation.getRequestContext().getOrderManagement().updateCart(cart, PaymentModes.ADVANCE.getCode(), null);
		}

	}

	/**
	 * This event gets fired when the user tries to choose "Invoice" as the payment mode.
	 */
	public static class ChooseInvoiceEvent extends DefaultYEventListener<SelectPaymentModeComponent>
	{
		@Override
		public void actionListener(final YEvent<SelectPaymentModeComponent> event)
		{
			final SelectPaymentModeComponent cmp = event.getComponent();
			final CartModel cart = cmp.getCart();
			YStorefoundation.getRequestContext().getOrderManagement().updateCart(cart, PaymentModes.INVOICE.getCode(), null);
		}
	}

	private CartModel cart = null;

	private YEventHandler<SelectPaymentModeComponent> ehChooseAdvance = null;
	private YEventHandler<SelectPaymentModeComponent> ehChooseInvoice = null;


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



	public YEventHandler<SelectPaymentModeComponent> getChooseAdvanceEvent()
	{
		return this.ehChooseAdvance;
	}

	public YEventHandler<SelectPaymentModeComponent> getChooseInvoiceEvent()
	{
		return this.ehChooseInvoice;
	}

}
