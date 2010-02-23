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
package ystorefoundationpackage.domain;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.wishlist2.model.Wishlist2Model;

import java.util.Locale;
import java.util.TimeZone;

import org.codehaus.yfaces.context.YSessionContext;
import org.codehaus.yfaces.util.YPropertyHandler;



/**
 * Storefoundation specific {@link YSessionContext} implementation.
 */
public interface SfSessionContext extends YSessionContext
{
	public static final String USER = "user";
	public static final String LANGUAGE = "language";
	public static final String CATALOG = "catalog";
	public static final String CATEGORY = "category";
	public static final String CURRENCY = "currency";
	public static final String PRODUCT = "product";
	public static final String CART = "cart";
	public static final String WISHLIST = "wishlist";//WishlistService.USERPROPERTY_WISHLIST;

	/** Listener for user change events. */
	interface UserChangeListener
	{
		public void performUserChanged(UserModel oldUser, UserModel newUser);
	}

	/** Listener for catalog change events. */
	interface CatalogChangeListener
	{
		public void performCatalogChanged(CatalogModel oldCatalog, CatalogModel newCatalog);
	}

	/** Listener for product change events. */
	interface ProductChangeListener
	{
		public void performProductChanged(ProductModel oldProduct, ProductModel newProduct);
	}

	/** Listener for category change events. */
	interface CategoryChangeListener
	{
		public void performCategoryChanged(CategoryModel oldCategory, CategoryModel newCategory);
	}

	/** Listener for language change events. */
	interface LanguageChangeListener
	{
		public void performLanguageChanged(LanguageModel oldLanguage, LanguageModel newLanguage);
	}

	/** Listener for currency change events. */
	interface CurrencyChangeListener
	{
		public void performCurrencyChanged(CurrencyModel oldCurrency, CurrencyModel newCurrency);
	}

	UserModel getUser();

	void setUser(final UserModel user);

	/**
	 * @return true if the session user is anonymous
	 */
	boolean isAnonymousUser();

	/**
	 * Returns the current language.
	 * 
	 * @return {@link LanguageModel}
	 */
	LanguageModel getLanguage();

	/**
	 * Sets the current language.
	 * 
	 * @param language
	 *           {@link LanguageModel}
	 */
	void setLanguage(final LanguageModel language);

	/**
	 * Returns the current currency.
	 * 
	 * @return {@link CurrencyModel}
	 */
	CurrencyModel getCurrency();

	/**
	 * Sets the current currency.
	 * 
	 * @param currency
	 *           {@link CurrencyModel}
	 */
	void setCurrency(final CurrencyModel currency);

	/**
	 * Returns the current catalog.
	 * 
	 * @return {@link CatalogModel}
	 */
	CatalogModel getCatalog();


	/**
	 * Sets the current catalog.
	 * 
	 * @param catalog
	 *           {@link CatalogModel}
	 */
	void setCatalog(final CatalogModel catalog);

	/**
	 * Returns the current category.
	 * 
	 * @return {@link CategoryModel}
	 */
	CategoryModel getCategory();

	/**
	 * Sets the current category.
	 * 
	 * @param category
	 *           {@link CategoryModel}
	 */
	void setCategory(final CategoryModel category);

	/**
	 * Returns the current product.
	 * 
	 * @return {@link ProductModel}
	 */
	ProductModel getProduct();

	/**
	 * Sets the current product.
	 * 
	 * @param product
	 *           {@link ProductModel}
	 */
	void setProduct(final ProductModel product);

	/**
	 * Returns the current cart.
	 * 
	 * @return {@link CartModel}
	 */
	CartModel getCart();

	/**
	 * Sets the current cart.
	 * 
	 * @param cart
	 *           {@link CartModel}
	 */
	void setCart(final CartModel cart);

	/**
	 * Returns the wish list for the current user. If the user does not have a wish list, one will be created.
	 * 
	 * @return {@link Wishlist2Model}
	 */
	Wishlist2Model getWishList();


	/**
	 * Sets the wish list for the current user.
	 * 
	 * @param wishList
	 *           {@link Wishlist2Model}
	 */
	void setWishList(final Wishlist2Model wishList);


	UserMessages getMessages();

	TimeZone getTimeZone();

	/**
	 * Returns the current session locale. As the session is available in view files the return value is very useful for
	 * locale sensitive components.
	 * 
	 * @return {@link Locale}
	 */
	Locale getLocale();

	void addUserChangeListener(final UserChangeListener listener);

	void addLanguageChangeListener(final LanguageChangeListener listener);

	void addCurrencyChangeListener(final CurrencyChangeListener listener);

	void addCatalogChangeListener(final CatalogChangeListener listener);

	void addCategoryChangeListener(final CategoryChangeListener listener);

	void addProductChangeListener(final ProductChangeListener listener);

	boolean isUserChanged();

	boolean isLanguageChanged();

	boolean isCurrencyChanged();

	boolean isCatalogChanged();

	boolean isCategoryChanged();

	boolean isCartChanged();

	boolean isProductChanged();

	YPropertyHandler getPropertyHandler();

}
