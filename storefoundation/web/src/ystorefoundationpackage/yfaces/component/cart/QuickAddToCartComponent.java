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
package ystorefoundationpackage.yfaces.component.cart;

import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;


/**
 * The user can add the product to cart using its code with this component.
 */
public interface QuickAddToCartComponent extends YComponent
{
	String getProductCode();

	void setProductCode(String productCode);

	Integer getProductQuantity();

	void setProductQuantity(Integer productQuantity);

	YComponentEventHandler<QuickAddToCartComponent> getAddProductByCodeEvent();
}
