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
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import ystorefoundationpackage.domain.OrderInfoContext;
import ystorefoundationpackage.domain.OrderInfoContextEntry;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 * 
 */
public class OrderInfoContextImpl extends AbstractEmailNotificationContextImpl implements OrderInfoContext
{

	private String currency;
	private String orderNumber;
	private final List<OrderInfoContextEntry> orderInfoEntries;
	private AddressModel deliveryAddress;
	private AddressModel paymentAddress;
	private String subtotalAmount;
	private String taxAmount;
	private String deliveryCost;
	private String paymentCost;
	private String discountInfo;
	private String totalAmount;

	public OrderInfoContextImpl(final OrderModel order)
	{
		super();

		this.orderNumber = order.getCode();
		this.deliveryAddress = order.getDeliveryAddress();
		this.paymentAddress = order.getPaymentAddress();
		this.orderInfoEntries = this.createOrderInfoEntries(order);
	}

	public List<OrderInfoContextEntry> getOrderInfoEntries()
	{
		return this.orderInfoEntries;
	}

	public String getTotalAmount()
	{
		return this.totalAmount;
	}

	public void setTotalAmount(final String totalAmount)
	{
		this.totalAmount = totalAmount;
	}

	public String getCurrency()
	{
		return this.currency;
	}

	public void setCurrency(final String currency)
	{
		this.currency = currency;
	}

	public String getOrderNumber()
	{
		return orderNumber;
	}

	public void setOrderNumber(final String orderNumber)
	{
		this.orderNumber = orderNumber;
	}

	public AddressModel getDeliveryAddress()
	{
		return deliveryAddress;
	}

	public void setDeliveryAddress(final AddressModel deliveryAddress)
	{
		this.deliveryAddress = deliveryAddress;
	}

	public AddressModel getPaymentAddress()
	{
		return paymentAddress;
	}

	public void setPaymentAddress(final AddressModel paymentAddress)
	{
		this.paymentAddress = paymentAddress;
	}

	public String getSubtotalAmount()
	{
		return subtotalAmount;
	}

	public void setSubtotalAmount(final String subtotalAmount)
	{
		this.subtotalAmount = subtotalAmount;
	}

	public String getTaxAmount()
	{
		return taxAmount;
	}

	public void setTaxAmount(final String taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	public String getDeliveryCost()
	{
		return deliveryCost;
	}

	public void setDeliveryCost(final String deliveryCost)
	{
		this.deliveryCost = deliveryCost;
	}

	public String getDiscountInfo()
	{
		return discountInfo;
	}

	public void setDiscountInfo(final String discountInfo)
	{
		this.discountInfo = discountInfo;
	}

	public String getPaymentCost()
	{
		return paymentCost;
	}

	public void setPaymentCost(final String paymentCost)
	{
		this.paymentCost = paymentCost;
	}

	private List<OrderInfoContextEntry> createOrderInfoEntries(final OrderModel order)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final List<OrderEntryModel> entries = (List) order.getEntries();
		final List<OrderInfoContextEntry> orderInfoEntries = new ArrayList<OrderInfoContextEntry>();


		//variant products do not have names, but their base product has
		for (final OrderEntryModel entry : entries)
		{
			final ProductModel product = entry.getProduct();
			if (product.getName() == null || product.getName().length() == 0)
			{
				if (product instanceof VariantProductModel)
				{
					product.setName((((VariantProductModel) product).getBaseProduct()).getName());
				}
				else
				{
					product.setName("");
				}
			}

			final String productLink = reqCtx.getURLFactory().getURLCreator(product.getClass()).createURL(product).toString();

			final CurrencyModel currency = entry.getOrder().getCurrency();
			final String basePrice = reqCtx.getContentManagement().getCurrencyNumberFormat(currency).format(
					entry.getBasePrice().doubleValue());
			final String totalPrice = reqCtx.getContentManagement().getCurrencyNumberFormat(currency).format(
					entry.getTotalPrice().doubleValue());

			final OrderInfoContextEntry orderInfoEntry = new OrderInfoContextEntry();
			orderInfoEntry.setOrderEntry(entry);
			orderInfoEntry.setProductLink(productLink);
			orderInfoEntry.setBasePrice(basePrice);
			orderInfoEntry.setTotalPrice(totalPrice);
			orderInfoEntries.add(orderInfoEntry);
		}
		return orderInfoEntries;
	}

}
