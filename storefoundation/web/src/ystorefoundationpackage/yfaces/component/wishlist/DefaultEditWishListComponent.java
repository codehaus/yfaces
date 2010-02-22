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
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;
import de.hybris.yfaces.context.YPageContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.ColumnCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;
import ystorefoundationpackage.domain.util.TransformProduct2BestProduct;
import ystorefoundationpackage.domain.util.list.BidiTransformedList;
import ystorefoundationpackage.yfaces.frame.WishListFrame;



/**
 * Implementation of the <code>EditWishListComponent</code> interface.
 */
public class DefaultEditWishListComponent extends AbstractYComponent implements EditWishListComponent
{

	private static final long serialVersionUID = -8318173017374116881L;

	private static final String WISH_LIST_PAGE = "wishListPage";

	private Wishlist2Model wishList = null;
	private Wishlist2Model destWishList = null;

	private Boolean[] checkBoxes = null;
	private transient DataTableAxisModel wishListTable = null;
	private transient List<Wishlist2EntryModel> selectedEntries = null;

	private List<? extends SelectItem> otherWishLists = null;

	private YComponentEventHandler<EditWishListComponent> ehSave = null;
	private YComponentEventHandler<EditWishListComponent> ehCancelSave = null;
	private YComponentEventHandler<EditWishListComponent> ehAddSelectedProductsToCart = null;
	private YComponentEventHandler<EditWishListComponent> ehCopyToAnotherWishList = null;
	private YComponentEventHandler<EditWishListComponent> ehDeleteSelectedProducts = null;

	//default constructor
	public DefaultEditWishListComponent()
	{
		super();
		this.ehSave = super.createEventHandler(new SaveWishListAction());
		this.ehCancelSave = super.createEventHandler(new CancelEditWishListAction());
		this.ehAddSelectedProductsToCart = super.createEventHandler(new AddSelectedProductsToCartAction());
		this.ehCopyToAnotherWishList = super.createEventHandler(new CopyToAnotherWishListAction());
		this.ehDeleteSelectedProducts = super.createEventHandler(new DeleteSelectedProducts());
	}

	@Override
	public void validate()
	{
		if (this.wishList == null)
		{
			this.wishList = YStorefoundation.getRequestContext().getSessionContext().getWishList();
		}
		this.otherWishLists = this.getWishLists();
	}

	//transient
	public DataTableAxisModel getWishListTable()
	{
		if (this.wishListTable == null)
		{
			this.wishListTable = createWishListTable();
		}
		return this.wishListTable;
	}

	//transient
	public List<Wishlist2EntryModel> getSelectedEntries()
	{
		if (this.selectedEntries == null)
		{
			this.selectedEntries = new ArrayList<Wishlist2EntryModel>();
			for (int i = 0; i < this.checkBoxes.length; i++)
			{
				if (this.checkBoxes[i].booleanValue())
				{
					this.selectedEntries.add((Wishlist2EntryModel) ((List) this.wishList.getEntries()).get(i));
				}
			}
		}
		return this.selectedEntries;
	}

	private DataTableAxisModel createWishListTable()
	{
		DataTableAxisModel model = null;
		final List<Wishlist2EntryModel> entries = new ArrayList<Wishlist2EntryModel>(getWishList().getEntries());
		if (entries.isEmpty())
		{
			model = DataTableFactory.createEmptyDataTableAxisModel();
		}
		else
		{
			final ColumnCollectionDataTableModel tmEntries = new ColumnCollectionDataTableModel();
			final List<ProductModel> products = new ArrayList<ProductModel>();
			for (final Wishlist2EntryModel _entry : entries)
			{
				products.add(_entry.getProduct());
			}

			//add all wish list entries
			tmEntries.addColumn(products);

			//add the price column
			tmEntries.addColumn(new BidiTransformedList(products, new TransformProduct2BestProduct()));

			//add the check boxes
			this.checkBoxes = new Boolean[tmEntries.getRowCount()];
			Arrays.fill(checkBoxes, Boolean.FALSE);
			tmEntries.addColumn(Arrays.asList(checkBoxes));

			//create DataTableAxisModel based on previous DataTableModel
			model = DataTableFactory.createDataTableAxisModel(tmEntries);
			model.setId("wishListTableModel");

			//set name for the entry marker
			model.getXAxis().getMarkerAt(0).setId("products");
			model.getXAxis().getMarkerAt(0).setTitle("name");

			//set price of product marker
			model.getXAxis().getMarkerAt(1).setId("price");
			model.getXAxis().getMarkerAt(1).setTitle("price");

			//set checkBox for the entry marker
			model.getXAxis().getMarkerAt(2).setId("select");
			model.getXAxis().getMarkerAt(2).setTitle("-");
		}
		return model;
	}

