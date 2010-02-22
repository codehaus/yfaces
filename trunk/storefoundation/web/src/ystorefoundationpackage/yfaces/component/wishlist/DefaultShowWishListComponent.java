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
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.WishListFrame;


/**
 * Implementation of the <code>ShowWishListComponent</code> interface.
 */
public class DefaultShowWishListComponent extends AbstractYComponent implements ShowWishListComponent
{

	private static final long serialVersionUID = -5245128193717584352L;

	private Wishlist2Model wishList = null;
	private List<Wishlist2EntryModel> entries = null;

	private YComponentEventHandler<ShowWishListComponent> ehAddListToCart = null;

	//default constructor
	public DefaultShowWishListComponent()
	{
		super();
		this.ehAddListToCart = super.createEventHandler(new AddAllProductsToCartAction());
	}

	@Override
	public void validate()
	{
		if (this.wishList == null)
		{
			this.wishList = YStorefoundation.getRequestContext().getSessionContext().getWishList();
		}
		setEntries();
	}


	@Override
	public void refresh()
	{
		this.wishList = YStorefoundation.getRequestContext().getSessionContext().getWishList();
		setEntries();
	}

	private void setEntries()
	{
		if (JaloBridge.getInstance().isAnonymous(this.wishList.getUser()))
		{
			this.entries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
					WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
			if (this.entries == null)
			{
				this.entries = new ArrayList<Wishlist2EntryModel>();
			}
		}
		else
		{
			this.entries = new ArrayList<Wishlist2EntryModel>(this.wishList.getEntries());
		}
	}

	/**
	 * This event gets fired when the user tries to add all products in the current wish list to cart.
	 */
	public static class AddAllProductsToCartAction extends DefaultYComponentEventListener<ShowWishListComponent>
	{
		private static final long serialVersionUID = 7388888426022245269L;

		@Override
		public void actionListener(final YComponentEvent<ShowWishListComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();
			final List<Wishlist2EntryModel> entries = event.getComponent().getWishListEntries();
			for (final Wishlist2EntryModel e : entries)
			{
				final AddToCartResult state = reqCtx.getOrderManagement().addToCart(sessCtx.getCart(), e.getProduct(), 1, null);
				if (state.equals(AddToCartResult.UNEXPECTED_ERROR))
				{
					sessCtx.getMessages().pushErrorMessage("[unexpectedError]");
					return;
				}
			}
			final String resource = reqCtx.getURLFactory().createExternalForm(WishListFrame.CART_LINK);
			final String toCartLink = "<a href=\"" + resource + "\">"
					+ reqCtx.getContentManagement().getLocalizedMessage("frames.productFrame.toCart") + "</a>";
			sessCtx.getMessages().pushInfoMessage("components.showWishListCmp.allProductsAddedToCart", toCartLink);
		}
	}

	public Wishlist2Model getWishList()
	{
		return this.wishList;
	}

	public void setWishList(final Wishlist2Model wishList)
	{
		this.wishList = wishList;
	}

	public boolean isWishListEmpty()
	{
		return (this.entries == null || this.entries.size() == 0);
	}

	public boolean isWishListDescriptionEmpty()
	{
		final String description = this.wishList.getDescription();
		return (description == null || description.trim().length() == 0);
	}

	public List<Wishlist2EntryModel> getWishListEntries()
	{
		return this.entries;
	}

	public YComponentEventHandler<ShowWishListComponent> getAddAllProductsToCartEvent()
	{
		return this.ehAddListToCart;
	}

}
