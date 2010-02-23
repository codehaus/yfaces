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


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;



/**
 * Manages one ore more collections as {@link DataTableModel}.<br/>
 * <br/>
 * While constructing this class the caller must decide whether the collections shall be interpreted as collections of
 * rows or as collections of columns.<br/>
 * <br/>
 * This decision also involves the possibility of adding more columns or rows later.<br/>
 * For instance when this object operates in columnmode, no rows can be added later.<br/>
 * <br/>
 * 
 * @see ColumnCollectionDataTableModel
 * @see RowCollectionDataTableModel
 * @see RotatedDataTableModel
 * 
 * 
 */
public abstract class CollectionDataTableModel implements DataTableModel //extends AbstractDataTableModel
{
	private static final Logger log = Logger.getLogger(CollectionDataTableModel.class);

	public static CollectionDataTableModel createInstance(final AXIS_2D direction, final List<List<Object>> initialData)
	{
		final CollectionDataTableModel result = (direction == AXIS_2D.X_AXIS) ? new ColumnCollectionDataTableModel(initialData)
				: new RowCollectionDataTableModel(initialData);

		return result;
	}

	private AXIS_2D direction = null;

	//either list of columns which holds rows
	//or list of rows which holds columns
	private List<List<Object>> dataList = null;


	/**
	 * Protected Constructor.
	 * 
	 * @param direction
	 *           the direction how the collections shall be interpreted
	 */
	protected CollectionDataTableModel(final AXIS_2D direction)
	{
		this.direction = direction;
	}

	/**
	 * Adds more data.<br/>
	 * According the direction of this model this method delegates either to {@link #addColumn(List)} or
	 * {@link #addRow(List)}.<br/>
	 * 
	 * @param data
	 *           Data to add.
	 */
	public void addData(final List data)
	{
		if (log.isDebugEnabled())
		{
			log.debug(((this.isEmpty()) ? "Initialize TableModel; " : "") + "Adding a " + this.direction.name());
		}

		//guarantee at least an empty instance
		if (this.isEmpty())
		{
			this.dataList = new ArrayList();
		}

		this.dataList.add(data);
	}

	public void setData(final List data)
	{

		if (data == null || data.isEmpty())
		{
			log.debug("Reset TableModel (clear all)");
			this.dataList = data;
		}
		else
		{
			if (data.get(0) instanceof List)
			{
				log.debug("Set initial data (compatible [List<List>])");
				this.dataList = data;
			}
			else
			{
				log.debug("Set initial data (converting [List -> List<List>])");
				this.dataList = new ArrayList<List<Object>>();
				this.dataList.add(data);
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#isEmpty()
	 */
	public boolean isEmpty()
	{
		return (this.dataList == null || this.dataList.isEmpty());
	}

	/**
	 * Adds a column.<br/>
	 * Throws a {@link DataTableException} when a column can't be added.
	 * 
	 * @param column
	 *           Column to add.
	 */
	public void addColumn(final List column)
	{
		throw new DataTableException("TableModel is based on rows; can't add a column", new UnsupportedOperationException());
	}

	/**
	 * Adds a row.<br/>
	 * Throws a {@link DataTableException} when a row can't be added.
	 * 
	 * @param row
	 *           Row to add.
	 */
	public void addRow(final List row)
	{
		throw new DataTableException("TableModel is based on columns; can't add a row", new UnsupportedOperationException());
	}


	public List<List<Object>> getData()
	{
		return this.dataList;
	}


}
