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

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.yfaces.component.YComponent;



/**
 * This component makes it possible for the user to view the cart briefly.
 */
public interface QuickViewCartComponent extends YComponent
{

	/**
	 * Returns the cart which shall be used.
	 * 
	 * @return {@link CartModel}
	 */
	CartModel getCart();

	/**
	 * Sets the Cart which shall be used.
	 * 
	 * @param cart
	 *           {@link CartModel} to set
	 */
	void setCart(CartModel cart);


	/**
	 * Returns the localization key which shall be used.<br/>
	 * Localized String gets passed one parameter for the carts total.
	 * 
	 * @return localization key
	 */
	String getLocalizationKey();

	/**
	 * Sets the localization key which shall be used.
	 * 
	 * @param key
	 *           localization key.
	 */
	void setLocalizationKey(String key);



	/**
	 * Calculates and returns the carts total.
	 * 
	 * @return formated String for carts total
	 */
	String getTotal();
}
