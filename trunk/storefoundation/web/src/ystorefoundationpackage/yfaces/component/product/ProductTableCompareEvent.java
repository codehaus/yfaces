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
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;

import java.util.List;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.frame.CompareProductsFrame;


/**
 * This event gets fired when the user tries to compare the selected products.
 */
public class ProductTableCompareEvent extends DefaultYComponentEventListener<ProductTableComponent>
{
	@Override
	public String action()
	{
		return "compareProductsPage";
	}

	@Override
	public void actionListener(final YComponentEvent<ProductTableComponent> event)
	{
		final ProductTableComponent pcmp = event.getComponent();
		final CompareProductsFrame sec = pcmp.getFrame().getPage().getOrCreateFrame(CompareProductsFrame.class);
		final CompareProductsComponent cmp = sec.getCompareProductsComponent().getValue();

		final List<ProductModel> products = pcmp.getSelectedProducts();

		cmp.setProductList(products);
		cmp.setCompareAttributes(YStorefoundation.getRequestContext().getSessionContext().getCategory());
	}
}
