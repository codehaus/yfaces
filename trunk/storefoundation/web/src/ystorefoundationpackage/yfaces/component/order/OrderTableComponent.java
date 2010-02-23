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

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;



/**
 * This component displays an order in table form. It includes much important information, such as tax,
 * discount(voucher), and delivery cost. This may be either a finalized order or a cart.
 */
public interface OrderTableComponent extends YModel
{

	/**
	 * Helper class for <code>OrderTableComponent</code>. It displays one row in the order table.
	 */
	public interface OrderTableRow
	{
		public String getFormattedTotalPrice();

		public String getFormattedBasePrice();

		public String getFormattedTax();

		public String getQuantity();

		public ProductModel getProduct();
	}

	public void setOrder(AbstractOrderModel order);

	public AbstractOrderModel getOrder();

	public String getFormattedDelivery();

	public String getFormattedSubTotal();

	public String getFormattedTotal();

	public String getFormattedPaymentTotal();

	public List<String> getFormattedDiscounts();

	public List<String> getFormattedTaxes();

	public List<OrderTableRow> getOrderTableRows();

	public YEventHandler<OrderTableComponent> getPlaceOrderEvent();

}
