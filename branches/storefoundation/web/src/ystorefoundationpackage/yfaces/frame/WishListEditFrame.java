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

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;

import ystorefoundationpackage.yfaces.component.wishlist.EditWishListComponent;

/**
 * Renders the page for the user to edit the selected wish list. The user can
 * edit the name, and description, and can copy and/or move some entries to
 * another wish list.
 * 
 */
public class WishListEditFrame extends AbstractYFrame {

	private static final long serialVersionUID = 4128676973222489852L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(WishListEditFrame.class);

	private EditWishListComponent editWishlistCmp = null;

	public EditWishListComponent getEditWishListComponent() {
		return this.editWishlistCmp;
	}

	public void setEditWishListComponent(EditWishListComponent cmp) {
		this.editWishlistCmp = cmp;
	}

}
