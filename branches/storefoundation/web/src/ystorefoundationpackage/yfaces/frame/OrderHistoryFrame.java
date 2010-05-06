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
package ystorefoundationpackage.yfaces.frame;

import org.codehaus.yfaces.component.AbstractYComponentContainer;

import ystorefoundationpackage.yfaces.component.order.OrderHistoryComponent;

/**
 * Renders all orders of the user as a list.
 * 
 */
public class OrderHistoryFrame extends AbstractYComponentContainer {
	private OrderHistoryComponent orderHistoryCmp = null;

	public OrderHistoryComponent getOrderHistoryComponent() {
		return this.orderHistoryCmp;
	}

	public void setOrderHistoryComponent(OrderHistoryComponent cmp) {
		this.orderHistoryCmp = cmp;
	}

}
