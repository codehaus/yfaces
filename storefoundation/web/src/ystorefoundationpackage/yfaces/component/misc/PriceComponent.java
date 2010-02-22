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
package ystorefoundationpackage.yfaces.component.misc;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.YComponent;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.domain.Price;


/**
 * This component displays a price.
 */
public interface PriceComponent extends YComponent
{

	/**
	 * Sets the products whose price shall be displayed. Null permitted. This method overrules
	 * {@link #setProductCode(String)}.
	 * 
	 * @param product
	 *           product to set.
	 */
	void setProduct(ProductModel product);

	/**
	 * Sets the product whose price shall be displayed implicitly by the products code. {@link #setProduct(ProductModel)}
	 * overrules this method.
	 * 
	 * @param code
	 *           product code to use
	 */
	void setProductCode(String code);


	/**
	 * Returns true when an existing old price (optional) for the product shall be displayed. Result doesn't say anything
	 * about the existence of an products old price.
	 * 
	 * @return true when enabled.
	 */
	boolean isShowOldPrice();

	/**
	 * Sets whether an optional existing old price shall be displayed.
	 * 
	 * @param enabled
	 *           true when enabled.
	 */
	void setShowOldPrice(boolean enabled);

	/**
	 * Returns the output format for an the old price.<br/>
	 * See {@link #setOldPriceMsgFormat(String)} for more information. <br/>
	 * 
	 * @return outputformat String
	 */
	String getOldPriceMsgFormat();

	/**
	 * Sets the output format for an optional available old price.<br/>
	 * Supported placeholders: {0} = old price (already localized) When no placeholder is given it's position is assumed
	 * at the end of the String.<br/>
	 * <br/>
	 * examples:<br/>
	 * outputformat = "price {0} is not available anymore"<br/>
	 * outputformat = "old price: {0}"<br/>
	 * outputformat = "old price:"<br/>
	 * 
	 * @param format
	 *           String as outputformat
	 */
	void setOldPriceMsgFormat(String format);

	/**
	 * Returns true when taxes shall be displayed.<br/>
	 * The result doesn't say anything about the availability of taxes.
	 * 
	 * @return true when taxes are displayed
	 */
	boolean isShowTaxes();

	/**
	 * Set to true when taxes shall be displayed.<br/>
	 * 
	 * @param enabled
	 *           true when display taxes
	 */
	void setShowTaxes(boolean enabled);

	/**
	 * Returns true when a table with all available Prices shall be displayed.<br/>
	 * The result doesn't say anything about the availability of multiple Prices.<br/>
	 * <br/>
	 * 
	 * @return true when enabled
	 */
	boolean isShowPriceTable();

	/**
	 * Set to true when a table with allavailable Prices shall be displayed.<br/>
	 * <br/>
	 * 
	 * @param enabled
	 *           true when enabled
	 */
	void setShowPriceTable(boolean enabled);

	/**
	 * Returns the outputformat for the price.<br/>
	 * See {@link #setOldPriceMsgFormat(String)} to learn more about the format String.
	 * 
	 * @return String as outputformat
	 */
	String getPriceMsgFormat();

	/**
	 * Sets the outputformat for the price.<br/>
	 * See {@link #setOldPriceMsgFormat(String)} to learn more about the format String.<br/>
	 * <br/>
	 * 
	 * @param outputformat
	 *           String as outputformat
	 */
	void setPriceMsgFormat(String outputformat);

	/**
	 * Returns the price which shall primarily used for display.<br/>
	 * If the product has only one {@link Price} that one will be returned.<br/>
	 * If the product has multiple {@link Price} a calculated one (generally the cheapes one) will be returned.<br/>
	 * <br/>
	 * 
	 * @return {@link Price}
	 */
	Price getDefaultPrice();

	/**
	 * Returns a table representation for all available prices.<br/>
	 * Returns null when amount of available prices is zero or one.<br/>
	 * 
	 * @return {@link DataTableAxisModel}
	 */
	DataTableAxisModel getPriceTable();
}
