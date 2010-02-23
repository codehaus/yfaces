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


import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;


/**
 * Uses a {@link CollectionDataTableModel} for a Column perspective.
 * 
 * 
 * 
 */
public class ColumnCollectionDataTableModel extends CollectionDataTableModel
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Logger */
	private static final Logger log = Logger.getLogger(ColumnCollectionDataTableModel.class);

	/**
	 * Constructor.
	 */
	public ColumnCollectionDataTableModel()
	{
		this(null);
	}

	/**
	 * Constructor.<br/>
	 * Can be passed a list of data(one column) or a list of lists(multiple columns)<br/>
	 * 
	 * @param initialData
	 *           Initial columns
	 */
	public ColumnCollectionDataTableModel(final List initialData)
	{
		super(AXIS_2D.X_AXIS);
		super.setData(initialData);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#getCellValue(int, int)
	 */
	public Object getValueAt(final int x, final int y)
	{
		return super.getData().get(x).get(y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return super.getData().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#getRowCount()
	 */
	public int getRowCount()
	{
		final int result = super.getData().isEmpty() ? 0 : super.getData().get(0).size();
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#setCellValue(int, int,
	 * java.lang.Object)
	 */
	public boolean setValueAt(final int x, final int y, final Object value)
	{
		super.getData().get(x).set(y, value);
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.tables.CollectionDataTableModel#addColumn(java.util.List)
	 */
	@Override
	public void addColumn(final List column)
	{
		if (super.getData() == null || super.getData().isEmpty())
		{
			super.setData(column);
		}
		else
		{
			if (column.size() != this.getRowCount())
			{
				throw new DataTableException("Columns doesn't equals in rowcount (" + column.size() + " vs. " + getRowCount() + ")");
			}
			log.debug("Add column (" + (getColumnCount() + 1) + ")");
			super.getData().add(column);
		}
	}

}
