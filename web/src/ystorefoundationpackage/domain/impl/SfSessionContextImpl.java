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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.model.Wishlist2EntryModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import org.codehaus.yfaces.context.YSessionContextImpl;
import org.codehaus.yfaces.util.YPropertyHandler;

import ystorefoundationpackage.domain.DefaultValues;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.UserMessages;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.history.HistoryCmpUserSessionListener;
import ystorefoundationpackage.yfaces.component.wishlist.WishlistCmpUserSessionListener;


/**
 * {@link SfSessionContext} implementation.
 */
public class SfSessionContextImpl extends YSessionContextImpl implements SfSessionContext
{
	private YPropertyHandler sessionProps = null;

	public SfSessionContextImpl()
	{
		super();
		this.messages = new UserMessages();
		this.sessionProps = new YPropertyHandler();

		final HistoryCmpUserSessionListener historyListener = new HistoryCmpUserSessionListener();
		final SfSessionContext2JaloSessionListener jalolistener = new SfSessionContext2JaloSessionListener();
		final SfSessionContextPropertyChangeListener sfListener = new SfSessionContextPropertyChangeListener();
		final WishlistCmpUserSessionListener wishlistListener = new WishlistCmpUserSessionListener();

		this.addUserChangeListener(jalolistener);
		this.addLanguageChangeListener(jalolistener);
		this.addCurrencyChangeListener(jalolistener);

		this.addUserChangeListener(sfListener);
		this.addCurrencyChangeListener(sfListener);
		this.addCatalogChangeListener(sfListener);
		this.addCategoryChangeListener(sfListener);
		this.addProductChangeListener(sfListener);


		this.addUserChangeListener(historyListener);
		this.addCategoryChangeListener(historyListener);
		this.addProductChangeListener(historyListener);

		this.addUserChangeListener(wishlistListener);
	}


	@Override
	protected void initialize()
	{
		// within this method it's safe to use the spring build context tree without getting
		// cyclic reference problems

		//set some defaults
		final DefaultValues defaultValues = YStorefoundation.getRequestContext().getDefaultValues();

		final CatalogModel catalog = defaultValues.getDefaultCatalog();
		final LanguageModel lang = defaultValues.getDefaultLanguage();
		final CurrencyModel cur = defaultValues.getDefaultCurrency();
		final UserModel user = defaultValues.getDefaultCustomer();

		sessionProps.setProperty(CATALOG, catalog, false);
		sessionProps.setProperty(CURRENCY, cur, false);
		sessionProps.setProperty(USER, user, false);
		sessionProps.setProperty(LANGUAGE, lang);
	}


	public UserModel getUser()
	{
		return sessionProps.getProperty(USER);
	}

	public void setUser(final UserModel user)
	{
		sessionProps.setProperty(USER, user);
	}

	/**
	 * @return true if the session user is anonymous
	 */
	public boolean isAnonymousUser()
	{
		return JaloBridge.getInstance().isAnonymous(getUser());
	}

	/**
	 * Returns the current language.
	 * 
	 * @return {@link LanguageModel}
	 */
	public LanguageModel getLanguage()
	{
		return sessionProps.getProperty(LANGUAGE);
	}

	/**
	 * Sets the current language.
	 * 
	 * @param language
	 *           {@link LanguageModel}
	 */
	public void setLanguage(final LanguageModel language)
	{
		sessionProps.setProperty(LANGUAGE, language);
	}

	/**
	 * Returns the current currency.
	 * 
	 * @return {@link CurrencyModel}
	 */
	public CurrencyModel getCurrency()
	{
		return sessionProps.getProperty(CURRENCY);
	}

	/**
	 * Sets the current currency.
	 * 
	 * @param currency
	 *           {@link CurrencyModel}
	 */
	public void setCurrency(final CurrencyModel currency)
	{
		sessionProps.setProperty(CURRENCY, currency);
	}

	/**
	 * Returns the current catalog.
	 * 
	 * @return {@link CatalogModel}
	 */
	public CatalogModel getCatalog()
	{
		return sessionProps.getProperty(CATALOG);
	}


	/**
	 * Sets the current catalog.
	 * 
	 * @param catalog
	 *           {@link CatalogModel}
	 */
	public void setCatalog(final CatalogModel catalog)
	{
		sessionProps.setProperty(CATALOG, catalog);
	}

