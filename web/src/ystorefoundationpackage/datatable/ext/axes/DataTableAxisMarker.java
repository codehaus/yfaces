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


import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.cell.DataTableCell;
import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;


/**
 * Represents a marking point at a {@link DataTableAxis}. The marking point has access to each cellvalues which crosses
 * this Marker and one of the Markers of the opposite {@link DataTableAxis}.
 * 
 * 
 */
public interface DataTableAxisMarker
{

	/**
	 * Returns the {@link DataTableAxisModel} which this marker belongs to. <br/>
	 * 
	 * @return DataTableAxisModel.
	 */
	public DataTableAxisModel getDataTableAxisModel();

	/**
	 * The index (either x or y) in combination with the passed parameter of {@link #getCellAt(int)} (either y or x) is
	 * taken to retrieve the cells sourcevalue.<br/>
	 * The sourcevalue is requested from the DataTableModel which belongs to the current DataTableAxisModel
	 * 
	 * @return accessindex.
	 * 
	 * @see #getCellAt(int)
	 */
	public int getAccessIndex();

	/**
	 * Returns the {@link DataTableAxis} which this Marker belongs to. <br/>
	 * 
	 * @return DataTableAxis
	 */
	public DataTableAxis getAxis();


	/**
	 * Requests the n-th {@link DataTableCell}.<br/>
	 * Depending on the type of the {@link DataTableAxis}, index specify either a column- or a rowindex.<br/>
	 * 
	 * @param index
	 *           requested cellindex (n)
	 * @return {@link DataTableCell}
	 * 
	 * @see #getAccessIndex()
	 */
	public DataTableCell getCellAt(int index);

	/**
	 * Requests the {@link DataTableCell} which crosses this {@link DataTableAxisMarker} and the passed one.<br/>
	 * 
	 * @param otherMarker
	 *           opposite {@link DataTableAxisMarker}.
	 * @return {@link DataTableCell}.
	 */
	public DataTableCell getCellAt(DataTableAxisMarker otherMarker);

	/**
	 * Requests the {@link DataTableCell} which crosses this {@link DataTableAxisMarker} and the passed one given by its
	 * ID.<br/>
	 * 
	 * @param otherMarkerId
	 *           id of opposite {@link DataTableAxisMarker}.
	 * @return {@link DataTableCell}
	 */
	public DataTableCell getCellAt(String otherMarkerId);


	/**
	 * Returns the ID.<br/>
	 * 
	 * @return ID String
	 */
	public String getId();

	/**
	 * Sets the ID.<br/>
	 * 
	 * @param id
	 *           ID to set
	 */
	public void setId(String id);

	public boolean isVisible();

	public void setVisible(boolean isVisible);

	/**
	 * Sets a title.<br/>
	 * The title can be of any type which gives other components the possibility to create different title
	 * representations based on the same title base.<br/>
	 * 
	 * @param title
	 *           Title to set
	 */
	public void setTitle(Object title);

	/**
	 * Returns the title.
	 * 
	 * @return title.
	 */
	public Object getTitle();

	/**
	 * Returns the {@link DataTableCellConverter}
	 * 
	 * @return {@link DataTableCellConverter}
	 */
	public DataTableCellConverter<?> getCellConverter();

	/**
	 * Sets the {@link DataTableCellConverter}
	 * 
	 * @param cellConverter
	 *           {@link DataTableCellConverter} to set.
	 */
	public void setCellConverter(DataTableCellConverter<?> cellConverter);

	/**
	 * Returns the markers position at the {@link DataTableAxis}.
	 * 
	 * @return markerposition
	 */
	public int getPosition();


	/**
	 * Removes the marker.
	 */
	public void remove();

	/**
	 * Creates a new Marker based on the sourcevalues of this marker. The created marker is added automatically as ne,
	 * last one to the list of available markers at the axis.<br/>
	 * 
	 * @return newly created {@link DataTableAxisMarker}
	 */
	public DataTableAxisMarker copy();

	/**
	 * Use this markers cellvalues as titles for all markers of the opposite axis.
	 */
	public void useAsTitleMarker();

}
