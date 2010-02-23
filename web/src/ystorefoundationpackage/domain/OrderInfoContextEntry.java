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
package ystorefoundationpackage.domain;

import de.hybris.platform.core.model.order.OrderEntryModel;


/**
 * Helper class which briefly describes the entry of the order.
 * 
 */
public class OrderInfoContextEntry
{

	private OrderEntryModel orderEntry;
	private String productLink;
	private String basePrice;
	private String totalPrice;

	public OrderEntryModel getOrderEntry()
	{
		return orderEntry;
	}

	public void setOrderEntry(final OrderEntryModel orderEntry)
	{
		this.orderEntry = orderEntry;
	}

	public String getProductLink()
	{
		return productLink;
	}

	public void setProductLink(final String productLink)
	{
		this.productLink = productLink;
	}

	public String getBasePrice()
	{
		return basePrice;
	}

	public void setBasePrice(final String basePrice)
	{
		this.basePrice = basePrice;
	}

	public String getTotalPrice()
	{
		return totalPrice;
	}

	public void setTotalPrice(final String totalPrice)
	{
		this.totalPrice = totalPrice;
	}

}
