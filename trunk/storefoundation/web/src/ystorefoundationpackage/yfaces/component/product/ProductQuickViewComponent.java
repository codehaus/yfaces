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

import org.codehaus.yfaces.component.YComponent;

import de.hybris.platform.core.model.product.ProductModel;


/**
 * This component displays one product briefly.
 */
public interface ProductQuickViewComponent extends YComponent
{

	/**
	 * Sets the product which shall be displayed.
	 * 
	 * @param product
	 *           product to set.
	 */
	public void setProduct(ProductModel product);

	/**
	 * Returns the product which shall be displayed.
	 * 
	 * @return Product to display
	 */
	public ProductModel getProduct();


	/**
	 * Returns the product code. This is simply the passed input parameter. May be null or the code of a non existing
	 * product.
	 * 
	 * @return product code.
	 */
	public String getProductCode();

	/**
	 * Sets the requested product by its code.
	 * 
	 * @param code
	 *           product code
	 */
	public void setProductCode(String code);

	/**
	 * Returns the output format which is used when no product is available.
	 * 
	 * @see #setNothingFoundMsgFormat(String)
	 * @return output format
	 */
	public String getNothingFoundMsgFormat();

	/**
	 * Sets the outputformat which is displayed when no product is available.<br/>
	 * Supported placeholders: {0} = productcode.<br/>
	 * 
	 * @param message
	 *           output format
	 */
	public void setNothingFoundMsgFormat(String message);


	public ProductModel getBestProduct();

}
