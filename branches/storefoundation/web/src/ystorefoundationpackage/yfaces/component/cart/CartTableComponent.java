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

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component provides the tabular visualization of a {@link CartModel}.<br/>
 * Basic operations (update, and delete) are allowed for each cart entry.<br/>
 */
public interface CartTableComponent extends YModel
{
	String ATTRIB_REMOVE_ENTRY = "entryToRemove";

	/**
	 * Small Wrapper around a {@link AbstractOrderEntryModel}. Does some general formattings and validations for the
	 * frontend.
	 */
	interface CartTableEntry
	{
		String getFormattedTotalPrice();

		String getFormattedBasePrice();

		String getQuantity();

		void setQuantity(String quantity);

		AbstractOrderEntryModel getSource();
	}


	List<CartTableEntry> getCartTableEntries();

	/**
	 * @return the current selected sort column
	 */
	String getSortColumn();

	/**
	 * Sets the current sort column.
	 */
	void setSortColumn(String sortColumn);

	/**
	 * @return true when sort order is ascending
	 */
	boolean isAscending();

	/**
	 * Sets the sort order.
	 * 
	 * @param ascending
	 *           true when ascending
	 */
	void setAscending(boolean ascending);

	/**
	 * Returns the {@link CartModel} which is used for visualization.
	 * 
	 * @return {@link CartModel}
	 */
	CartModel getCart();

	/**
	 * Sets the {@link CartModel} which shall be visualized.
	 * 
	 * @param cartBean
	 *           {@link CartModel} to set.
	 */
	void setCart(CartModel cartBean);


	/**
	 * Returns a formatted String for the carts total. (price and currency).
	 * 
	 * @return formatted String
	 */
	String getFormattedSubTotal();


	/**
	 * Eventhandler for updating all cart entries
	 * 
	 * @return {@link YEventHandler}
	 */
	YEventHandler<CartTableComponent> getUpdateCartTableEntriesEvent();

	/**
	 * Eventhandler for removing a cart entry.
	 * 
	 * @return {@link YEventHandler}
	 */
	YEventHandler<CartTableComponent> getRemoveCartTableEntryEvent();

	/**
	 * Eventhandler for sorting the carttable by a selected column.
	 * 
	 * @return {@link YEventHandler}
	 */
	YEventHandler<CartTableComponent> getSortCartTableEvent();


}
