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
package ystorefoundationpackage;

/**
 * Enumeration of {@link org.codehaus.yfaces.component.YModel} view id's. Useful in combination with
 * {@link org.codehaus.yfaces.component.YFrame#createComponentBinding(String)}
 */
public enum YComponent
{
	COMPARE_PRODUCTS("compareProductsCmp"), //
	PRODUCT_TABLE("productTableCmp"), //
	EDIT_ADDRESS("editAddressCmp"), //
	SHOW_ADDRESS("showAddressCmp"), //
	LIST_ADDRESS("listAddressCmp"), //
	SHOW_PAYMENT("showPaymentCmp");


	public final String viewId;

	private YComponent(final String viewId)
	{
		this.viewId = viewId;
	}

}
