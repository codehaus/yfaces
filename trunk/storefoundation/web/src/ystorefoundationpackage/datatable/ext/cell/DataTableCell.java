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
 * Represents a Cell within a DataTable.<br>
 * A cellvalue is the result of a sourcevalue combined with a cellconverter.<br/>
 * 
 * 
 */
public interface DataTableCell
{
	/**
	 * A cellvalue is the converted sourcevalue or the sourcevalue itself when no converter was set.
	 * 
	 * @return converted cellvalue.
	 */
	public Object getValue();

	/**
	 * Sets the value for this cell.
	 * 
	 * @param value
	 */
	public void setValue(Object value);

	/**
	 * Returns the plain, unconverted value of this cell.
	 * 
	 * @return unconverted cellvalue.
	 */
	public Object getSourceValue();

	public boolean setSourceValue(Object value);

	/**
	 * Sets a converter for this cell.
	 * 
	 * @param converter
	 *           Cellconverter, null permitted.
	 */
	public void setCellConverter(DataTableCellConverter converter);

}
