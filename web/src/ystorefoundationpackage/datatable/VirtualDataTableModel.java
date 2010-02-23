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


//TODO: remove axis dependencies (AXIS_2D)

/**
 * A {@link VirtualDataTableModel} wraps one or more {@link DataTableModel} and represents them as one, single
 * {@link DataTableModel}.<br/>
 * <br/>
 * Basically a {@link VirtualDataTableModel} has as many columns as the sum of all wrapped {@link DataTableModel}
 * columns.<br/>
 * <br/>
 * However, there no limitations regarding order and amount of virtual columns. Each virtual column is associated with
 * an existing column of a wrapped model. But your're free to remove virtual columns, change the order or add two or
 * more virtual columns linked with the same sourcecolumn.
 * 
 * 
 */
public class VirtualDataTableModel implements DataTableModel
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(VirtualDataTableModel.class);

	private VirtualDataTableMarker[] sourceColumnMapping = null;
	private VirtualDataTableMarker[] userColumnMapping = null;

	private int columnCount = -1;
	private int rowCount = -1;

	/**
	 * Constructs a new {@link VirtualDataTableModel}.<br/>
	 * Uses the given List of {@link DataTableModel} for representing. one single Model. <br/>
	 * 
	 * @param bounded
	 *           List of Models which shall be bounded to one single model.
	 */
	public VirtualDataTableModel(final List<DataTableModel> bounded)
	{
		if (bounded == null)
		{
			throw new NullPointerException("Null not permitted as DataTableModel");
		}

		if (!bounded.isEmpty())
		{
			this.rowCount = bounded.iterator().next().getRowCount();
			final List<VirtualDataTableMarker> _tableModels = new ArrayList();

			this.columnCount = 0;
			//go through all models...
			for (final DataTableModel model : bounded)
			{
				if (model.getRowCount() != this.rowCount)
				{
					throw new DataTableException("Models aren't compatible in rowsize " + "(detected:" + this.rowCount + " but got:"
							+ model.getRowCount() + ")");
				}

				//...go through all available columns per model 
				for (int i = 0; i < model.getColumnCount(); i++)
				{
					final VirtualDataTableMarker _boundedModel = new VirtualDataTableMarker(model, AXIS_2D.X_AXIS, i);
					_tableModels.add(_boundedModel);
					this.columnCount++;
				}
			}

			//create a sourcemapping
			this.sourceColumnMapping = _tableModels.toArray(new VirtualDataTableMarker[_tableModels.size()]);
			//...and a usermapping
			this.userColumnMapping = _tableModels.toArray(new VirtualDataTableMarker[_tableModels.size()]);


			if (log.isDebugEnabled())
			{
				final StringBuilder msg = new StringBuilder("Mapping: ");
				for (int i = 0; i < sourceColumnMapping.length; i++)
				{
					msg.append(i + "->" + sourceColumnMapping[i].getDataTableModel() + "(" + sourceColumnMapping[i].getAccessIndex()
							+ ")");
				}
				log.debug(msg);
			}
		}
	}

	/**
	 * Sets the mapping for virtual columns.<br/>
	 * By default each virtual column represents a real one (with data and position).<br/>
	 * This method allows configuring a virtual column mask.<br/>
	 * <br/>
	 * The size of the array represents the number of virtual columns. Each value of the array represents the index of a
	 * non-virtual column.
	 * 
	 * @param columnMapping
	 *           new virtual columnmapping.
	 */
	public void setColumnMapping(final int[] columnMapping)
	{
		this.userColumnMapping = new VirtualDataTableMarker[columnMapping.length];

		for (int i = 0; i < columnMapping.length; i++)
		{
			final int sourceColumnIndex = columnMapping[i];
			if (sourceColumnIndex > this.sourceColumnMapping.length)
			{
				throw new DataTableException("Invalid Columnmapping; sourcecolumnindex " + sourceColumnIndex + " not available.",
						new ArrayIndexOutOfBoundsException());
			}
			this.userColumnMapping[i] = this.sourceColumnMapping[sourceColumnIndex];
		}

		this.columnCount = this.userColumnMapping.length;
	}

	/**
	 * Resets all virtual columns to the default. (One virtual column for one existing column).
	 */
	public void resetColumnMapping()
	{
		this.userColumnMapping = this.sourceColumnMapping;
	}

	/*
	 * (non-Javadoc) Take the correct DataModel from the List of the wrapped ones and delegate the call.
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getCellValue(int, int)
	 */
	public Object getValueAt(final int x, final int y)
	{
		//return this.userColumnMapping[x].getCellValue(y);
		return this.userColumnMapping[x].getCellAt(y).getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#setCellValue(int, int, java.lang.Object)
	 */
	public boolean setValueAt(final int x, final int y, final Object value)
	{
		//return this.userColumnMapping[x].setCellValue(y, value);
		this.userColumnMapping[x].getCellAt(y).setValue(value);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return this.columnCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return this.rowCount;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.rowCount != -1;
	}

}
