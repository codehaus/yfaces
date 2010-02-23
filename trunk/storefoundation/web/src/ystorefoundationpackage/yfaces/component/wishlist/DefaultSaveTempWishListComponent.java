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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.WishListFrame;


/**
 * Implementation of the <code>SaveTempWishListComponent</code> interface.
 */
public class DefaultSaveTempWishListComponent extends AbstractYComponent implements SaveTempWishListComponent
{

	private static final long serialVersionUID = -4490451201616030178L;

	private List<Wishlist2EntryModel> tempEntries = null;
	private Wishlist2Model wishList = null;
	private List<? extends SelectItem> existedWishLists = null;
	private String name = null;

	private YComponentEventHandler<SaveTempWishListComponent> ehCreateAsNew = null;
	private YComponentEventHandler<SaveTempWishListComponent> ehSaveToAnother = null;
	private YComponentEventHandler<SaveTempWishListComponent> ehDiscard = null;

	//default constructor
	public DefaultSaveTempWishListComponent()
	{
		super();
		this.ehCreateAsNew = super.createEventHandler(new CreateWishListAction());
		this.ehSaveToAnother = super.createEventHandler(new SaveToAnotherWishListAction());
		this.ehDiscard = super.createEventHandler(new DiscardTempWishListAction());
	}

	@Override
	public void validate()
	{
		this.tempEntries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
				WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);

		this.wishList = this.findAllWishLists().get(0);
		this.existedWishLists = this.getWishLists();
	}

	/**
	 * This event gets fired when the user tries to create a new wish list to save all products in the temporary one.
	 */
	public static class CreateWishListAction extends DefaultYComponentEventListener<SaveTempWishListComponent>
	{

		private static final long serialVersionUID = -6655651132222410232L;

		@Override
		public void actionListener(final YComponentEvent<SaveTempWishListComponent> event)
		{
			final String name = event.getComponent().getName();
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			final List<Wishlist2EntryModel> entries = event.getComponent().getTempEntries();
			for (final Wishlist2EntryModel entry : entries)
			{
				getModelService().save(entry);
			}
			event.getComponent().removeAnonymousWishListEntries();

			//TODO local
			final Wishlist2Model tempWishList = createWishlist(userSession.getUser(), name, entries);

			userSession.setWishList(tempWishList);
			userSession.getMessages().pushInfoMessage("components.saveTempWishListCmp.wishListSaved");
		}

		private ModelService getModelService()
		{
			return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
		}

		private Wishlist2Model createWishlist(final UserModel user, String name, final List<Wishlist2EntryModel> entries)
		{
			if (name == null || name.trim().length() == 0)
			{
				name = YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(
						"frames.wishListFrame.newWishList");
			}
			final Wishlist2Model wishList = new Wishlist2Model();
			wishList.setUser(user);
			wishList.setName(name);
			wishList.setEntries(entries == null ? new ArrayList<Wishlist2EntryModel>() : entries);
			wishList.setDefault(Boolean.FALSE);
			if (!JaloBridge.getInstance().isAnonymous(user))
			{
				getModelService().save(wishList);
			}
			return wishList;
		}
	}

	/**
	 * This event gets fired when the user tries to move all products in the temporary wish list to an existed one.
	 */
	public static class SaveToAnotherWishListAction extends DefaultYComponentEventListener<SaveTempWishListComponent>
	{

		private static final long serialVersionUID = -962727992958999161L;

		@Override
		public void actionListener(final YComponentEvent<SaveTempWishListComponent> event)
		{
			final Wishlist2Model wishList = event.getComponent().getWishList();

			wishList.setEntries(addTempEntries(wishList.getEntries()));
			event.getComponent().removeAnonymousWishListEntries();
			getModelService().save(wishList);
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			userSession.setWishList(wishList);
		}

		private List<Wishlist2EntryModel> addTempEntries(final List<Wishlist2EntryModel> originalEntries)
		{
			final List<ProductModel> entryProducts = new ArrayList<ProductModel>();
			final List<Wishlist2EntryModel> destEntries = new ArrayList<Wishlist2EntryModel>();
			for (final Wishlist2EntryModel originalEntry : originalEntries)
			{
				entryProducts.add(originalEntry.getProduct());
				destEntries.add(originalEntry);
			}

			final List<Wishlist2EntryModel> entries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get(WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
			for (final Wishlist2EntryModel entry : entries)
			{
				if (!entryProducts.contains(entry.getProduct()))
				{
					getModelService().save(entry);
					destEntries.add(entry);
				}
			}
			return destEntries;
		}

		private ModelService getModelService()
		{
			return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
		}

	}

	/**
	 * This event gets fired when the user does not need to save the temporary wish list, and tries to discard it.
	 */
	public static class DiscardTempWishListAction extends DefaultYComponentEventListener<SaveTempWishListComponent>
	{

		private static final long serialVersionUID = -7183245765359122712L;

		@Override
		public void actionListener(final YComponentEvent<SaveTempWishListComponent> event)
		{
			event.getComponent().removeAnonymousWishListEntries();
		}

	}

	public List<Wishlist2EntryModel> getTempEntries()
	{
		return this.tempEntries;
	}

	private List<SelectItem> getWishLists()
	{
		final List<SelectItem> wishLists = new ArrayList<SelectItem>();
		for (final Wishlist2Model wishList : this.findAllWishLists())
		{
			final String name = wishList.getName();
			wishLists.add(new SelectItem(wishList, name));
		}
		return wishLists;
	}

	private List<Wishlist2Model> findAllWishLists()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final SfSessionContext userSession = reqCtx.getSessionContext();
		List<Wishlist2Model> result = reqCtx.getPlatformServices().getWishlistService().getWishlists(userSession.getUser());
		if (result == null || result.isEmpty())
		{
			final String name = reqCtx.getContentManagement().getLocalizedMessage("frames.wishListFrame.newWishList");
			final Wishlist2Model wishList = reqCtx.getPlatformServices().getWishlistService().createDefaultWishlist(
					userSession.getUser(), name, null);
			result = Arrays.asList(wishList);
		}
		return result;

	}

	public void removeAnonymousWishListEntries()
	{
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
	}

	public List<? extends SelectItem> getExistedWishLists()
	{
		return this.existedWishLists;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public YComponentEventHandler<SaveTempWishListComponent> getCreateWishListEvent()
	{
		return this.ehCreateAsNew;
	}

	public YComponentEventHandler<SaveTempWishListComponent> getSaveToAnotherWishListEvent()
	{
		return this.ehSaveToAnother;
	}

	public YComponentEventHandler<SaveTempWishListComponent> getDiscardTempWishListEvent()
	{
		return this.ehDiscard;
	}

	public Wishlist2Model getWishList()
	{
		return this.wishList;
	}

	public void setWishList(final Wishlist2Model wishList)
	{
		this.wishList = wishList;
	}

}
