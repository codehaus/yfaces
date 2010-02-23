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


public class CompositeCellConverter implements DataTableCellConverter
{
	private DataTableCellConverter[] converters = null;

	public CompositeCellConverter(final DataTableCellConverter... converters)
	{
		this.converters = converters;
	}

	public Object getConvertedCellValue(final Object sourceCellValue)
	{
		Object result = sourceCellValue;
		for (final DataTableCellConverter converter : this.converters)
		{
			if (converter != null)
			{
				result = converter.getConvertedCellValue(result);
			}
		}
		return result;
	}

}
