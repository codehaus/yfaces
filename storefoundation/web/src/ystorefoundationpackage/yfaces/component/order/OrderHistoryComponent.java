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

import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * This component lists all the orders which the user has ordered.
 */
public interface OrderHistoryComponent extends YModel
{
	//used columnnames (these names are used as DAO searchattributes (for ordering))
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_PRICE = "price";
	public static final String COLUMN_STATE = "state";

	//Model (in/out)
	/**
	 * Returns the user whose orderhistory is used.
	 * 
	 * @return user
	 */
	public UserModel getUser();

	/**
	 * Sets a user whose orderhistory shall be used.
	 * 
	 * @param user
	 *           user to set
	 */
	public void setUser(UserModel user);

	/**
	 * Returns the list of orders who are used.
	 * 
	 * @return list of orders
	 */
	public List<OrderModel> getOrderList();

	/**
	 * Sets a list of orders who shall be used. This orderlist overrules the list of an optionally set user.
	 * 
	 * @param orderList
	 *           list of orders
	 */
	public void setOrderList(List<OrderModel> orderList);

	/**
	 * @return the name of the column used for sorting
	 */
	public String getSortColumn();

	/**
	 * Sets the column which is used for table sorting. Possible values are one of the column constants.
	 * 
	 * @param sortColumn
	 *           name of sortcolumn
	 */
	public void setSortColumn(String sortColumn);

	public boolean isSortAscending();

	public void setSortAscending(boolean isAscending);

	public String getOrderCode();

	public void setOrderCode(String orderCode);

	public EnumerationValueModel getOrderState();

	public void setOrderState(EnumerationValueModel orderState);

	//Model (out)
	public SfSelectItemGroup<EnumerationValueModel> getOrderStateSelector();

	public DataTableAxisModel getOrderHistoryTable();

	//events
	public YEventHandler<OrderHistoryComponent> getSortOrderHistoryTableEvent();

	public YEventHandler<OrderHistoryComponent> getOrderStateSelectEvent();

}
