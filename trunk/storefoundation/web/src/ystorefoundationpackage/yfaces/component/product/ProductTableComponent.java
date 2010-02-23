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

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;



/**
 * This component lists the products in the table form.
 */
public interface ProductTableComponent extends YComponent
{
	public List<ProductModel> getProductList();

	public void setProductList(List<ProductModel> productList);

	public void setPriceColumnEnabled(boolean isEnabled);

	public boolean isPriceColumnEnabled();

	public boolean isClassificationColumnEnabled();

	public void setClassificationColumnEnabled(boolean enabled);

	public List<ClassAttributeAssignmentModel> getClassAttributeAssignments();

	public void setClassAttributeAssignments(List<ClassAttributeAssignmentModel> assignments);

	public void setClassificationColumnCount(int count);

	public int getClassificationColumnCount();

	public boolean isSelectColumnEnabled();

	public void setSelectColumnEnabled(boolean enabled);

	public int getVisibleRowCount();

	public void setVisibleRowCount(int rowCount);

	public List<ProductModel> getSelectedProducts();

	public String getSortColumn();

	public void setSortColumn(String name);

	public boolean getSortAscending(); // NOPMD

	public void setSortAscending(boolean asc);

	//transient members
	public DataTableAxisModel getTableModel();

	//	public void setTableModel(DataTableAxisModel tableModel);

	public YComponentEventHandler<ProductTableComponent> getCompareEvent();

	public YComponentEventHandler<ProductTableComponent> getSortEvent();

}
