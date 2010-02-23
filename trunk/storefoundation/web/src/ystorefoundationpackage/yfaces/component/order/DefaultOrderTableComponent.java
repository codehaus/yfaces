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
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.MailManagement;
import ystorefoundationpackage.domain.OrderInfoContext;
import ystorefoundationpackage.domain.PriceLeverage;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.UserMessages;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PlaceOrderResult;


/**
 * Implementation of the <code>OrderTableComponent</code> interface.
 */
public class DefaultOrderTableComponent extends AbstractYModel implements OrderTableComponent
{
	private AbstractOrderModel order = null;

	private YEventHandler<OrderTableComponent> ehPlaceOrder = null;

	private transient List<OrderTableRow> tableRows = null;
	private transient String formattedSubTotal = null;
	private transient String formattedTotal = null;
	private transient String formattedDeliveryTotal = null;
	private transient String formattedPaymentTotal = null;
	private transient List<String> formattedDiscounts = null;
	private transient List<String> formattedTaxes = null;
	private transient OrderTableComponentFormatter formatter = null;

	/**
	 * Implementation of the <code>OrderTableRow</code> interface.
	 */
	public class OrderTableRowImpl implements OrderTableRow
	{
		protected String title = null;
		protected String formattedTotalPrice = null;
		protected String formattedBasePrice = null;
		protected String formattedSubTotal = null;
		protected String quantity = null;
		protected String formattedTax = null;
		protected AbstractOrderEntryModel source = null;

		public OrderTableRowImpl(final AbstractOrderEntryModel orderEntry)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			final CurrencyModel currency = orderEntry.getOrder().getCurrency();
			final NumberFormat nf = reqCtx.getContentManagement().getCurrencyNumberFormat(currency);
			this.formattedTotalPrice = nf.format(orderEntry.getTotalPrice().doubleValue());
			this.formattedBasePrice = nf.format(orderEntry.getBasePrice().doubleValue());
			final List<PriceLeverage> taxes = reqCtx.getOrderManagement().getTaxes(orderEntry);
			if (taxes.size() > 1)
			{
				throw new YFacesException(DefaultOrderTableComponent.this, "Can't handle multiple taxes per ordereentry");
			}

			final double taxValue = taxes.size() == 1 ? taxes.iterator().next().getValue() : 0;
			this.formattedTax = reqCtx.getContentManagement().getNumberFormat().format(taxValue) + " %";

			this.quantity = String.valueOf(orderEntry.getQuantity());
			this.source = orderEntry;
		}

		public String getFormattedTotalPrice()
		{
			return this.formattedTotalPrice;
		}

		public String getFormattedBasePrice()
		{
			return this.formattedBasePrice;
		}

		public String getFormattedTax()
		{
			return this.formattedTax;
		}

		public String getQuantity()
		{
			return this.quantity;
		}

