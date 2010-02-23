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

import java.util.Collection;
import java.util.List;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;



/**
 * This component displays the similar products of the selected product.
 */
public interface ProductReferencesComponent extends YModel
{
	public static final int TYPE_CROSSELLS = 1;
	public static final int TYPE_UPSELLS = 2;

	/**
	 * Helper class which contains the similar products of the selected product.
	 */
	public interface ProductReferenceGroup
	{
		public Object getGroupId();

		public void setProductList(List<ProductModel> products);

		public List<ProductModel> getProductList();

		public boolean isSelectionEnabled();

		public void setSelectionEnabled(boolean enabled);

		public String getHeadline();

		public void setHeadline(String headline);

		public List<ProductModel> getSelectedProducts();

		public DataTableAxisModel getTable();
	}

	public void setProduct(ProductModel product);

	public ProductModel getProduct();

	public String getHeadline();

	public void setHeadline(String headline);

	public int getLayout();

	public void setLayout(int layout);

	public ProductReferenceGroup getOrCreateProductReferenceGroup(Object groupId);

	public Collection<ProductReferenceGroup> getProductReferenceGroups();

	public YEventHandler<ProductReferencesComponent> getAddSelectionToCartEvent();
}
