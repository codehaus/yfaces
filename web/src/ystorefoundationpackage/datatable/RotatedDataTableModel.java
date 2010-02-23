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


/**
 * Wraps a {@link DataTableModel} and provides an inverted view of the wrapped table.<br/>
 * Inverted means rotated by 90 degree.
 * 
 * 
 */
public class RotatedDataTableModel implements DataTableModel
{
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	//the model which shall be inverted
	private DataTableModel boundedModel = null;

	/**
	 * Constructs a new {@link RotatedDataTableModel}.
	 * 
	 * @param boundedModel
	 *           The wrapped model which shall be rotated.
	 */
	public RotatedDataTableModel(final DataTableModel boundedModel)
	{
		this.boundedModel = boundedModel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getCellValue(int, int)
	 */
	public Object getValueAt(final int x, final int y)
	{
		return this.boundedModel.getValueAt(y, x);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return this.boundedModel.getRowCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return this.boundedModel.getColumnCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.boundedModel.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableModel#setCellValue(int, int, java.lang.Object)
	 */
	public boolean setValueAt(final int x, final int y, final Object value)
	{
		return this.boundedModel.setValueAt(y, x, value);
	}




}
