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

import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.SfSessionContext.UserChangeListener;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.WishListFrame;


/**
 * Listener for wish list components. It is called every time when the user logs in or logs out.
 */
public class WishlistCmpUserSessionListener implements UserChangeListener
{
	public void performUserChanged(final UserModel oldUser, final UserModel newUser)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final SfSessionContext sessCtx = reqCtx.getSessionContext();
		final boolean anonymousUser = JaloBridge.getInstance().isAnonymous(newUser);

		if (anonymousUser)
		{
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
		}
		else
		{
			//give the anonymous user the hint that he can save the wish list if it is not empty
			final List<Wishlist2EntryModel> entries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get(WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
			if (entries != null && !entries.isEmpty())
			{
				final String wishListName = sessCtx.getWishList().getName();
				final String resource = reqCtx.getURLFactory().createExternalForm("/pages/wishListPage.jsf");
				final String toWishListLink = "<a href=\"" + resource + "\">"
						+ reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.wishListLink") + "</a>";
				sessCtx.getMessages().pushInfoMessage("frames.wishListFrame.tempWishList", wishListName, toWishListLink);
			}
		}

		//reset the default wish list for the new user
		Wishlist2Model defaultWishList;
		try
		{
			defaultWishList = reqCtx.getPlatformServices().getWishlistService().getDefaultWishlist(newUser);
		}
		catch (final NullPointerException ne)
		{
			defaultWishList = createWishlist(newUser, anonymousUser);
		}
		sessCtx.setWishList(defaultWishList);
	}

	/*
	 * create a wish list for the user if it is not available, anonymous user can also have a wish list, but it will not
	 * be saved in the database
	 */
	private Wishlist2Model createWishlist(final UserModel user, final boolean anonymousUser)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final Wishlist2Model wishlist = new Wishlist2Model();
		wishlist.setUser(user);
		wishlist.setDefault(Boolean.TRUE);
		wishlist.setEntries(new ArrayList<Wishlist2EntryModel>());
		wishlist.setName(reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.newWishList"));
		if (!anonymousUser)
		{
			reqCtx.getPlatformServices().getModelService().save(wishlist);
		}
		return wishlist;
	}

}