		public ProductModel getProduct()
		{
			return this.source.getProduct();
		}
	}

	/**
	 * This event gets fired when the user tries to place the orders. If the process is successful, an confirmation email
	 * with the order information in detail will be sent.
	 */
	public static class PlaceOrderEvent extends DefaultYEventListener<OrderTableComponent>
	{

		private static final Logger log = Logger.getLogger(PlaceOrderEvent.class);

		@Override
		public void actionListener(final YEvent<OrderTableComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();

			final OrderTableComponent cmp = event.getComponent();
			if (cmp.getOrder() instanceof CartModel)
			{
				final PlaceOrderResult result = reqCtx.getOrderManagement().placeOrder((CartModel) cmp.getOrder());

				//result may be of interest for possible other listeners (summaryframe)
				event.getAttributes().put(PlaceOrderResult.class.getName(), result);

				// multiple errors may occur
				if (result.getErrorCode() != 0)
				{
					final UserMessages messages = sessCtx.getMessages();
					if (result.containsError(PlaceOrderResult.CHOOSE_DELIVERY_ADDRESS))
					{
						messages.pushInfoMessage("chooseDeliveryAddressFirst");
					}
					if (result.containsError(PlaceOrderResult.CHOOSE_PAYMENT_ADDRESS))
					{
						messages.pushInfoMessage("choosePaymentAddressFirst");
					}
					if (result.containsError(PlaceOrderResult.CHOOSE_DELIVERY_MODE))
					{
						messages.pushInfoMessage("chooseDeliveryModeFirst");
					}
					if (result.containsError(PlaceOrderResult.CHOOSE_PAYMENT_MODE))
					{
						messages.pushInfoMessage("choosePaymentModeFirst");
					}
				}
				else
				{
					final MailManagement mailTemplates = reqCtx.getMailManagement();
					final OrderInfoContext ctx = mailTemplates.getOrderMailContext(sessCtx.getUser(), result.getOrder());
					final String mailSubject = reqCtx.getContentManagement().getLocalizedMessage(
							"components.orderTableCmp.emailSubject");

					final boolean send = mailTemplates.sendMail(sessCtx.getUser(), mailSubject, ctx);
					if (send)
					{
						log.debug("Successfully send email '" + mailSubject + "' to "
								+ sessCtx.getUser().getDefaultShipmentAddress().getEmail());
					}
					else
					{
						log.info("Error sending email '" + mailSubject + "' to "
								+ sessCtx.getUser().getDefaultShipmentAddress().getEmail());
					}
					sessCtx.setCart(null);
				}
			}
		}
	}

	public DefaultOrderTableComponent()
	{
		super();
		this.ehPlaceOrder = super.createEventHandler(new PlaceOrderEvent());
	}


	public YEventHandler<OrderTableComponent> getPlaceOrderEvent()
	{
		return this.ehPlaceOrder;
	}

	public void setOrder(final AbstractOrderModel order)
	{
		this.order = order;
	}

	public AbstractOrderModel getOrder()
	{
		return this.order;
	}

	@Override
	public void validate()
	{
		//when no order is given take the current cart
		if (this.order == null)
		{
			this.order = YStorefoundation.getRequestContext().getSessionContext().getCart();
		}

		//disable placeorderevent for OrderBeans who already have a status (=all except CartBeans)
		final boolean enablePlaceOrder = this.getOrder().getStatus() == null;
		this.ehPlaceOrder.setEnabled(enablePlaceOrder);
	}

	@Override
	public void refresh()
	{
		this.tableRows = null;
		this.formattedTaxes = null;
		this.formattedDiscounts = null;
		this.formattedTotal = null;
		this.formattedSubTotal = null;
		this.formattedPaymentTotal = null;
	}

	public String getFormattedDelivery()
	{
		if (this.formattedDeliveryTotal == null)
		{
			this.formattedDeliveryTotal = getFormatter().getFormattedDelivery(getOrder());
		}
		return this.formattedDeliveryTotal;
	}

	public String getFormattedPaymentTotal()
	{
		if (this.formattedPaymentTotal == null && getOrder().getPaymentCost().doubleValue() > 0)
		{
			this.formattedPaymentTotal = getFormatter().getFormattedPaymentTotal(getOrder());
		}
		return this.formattedPaymentTotal;
	}

	public String getFormattedSubTotal()
	{
		if (this.formattedSubTotal == null)
		{
			this.formattedSubTotal = getFormatter().getFormattedSubTotal(getOrder());
		}
		return this.formattedSubTotal;
	}

	public String getFormattedTotal()
	{
		if (this.formattedTotal == null)
		{
			this.formattedTotal = getFormatter().getFormattedTotal(getOrder());
		}
		return this.formattedTotal;
	}

	public List<String> getFormattedDiscounts()
	{
		if (this.formattedDiscounts == null)
		{
			this.formattedDiscounts = getFormatter().getFormattedDiscounts(getOrder());
		}
		return this.formattedDiscounts;
	}

	public List<String> getFormattedTaxes()
	{
		if (this.formattedTaxes == null)
		{
			this.formattedTaxes = getFormatter().getFormattedTaxes(getOrder());
		}
		return this.formattedTaxes;
	}


	public List<OrderTableRow> getOrderTableRows()
	{
		if (this.tableRows == null)
		{
			this.tableRows = this.createOrderTableRows();
		}
		return this.tableRows;
	}


	private List<OrderTableRow> createOrderTableRows()
	{
		final List<OrderTableRow> result = new ArrayList<OrderTableRow>();
		final List<AbstractOrderEntryModel> entries = this.getOrder().getEntries();
		for (final AbstractOrderEntryModel entry : entries)
		{
			result.add(new OrderTableRowImpl(entry));
		}
		return result;
	}

	private OrderTableComponentFormatter getFormatter()
	{
		if (this.formatter == null)
		{
			this.formatter = new OrderTableComponentFormatter();
		}
		return this.formatter;
	}

}
