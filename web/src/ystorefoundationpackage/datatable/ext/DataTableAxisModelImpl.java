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
package ystorefoundationpackage.datatable.ext;


import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableException;
import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;


public class DataTableAxisModelImpl implements DataTableAxisModel
{

	private static final Logger log = Logger.getLogger(DataTableAxisModelImpl.class);

	private DataTableAxis xAxis = null;
	private DataTableAxis yAxis = null;
	private DataTableModel boundedModel = null;

	private String id = null;

	//
	// Implements DataTableAxisModel
	//

	public DataTableAxisModelImpl(final DataTableModel boundedModel)
	{
		this(boundedModel, null);
	}

	public DataTableAxisModelImpl(final DataTableModel boundedModel, final String id)
	{
		this.id = (id != null) ? id : "";
		this.boundedModel = boundedModel;
	}

	public String getId()
	{
		return this.id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public DataTableModel getDataTableModel()
	{
		return this.boundedModel;
	}


	public DataTableAxis getAxis(final AXIS_2D axisType)
	{
		final DataTableAxis result = (axisType == AXIS_2D.X_AXIS) ? this.xAxis : this.yAxis;
		if (result == null)
		{
			throw new DataTableException("No axis defined as " + axisType, new NullPointerException("axis"));
		}
		return result;
	}

	public void setAxis(final DataTableAxis axis)
	{
		if (axis == null)
		{
			throw new DataTableException("No axis specified", new NullPointerException());
		}
		if (log.isDebugEnabled())
		{
			log.debug("Set axis (id:" + axis.getId() + "; type" + axis.getType() + "; class:" + axis.getClass().getSimpleName()
					+ "); axismodel (id:" + this.getId() + ")");
		}

		switch (axis.getType())
		{
			case X_AXIS:
				this.xAxis = axis;
				break;
			case Y_AXIS:
				this.yAxis = axis;
				break;
			default:
				throw new DataTableException("Unexpected error");
		}
	}

	public DataTableAxisMarker getMarkerById(final AXIS_2D axisType, final String markerId)
	{
		return getAxis(axisType).getMarkerById(markerId);
	}

	public DataTableAxisMarker getTitleMarker(final AXIS_2D axisType)
	{
		return getAxis(axisType).getTitleMarker();
	}

	public List<DataTableAxisMarker> getValueMarkers(final AXIS_2D axisType)
	{
		return getAxis(axisType).getMarkerList();
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.DataTableAxisModel#getXAxis()
	 */
	public DataTableAxis getXAxis()
	{
		return getAxis(AXIS_2D.X_AXIS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.DataTableAxisModel#getYAxis()
	 */
	public DataTableAxis getYAxis()
	{
		return getAxis(AXIS_2D.Y_AXIS);
	}

	public List<DataTableAxisMarker> getColumns()
	{
		return getXAxis().getMarkerList();
	}

	public List<DataTableAxisMarker> getRows()
	{
		return getYAxis().getMarkerList();
	}


	public DataTableAxisMarker getColumnById(final String id)
	{
		return getXAxis().getMarkerById(id);
	}

	public DataTableAxisMarker getRowById(final String id)
	{
		return getYAxis().getMarkerById(id);
	}

	public DataTableAxisMarker getTitleColumn()
	{
		return getXAxis().getTitleMarker();
	}

	public DataTableAxisMarker getTitleRow()
	{
		return getYAxis().getTitleMarker();
	}



	public Object getSourceValueAt(final int x, final int y)
	{
		return this.getColumns().get(x).getCellAt(y).getSourceValue();
	}

	public boolean setSourceValueAt(final int x, final int y, final Object sourceValue)
	{
		return this.getColumns().get(x).getCellAt(y).setSourceValue(sourceValue);
	}




	//
	// Implements DataTableModel
	//


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#getColumnCount()
	 */
	public int getColumnCount()
	{
		return this.getColumns().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#getRowCount()
	 */
	public int getRowCount()
	{
		return this.getRows().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#getValueAt(int, int)
	 */
	public Object getValueAt(final int x, final int y)
	{
		return this.getColumns().get(x).getCellAt(y).getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#isEmpty()
	 */
	public boolean isEmpty()
	{
		return this.getDataTableModel().isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableModel#setValueAt(int, int, java.lang.Object)
	 */
	public boolean setValueAt(final int x, final int y, final Object value)
	{
		this.getColumns().get(x).getCellAt(y).setValue(value);
		return true;
	}

}
