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
package ystorefoundationpackage.yfaces.frame;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.yfaces.component.product.ProductTableComponent;


/**
 * Renders the search results.
 * 
 */
public class SearchResultFrame extends AbstractYFrame
{

	private static final long serialVersionUID = 57824365989445L;

	private YComponentBinding<ProductTableComponent> productTableCmp = null;

	public SearchResultFrame()
	{
		super();
		this.productTableCmp = super.createComponentBinding(YComponent.PRODUCT_TABLE.viewId);
	}

	/**
	 * @return {@link YComponentBinding} for {@link ProductTableComponent}
	 */
	public YComponentBinding<ProductTableComponent> getProductTableComponent()
	{
		return this.productTableCmp;
	}

}