	/**
	 * Returns the current category.
	 * 
	 * @return {@link CategoryModel}
	 */
	public CategoryModel getCategory()
	{
		return sessionProps.getProperty(CATEGORY);
	}

	/**
	 * Sets the current category.
	 * 
	 * @param category
	 *           {@link CategoryModel}
	 */
	public void setCategory(final CategoryModel category)
	{
		sessionProps.setProperty(CATEGORY, category);
	}

	/**
	 * Returns the current product.
	 * 
	 * @return {@link ProductModel}
	 */
	public ProductModel getProduct()
	{
		return sessionProps.getProperty(PRODUCT);
	}

	/**
	 * Sets the current product.
	 * 
	 * @param product
	 *           {@link ProductModel}
	 */
	public void setProduct(final ProductModel product)
	{
		sessionProps.setProperty(PRODUCT, product);
	}

	/**
	 * Returns the current cart.
	 * 
	 * @return {@link CartModel}
	 */
	public CartModel getCart()
	{
		CartModel result = sessionProps.getProperty(CART);

		//lazy cart creation (performance)
		if (result == null)
		{
			//result = Webfoundation.getInstance().getServices().getCartService().createDefaultCart(getUser());
			result = YStorefoundation.getRequestContext().getDefaultValues().getDefaultCart();
			sessionProps.setProperty(CART, result);
		}
		return result;
	}

	/**
	 * Sets the current cart.
	 * 
	 * @param cart
	 *           {@link CartModel}
	 */
	public void setCart(final CartModel cart)
	{
		sessionProps.setProperty(CART, cart);
	}

	/**
	 * Returns the wish list for the current user. If the user does not have a wish list, one will be created.
	 * 
	 * @return {@link Wishlist2Model}
	 */
	public Wishlist2Model getWishList()
	{
		Wishlist2Model result = sessionProps.getProperty(WISHLIST);
		if (result == null)
		{
			try
			{
				result = YStorefoundation.getRequestContext().getPlatformServices().getWishlistService()
						.getDefaultWishlist(getUser());
			}
			catch (final NullPointerException ne)
			{
				result = createWishlist(getUser(), JaloBridge.getInstance().isAnonymous(getUser()));
			}
			sessionProps.setProperty(WISHLIST, result);
		}
		return result;
	}

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

	/**
	 * Sets the wish list for the current user.
	 * 
	 * @param wishList
	 *           {@link Wishlist2Model}
	 */
	public void setWishList(final Wishlist2Model wishList)
	{
		sessionProps.setProperty(WISHLIST, wishList);
	}

	private UserMessages messages = null;

	public UserMessages getMessages()
	{
		return this.messages;
	}

	public TimeZone getTimeZone()
	{
		return TimeZone.getDefault();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.domain.SfSessionContext#getLocale()
	 */
	public Locale getLocale()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getLocalizationService().getCurrentLocale();
	}

	public void addUserChangeListener(final UserChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.USER, UserChangeListener.class, listener);
	}

	public void addLanguageChangeListener(final LanguageChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.LANGUAGE, LanguageChangeListener.class, listener);
	}

	public void addCurrencyChangeListener(final CurrencyChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.CURRENCY, CurrencyChangeListener.class, listener);
	}

	public void addCatalogChangeListener(final CatalogChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.CATALOG, CatalogChangeListener.class, listener);
	}

	public void addCategoryChangeListener(final CategoryChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.CATEGORY, CategoryChangeListener.class, listener);
	}

	public void addProductChangeListener(final ProductChangeListener listener)
	{
		sessionProps.addPropertyChangeListener(SfSessionContextImpl.PRODUCT, ProductChangeListener.class, listener);
	}


	public boolean isUserChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.USER);
	}

	public boolean isLanguageChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.LANGUAGE);
	}

	public boolean isCurrencyChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.CURRENCY);
	}

	public boolean isCatalogChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.CATALOG);
	}

	public boolean isCategoryChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.CATEGORY);
	}

	public boolean isCartChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.CART);
	}

	public boolean isProductChanged()
	{
		return sessionProps.isPropertyChanged(SfSessionContextImpl.PRODUCT);
	}

	public YPropertyHandler getPropertyHandler()
	{
		return this.sessionProps;
	}

	@Override
	protected void refresh()
	{
		super.refresh();
		this.sessionProps.resetPropertyChanged();
	}

}
