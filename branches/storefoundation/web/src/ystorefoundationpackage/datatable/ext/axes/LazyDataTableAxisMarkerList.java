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



import java.util.AbstractList;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableException;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;



/**
 * A List whose elements are lazily created as {@link DataTableAxisMarker} This Marker is used for very large table date
 * (row or column).
 * 
 * 
 */
//TODO: support predefined axes which aren't lazy
public class LazyDataTableAxisMarkerList extends AbstractList<DataTableAxisMarker>
{
	private static final Logger log = Logger.getLogger(LazyDataTableAxisMarkerList.class);

	private DataTableAxisModel tableModel = null;
	private DataTableAxis axis = null;

	private int sizeVariable = 0;

	private final Map<Integer, DataTableAxisMarkerImpl> lutMap = Collections.EMPTY_MAP;

	/**
	 * Constructs a new lazy list.<br/>
	 * Each lazy instantiated {@link DataTableAxis} gets initialized with the parameters given to this constructor and
	 * the index passed to the get-call.<br/>
	 * <br/>
	 * 
	 * @param sourceModel
	 *           The model
	 * @param axis
	 *           The axis
	 * @param size
	 *           The size of this list.
	 */
	public LazyDataTableAxisMarkerList(final DataTableAxisModel sourceModel, final DataTableAxis axis, final int size)
	{
		super();
		this.tableModel = sourceModel;
		this.axis = axis;

		this.sizeVariable = size;

		if (log.isDebugEnabled())
		{
			log.debug("New " + this.getClass().getSimpleName() + " constructed. (Boundedmodel:" + sourceModel + "; Axistype:"
					+ this.axis.getType().name() + "; count: " + this.size() + ")");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public DataTableAxisMarker get(final int index)
	{
		DataTableAxisMarker result = lutMap.get(Integer.valueOf(index));
		if (result == null)
		{
			result = DataTableFactory.createMapMarker(tableModel, axis, index);
			((DataTableAxisMarkerImpl) result).setPosition(index);
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractList#add(int, java.lang.Object)
	 */
	@Override
	public void add(final int index, final DataTableAxisMarker element)
	{
		throw new DataTableException("Can't add a element to a lazy datatableaxislist", new UnsupportedOperationException());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size()
	{
		return this.sizeVariable;
	}



}
