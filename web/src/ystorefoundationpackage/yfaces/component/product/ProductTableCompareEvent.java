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

import java.util.List;

import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.frame.CompareProductsFrame;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * This event gets fired when the user tries to compare the selected products.
 */
public class ProductTableCompareEvent extends
		DefaultYEventListener<ProductTableComponent> {
	@Override
	public String action() {
		return "compareProductsPage";
	}

	@Override
	public void actionListener(final YEvent<ProductTableComponent> event) {
		final ProductTableComponent pcmp = event.getComponent();
		final CompareProductsFrame sec = pcmp.getFrame().getPage()
				.getOrCreateFrame(CompareProductsFrame.class);
		final CompareProductsComponent cmp = sec.getCompareProductsComponent();

		final List<ProductModel> products = pcmp.getSelectedProducts();

		cmp.setProductList(products);
		cmp.setCompareAttributes(YStorefoundation.getRequestContext()
				.getSessionContext().getCategory());
	}
}
