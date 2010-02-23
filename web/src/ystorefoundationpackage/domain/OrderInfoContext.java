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

import de.hybris.platform.core.model.user.AddressModel;

import java.util.List;


/**
 * Context information for email notification when the user places the order.
 * 
 */
public interface OrderInfoContext extends EmailNotificationContext
{

	/**
	 * Returns all {@link OrderInfoContextEntry} of the specific order.
	 * 
	 * @return list of {@link OrderInfoContextEntry}
	 */
	public List<OrderInfoContextEntry> getOrderInfoEntries();

	/**
	 * @return the sub total amount of the order
	 */
	public String getSubtotalAmount();

	/**
	 * Sets the sub total amount of the order
	 * 
	 * @param subtotalAmount
	 *           sub total amount
	 */
	public void setSubtotalAmount(String subtotalAmount);

	/**
	 * @return delivery cost
	 */
	public String getDeliveryCost();

	/**
	 * Sets the delivery cost
	 * 
	 * @param deliveryCost
	 *           delivery cost
	 */
	public void setDeliveryCost(String deliveryCost);

	/**
	 * @return tax amount
	 */
	public String getTaxAmount();

	/**
	 * Sets the tax amount
	 * 
	 * @param taxAmount
	 *           tax amount
	 */
	public void setTaxAmount(String taxAmount);

	/**
	 * @return discount information
	 */
	public String getDiscountInfo();

	/**
	 * Sets the discount information
	 * 
	 * @param discountInfo
	 */
	public void setDiscountInfo(String discountInfo);

	/**
	 * @return payment cost
	 */
	public String getPaymentCost();

	/**
	 * Sets the payment cost
	 * 
	 * @param paymentCost
	 *           payment cost
	 */
	public void setPaymentCost(String paymentCost);

	/**
	 * @return total amount the user pays
	 */
	public String getTotalAmount();

	/**
	 * Sets the total amount the user pays
	 * 
	 * @param totalAmount
	 *           total amount
	 */
	public void setTotalAmount(String totalAmount);

	/**
	 * @return the currency of the order
	 */
	public String getCurrency();

	/**
	 * Sets the currency of the order
	 * 
	 * @param currency
	 *           currency
	 */
	public void setCurrency(String currency);

	/**
	 * @return the unique order number
	 */
	public String getOrderNumber();

	/**
	 * Sets the unique order number
	 * 
	 * @param orderNumber
	 *           unique order number
	 */
	public void setOrderNumber(String orderNumber);

	/**
	 * @return delivery address
	 */
	public AddressModel getDeliveryAddress();

	/**
	 * Sets the delivery address
	 * 
	 * @param deliveryAddress
	 *           delivery address
	 */
	public void setDeliveryAddress(AddressModel deliveryAddress);

	/**
	 * @return payment address
	 */
	public AddressModel getPaymentAddress();

	/**
	 * Sets the payment address
	 * 
	 * @param paymentAddress
	 *           payment address
	 */
	public void setPaymentAddress(AddressModel paymentAddress);

}
