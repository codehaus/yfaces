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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableException;
import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;


/**
 * 
 * 
 */
public class DataTableAxisImpl implements DataTableAxis
{

	private static final Logger log = Logger.getLogger(DataTableAxisImpl.class);

	private String id = null;
	private List<DataTableAxisMarker> markersList = null;
	private DataTableAxisMarker titleMarker = null;
	private Map<String, List<DataTableAxisMarker>> markerGroupMap = null;
	private Map<String, DataTableAxisMarker> markersMap = null;
	private AXIS_2D axisType = null;

	public DataTableAxisImpl(final AXIS_2D axisType, final String id)
	{
		this.axisType = axisType;
		this.id = id;
		this.markersList = new ArrayList<DataTableAxisMarker>();
		this.markersMap = new HashMap<String, DataTableAxisMarker>();
		this.markerGroupMap = new HashMap<String, List<DataTableAxisMarker>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#getId()
	 */
	public String getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#setId(java.lang.String)
	 */
	public void setId(final String id)
	{
		this.id = id;
	}


	public AXIS_2D getType()
	{
		return this.axisType;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#addGoup(java.lang.String, java.util.List)
	 */
	public void addMarkerGroup(final String groupId, final List<DataTableAxisMarker> markerList)
	{
		if (markerList == null)
		{
			throw new DataTableException("No markers specified.", new NullPointerException());
		}

		for (final DataTableAxisMarker marker : markerList)
		{
			this.addMarkerToGroup(groupId, marker);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#addMarkerToGroup(java.lang.String,
	 * de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarker)
	 */
	public void addMarkerToGroup(final String groupId, final DataTableAxisMarker marker)
	{
		if (marker == null)
		{
			throw new DataTableException("No marker specified", new NullPointerException());
		}

		List<DataTableAxisMarker> markers = this.markerGroupMap.get(groupId);

		if (log.isDebugEnabled())
		{
			final String mode = markers != null ? "update" : "initial";
			final String count = markers != null ? String.valueOf(markers.size() + 1) : "1";
			log.debug("[" + this.getId() + "]: Add marker '" + marker.getId() + "' to group '" + groupId + "' [" + mode
					+ "] markers:" + count);
		}

		if (markers == null)
		{
			markers = new ArrayList<DataTableAxisMarker>();
			this.markerGroupMap.put(groupId, markers);
		}

		markers.add(marker);

	}

	public DataTableAxisMarker appendMarker(final DataTableAxisMarker templateMarker, final String id, final CharSequence title)
	{
		return this.appendMarker(templateMarker, id, title, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#appendMarker(de.hybris.platform.webfoundation
	 * .datatable.ext.axes.DataTableAxisMarker)
	 */
	public DataTableAxisMarker appendMarker(final DataTableAxisMarker templateMarker, final String id, final CharSequence title,
			final DataTableCellConverter cellConverter)
	{
		final DataTableAxisMarker result = templateMarker.copy();
		result.setId(id);
		result.setTitle(title);
		result.setCellConverter(cellConverter);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#getAllMarkers()
	 */
	public List<DataTableAxisMarker> getMarkerList()
	{
		return this.markersList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#getGroup(java.lang.String)
	 */
	public List<DataTableAxisMarker> getMarkerGroup(final String groupId)
	{
		return this.markerGroupMap.get(groupId);
	}

	public Map<String, List<DataTableAxisMarker>> getMarkerGroupMap()
	{
		return Collections.unmodifiableMap(this.markerGroupMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#getMarkerAt(int)
	 */
	public DataTableAxisMarker getMarkerAt(final int index)
	{
		return this.markersList.get(index);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#removeMarkerAt(int)
	 */
	public DataTableAxisMarker removeMarkerAt(final int index)
	{
		//remove from list
		final DataTableAxisMarker removed = this.markersList.remove(index);

		if (removed != null)
		{
			//remove from idmap
			final String markerId = removed.getId();
			if (markerId != null && this.markersMap.containsKey(markerId))
			{
				this.markersMap.remove(markerId);
			}

			//remove from groupmap
			for (final Map.Entry<String, List<DataTableAxisMarker>> entry : this.markerGroupMap.entrySet())
			{
				if (entry.getValue().contains(removed))
				{
					entry.getValue().remove(removed);
				}
			}

			//update positions of following markers
			for (int i = index; i < this.markersList.size(); i++)
			{
				((DataTableAxisMarkerImpl) markersList.get(i)).setPosition(i);
			}
		}

		return removed;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxis#getMarkerById(java.lang.String)
	 */
	public DataTableAxisMarker getMarkerById(final String id)
	{
		return this.markersMap.get(id);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#getTitleCells()
	 */
	public DataTableAxisMarker getTitleMarker()
	{
		return this.titleMarker;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#removeGroup(java.lang.String)
	 */
	public void removeMarkerGroup(final String groupId)
	{
		if (log.isDebugEnabled())
		{
			log.debug("[" + this.getId() + "]: removing markergroup '" + groupId + "' "
					+ (markerGroupMap.containsKey(groupId) ? "" : "not possible (not found)"));
		}

		this.markerGroupMap.remove(groupId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see datatable.ext.axes.DataTableAxis#setTitleCells(datatable.ext.axes.DataTableAxisMarker)
	 */
	public void setTitleMarker(final DataTableAxisMarker titleMarker)
	{
		if (log.isDebugEnabled())
		{
			log.debug("[" + this.getId() + "]: set titlemarker (id:" + titleMarker.getId() + ")");
		}
		this.titleMarker = titleMarker;
	}

	public Map<String, DataTableAxisMarker> getMarkerIdMap()
	{
		return this.markersMap;
	}



	protected void setMarkerList(final List<DataTableAxisMarker> markers)
	{
		if (markers instanceof LazyDataTableAxisMarkerList)
		{
			this.setMarkerList((LazyDataTableAxisMarkerList) markers);
		}
		else
		{
			if (log.isDebugEnabled())
			{
				log.debug("[" + this.getId() + "]: set markers [" + (this.markersMap == null ? "initial" : "update") + "] "
						+ "(fixed; size:" + markers.size() + "); id evaluation enabled");
			}

			this.markersMap = new HashMap<String, DataTableAxisMarker>();
			this.markersList = new ArrayList<DataTableAxisMarker>();
			for (final DataTableAxisMarker marker : markers)
			{
				this.addMarker(marker);
			}
		}
	}

	private void setMarkerList(final LazyDataTableAxisMarkerList markers)
	{
		if (log.isDebugEnabled())
		{
			log.debug("[" + this.getId() + "]: set markers [" + (this.markersMap == null ? "initial" : "update") + "] "
					+ "(dynamic; size:" + markers.size() + "); id evaluation disabled");
		}
		this.markersMap = new HashMap<String, DataTableAxisMarker>();
		this.markersList = markers;
	}

	protected void updateMarkerIdMap(final String oldId, final DataTableAxisMarker marker)
	{
		final String newId = marker.getId();
		final Map<String, DataTableAxisMarker> markersMap = this.getMarkerIdMap();
		final boolean isOverwrite = markersMap.containsKey(newId);

		//remove old key when available
		if (oldId != null)
		{
			markersMap.remove(oldId);
		}

		//add new key when not null
		if (newId != null)
		{
			markersMap.put(newId, marker);
		}

		if (log.isDebugEnabled())
		{
			String msg = "";
			if (oldId != null)
			{
				msg = msg + "remove key '" + oldId + "'; ";
			}

			if (newId != null)
			{
				msg = msg + "put marker under '" + newId + "' [" + (isOverwrite ? "overwrite" : "initial") + "]";
			}

			if (msg.length() == 0)
			{
				msg = " no (marker has no id)";
			}

			log.debug("[" + this.getId() + "] update marker-ID LUT: " + msg + "(LUT size:" + markersMap.size() + ")");
		}
	}


	protected void addMarker(final DataTableAxisMarker marker)
	{
		if (marker == null)
		{
			throw new DataTableException("no marker specified.", new NullPointerException());
		}

		this.markersList.add(marker);
		((DataTableAxisMarkerImpl) marker).setPosition(markersList.size() - 1);
		this.updateMarkerIdMap(null, marker);

		if (log.isDebugEnabled())
		{
			log.debug("[" + this.getId() + "]: added marker (" + marker.getClass().getSimpleName() + "; id:" + marker.getId()
					+ ") to list (size: " + this.markersList.size() + " markers)");
		}


	}




}
