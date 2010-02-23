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
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;


/**
 * Default values are used whenever a customer initially enters a shop.
 */
public interface DefaultValues
{
	String INFO_MESSAGE = "info";
	String ERROR_MESSAGE = "error";

	/**
	 * The catalog which shall be displayed/selected.
	 * 
	 * @return {@link CatalogModel}
	 */
	CatalogModel getDefaultCatalog();

	List<CatalogModel> getDefaultCatalogs();


	//may become useless (currently only used for intelligent post initialization of components)
	/**
	 * Finds the initial default category which shall be taken for the store front when no working category is available.
	 * 
	 * @return default category
	 */
	CategoryModel getDefaultCategory();

	/**
	 * Returns the default currency.
	 * 
	 * @return default currency {@link CurrencyModel}
	 */
	CurrencyModel getDefaultCurrency();

	/**
	 * Returns the default language. Guarantees that one language is active in the user session.
	 * 
	 * @return selected language {@link LanguageModel}
	 */
	LanguageModel getDefaultLanguage();


	UserModel getDefaultCustomer();

	/**
	 * Returns the default cart.
	 * <p>
	 * Default cart is empty and contains some defaults according the current user.
	 * <ul>
	 * <li>payment- and delivery address</li>
	 * <li>payment mode (e.g. credit, debit, invoice, ...)</li>
	 * <li>payment information (depending on payment mode, eg. credit card account information)</li>
	 * <li>delivery mode (e.g. DHL, Fed Ex, ...)</li>
	 * </ul>
	 * 
	 * @return {@link CartModel}
	 */
	CartModel getDefaultCart();



}
