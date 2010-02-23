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

import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;

import java.util.List;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;


/**
 * This component displays one product in the wish list in detail.
 */
public interface ShowWishListEntryComponent extends YComponent
{

	public static final String ATTRIB_SAVE_WISH_LIST_ENTRY = "saveWishListEntry";
	public static final String ATTRIB_REMOVE_WISH_LIST_ENTRY = "removeWishListEntry";
	public static final String ATTRIB_ADD_PRODUCT_TO_CART = "addProductToCart";

	//model:
	public Wishlist2EntryModel getWishListEntry();

	public void setWishListEntry(Wishlist2EntryModel entry);

	public Wishlist2EntryPriority getPriority();

	public void setPriority(Wishlist2EntryPriority priority);

	public List<? extends SelectItem> getPriorities();

	public String getAddedDate();

	//event: save a wish list entry, delete a wish list entry, 
	//and add the product in the entry to cart
	public YComponentEventHandler<ShowWishListEntryComponent> getSaveWishListEntryEvent();

	public YComponentEventHandler<ShowWishListEntryComponent> getRemoveWishListEntryEvent();

	public YComponentEventHandler<ShowWishListEntryComponent> getAddProductToCartEvent();

}
