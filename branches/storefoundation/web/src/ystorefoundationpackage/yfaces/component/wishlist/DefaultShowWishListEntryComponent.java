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
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;
import ystorefoundationpackage.yfaces.frame.WishListFrame;



/**
 * Implementation of the <code>ShowWishListEntryComponent</code> interface.
 */
public class DefaultShowWishListEntryComponent extends AbstractYModel implements ShowWishListEntryComponent
{

	private static final Logger log = Logger.getLogger(DefaultShowWishListEntryComponent.class);

	private static final long serialVersionUID = -1452129064817156977L;

	private Wishlist2EntryModel entry = null;
	private Wishlist2EntryPriority priority = null;
	private List<? extends SelectItem> priorities = null;

	private YEventHandler<ShowWishListEntryComponent> ehSaveEntry = null;
	private YEventHandler<ShowWishListEntryComponent> ehDeleteEntry = null;
	private YEventHandler<ShowWishListEntryComponent> ehAddEntryToCart = null;

	//default constructor
	public DefaultShowWishListEntryComponent()
	{
		super();
		this.ehSaveEntry = super.createEventHandler(new SaveWishListEntryAction());
		this.ehDeleteEntry = super.createEventHandler(new DeleteWishListEntryAction());
		this.ehAddEntryToCart = super.createEventHandler(new addProductToCartAction());
	}

	@Override
	public void validate()
	{
		if (entry == null)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			if (userSession.getWishList().getEntries() != null && userSession.getWishList().getEntries().size() > 0)
			{
				this.entry = (Wishlist2EntryModel) ((List) userSession.getWishList().getEntries()).get(0);
			}
			else
			{
				log.error("wish list entry is null");
				return;
			}
		}
		this.priority = this.entry.getPriority();
		if (this.priorities == null)
		{
			this.priorities = preparePriorities();
		}
	}

	/**
	 * This event gets fired when the user tries to save the changes for the selected product.
	 */
	public static class SaveWishListEntryAction extends DefaultYEventListener<ShowWishListEntryComponent>
	{
		private static final long serialVersionUID = -213619171752435928L;

		@Override
		public void actionListener(final YEvent<ShowWishListEntryComponent> event)
		{
			final Wishlist2EntryModel entry = (Wishlist2EntryModel) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_SAVE_WISH_LIST_ENTRY);
			entry.setPriority(event.getComponent().getPriority());
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(entry);
			YStorefoundation.getRequestContext().getSessionContext().setWishList(entry.getWishlist());
		}
	}

	/**
	 * This event gets fired when the user tries to remove the selected product in the current wish list.
	 */
	public static class DeleteWishListEntryAction extends DefaultYEventListener<ShowWishListEntryComponent>
	{
		private static final long serialVersionUID = 4363471051604447023L;

		@Override
		public void actionListener(final YEvent<ShowWishListEntryComponent> event)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			final Wishlist2Model wishList = userSession.getWishList();
			final List<Wishlist2EntryModel> entries = new ArrayList<Wishlist2EntryModel>(wishList.getEntries());
			final Wishlist2EntryModel entry = (Wishlist2EntryModel) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_REMOVE_WISH_LIST_ENTRY);
			boolean removed = false;
			for (final Wishlist2EntryModel e : entries)
			{
				if (e.getProduct().equals(entry.getProduct()))
				{
					entries.remove(e);
					wishList.setEntries(entries);
					YStorefoundation.getRequestContext().getPlatformServices().getModelService().remove(e);
					YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(wishList);
					userSession.setWishList(wishList);
					removed = true;
					break;
				}
			}
			if (!removed)
			{
				userSession.getMessages().pushInfoMessage(
						"components.showWishListEntryCmp.deleteNotSuccessful",
						((Wishlist2EntryModel) event.getFacesEvent().getComponent().getAttributes().get(ATTRIB_REMOVE_WISH_LIST_ENTRY))
								.getProduct().getName());
				return;
			}
		}
	}

	public static class addProductToCartAction extends DefaultYEventListener<ShowWishListEntryComponent>
	{
		private static final long serialVersionUID = -3780547621031958408L;

		@Override
		public void actionListener(final YEvent<ShowWishListEntryComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();
			final Wishlist2EntryModel entry = (Wishlist2EntryModel) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_ADD_PRODUCT_TO_CART);
			final AddToCartResult state = reqCtx.getOrderManagement().addToCart(sessCtx.getCart(), entry.getProduct(), 1, null);
			final String resource = reqCtx.getURLFactory().createExternalForm(WishListFrame.CART_LINK);
			final String toCartLink = "<a href=\"" + resource + "\">"
					+ reqCtx.getContentManagement().getLocalizedMessage("frames.productFrame.toCart") + "</a>";
			switch (state)
			{
				case ADDED_ONE:
					sessCtx.getMessages().pushInfoMessage("addedtocart_one", toCartLink);
					break;
				case ADDED_SEVERAL:
					sessCtx.getMessages().pushInfoMessage("addedtocart_many", String.valueOf(entry.getDesired()), toCartLink);
					break;
				case UNEXPECTED_ERROR:
					sessCtx.getMessages().pushErrorMessage("[unexpectedError]");
					break;
			}
		}
	}

	public Wishlist2EntryModel getWishListEntry()
	{
		return this.entry;
	}

	public void setWishListEntry(final Wishlist2EntryModel entry)
	{
		this.entry = entry;
	}

	public String getAddedDate()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

		SimpleDateFormat format;
		final String isoCode = userSession.getLanguage().getIsocode();
		if ("de".equals(isoCode))
		{
			format = new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN);
		}
		else
		{
			format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
		}
		return format.format(this.entry.getAddedDate());
	}

	public YEventHandler<ShowWishListEntryComponent> getSaveWishListEntryEvent()
	{
		return this.ehSaveEntry;
	}

	public YEventHandler<ShowWishListEntryComponent> getRemoveWishListEntryEvent()
	{
		return this.ehDeleteEntry;
	}

	public YEventHandler<ShowWishListEntryComponent> getAddProductToCartEvent()
	{
		return this.ehAddEntryToCart;
	}

	public Wishlist2EntryPriority getPriority()
	{
		return this.priority;
	}

	public void setPriority(final Wishlist2EntryPriority priority)
	{
		this.priority = priority;
	}

	public List<? extends SelectItem> getPriorities()
	{
		return this.priorities;
	}

	private List<SelectItem> preparePriorities()
	{
		final List<SelectItem> priorities = new ArrayList<SelectItem>();
		for (final Wishlist2EntryPriority priority : Wishlist2EntryPriority.values())
		{
			final String label = YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(
					"components.showWishListEntryCmp." + priority.getCode().toLowerCase());
			priorities.add(new SelectItem(priority, label));
		}
		return priorities;
	}

}
