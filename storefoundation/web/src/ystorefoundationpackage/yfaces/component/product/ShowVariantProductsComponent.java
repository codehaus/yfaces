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

import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.codehaus.yfaces.component.YComponent;

import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.yfaces.component.product.DefaultShowVariantProductsComponent.SimpleVariantProduct;


/**
 * This component displays all variant products of the current product.
 */
public interface ShowVariantProductsComponent extends YComponent
{

	//no event for the showVariantProducts component

	//model
	public ProductModel getCurrentProduct();

	public void setCurrentProduct(ProductModel product);

	public boolean isPrintPage();

	public void setPrintPage(boolean printPage);

	public List<SimpleVariantProduct> getVariantProducts();

	public List<FormattedAttribute> getVariantAttributeDescriptors();

}
