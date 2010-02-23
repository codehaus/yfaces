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


import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YComponentBinding;

import ystorefoundationpackage.yfaces.component.cms.DefaultTextParagraphsComponent;
import ystorefoundationpackage.yfaces.component.cms.TextParagraphsComponent;
import ystorefoundationpackage.yfaces.component.product.DefaultProductsQuickViewComponent;
import ystorefoundationpackage.yfaces.component.product.ProductsQuickViewComponent;


/**
 * Renders the default page of store foundation.
 * 
 */
public class WelcomeFrame extends AbstractYFrame
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(WelcomeFrame.class);

	private YComponentBinding<TextParagraphsComponent> textParagraphsCmp = null;
	private YComponentBinding<ProductsQuickViewComponent> productsQuickViewCmp = null;

	public WelcomeFrame()
	{
		super();
		this.productsQuickViewCmp = super.createComponentBinding(this.createRandomProductsComponent());
		this.textParagraphsCmp = super.createComponentBinding(this.createTextParagraphsComponent());
	}

	public YComponentBinding<TextParagraphsComponent> getTextParagraphsComponent()
	{
		return this.textParagraphsCmp;
	}

	public YComponentBinding<ProductsQuickViewComponent> getRandomProductsComponent()
	{
		return this.productsQuickViewCmp;
	}

	private TextParagraphsComponent createTextParagraphsComponent()
	{
		final TextParagraphsComponent result = new DefaultTextParagraphsComponent();
		result.setTextParagraphPageId("frontpage");
		return result;
	}

	/**
	 * Creates a {@link ProductsQuickViewComponent} for this Frame.
	 * 
	 * @return {@link ProductsQuickViewComponent}
	 */
	private ProductsQuickViewComponent createRandomProductsComponent()
	{
		final ProductsQuickViewComponent result = new DefaultProductsQuickViewComponent();
		result.setCategoryCode("topseller");
		return result;
	}

}
