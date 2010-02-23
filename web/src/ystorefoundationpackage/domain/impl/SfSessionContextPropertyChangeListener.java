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
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.SfSessionContext.CatalogChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.CategoryChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.CurrencyChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.ProductChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.UserChangeListener;


/**
 * 
 * 
 */
public class SfSessionContextPropertyChangeListener implements UserChangeListener, CatalogChangeListener, CategoryChangeListener,
		CurrencyChangeListener, ProductChangeListener
{

	public void performUserChanged(final UserModel oldUser, final UserModel newUser)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		userSession.setCart(null);

		if (newUser.getSessionCurrency() != null)
		{
			userSession.getPropertyHandler().setProperty(SfSessionContext.CURRENCY, newUser.getSessionCurrency(), false);
		}
		if (newUser.getSessionLanguage() != null)
		{
			userSession.setLanguage(newUser.getSessionLanguage());
		}

		if (JaloBridge.getInstance().isAnonymous(newUser))
		{
			final CatalogModel catalog = YStorefoundation.getRequestContext().getDefaultValues().getDefaultCatalog();
			userSession.setCatalog(catalog);
		}

		// userSession.getAttributes().clear();
	}


	public void performCatalogChanged(final CatalogModel oldCatalog, final CatalogModel newCatalog)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

		//reset category when not explicitly set 
		if (!userSession.isCategoryChanged())
		{
			userSession.setCategory(null);
		}

		//reset product when not explicitly set		
		if (!userSession.isProductChanged())
		{
			userSession.setProduct(null);
		}
	}

	public void performCategoryChanged(final CategoryModel oldCategory, final CategoryModel newCategory)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

		if (newCategory != null)
		{
			//			final CatalogModel newCatalog = Webfoundation.getInstance().getServices().getCatalogService()
			//					.findByCategory(newCategory);
			final CatalogModel newCatalog = newCategory.getCatalogVersion().getCatalog();
			final CatalogModel oldCatalog = userSession.getCatalog();

			if (!newCatalog.equals(oldCatalog))
			{
				userSession.setCatalog(newCatalog);
			}

			//reset product when not explicitly set		
			if (!userSession.isProductChanged())
			{
				userSession.setProduct(null);
			}
		}
	}

	public void performProductChanged(final ProductModel oldProduct, final ProductModel newProduct)
	{
		if (newProduct != null)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final List<List<CategoryModel>> catgPath = reqCtx.getProductManagement().getCategoryPath(newProduct);
			final List<CategoryModel> singlePath = catgPath.get(0);
			final CategoryModel catg = singlePath.get(singlePath.size() - 1);
			reqCtx.getSessionContext().setCategory(catg);
		}
	}

	public void performCurrencyChanged(final CurrencyModel oldCurrency, final CurrencyModel newCurrency)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		userSession.setCart(null);

		final CartModel cart = userSession.getCart();
		YStorefoundation.getRequestContext().getOrderManagement().updateCart(cart);
	}

}
