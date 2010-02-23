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

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component deals with a temporary wish list. Assume this situation: the anonymous user saves some products in the
 * wish list, and after that the user logs in. Because the anonymous wish list is only valid in the session, the user is
 * now allowed to save it, delete it, or move all products to an existed wish list.
 */
public interface SaveTempWishListComponent extends YModel
{

	//model

	/**
	 * Gets the temporary wish list entries of the anonymous user
	 * 
	 * @return List<WishlistEntryModel> the list of all temporary wish list entries
	 */
	public List<Wishlist2EntryModel> getTempEntries();

	/**
	 * Gets an existed wish list of the user, and all entries in the temporary wish list will be added to this list
	 * 
	 * @return WishlistModel
	 */
	public Wishlist2Model getWishList();

	/**
	 * Sets an existed wish list of the user
	 * 
	 * @param wishList
	 *           the wish list which all entries in the temporary wish list will be added to
	 */
	public void setWishList(Wishlist2Model wishList);

	public List<? extends SelectItem> getExistedWishLists();

	public String getName();

	public void setName(String name);

	/**
	 * Removes the wish list entries for the anonymous user in the session
	 */
	public void removeAnonymousWishListEntries();

	//event: create a new wish list, save all products in an existed wish list, and discard the temp wish list
	public YEventHandler<SaveTempWishListComponent> getCreateWishListEvent();

	public YEventHandler<SaveTempWishListComponent> getSaveToAnotherWishListEvent();

	public YEventHandler<SaveTempWishListComponent> getDiscardTempWishListEvent();

}
