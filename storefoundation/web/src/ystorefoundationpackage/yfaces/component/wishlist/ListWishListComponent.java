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

import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component lists all the wish lists for the user.
 */
public interface ListWishListComponent extends YModel
{

	public static final String ATTRIB_SELECT_WISH_LIST = "selectedWishList";
	public static final String ATTRIB_DELETE_WISH_LIST = "deleteWishList";
	public static final String ATTRIB_MAKE_NEW_DEFAUTL_WISH_LIST = "makeNewDefaultWishList";
	public static final String ATTRIB_EDIT_WISH_LIST = "editWishList";

	//model
	public List<Wishlist2Model> getAllWishLists();

	public void setAllWishLists(List<Wishlist2Model> wishLists);

	public String getWishListName();

	public void setWishListName(String wishListName);

	//event: select one wish list, and add a wish list, 
	//delete a wish list, make the new default wish list, and edit a wish list
	public YEventHandler<ListWishListComponent> getSelectWishListEvent();

	public YEventHandler<ListWishListComponent> getAddWishListEvent();

	public YEventHandler<ListWishListComponent> getDeleteWishListEvent();

	public YEventHandler<ListWishListComponent> getMakeNewDefaultWishListEvent();

	public YEventHandler<ListWishListComponent> getEditWishListEvent();

}
