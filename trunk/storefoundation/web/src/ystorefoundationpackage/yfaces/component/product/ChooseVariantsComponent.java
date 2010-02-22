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
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;

import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * The user can choose a variant product with this component.
 */
public interface ChooseVariantsComponent extends YComponent
{

	//model
	public ProductModel getProduct();

	public void setProduct(ProductModel product);

	public ProductModel getCurrentVariantProduct();

	public ProductModel getBaseProduct();

	public List<ProductModel> getVariantProducts();

	public List<SfSelectItemGroup> getAttributesSelectorList();

	//events: select the attributes of the variant product
	public YComponentEventHandler<ChooseVariantsComponent> getShowVariantEvent();

}
