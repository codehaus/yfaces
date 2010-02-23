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
package ystorefoundationpackage.datatable.ext.axes;


import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;
import ystorefoundationpackage.datatable.ext.cell.CompositeCellConverter;
import ystorefoundationpackage.datatable.ext.cell.DataTableCell;
import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;
import ystorefoundationpackage.datatable.ext.cell.DefaultDataTableCell;


/**
 * A default implementation of a {@link DataTableAxis}.<br/>
 * <br/>
 * 
 * 
 */
public class DataTableAxisMarkerImpl implements DataTableAxisMarker
{
	private static final Logger log = Logger.getLogger(DataTableAxisMarkerImpl.class);

	private String id = null;
	private Object title = null;

	//model, axis and accessindex; these values define how to find the cellvalue
	private DataTableAxisModel axisModel = null;
	private DataTableAxis axis = null;
	private int accessIndex = -1;

	private int position = -1;

	//cellconverter affects each value which is addressed by this marker
	private DataTableCellConverter<?> cellConverter = null;

	private boolean visible = true;


	/**
	 * Constructs a new {@link DataTableAxisMarker}
	 * 
	 * @param model
	 *           The used {@link DataTableModel}
	 * @param axis
	 *           The axis which this marker shall belong to
	 * @param accessIndex
	 *           the source-accessindex at the bounded {@link DataTableModel}
	 */
	public DataTableAxisMarkerImpl(final DataTableAxisModel model, final DataTableAxis axis, final int accessIndex)
	{
		this(null, model, axis, accessIndex);
	}

	/**
	 * Constructs a new {@link DataTableAxisMarker}
	 * 
	 * @param id
	 *           The id of this model.
	 * @param model
	 *           The used {@link DataTableModel}
	 * @param axis
	 *           The axis which this marker shall belong to
	 * @param accessIndex
	 *           the source-accessindex at the bounded {@link DataTableModel}
	 */
	public DataTableAxisMarkerImpl(final String id, final DataTableAxisModel model, final DataTableAxis axis, final int accessIndex)
	{
		//TODO: check for valid accessindex; throw exception otherwise
		this.id = id;
		this.axisModel = model;
		this.accessIndex = accessIndex;
		this.axis = axis;
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
	 * @see hybris.faces.model.datatable.DataTableAxis#getAxisType()
	 */
	public DataTableAxis getAxis()
	{
		return this.axis;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableAxis#getDataTableModel()
	 */
	public DataTableAxisModel getDataTableAxisModel()
	{
		return this.axisModel;
	}

	/*
	 * (non-Javadoc) By opposite marker identified by ID.
	 * 
	 * @see datatable.ext.axes.DataTableAxisMarker#getCellAt(java.lang.String)
	 */
	public DataTableCell getCellAt(final String otherMarkerId)
	{
		final DataTableAxisMarker otherMarker = this.axisModel.getMarkerById(this.getAxis().getType().invert(), otherMarkerId);
		return getCellAt(otherMarker);
	}


	/*
	 * (non-Javadoc) By opposite Marker.
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.model.datatable.DataTableAxis#getCell(de.hybris.platform.webfoundation.model.
	 * datatable.DataTableAxis)
	 */
	public DataTableCell getCellAt(final DataTableAxisMarker oppositeMarker)
	{
		final DataTableCellConverter<?> compositeConverter = new CompositeCellConverter(this.cellConverter, oppositeMarker
				.getCellConverter());

		final DataTableModel sourceModel = this.axisModel.getDataTableModel();
		final int oppositeAccessIndex = oppositeMarker.getAccessIndex();

		final DataTableCell result = (this.axis.getType() == AXIS_2D.X_AXIS) ? new DefaultDataTableCell(sourceModel,
				this.accessIndex, oppositeAccessIndex, compositeConverter) : new DefaultDataTableCell(sourceModel,
				oppositeAccessIndex, this.accessIndex, compositeConverter);

		return result;

	}

	/*
	 * (non-Javadoc) By opposite Marker identified by index.
	 * 
	 * @see de.hybris.platform.webfoundation.model.datatable.DataTableAxis#getCell(int)
	 */
	public DataTableCell getCellAt(final int oppositeMarkerIndex)
	{
		final DataTableCell result = (this.axis.getType() == AXIS_2D.X_AXIS) ? this.getCellAt(this.axisModel.getYAxis()
				.getMarkerAt(oppositeMarkerIndex)) : this.getCellAt(this.axisModel.getXAxis().getMarkerAt(oppositeMarkerIndex));

		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableAxis#getId()
	 */
	public String getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableAxis#setId(java.lang.String)
	 */
	public void setId(final String id)
	{
		final String oldId = this.id;
		this.id = id;
		((DataTableAxisImpl) this.axis).updateMarkerIdMap(oldId, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#isVisible()
	 */
	public boolean isVisible()
	{
		return this.visible;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#setVisible(boolean)
	 */
	public void setVisible(final boolean isVisible)
	{
		this.visible = isVisible;
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker#getCellConverter()
	 */
	public DataTableCellConverter<?> getCellConverter()
	{
		return this.cellConverter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker#setCellConverter(de.hybris.platform.
	 * webfoundation.datatable.ext.cell.DataTableCellConverter)
	 */
	public void setCellConverter(final DataTableCellConverter<?> cellConverter)
	{
		this.cellConverter = cellConverter;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker#getTitle()
	 */
	public Object getTitle()
	{
		final Object result = this.title != null ? this.title : this.getAxis().getTitleMarker() != null ? this.getAxis()
				.getTitleMarker().getCellAt(this.accessIndex).getValue() : null;

		if (log.isDebugEnabled())
		{
			final String titleByMarker = (this.title != null) ? this.title.toString() : "null";
			final String titleByAxis = (this.getAxis().getTitleMarker() != null) ? "" : "";
			log.debug("[" + this.getAxis().getId() + "] Title by Marker:" + titleByMarker + "; Title by Axis (TitleMarker): "
					+ titleByAxis + "; result:" + (result != null ? result : "null"));
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker#setTitle(java.lang.Object)
	 */
	public void setTitle(final Object title)
	{
		this.title = title;
	}

	public void useAsTitleMarker()
	{
		this.axisModel.getAxis(this.axis.getType().invert()).setTitleMarker(this);
		this.remove();
	}

	/**
	 * Sets the axisposition of this marker.
	 * 
	 * @param position
	 *           Axisposition
	 */
	protected void setPosition(final int position)
	{
		this.position = position;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker#getPosition()
	 */
	public int getPosition()
	{
		return this.position;
	}

	public DataTableAxisMarker copy()
	{
		final DataTableAxisMarker result = new DataTableAxisMarkerImpl(this.axisModel, this.axis, this.accessIndex);
		((DataTableAxisImpl) this.axis).addMarker(result);
		return result;
	}

	public void remove()
	{
		this.axis.removeMarkerAt(this.position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.getClass().getSimpleName() + "(model:" + axisModel.hashCode() + "; accessindex:" + accessIndex + "; axis:"
				+ this.axis.getType().name() + ")";
	}



}
