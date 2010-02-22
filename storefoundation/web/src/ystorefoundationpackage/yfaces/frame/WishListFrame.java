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

import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventListener;
import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.component.wishlist.DefaultEditWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.DefaultListWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.EditWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.ListWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.SaveTempWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.ShowWishListComponent;
import ystorefoundationpackage.yfaces.component.wishlist.ShowWishListEntryComponent;


/**
 * Renders all wish lists and all products of the default wish list.
 * 
 */
public class WishListFrame extends AbstractYFrame
{

	private static final long serialVersionUID = 3293470797692376000L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(WishListFrame.class);

	public static final String ANONYMOUS_WISH_LIST_ENTRIES = "anonymousWishListEntries";
	public static final String WISH_LIST_PAGE = "wishListPage";
	public static final String WISH_LIST_EDIT_PAGE = "wishListEditPage";
	public static final String CART_LINK = "/pages/cartPage.jsf";

	private YComponentBinding<ListWishListComponent> listWishListCmp = null;
	private YComponentBinding<ShowWishListComponent> showWishListCmp = null;
	private YComponentBinding<ShowWishListEntryComponent> showWishListEntryCmp = null;
	private YComponentBinding<SaveTempWishListComponent> saveTempWishListCmp = null;

	//default constructor
	public WishListFrame()
	{
		super();
		this.listWishListCmp = super.createComponentBinding(createListWishListComponent());
		this.showWishListCmp = super.createComponentBinding();
		this.showWishListEntryCmp = super.createComponentBinding();
		this.saveTempWishListCmp = super.createComponentBinding();

	}

	private ListWishListComponent createListWishListComponent()
	{
		final ListWishListComponent cmp = new DefaultListWishListComponent();
		final YComponentEventListener<ListWishListComponent> editListener = cmp.getEditWishListEvent().getListener();
		editListener.setAction(WISH_LIST_EDIT_PAGE);
		editListener.setActionListener(super.createExpressionString("doEditListWishList"));
		return cmp;
	}

	/**
	 * External {@link YComponentEventListener} for {@link ListWishListComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doEditListWishList(final YComponentEvent<ListWishListComponent> event)
	{
		final Wishlist2Model wishList = (Wishlist2Model) event.getFacesEvent().getComponent().getAttributes().get(
				ListWishListComponent.ATTRIB_EDIT_WISH_LIST);
		YStorefoundation.getRequestContext().getSessionContext().setWishList(wishList);
		final WishListEditFrame frame = getWishListEditFrame();
		final EditWishListComponent cmp = new DefaultEditWishListComponent();
		cmp.setWishList(wishList);
		cmp.getCancelEditWishListEvent().getListener().setAction(WISH_LIST_PAGE);
		frame.getEditWishListComponent().setValue(cmp);
	}

	private WishListEditFrame getWishListEditFrame()
	{
		final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final WishListEditFrame frame = nextPage.getOrCreateFrame(WishListEditFrame.class);
		return frame;
	}

	public YComponentBinding<ListWishListComponent> getListWishListComponent()
	{
		return this.listWishListCmp;
	}

	public YComponentBinding<ShowWishListComponent> getShowWishListComponent()
	{
		return this.showWishListCmp;
	}

	public YComponentBinding<ShowWishListEntryComponent> getShowWishListEntryComponent()
	{
		return this.showWishListEntryCmp;
	}

	public YComponentBinding<SaveTempWishListComponent> getSaveTempWishListComponent()
	{
		return this.saveTempWishListCmp;
	}

	public boolean isAnonymousUser()
	{
		return JaloBridge.getInstance().isAnonymous(YStorefoundation.getRequestContext().getSessionContext().getUser());
	}

	public boolean isAnonymousWishListEmpty()
	{
		final List<Wishlist2EntryModel> entries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(ANONYMOUS_WISH_LIST_ENTRIES);
		return (entries == null || entries.isEmpty());
	}

}
