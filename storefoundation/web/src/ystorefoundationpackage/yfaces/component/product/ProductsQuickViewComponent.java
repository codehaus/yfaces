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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.YComponent;

import java.util.List;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;


/**
 * This component displays several products briefly. All products can be placed in the table form.
 */
public interface ProductsQuickViewComponent extends YComponent
{

	public void setTitle(String title);

	public String getTitle();

	public void setCategoryCode(String catg);

	public void setCategory(CategoryModel catg);

	public void setCatalogCode(String catalog);

	public void setCatalog(CatalogModel catalog);

	public void setProductCount(int count);

	public int getProductCount();

	public List<ProductModel> getProductList();

	public void setProductList(List<ProductModel> products);

	public DataTableAxisModel getProductTable();

	public int getRows();

	public void setRows(int rows);

	public int getColumns();

	public void setColumns(int columns);

}
