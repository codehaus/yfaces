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
package ystorefoundationpackage.yfaces.component.product.fv;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;

import ystorefoundationpackage.faces.SfSelectItemGroup;



/**
 * Similar to search function. The conditions are limited to the available features for the products.
 */
public interface FeatureValueSelectorComponent extends YComponent
{

	public void setCategory(CategoryModel category);

	public CategoryModel getCategory();

	/**
	 * A List of {@link SfSelectItemGroup}
	 * 
	 * @return A List of FeatureValueSelectors
	 */
	public List<SfSelectItemGroup> getSelectorList();

	//	public void setSelectorList(List<SfSelectItemGroup> selectorList);

	public void setFeatureValuesSelectorCount(Integer count);

	public Integer getFeatureValuesSelectorCount();

	public boolean getFunnelMode(); // NOPMD

	public void setFunnelMode(boolean mode);

	public Boolean getIgnoreClassificationClass();

	public void setIgnoreClassificationClass(Boolean ignore);

	/**
	 * Returns the initial List of Products.
	 * 
	 * @return the initial List of Products.
	 */
	public List<ProductModel> getProductList();

	/**
	 * Sets the initial List of Products.
	 * 
	 * @param productList
	 *           the initial List of Products
	 */
	public void setProductList(List<ProductModel> productList);


	public List<ProductModel> getFilteredProductList();

	/**
	 * Sets the current List of Products (filtered list)
	 * 
	 * @param productList
	 */
	public void setFilteredProductList(List<ProductModel> productList);

	public YComponentEventHandler<FeatureValueSelectorComponent> getSubmitEvent();

	public YComponentEventHandler<FeatureValueSelectorComponent> getResetEvent();

}
