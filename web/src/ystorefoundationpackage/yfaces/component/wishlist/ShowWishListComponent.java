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

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component displays all the products in the current active wish list of the user.
 */
public interface ShowWishListComponent extends YModel
{

	//model
	public Wishlist2Model getWishList();

	public void setWishList(Wishlist2Model wishList);

	public boolean isWishListEmpty();

	public boolean isWishListDescriptionEmpty();

	public List<Wishlist2EntryModel> getWishListEntries();

	//events: add all products to cart
	public YEventHandler<ShowWishListComponent> getAddAllProductsToCartEvent();

}
