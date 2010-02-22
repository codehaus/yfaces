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


import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableModel;



/**
 * A default implementation for a {@link DataTableCell}.
 * 
 * 
 */
public class DefaultDataTableCell implements DataTableCell
{
	private static final Logger log = Logger.getLogger(DefaultDataTableCell.class);

	private int x = 0;
	private int y = 0;
	private DataTableModel model = null;

	private DataTableCellConverter cellConverter = null;

	/**
	 * Constructor.<br/>
	 * 
	 * @param model
	 *           accessed object.
	 * @param x
	 *           column
	 * @param y
	 *           row
	 */
	public DefaultDataTableCell(final DataTableModel model, final int x, final int y)
	{
		this(model, x, y, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param model
	 *           accessed object
	 * @param x
	 *           column
	 * @param y
	 *           row
	 * @param cellConverter
	 *           cellconverter to use
	 */
	public DefaultDataTableCell(final DataTableModel model, final int x, final int y, final DataTableCellConverter cellConverter)
	{
		this.model = model;
		this.x = x;
		this.y = y;
		this.cellConverter = cellConverter;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see webfoundation.model.datatable.DataTableCell#getSourceValue()
	 */
	public Object getSourceValue()
	{
		return model.getValueAt(x, y);
	}


	public boolean setSourceValue(final Object value)
	{
		return model.setValueAt(x, y, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see webfoundation.model.datatable.DataTableCell#getValue()
	 */
	public Object getValue()
	{
		if (log.isDebugEnabled())
		{
			log.debug("Request cellvalue; model:" + model.hashCode() + " col:" + x + "; row:" + y);
		}

		final Object cellValue = this.getSourceValue();
		return (this.cellConverter != null) ? this.cellConverter.getConvertedCellValue(cellValue) : cellValue;

	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see webfoundation.model.datatable.DataTableCell#setValue(java.lang.Object)
	 */
	public void setValue(final Object value)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Set cellvalue; model:" + model.hashCode() + " col:" + x + "; row:" + y);
		}

		model.setValueAt(x, y, value);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * webfoundation.model.datatable.DataTableCell#setCellConverter(webfoundation.model.datatable.DataTableCellConverter)
	 */
	public void setCellConverter(final DataTableCellConverter converter)
	{
		this.cellConverter = converter;
	}

	@Override
	public String toString()
	{
		return "cell:" + getValue();
	}





}
