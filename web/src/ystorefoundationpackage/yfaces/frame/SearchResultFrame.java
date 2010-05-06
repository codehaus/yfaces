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

import org.codehaus.yfaces.component.AbstractYComponentContainer;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.yfaces.component.product.ProductTableComponent;

/**
 * Renders the search results.
 * 
 */
public class SearchResultFrame extends AbstractYComponentContainer {

	private static final long serialVersionUID = 57824365989445L;

	private ProductTableComponent productTableCmp = null;

	public SearchResultFrame() {
		super();
	}

	/**
	 * @return {@link ProductTableComponent}
	 */
	public ProductTableComponent getProductTableComponent() {
		if (this.productTableCmp == null) {
			this.productTableCmp = super
					.createDefaultYModel(YComponent.PRODUCT_TABLE.viewId);
		}
		return this.productTableCmp;
	}

	public void setProductTableComponent(ProductTableComponent cmp) {
		this.productTableCmp = cmp;
	}

}
