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


import java.util.List;
import java.util.Map;

import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;


/**
 * Represents a dimension of the table.<br/>
 * Currently this may either be a x-Axis (column) or y-Axis (row).<br/>
 * 
 * 
 */
public interface DataTableAxis
{

	/**
	 * An axis may be of type row or column.
	 */
	public static enum AXIS_2D
	{
		/** Axis addresses rows. **/
		Y_AXIS,

		/** Axis addresses columns. **/
		X_AXIS;


		/**
		 * @return the inverted axis.
		 */
		public AXIS_2D invert()
		{
			return (this == Y_AXIS) ? X_AXIS : Y_AXIS;
		}
	}


	/**
	 * Returns the ID.
	 * 
	 * @return ID
	 */
	public String getId();

	/**
	 * Sets the ID
	 * 
	 * @param id
	 *           the id to set.
	 */
	public void setId(String id);


	/**
	 * Returns the axis type.<br/>
	 * 
	 * @return {@link AXIS_2D#Y_AXIS} or {@link AXIS_2D#X_AXIS}
	 */
	public AXIS_2D getType();

	/**
	 * Returns the titlemarker for this axis when available.
	 * 
	 * @return {@link DataTableAxisMarker} used for titles or null
	 */
	public DataTableAxisMarker getTitleMarker();

	/**
	 * Sets a special marker whose cellvalues are used as title for each {@link DataTableAxisMarker} of this axis.<br/>
	 * 
	 * When a {@link DataTableAxisMarker} has an explicitely assigned title via
	 * {@link DataTableAxisMarker#setTitle(Object)} than this one wins over the value of this titlemarker.<br/>
	 * 
	 * The passed Marker will be ignored within {@link #getMarkerList()}<br/>
	 * 
	 * @param titleMarker
	 */
	public void setTitleMarker(DataTableAxisMarker titleMarker);

	/**
	 * Returns a List of all {@link DataTableAxisMarker} assigned to this axis. An optional sel titlemarker will be
	 * ignored.
	 * 
	 * @return List fo markers.
	 */
	public List<DataTableAxisMarker> getMarkerList();


	public DataTableAxisMarker appendMarker(DataTableAxisMarker templateMarker, String id, CharSequence title);

	/**
	 * Copies the passed marker and adds the result to this axis markers as last marker.
	 * 
	 * @param templateMarker
	 */
	public DataTableAxisMarker appendMarker(DataTableAxisMarker templateMarker, String id, CharSequence title,
			DataTableCellConverter cellConverter);



	/**
	 * Returns the {@link DataTableAxisMarker} by its position.
	 * 
	 * @param index
	 *           markerposition.
	 * @return {@link DataTableAxisMarker}
	 * 
	 * @throws IndexOutOfBoundsException
	 *            when requested index is greater then available number of markers.
	 */
	public DataTableAxisMarker getMarkerAt(int index);

	/**
	 * Removes a {@link DataTableAxisMarker} by its position.
	 * 
	 * @param index
	 *           markerposition
	 * @return removed {@link DataTableAxisMarker}
	 * 
	 * @throws IndexOutOfBoundsException
	 */
	public DataTableAxisMarker removeMarkerAt(int index);

	//id stuff
	/**
	 * Returns a {@link DataTableAxisMarker} by its ID.<br/>
	 * 
	 * @param markerId
	 *           ID of the requested marker.
	 * @return {@link DataTableAxisMarker} or null
	 */
	public DataTableAxisMarker getMarkerById(String markerId);

	public Map<String, DataTableAxisMarker> getMarkerIdMap();

	//grouping stuff
	public Map<String, List<DataTableAxisMarker>> getMarkerGroupMap();

	/**
	 * Returns a List of {@link DataTableAxisMarker} which are available under a given group.
	 * 
	 * @param groupId
	 *           ID of the group
	 * @return List of Markers
	 */
	public List<DataTableAxisMarker> getMarkerGroup(String groupId);

	/**
	 * Add a list of makers under the given id.<br/>
	 * Overwrites an already existing group when available.<br/>
	 * 
	 * @param groupId
	 *           ID of the group
	 * @param markerList
	 *           List of markers
	 */
	public void addMarkerGroup(String groupId, List<DataTableAxisMarker> markerList);

	/**
	 * Appends a single {@link DataTableAxisMarker} to the list of Markers put under the given groupid.<br/>
	 * When no group with the passed ID exits, a new one is initially created.<br/>
	 * 
	 * @param groupId
	 *           ID of the group
	 * @param marker
	 *           {@link DataTableAxisMarker} which shall be appended
	 */
	public void addMarkerToGroup(String groupId, DataTableAxisMarker marker);

	/**
	 * Removes the whole List of {@link DataTableAxisMarker} found under the given groupid.
	 * 
	 * @param groupId
	 *           ID of the group
	 */
	public void removeMarkerGroup(String groupId);


}
