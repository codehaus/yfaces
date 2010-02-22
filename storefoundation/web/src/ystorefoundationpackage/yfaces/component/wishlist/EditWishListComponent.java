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
package ystorefoundationpackage.yfaces.component.wishlist;


import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;

import javax.faces.model.SelectItem;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;


/**
 * This component makes it possible for the user to edit the wish list.
 */
public interface EditWishListComponent extends YComponent
{

	public static final String ATTRIB_COPY_OR_MOVE = "copyOrMove";

	//model
	public Wishlist2Model getWishList();

	public void setWishList(Wishlist2Model wishList);

	public Wishlist2Model getDestWishList();

	public void setDestWishList(Wishlist2Model destWishList);

	public List<Wishlist2EntryModel> getSelectedEntries();

	public DataTableAxisModel getWishListTable();

	public List<? extends SelectItem> getOtherWishLists();

	//general events: save changes, and cancel to save changes
	public YComponentEventHandler<EditWishListComponent> getSaveWishListEvent();

	public YComponentEventHandler<EditWishListComponent> getCancelEditWishListEvent();

	//events for selected products: add selected products to cart, 
	//and copy(or move) selected products to another wish list
	public YComponentEventHandler<EditWishListComponent> getAddSelectedProductsToCartEvent();

	public YComponentEventHandler<EditWishListComponent> getCopyToAnotherWishListEvent();

	public YComponentEventHandler<EditWishListComponent> getDeleteSelectedProductsEvent();

}
