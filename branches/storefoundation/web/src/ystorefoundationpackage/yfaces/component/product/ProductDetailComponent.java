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

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.product.ProductModel;

import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;



/**
 * This component displays the product in detail.
 */
public interface ProductDetailComponent extends YModel
{
	//model
	public ProductModel getProduct();

	public void setProduct(ProductModel product);

	public Prices getPricing();

	public boolean isVariantsAvailable();

	public String getProductName();

	public String getProductDescription();

	public ProductFeatures getFeatures();

	public int getQuantity();

	public void setQuantity(int quantity);

	public String getQuantityInCart();

	public boolean isPrintPage();

	public void setPrintPage(boolean printPage);

	//event: addToCart, and tellAFriend, and addToWishList, and printPage
	public YEventHandler<ProductDetailComponent> getAddToCartEvent();

	public YEventHandler<ProductDetailComponent> getTellAFriendEvent();

	public YEventHandler<ProductDetailComponent> getCustomerReviewEvent();

	public YEventHandler<ProductDetailComponent> getAddToWishListEvent();

	public YEventHandler<ProductDetailComponent> getPrintPageEvent();

}
