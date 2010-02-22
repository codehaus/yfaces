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
import ystorefoundationpackage.yfaces.component.product.CompareProductsComponent;


/**
 * Renders the classification information of the selected products.
 * 
 */
public class CompareProductsFrame extends AbstractYFrame
{
	private YComponentBinding<CompareProductsComponent> compareProductsCmp = null;

	public CompareProductsFrame()
	{
		super();
		this.compareProductsCmp = super.createComponentBinding(YComponent.COMPARE_PRODUCTS.viewId);
	}

	public YComponentBinding<CompareProductsComponent> getCompareProductsComponent()
	{
		return this.compareProductsCmp;
	}

}