	/**
	 * This event gets fired when the user wants to save the changes for the wish list. The main wish list page will be
	 * loaded after the save action.
	 */
	public static class SaveWishListAction extends DefaultYComponentEventListener<EditWishListComponent>
	{

		private static final long serialVersionUID = 2240734801024800639L;

		@Override
		public void actionListener(final YComponentEvent<EditWishListComponent> event)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(event.getComponent().getWishList());
			userSession.setWishList(event.getComponent().getWishList());
		}

		@Override
		public String action()
		{
			return "wishListPage";
		}

	}

	/**
	 * This event gets fired when the user cancels the changes for the wish list. The previous page will be loaded after
	 * the cancel action.
	 */
	public static class CancelEditWishListAction extends DefaultYComponentEventListener<EditWishListComponent>
	{

		private static final long serialVersionUID = 4450107034950586103L;

		@Override
		public String action()
		{
			final YPageContext pageCtx = YStorefoundation.getRequestContext().getPageContext();
			if (pageCtx != null && pageCtx.getPreviousPage() != null)
			{
				return pageCtx.getPreviousPage().getNavigationId();
			}
			else
			{
				return null;
			}
		}
	}

	public static class AddSelectedProductsToCartAction extends DefaultYComponentEventListener<EditWishListComponent>
	{

		private static final long serialVersionUID = -5470632557512302108L;

		@Override
		public void actionListener(final YComponentEvent<EditWishListComponent> event)
		{
			final List<Wishlist2EntryModel> selectedEntries = event.getComponent().getSelectedEntries();
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			if (selectedEntries != null && !selectedEntries.isEmpty())
			{
				final SfSessionContext userSession = reqCtx.getSessionContext();
				for (final Wishlist2EntryModel _entry : selectedEntries)
				{
					final AddToCartResult state = reqCtx.getOrderManagement().addToCart(userSession.getCart(), _entry.getProduct(), 1,
							null);
					if (state.equals(AddToCartResult.UNEXPECTED_ERROR))
					{
						userSession.getMessages().pushErrorMessage("[unexpectedError]");
						return;
					}
				}
				final String resource = reqCtx.getURLFactory().createExternalForm(WishListFrame.CART_LINK);
				final String toCartLink = "<a href=\"" + resource + "\">"
						+ reqCtx.getContentManagement().getLocalizedMessage("frames.productFrame.toCart") + "</a>";
				userSession.getMessages().pushInfoMessage("components.editWishListCmp.selectedProductsAddedToCart", toCartLink);
			}
		}

		@Override
		public String action()
		{
			return WISH_LIST_PAGE;
		}

	}

	public static class CopyToAnotherWishListAction extends DefaultYComponentEventListener<EditWishListComponent>
	{

		private static final Logger log = Logger.getLogger(CopyToAnotherWishListAction.class);

		private static final long serialVersionUID = 3835188757941423228L;

		@Override
		public void actionListener(final YComponentEvent<EditWishListComponent> event)
		{
			final List<Wishlist2EntryModel> selectedEntries = event.getComponent().getSelectedEntries();
			final Wishlist2Model destWishList = event.getComponent().getDestWishList();
			final Wishlist2Model srcWishList = event.getComponent().getWishList();
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			if (selectedEntries != null && !selectedEntries.isEmpty())
			{
				//				final WishlistService wishListService = SfRequestContext.getCurrentContext().getPlatformServices()
				//						.getWishlistService();
				final String copyOrMove = (String) event.getFacesEvent().getComponent().getAttributes().get(ATTRIB_COPY_OR_MOVE);
				boolean removeAfterCopy = false;
				if ("MOVE".equals(copyOrMove))
				{
					removeAfterCopy = true;
				}
				final List<Wishlist2EntryModel> srcEntries = new ArrayList<Wishlist2EntryModel>(srcWishList.getEntries());
				final List<Wishlist2EntryModel> destEntries = new ArrayList<Wishlist2EntryModel>(destWishList.getEntries());
				final List<ProductModel> destProducts = new ArrayList<ProductModel>();
				for (final Wishlist2EntryModel destEntry : destEntries)
				{
					destProducts.add(destEntry.getProduct());
				}
				for (final Wishlist2EntryModel selectedEntry : selectedEntries)
				{
					if (destProducts.contains(selectedEntry.getProduct()))
					{
						log.info("will not overwrite: " + selectedEntry.getProduct().getName());
					}
					else
					{
						final Wishlist2EntryModel newEntry = copyWishListEntry(selectedEntry);
						destEntries.add(newEntry);
					}
				}
				if (removeAfterCopy)
				{
					for (final Wishlist2EntryModel selectedEntry : selectedEntries)
					{
						srcEntries.remove(selectedEntry);
						getModelService().remove(selectedEntry);
					}
					srcWishList.setEntries(srcEntries);
					getModelService().save(srcWishList);
					userSession.getMessages().pushInfoMessage("components.editWishListCmp.movedComponents", destWishList.getName());
				}
				else
				{
					userSession.getMessages().pushInfoMessage("components.editWishListCmp.copiedComponents", destWishList.getName());
				}
				destWishList.setEntries(destEntries);
				getModelService().save(destWishList);
			}
			userSession.setWishList(srcWishList);
		}

		@Override
		public String action()
		{
			return WISH_LIST_PAGE;
		}

		private ModelService getModelService()
		{
			return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
		}

		private Wishlist2EntryModel copyWishListEntry(final Wishlist2EntryModel srcEntry)
		{
			final Wishlist2EntryModel destEntry = new Wishlist2EntryModel();
			destEntry.setProduct(srcEntry.getProduct());
			destEntry.setDesired(srcEntry.getDesired());
			destEntry.setReceived(srcEntry.getReceived());
			destEntry.setAddedDate(new Date());
			destEntry.setPriority(srcEntry.getPriority());
			getModelService().save(destEntry);
			return destEntry;
		}
	}

	private List<SelectItem> getWishLists()
	{
		final List<SelectItem> wishLists = new ArrayList<SelectItem>();
		final List<Wishlist2Model> existedWishList = new ArrayList<Wishlist2Model>(this.findAllWishLists());
		existedWishList.remove(this.getWishList());
		for (final Wishlist2Model wishList : existedWishList)
		{
			final String name = wishList.getName();
			wishLists.add(new SelectItem(wishList, name));
		}
		return wishLists;
	}

	private List<Wishlist2Model> findAllWishLists()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		List<Wishlist2Model> result = YStorefoundation.getRequestContext().getPlatformServices().getWishlistService().getWishlists(
				userSession.getUser());
		if (result == null || result.isEmpty())
		{
			final Wishlist2Model wishList = YStorefoundation.getRequestContext().getPlatformServices().getWishlistService()
					.createDefaultWishlist(userSession.getUser(), null, null);
			result = Arrays.asList(wishList);
		}
		return result;

	}

	public static class DeleteSelectedProducts extends DefaultYComponentEventListener<EditWishListComponent>
	{

		private static final long serialVersionUID = -5116166455045754780L;

		@Override
		public void actionListener(final YComponentEvent<EditWishListComponent> event)
		{
			final List<Wishlist2EntryModel> selectedEntries = event.getComponent().getSelectedEntries();
			if (selectedEntries != null && !selectedEntries.isEmpty())
			{
				final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
				final Wishlist2Model wishList = userSession.getWishList();
				final List<Wishlist2EntryModel> entries = new ArrayList<Wishlist2EntryModel>(wishList.getEntries());
				final ModelService modelService = YStorefoundation.getRequestContext().getPlatformServices().getModelService();
				for (final Wishlist2EntryModel _entry : selectedEntries)
				{
					entries.remove(_entry);
					modelService.remove(_entry);
				}
				wishList.setEntries(entries);
				modelService.save(wishList);
				userSession.setWishList(wishList);
			}
		}

		@Override
		public String action()
		{
			return WISH_LIST_PAGE;
		}

	}

	public List<? extends SelectItem> getOtherWishLists()
	{
		return this.otherWishLists;
	}

	public Wishlist2Model getWishList()
	{
		return this.wishList;
	}

	public void setWishList(final Wishlist2Model wishList)
	{
		this.wishList = wishList;
	}

	public YComponentEventHandler<EditWishListComponent> getSaveWishListEvent()
	{
		return this.ehSave;
	}

	public YComponentEventHandler<EditWishListComponent> getCancelEditWishListEvent()
	{
		return this.ehCancelSave;
	}

	public YComponentEventHandler<EditWishListComponent> getAddSelectedProductsToCartEvent()
	{
		return this.ehAddSelectedProductsToCart;
	}

	public YComponentEventHandler<EditWishListComponent> getCopyToAnotherWishListEvent()
	{
		return this.ehCopyToAnotherWishList;
	}

	public YComponentEventHandler<EditWishListComponent> getDeleteSelectedProductsEvent()
	{
		return this.ehDeleteSelectedProducts;
	}

	public Wishlist2Model getDestWishList()
	{
		return this.destWishList;
	}

	public void setDestWishList(final Wishlist2Model destWishList)
	{
		this.destWishList = destWishList;
	}

}
