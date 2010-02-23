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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

import javax.faces.context.FacesContext;

import org.codehaus.yfaces.component.AbstractYFrame;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Renders the page to display all important information(especially order id) of the order.
 * 
 */
public class OrderThankYouFrame extends AbstractYFrame
{
	private OrderModel order = null;

	public OrderModel getOrder()
	{
		if (this.order == null)
		{
			final String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("orderid");
			final UserModel user = YStorefoundation.getRequestContext().getSessionContext().getUser();
			//order = Webfoundation.getInstance().getServices().getOrderService().findById(Long.parseLong(id), user);
			order = YStorefoundation.getRequestContext().getOrderManagement().getOrder(user, id);
		}
		return this.order;
	}

}
