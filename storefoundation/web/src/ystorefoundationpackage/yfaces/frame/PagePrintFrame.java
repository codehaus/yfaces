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
import org.codehaus.yfaces.component.YComponentBinding;

import ystorefoundationpackage.yfaces.component.product.DefaultProductDetailComponent;
import ystorefoundationpackage.yfaces.component.product.ProductDetailComponent;


/**
 * Renders the concrete description of the selected product which can be printed.
 * 
 */
public class PagePrintFrame extends AbstractYFrame
{

	private static final long serialVersionUID = -8774098273550197135L;

	private YComponentBinding<ProductDetailComponent> productDetailCmp = null;

	public PagePrintFrame()
	{
		super();
		this.productDetailCmp = super.createComponentBinding(this.createProductDetailComponentForPagePrint());
	}

	public YComponentBinding<ProductDetailComponent> getProductDetailComponentForPagePrint()
	{
		return this.productDetailCmp;
	}

	private ProductDetailComponent createProductDetailComponentForPagePrint()
	{
		final ProductDetailComponent cmp = new DefaultProductDetailComponent();
		cmp.setPrintPage(true);
		return cmp;
	}

}
