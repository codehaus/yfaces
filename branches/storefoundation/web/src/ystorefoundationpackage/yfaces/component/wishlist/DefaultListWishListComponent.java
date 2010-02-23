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

import java.util.Arrays;
import java.util.List;

import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>ListWishListComponent</code> interface.
 */
public class DefaultListWishListComponent extends AbstractYModel implements ListWishListComponent
{

	private static final long serialVersionUID = 802271656227745924L;

	private List<Wishlist2Model> wishLists = null;
	private String wishListName = null;

	private YEventHandler<ListWishListComponent> ehSelectList = null;
	private YEventHandler<ListWishListComponent> ehAddList = null;
	private YEventHandler<ListWishListComponent> ehDeleteList = null;
	private YEventHandler<ListWishListComponent> ehCreateNewDefault = null;
	private YEventHandler<ListWishListComponent> ehEditList = null;

	//default constructor
	public DefaultListWishListComponent()
	{
		super();
		this.ehSelectList = super.createEventHandler(new SelectWishListAction());
		this.ehAddList = super.createEventHandler(new AddWishListAction());
		this.ehDeleteList = super.createEventHandler(new DeleteWishListAction());
		this.ehCreateNewDefault = super.createEventHandler(new MakeNewDefaultWishListAction());
		this.ehEditList = super.createEventHandler(new EditWishListAction());
	}

	@Override
	public void validate()
	{
		if (this.wishLists == null)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			final SfSessionContext userSession = reqCtx.getSessionContext();
			try
			{
				this.wishLists = reqCtx.getPlatformServices().getWishlistService().getWishlists(userSession.getUser());
			}
			catch (final NullPointerException ne)
			{
				final Wishlist2Model wishList = reqCtx.getPlatformServices().getWishlistService().createDefaultWishlist(
						userSession.getUser(), reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.newWishList"),
						null);
				this.wishLists = Arrays.asList(wishList);
			}
		}
	}

	@Override
	public void refresh()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

		this.wishLists = YStorefoundation.getRequestContext().getPlatformServices().getWishlistService().getWishlists(
				userSession.getUser());
	}

	/**
	 * This event gets fired when the user tries to manage the selected wish list. It will be set as the active one.
	 */
	public static class SelectWishListAction extends DefaultYEventListener<ListWishListComponent>
	{

		private static final long serialVersionUID = 3322419333099500345L;

		@Override
		public void actionListener(final YEvent<ListWishListComponent> event)
		{
			final Wishlist2Model wishList = (Wishlist2Model) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_SELECT_WISH_LIST);
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			userSession.setWishList(wishList);
		}

	}

	/**
	 * This event gets fired when the user tries to create a new wish list. The new wish list will be set as the active
	 * one at once, so that the user can easily manage it.
	 */
	public static class AddWishListAction extends DefaultYEventListener<ListWishListComponent>
	{

		private static final long serialVersionUID = -7968598842889652604L;

		@Override
		public void actionListener(final YEvent<ListWishListComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext userSession = reqCtx.getSessionContext();

			String name = event.getComponent().getWishListName();
			if (name == null || name.length() == 0)
			{
				name = reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.newWishList");
			}
			final Wishlist2Model newWishList = reqCtx.getPlatformServices().getWishlistService().createWishlist(
					userSession.getUser(), name, null);
			userSession.setWishList(newWishList);
			event.getComponent().setWishListName(null);
		}

	}

	/**
	 * This event gets fired when the user tries to delete the select wish list. If the default wish list is deleted,
	 * then the first one of the other wish lists will be set as the default.
	 */
	public static class DeleteWishListAction extends DefaultYEventListener<ListWishListComponent>
	{

		private static final long serialVersionUID = -5902834545922953825L;

		@Override
		public void actionListener(final YEvent<ListWishListComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext userSession = reqCtx.getSessionContext();

			final Wishlist2Model dealedWishList = (Wishlist2Model) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_DELETE_WISH_LIST);
			final boolean defaultWishList = dealedWishList.getDefault().booleanValue();
			final boolean currentWishList = dealedWishList.equals(userSession.getWishList());

			reqCtx.getPlatformServices().getModelService().remove(dealedWishList);

			//if the default wish list was deleted, then the first one of all wish lists of the user will be set as the default
			if (defaultWishList)
			{
				final List<Wishlist2Model> list = reqCtx.getPlatformServices().getWishlistService().getWishlists(
						userSession.getUser());

				//TODO marker
				Wishlist2Model wishList = null;
				if (list != null && !list.isEmpty())
				{
					wishList = list.get(0);
					wishList.setDefault(Boolean.TRUE);
					reqCtx.getPlatformServices().getModelService().save(wishList);
				}
				else
				{
					wishList = reqCtx.getPlatformServices().getWishlistService().createDefaultWishlist(userSession.getUser(),
							reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.newWishList"), null);
				}

				reqCtx.getSessionContext().setWishList(wishList);
			}

			//if the current wish list was deleted, set the default wish list as the current one
			if (currentWishList)
			{
				userSession.setWishList(reqCtx.getPlatformServices().getWishlistService().getDefaultWishlist(userSession.getUser()));
			}
		}

	}

	/**
	 * This event gets fired when the user tries to set the new default wish list.
	 */
	public static class MakeNewDefaultWishListAction extends DefaultYEventListener<ListWishListComponent>
	{

		private static final long serialVersionUID = 3602865892403612436L;

		@Override
		public void actionListener(final YEvent<ListWishListComponent> event)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			final Wishlist2Model oldDefaultWishList = YStorefoundation.getRequestContext().getPlatformServices()
					.getWishlistService().getDefaultWishlist(userSession.getUser());
			oldDefaultWishList.setDefault(Boolean.FALSE);
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(oldDefaultWishList);
			final Wishlist2Model newDefaultWishList = (Wishlist2Model) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_MAKE_NEW_DEFAUTL_WISH_LIST);
			newDefaultWishList.setDefault(Boolean.TRUE);
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(newDefaultWishList);
			userSession.setWishList(newDefaultWishList);
		}

	}

	/**
	 * This event gets fired when the user tries to edit the information of the selected wish list.
	 */
	public static class EditWishListAction extends DefaultYEventListener<ListWishListComponent>
	{

		private static final long serialVersionUID = 1009072425560365109L;

		@Override
		public String action()
		{
			return "wishListEditPage";
		}

	}

	public List<Wishlist2Model> getAllWishLists()
	{
		return this.wishLists;
	}

	public void setAllWishLists(final List<Wishlist2Model> wishLists)
	{
		this.wishLists = wishLists;
	}

	public YEventHandler<ListWishListComponent> getSelectWishListEvent()
	{
		return this.ehSelectList;
	}

	public YEventHandler<ListWishListComponent> getAddWishListEvent()
	{
		return this.ehAddList;
	}

	public YEventHandler<ListWishListComponent> getDeleteWishListEvent()
	{
		return this.ehDeleteList;
	}

	public YEventHandler<ListWishListComponent> getMakeNewDefaultWishListEvent()
	{
		return this.ehCreateNewDefault;
	}

	public YEventHandler<ListWishListComponent> getEditWishListEvent()
	{
		return this.ehEditList;
	}

	public String getWishListName()
	{
		return this.wishListName;
	}

	public void setWishListName(final String wishListName)
	{
		this.wishListName = wishListName;
	}

}
