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

import org.codehaus.yfaces.component.AbstractYFrame;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.yfaces.component.product.CompareProductsComponent;

/**
 * Renders the classification information of the selected products.
 * 
 */
public class CompareProductsFrame extends AbstractYFrame {
	private CompareProductsComponent compareProductsComponent = null;

	public CompareProductsFrame() {
		super();
	}

	public CompareProductsComponent getCompareProductsComponent() {
		if (this.compareProductsComponent == null) {
			this.compareProductsComponent = super
					.createDefaultYModel(YComponent.COMPARE_PRODUCTS.viewId);
		}
		return compareProductsComponent;
	}

	public void setCompareProductsComponent(CompareProductsComponent cmp) {
		this.compareProductsComponent = cmp;
	}

}
