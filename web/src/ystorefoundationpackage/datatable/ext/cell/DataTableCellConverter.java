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
package ystorefoundationpackage.datatable.ext.cell;

/**
 * Converts a Cellvalue into a custom one.
 * 
 * 
 * 
 * @param <V>
 *           the type of the converted value
 */
public interface DataTableCellConverter<V>
{
	/**
	 * Converts a Value into another one.
	 * 
	 * @param sourceCellValue
	 *           the original cellvalue
	 * @return the converted value
	 */
	public V getConvertedCellValue(Object sourceCellValue);

}
