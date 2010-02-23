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
package ystorefoundationpackage.datatable;

import java.io.Serializable;



/**
 * A model which holds (2-dimensional) tabular data. A more powerful tableview is provided by the
 * {@link ystorefoundationpackage.datatable.ext.DataTableAxisModel}
 */
public interface DataTableModel extends Serializable
{

	/**
	 * Returns the number of columns.<br/>
	 * 
	 * @return number of columns.
	 */
	public int getColumnCount();


	/**
	 * Return the number of rows.<br/>
	 * 
	 * @return number of rows.
	 */
	public int getRowCount();


	/**
	 * The table is empty, when there're no cells available.<br/>
	 * This is the case when {@link #getColumnCount()} and {@link #getRowCount()} return 0. <br/>
	 * 
	 * @return true when this model has no data.
	 */
	public boolean isEmpty();


	/**
	 * Returns the cellvalue at the given position.<br/>
	 * 
	 * @param x
	 *           the column (x-index)
	 * @param y
	 *           the row (y-index)
	 * @return The cellvalue
	 */
	public Object getValueAt(int x, int y);

	/**
	 * Sets the cellvalue at the given position.<br/>
	 * Note that this may not be supported by all TableModels.
	 * 
	 * @param x
	 *           the column (x-index)
	 * @param y
	 *           the row (y-index)
	 * @param value
	 *           the cellvalue to set
	 * @return true when this operation was successfully
	 */
	public boolean setValueAt(int x, int y, Object value);


}
