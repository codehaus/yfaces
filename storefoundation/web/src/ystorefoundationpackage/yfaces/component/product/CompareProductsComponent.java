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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;



/**
 * This component compares the selected products according to the given compare attributes. It provides a comparison
 * table with some additional options (rotate)<br/>
 * <br/>
 * Depends on: items (classification system)
 */
public interface CompareProductsComponent extends YComponent
{
	public List<ProductModel> getProductList();

	public void setProductList(List<ProductModel> products);

	public void setCompareAttributes(List attributes);

	public void setCompareAttributes(CategoryModel catg);

	public void setRotated(boolean rotated);

	public boolean getRotated(); // NOPMD

	public DataTableAxisModel getCompareTable();

	public YComponentEventHandler<CompareProductsComponent> getRotateTableEvent();
}
