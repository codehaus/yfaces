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
 * Uses a {@link CollectionDataTableModel} for a Row perspective.
 * 
 * 
 */
public class RowCollectionDataTableModel extends CollectionDataTableModel
{

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(RowCollectionDataTableModel.class);


	/**
	 * Constructor.
	 */
	public RowCollectionDataTableModel()
	{
		this(new ArrayList());
	}

	/**
	 * Constructor.<br/>
	 * Can be passed a list of data(one row) or a list of lists(multiple rows)<br/>
	 * 
	 * @param initialData
	 */
	public RowCollectionDataTableModel(final List initialData)
	{
		super(AXIS_2D.Y_AXIS);
		super.setData(initialData);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#getCellValue(int, int)
	 */
	public Object getValueAt(final int x, final int y)
	{
		return super.getData().get(y).get(x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		final int result = super.getData().isEmpty() ? 0 : super.getData().get(0).size();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return super.getData().size();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.AbstractDataTableModel#setCellValue(int, int,
	 * java.lang.Object)
	 */
	public boolean setValueAt(final int x, final int y, final Object value)
	{
		super.getData().get(y).set(x, value);
		return true;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.tables.CollectionDataTableModel#addRow(java.util.List)
	 */
	@Override
	public void addRow(final List row)
	{
		if (super.getData() == null || super.getData().isEmpty())
		{
			super.setData(row);
		}
		else
		{
			log.debug("Add row (" + (getRowCount() + 1) + ")");
			if (row.size() != this.getColumnCount())
			{
				throw new DataTableException("Rows don't equals in columncount (" + row.size() + " vs. " + this.getColumnCount()
						+ ")");
			}
			super.getData().add(row);
		}
	}

}
