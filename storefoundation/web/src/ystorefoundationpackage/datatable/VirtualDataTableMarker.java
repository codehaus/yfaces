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


import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;
import ystorefoundationpackage.datatable.ext.cell.DataTableCell;
import ystorefoundationpackage.datatable.ext.cell.DefaultDataTableCell;


/**
 * A default implementation of a {@link ystorefoundationpackage.datatable.ext.axes.DataTableAxis}.<br/>
 * <br/>
 * 
 * 
 */
class VirtualDataTableMarker
{
	private int accessIndex = -1;
	private DataTableModel model = null;
	private AXIS_2D axisType = null;

	/**
	 * Constructs a new {@link ystorefoundationpackage.datatable.ext.axes.DataTableAxis}.<br/>
	 * Please look at the {@link ystorefoundationpackage.datatable.ext.axes.DataTableAxis} to learn what each parameter
	 * means.
	 * 
	 * @param model
	 *           The {@link DataTableModel} which this axis is connected to.
	 * @param axisType
	 *           The type of this axis (column or row)
	 * @param accessIndex
	 *           The accessindex for the bounded model.
	 */
	public VirtualDataTableMarker(final DataTableModel model, final AXIS_2D axisType, final int accessIndex)
	{
		//TODO: check for valid accessindex; throw exception otherwise
		this.model = model;
		this.accessIndex = accessIndex;
		this.axisType = axisType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableAxis#getAccessIndex()
	 */
	public int getAccessIndex()
	{
		return this.accessIndex;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableAxis#getDataTableModel()
	 */
	public DataTableModel getDataTableModel()
	{
		return this.model;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableAxis#getCell(int)
	 */
	public DataTableCell getCellAt(final int otherIndex)
	{
		final DataTableCell result = (this.axisType == AXIS_2D.X_AXIS) ? new DefaultDataTableCell(this.model, this.accessIndex,
				otherIndex) : new DefaultDataTableCell(this.model, otherIndex, this.accessIndex);

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "(model:" + model.hashCode() + "; accessindex:" + accessIndex + "; axis:"
				+ this.axisType.name() + ")";
	}

}
