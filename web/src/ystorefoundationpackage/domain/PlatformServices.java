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


import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.cms.CmsService;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.OrderService;
import de.hybris.platform.product.PriceService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.i18n.LocalizationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.wishlist2.Wishlist2Service;


/**
 *
 */
public interface PlatformServices
{
	CustomerReviewService getCustomerReviewService();

	FlexibleSearchService getFlexibleSearchService();

	ModelService getModelService();

	UserService getUserService();

	I18NService getI18NService();

	LocalizationService getLocalizationService();

	CartService getCartService();

	CatalogService getCatalogService();

	CategoryService getCategoryService();

	ClassificationService getClassificationService();

	OrderService getOrderService();

	ProductService getProductService();

	TypeService getTypeService();

	Wishlist2Service getWishlistService();

	CmsService getCmsService();

	VoucherService getVoucherService();

	PriceService getPriceService();

}
