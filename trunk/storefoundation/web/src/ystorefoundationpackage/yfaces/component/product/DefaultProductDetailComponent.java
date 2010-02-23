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
package ystorefoundationpackage.yfaces.component.product;


import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.wishlist2.enums.Wishlist2EntryPriority;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.GlobalFrame;
import ystorefoundationpackage.yfaces.frame.LoginFrame;
import ystorefoundationpackage.yfaces.frame.WishListFrame;


/**
 * Implementation of the <code>ProductDetailComponent</code> interface.
 */
public class DefaultProductDetailComponent extends AbstractYComponent implements ProductDetailComponent
{

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultProductDetailComponent.class.getName());

	private static final long serialVersionUID = 7705524089152673380L;

	private static final String TO_CART_LINK = "/pages/cartPage.jsf";
	private static final String TO_WISH_LIST_LINK = "/pages/wishListPage.jsf";

	private ProductModel product = null;
	private String productDescription = null;
	private String productName = null;
	//private transient FeatureContainer features = null;
	private transient ProductFeatures features = null;

	private int quantity = 1;
	private boolean printPage = false;
	private transient String quantityInCart = null;

	private YComponentEventHandler<ProductDetailComponent> ehAddToCart = null;
	private YComponentEventHandler<ProductDetailComponent> ehTellFriend = null;
	private YComponentEventHandler<ProductDetailComponent> ehCustomerReview = null;
	private YComponentEventHandler<ProductDetailComponent> ehAddToWishlist = null;
	private YComponentEventHandler<ProductDetailComponent> ehPrint = null;

	/**
	 * Constructor.
	 */
	public DefaultProductDetailComponent()
	{
		super();
		this.ehAddToCart = super.createEventHandler(new AddToCartAction());
		this.ehTellFriend = super.createEventHandler();
		this.ehCustomerReview = super.createEventHandler(new CustomerReviewAction());
		this.ehAddToWishlist = super.createEventHandler(new AddToWishListAction());
		this.ehPrint = super.createEventHandler(new PrintPageAction());
	}

	@Override
	public void validate()
	{
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}
		if (this.product != null)
		{
			setLocalizedAttributes(this.product);
		}
	}

	@Override
	public void refresh()
	{
		final SfSessionContext sessCtx = YStorefoundation.getRequestContext().getSessionContext();
		this.product = sessCtx.getProduct();
		setLocalizedAttributes(this.product);

		if (sessCtx.isLanguageChanged())
		{
			this.features = null;
		}
	}

	private void setLocalizedAttributes(final ProductModel p)
	{
		//check if p is a base product
		if (p.getVariantType() != null)
		{
			final Collection<VariantProductModel> c = p.getVariants();
			if (c != null && !c.isEmpty())
			{
				final ProductModel variant = c.iterator().next();
				this.productName = p.getName();
				this.productDescription = p.getDescription();
				this.product = variant;
				YStorefoundation.getRequestContext().getSessionContext().setProduct(this.product);
			}
			else
			{
				this.productName = p.getName();
				this.productDescription = p.getDescription();
				this.product = p;
			}
		}
		else
		{
			if (p instanceof VariantProductModel)
			{
				final ProductModel base = ((VariantProductModel) p).getBaseProduct();
				this.productName = base.getName();
				this.productDescription = base.getDescription();
				this.product = p;
			}
			else
			{
				this.productName = p.getName();
				this.productDescription = p.getDescription();
				this.product = p;
			}
		}
	}

	public YComponentEventHandler<ProductDetailComponent> getAddToCartEvent()
	{
		return this.ehAddToCart;
	}

	public YComponentEventHandler<ProductDetailComponent> getTellAFriendEvent()
	{
		return this.ehTellFriend;
	}

	public YComponentEventHandler<ProductDetailComponent> getAddToWishListEvent()
	{
		return this.ehAddToWishlist;
	}

	public YComponentEventHandler<ProductDetailComponent> getPrintPageEvent()
	{
		return this.ehPrint;
	}

	/**
	 * This event gets fired when the user tries to add the current product to the cart.
	 */
	public static class AddToCartAction extends DefaultYComponentEventListener<ProductDetailComponent>
	{

		private static final long serialVersionUID = -1855223946408219115L;

		@Override
		public void actionListener(final YComponentEvent<ProductDetailComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();

			final ProductDetailComponent cmp = event.getComponent();
			final AddToCartResult state = reqCtx.getOrderManagement().addToCart(sessCtx.getCart(), cmp.getProduct(),
					cmp.getQuantity(), null);
			final String resource = reqCtx.getURLFactory().createExternalForm(TO_CART_LINK);
			final String toCartLink = "<a href=\"" + resource + "\">"
					+ reqCtx.getContentManagement().getLocalizedMessage("frames.productFrame.toCart") + "</a>";
			switch (state)
			{
				case ADDED_ONE:
					sessCtx.getMessages().pushInfoMessage("addedtocart_one", toCartLink);
					break;
				case ADDED_SEVERAL:
					sessCtx.getMessages().pushInfoMessage("addedtocart_many", String.valueOf(cmp.getQuantity()), toCartLink);
					break;
				case UNEXPECTED_ERROR:
					sessCtx.getMessages().pushErrorMessage("[unexpectedError]");
					break;
			}
		}

	}

	/**
	 * This event gets fired when the user tries to write a comment for the current product.
	 */
	public static class CustomerReviewAction extends DefaultYComponentEventListener<ProductDetailComponent>
	{
		private static final long serialVersionUID = -7652937426656456727L;

		@Override
		public String action()
		{
			String result = "createCustomerReviewPage";
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			if (JaloBridge.getInstance().isAnonymous(userSession.getUser()))
			{
				final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
				final YPageContext page = convCtx.getOrCreateNextPage();
				page.getOrCreateFrame(LoginFrame.class).getLoginComponent().getValue().setSuccessForward(result);
				page.getOrCreateFrame(GlobalFrame.class).getLoginComponent().getValue().setSuccessForward(result);
				result = NavigationOutcome.LOGIN_PAGE.id;
			}
			return result;
		}

	}

	/**
	 * This event gets fired when the user tries to add the current product to the active wish list.
	 */
	public static class AddToWishListAction extends DefaultYComponentEventListener<ProductDetailComponent>
	{

		private static final long serialVersionUID = 1758221267133494966L;

		@Override
		public void actionListener(final YComponentEvent<ProductDetailComponent> event)
		{
			final SfRequestContext sfCtx = YStorefoundation.getRequestContext();
			final SfSessionContext userSession = sfCtx.getSessionContext();
			final ProductDetailComponent cmp = event.getComponent();
			final Wishlist2Model wishList = userSession.getWishList();
			final ProductModel product = cmp.getProduct();

			List<Wishlist2EntryModel> entries;
			final boolean anonymousUser = JaloBridge.getInstance().isAnonymous(userSession.getUser());
			if (anonymousUser)
			{
				entries = (List<Wishlist2EntryModel>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
						WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES);
				if (entries == null)
				{
					entries = new ArrayList<Wishlist2EntryModel>();
				}
			}
			else
			{
				entries = new ArrayList<Wishlist2EntryModel>(wishList.getEntries());
			}
			final Iterator<Wishlist2EntryModel> it = entries.iterator();
			while (it.hasNext())
			{
				final Wishlist2EntryModel entry = it.next();
				if (entry.getProduct().getCode().equals(product.getCode()))
				{
					userSession.getMessages().pushInfoMessage("components.productDetailCmp.alreadyInWishList", cmp.getProductName());
					return;
				}
			}

			entries.add(createEntry(product, anonymousUser));
			wishList.setEntries(entries);
			userSession.setWishList(wishList);
			if (!anonymousUser)
			{
				sfCtx.getPlatformServices().getModelService().save(wishList);
			}
			else
			{
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(WishListFrame.ANONYMOUS_WISH_LIST_ENTRIES,
						entries);
			}

			final String resource = sfCtx.getURLFactory().createExternalForm(TO_WISH_LIST_LINK);
			final String toWishListLink = "<a href=\"" + resource + "\">" + wishList.getName() + "</a>";
			userSession.getMessages().pushInfoMessage("components.productDetailCmp.addedToWishList", cmp.getProductName(),
					toWishListLink);
		}

		//only for anonymous user
		private Wishlist2EntryModel createEntry(final ProductModel product, final boolean anonymousUser)
		{
			final Wishlist2EntryModel entry = new Wishlist2EntryModel();
			entry.setProduct(product);
			entry.setDesired(Integer.valueOf(1));
			entry.setReceived(Integer.valueOf(0));
			entry.setAddedDate(new Date());
			entry.setPriority(Wishlist2EntryPriority.MEDIUM);
			if (!anonymousUser)
			{
				YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(entry);
			}
			return entry;
		}
	}

	/**
	 * This event gets fired when the user tries to print the current page.
	 */
	public static class PrintPageAction extends DefaultYComponentEventListener<ProductDetailComponent>
	{
		private static final long serialVersionUID = 3602251250192322146L;

		@Override
		public String action()
		{
			return "pagePrintPage";
		}
	}

	public ProductModel getProduct()
	{
		return this.product;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

	public boolean isVariantsAvailable()
	{
		return this.product.getVariantType() != null || this.product instanceof VariantProductModel;
	}

	public String getProductName()
	{
		return productName;
	}

	public String getProductDescription()
	{
		return this.productDescription;
	}

	private transient Prices pricings = null;

	public Prices getPricing()
	{
		if (this.pricings == null)
		{
			this.pricings = YStorefoundation.getRequestContext().getOrderManagement().getPrices(this.product);
		}
		return this.pricings;
	}

	public ProductFeatures getFeatures()
	{
		if (this.features == null)
		{
			//this.features = JaloBridge.getInstance().getFeatures(this.product);
			this.features = YStorefoundation.getRequestContext().getProductManagement().getFeatures(this.product);
		}
		return this.features;
	}


	public int getQuantity()
	{
		return this.quantity;
	}

	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	public boolean isPrintPage()
	{
		return this.printPage;
	}

	public void setPrintPage(final boolean printPage)
	{
		this.printPage = printPage;
	}

	public String getQuantityInCart()
	{
		if (this.quantityInCart == null)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			final Map<UnitModel, Long> quantityMap = getProductCount(userSession.getCart(), getProduct());
			this.quantityInCart = "0";
			if (!quantityMap.isEmpty())
			{
				final ArrayList<String> entries = new ArrayList<String>();
				for (final Map.Entry<UnitModel, Long> entry : quantityMap.entrySet())
				{
					final UnitModel unit = entry.getKey();
					final Long count = entry.getValue();
					entries.add(count + (unit.getName() != null ? " " + unit.getName() : " ???"));
				}
				this.quantityInCart = StringUtils.join(entries, ",");
			}
		}
		return this.quantityInCart;
	}

	/*
	 * Methods for Customer Reviews
	 */

	public boolean isCustomerReviewAvailable()
	{
		final Integer reviews = this.product.getNumberOfReviews();
		return (reviews != null && reviews.intValue() > 0);
		//		final Product p = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(product);
		//		final int number = CustomerReviewManager.getInstance().getNumberOfReviews(p).intValue();
		//		return (number > 0);
	}

	public YComponentEventHandler<ProductDetailComponent> getCustomerReviewEvent()
	{
		return this.ehCustomerReview;
	}

	private Map<UnitModel, Long> getProductCount(final CartModel cart, final ProductModel product)
	{
		Map<UnitModel, Long> result = Collections.EMPTY_MAP;

		final boolean isBaseProduct = product.getVariantType() != null;

		if (!isBaseProduct)
		{
			result = new HashMap<UnitModel, Long>();
			final List<AbstractOrderEntryModel> entries = JaloBridge.getInstance().getCartEntriesByProduct(cart, product);
			for (final AbstractOrderEntryModel entry : entries)
			{
				final UnitModel unit = entry.getUnit();
				final Long entryQuantity = entry.getQuantity();

				final Long oldQuantity = result.get(unit);
				final Long newQuantity = (oldQuantity != null) ? Long.valueOf(oldQuantity.longValue() + entryQuantity.longValue())
						: entryQuantity;

				result.put(unit, newQuantity);
			}
		}

		return result;
	}

}
