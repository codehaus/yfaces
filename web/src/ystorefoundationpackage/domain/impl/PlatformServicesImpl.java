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

import ystorefoundationpackage.domain.PlatformServices;


/**
 * 
 * 
 */
public class PlatformServicesImpl implements PlatformServices
{
	private CustomerReviewService customerReviewService = null;
	private ModelService modelService = null;
	private I18NService i18NService = null;
	private UserService userService = null;
	private LocalizationService localizationService = null;
	private CartService cartService = null;
	private OrderService orderService = null;
	private FlexibleSearchService flexibleSearchService = null;
	private CmsService cmsService = null;
	private VoucherService voucherService = null;
	private PriceService priceService = null;
	private ClassificationService classificationService = null;

	/**
	 * @return the classificationService
	 */
	public ClassificationService getClassificationService()
	{
		return classificationService;
	}

	/**
	 * @param classificationService
	 *           the classificationService to set
	 */
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;
	}

	/**
	 * @return the priceService
	 */
	public PriceService getPriceService()
	{
		return priceService;
	}

	/**
	 * @param priceService
	 *           the priceService to set
	 */
	public void setPriceService(final PriceService priceService)
	{
		this.priceService = priceService;
	}

	/**
	 * @return the flexibleSearchService
	 */
	public FlexibleSearchService getFlexibleSearchService()
	{
		return flexibleSearchService;
	}

	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

	/**
	 * @return the orderService
	 */
	public OrderService getOrderService()
	{
		return orderService;
	}

	/**
	 * @param orderService
	 *           the orderService to set
	 */
	public void setOrderService(final OrderService orderService)
	{
		this.orderService = orderService;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	private CatalogService catalogService = null;
	private CategoryService categoryService = null;
	private ProductService productService = null;
	private TypeService typeService = null;
	private Wishlist2Service wishlistService = null;


	/**
	 * @param typeService
	 *           the typeService to set
	 */
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	@Override
	public TypeService getTypeService()
	{
		return this.typeService;
	}

	public Wishlist2Service getWishlistService()
	{
		return wishlistService;
	}

	public void setWishlistService(final Wishlist2Service wishlistService)
	{
		this.wishlistService = wishlistService;
	}

	/**
	 * @return the localizationService
	 */
	public LocalizationService getLocalizationService()
	{
		return localizationService;
	}

	/**
	 * @param localizationService
	 *           the localizationService to set
	 */
	public void setLocalizationService(final LocalizationService localizationService)
	{
		this.localizationService = localizationService;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	/**
	 * @return the i18NService
	 */
	public I18NService getI18NService()
	{
		return i18NService;
	}

	/**
	 * @param service
	 *           the i18NService to set
	 */
	public void setI18NService(final I18NService service)
	{
		i18NService = service;
	}

	public CustomerReviewService getCustomerReviewService()
	{
		return customerReviewService;
	}

	public void setCustomerReviewService(final CustomerReviewService customerReviewService)
	{
		this.customerReviewService = customerReviewService;
	}

	public ModelService getModelService()
	{
		return modelService;
	}

	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	public CatalogService getCatalogService()
	{
		return catalogService;
	}

	public void setCatalogService(final CatalogService catalogService)
	{
		this.catalogService = catalogService;
	}

	public CategoryService getCategoryService()
	{
		return categoryService;
	}

	public void setCategoryService(final CategoryService categoryService)
	{
		this.categoryService = categoryService;
	}

	public ProductService getProductService()
	{
		return productService;
	}

	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}

	public CmsService getCmsService()
	{
		return cmsService;
	}

	public void setCmsService(final CmsService cmsService)
	{
		this.cmsService = cmsService;
	}

	public VoucherService getVoucherService()
	{
		return voucherService;
	}

	public void setVoucherService(final VoucherService voucherService)
	{
		this.voucherService = voucherService;
	}

}
