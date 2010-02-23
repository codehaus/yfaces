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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.ColumnCollectionDataTableModel;
import ystorefoundationpackage.datatable.DataTableException;
import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.RowCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModelImpl;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;


public class DataTableFactory
{

	private static final Logger log = Logger.getLogger(DataTableFactory.class);

	public static DataTableAxisModel createEmptyDataTableAxisModel()
	{
		return createDataTableAxisModelInternal("empty", new ColumnCollectionDataTableModel(Collections.EMPTY_LIST), -1);
	}

	public static DataTableAxisModel createDataTableAxisModel(final List dataSource, final AXIS_2D axis, final int span)
	{
		if (span < 1)
		{
			throw new DataTableException("Spanning must be at least greater than zero (got " + span + ")");
		}

		if (axis == null)
		{
			throw new DataTableException("No alignment (axis)  for datasource specified", new NullPointerException());
		}

		DataTableAxisModel result = null;

		if (axis == AXIS_2D.Y_AXIS)
		{
			final DataTableModel model = new ColumnCollectionDataTableModel(dataSource);
			result = DataTableFactory.createDataTableAxisModelInternal("", model, span, -1);
		}

		if (axis == AXIS_2D.X_AXIS)
		{
			final DataTableModel model = new RowCollectionDataTableModel(dataSource);
			result = DataTableFactory.createDataTableAxisModelInternal("", model, -1, span);
		}


		return result;
	}

	/**
	 * Creates a {@link DataTableAxisModel} based on basic {@link DataTableModel}.<br/>
	 * 
	 * @param model
	 *           {@link DataTableModel} which provides the sourcevalues for the cells
	 * @return {@link DataTableAxisModel}
	 */
	public static DataTableAxisModel createDataTableAxisModel(final DataTableModel model)
	{
		final String logMsg = "Try to build " + DataTableAxisModel.class.getSimpleName() + " for "
				+ model.getClass().getSimpleName();

		if (model instanceof ColumnCollectionDataTableModel)
		{
			log.debug(logMsg + "; basemodel:" + ColumnCollectionDataTableModel.class.getSimpleName());
			return createDataTableAxisModelInternal("AxisModel_colbased", (ColumnCollectionDataTableModel) model, -1);
		}

		if (model instanceof RowCollectionDataTableModel)
		{
			log.debug(logMsg + "; basemodel:" + RowCollectionDataTableModel.class.getSimpleName());
			return createDataTableAxisModelInternal("AxisModel_rowbased", (RowCollectionDataTableModel) model, -1);

		}

		throw new DataTableException("Currently no support for " + model.getClass() + " as "
				+ DataTableAxisModel.class.getSimpleName());
	}


	protected static DataTableAxisMarkerMap createMapMarker(final DataTableAxisModel axisModel, final DataTableAxis axis,
			final int axisOffset)
	{
		return new DataTableAxisMarkerMap(axisModel, axis, axisOffset);
	}

	private static DataTableAxisModel createDataTableAxisModelInternal(final String id, final DataTableModel model,
			final int colSpan, final int rowSpan)
	{
		// create axismodel
		final DataTableAxisModelImpl result = new DataTableAxisModelImpl(model, id);

		// create axes
		final DataTableAxisImpl xAxis = new DataTableAxisImpl(AXIS_2D.X_AXIS, "xAxis");
		final DataTableAxisImpl yAxis = new DataTableAxisImpl(AXIS_2D.Y_AXIS, "yAxis");

		final int colCount = model.getColumnCount();
		final int rowCount = model.getRowCount();

		//create columns
		List<DataTableAxisMarker> xMarkers = null;
		if (colSpan < 1)
		{
			//...create dynamic axismarkers for rows
			xMarkers = new LazyDataTableAxisMarkerList(result, xAxis, colCount);
		}
		else
		{
			xMarkers = new ArrayList<DataTableAxisMarker>(colSpan);
			for (int i = 0; i < colSpan; i++)
			{
				final int dataOffset = (i < colCount) ? i : colCount - 1;
				final DataTableAxisMarker marker = createMapMarker(result, xAxis, dataOffset);
				xMarkers.add(marker);
			}
		}

		//create rows
		List<DataTableAxisMarker> yMarkers = null;
		if (rowSpan < 1)
		{
			//...create dynamic axismarkers for rows
			yMarkers = new LazyDataTableAxisMarkerList(result, yAxis, rowCount);
		}
		else
		{
			yMarkers = new ArrayList<DataTableAxisMarker>(colSpan);
			for (int i = 0; i < colSpan; i++)
			{
				final int dataOffset = (i < rowCount) ? i : rowCount - 1;
				final DataTableAxisMarker marker = createMapMarker(result, yAxis, dataOffset);
				yMarkers.add(marker);
			}
		}

		xAxis.setMarkerList(xMarkers);
		yAxis.setMarkerList(yMarkers);

		// set axes
		result.setAxis(xAxis);
		result.setAxis(yAxis);

		return result;

	}

	private static DataTableAxisModel createDataTableAxisModelInternal(final String axisModelId,
			final ColumnCollectionDataTableModel tableModel, final int colSpan)
	{
		// create axismodel
		final DataTableAxisModelImpl axisModel = new DataTableAxisModelImpl(tableModel, axisModelId);

		// create axes
		final DataTableAxisImpl xAxis = new DataTableAxisImpl(AXIS_2D.X_AXIS, "xAxis");
		final DataTableAxisImpl yAxis = new DataTableAxisImpl(AXIS_2D.Y_AXIS, "yAxis");

		// create concrete axismarkers for columns
		final int columnCount = tableModel.getColumnCount();
		final List<DataTableAxisMarker> xAxisMarkers = new ArrayList();
		for (int i = 0; i < columnCount; i++)
		{
			final DataTableAxisMarker marker = createMapMarker(axisModel, xAxis, i);
			xAxisMarkers.add(marker);
		}
		xAxis.setMarkerList(xAxisMarkers);

		List<DataTableAxisMarker> yAxisMarkers = null;

		//when no rowcount is specified...
		if (colSpan < 1)
		{
			//...create dynamic axismarkers for rows
			yAxisMarkers = new LazyDataTableAxisMarkerList(axisModel, yAxis, tableModel.getRowCount());
		}
		else
		{
			yAxisMarkers = new ArrayList<DataTableAxisMarker>(colSpan);
			for (int i = 0; i < colSpan; i++)
			{
				final int dataOffset = (i < tableModel.getRowCount()) ? i : tableModel.getRowCount();
				final DataTableAxisMarker marker = createMapMarker(axisModel, yAxis, dataOffset);
				yAxisMarkers.add(marker);
			}
		}
		//		// create dynamic axismarkers for rows
		//		LazyDataTableAxisMarkerList yAxisMarkers = new LazyDataTableAxisMarkerList(
		//				axisModel, yAxis, tableModel.getRowCount());
		yAxis.setMarkerList(yAxisMarkers);

		// set axes
		axisModel.setAxis(xAxis);
		axisModel.setAxis(yAxis);

		return axisModel;
	}

	private static DataTableAxisModel createDataTableAxisModelInternal(final String axisModelId,
			final RowCollectionDataTableModel tableModel, final int columnCount)
	{
		// create axismodel
		final DataTableAxisModelImpl axisModel = new DataTableAxisModelImpl(tableModel, axisModelId);

		// create axes
		final DataTableAxisImpl xAxis = new DataTableAxisImpl(AXIS_2D.X_AXIS, "xAxis");
		final DataTableAxisImpl yAxis = new DataTableAxisImpl(AXIS_2D.Y_AXIS, "yAxis");

		// create concrete axismarkers for row
		final int rowCount = tableModel.getRowCount();
		final List<DataTableAxisMarker> yAxisMarkers = new ArrayList();
		for (int i = 0; i < rowCount; i++)
		{
			final DataTableAxisMarker marker = createMapMarker(axisModel, yAxis, i);
			yAxisMarkers.add(marker);
		}
		yAxis.setMarkerList(yAxisMarkers);

		//ADDED start
		List<DataTableAxisMarker> xAxisMarkers = null;

		//when no rowcount is specified...
		if (columnCount < 1)
		{
			//...create dynamic axismarkers for rows
			xAxisMarkers = new LazyDataTableAxisMarkerList(axisModel, xAxis, tableModel.getColumnCount());
		}
		else
		{
			xAxisMarkers = new ArrayList<DataTableAxisMarker>(columnCount);
			for (int i = 0; i < columnCount; i++)
			{
				final int dataOffset = (i < tableModel.getColumnCount()) ? i : tableModel.getColumnCount();
				final DataTableAxisMarker marker = createMapMarker(axisModel, xAxis, dataOffset);
				xAxisMarkers.add(marker);
			}
		}
		//ADDED end		


		//		// create dynamic axismarkers for rows
		//		LazyDataTableAxisMarkerList xAxisMarkers = new LazyDataTableAxisMarkerList(
		//				axisModel, xAxis, tableModel.getColumnCount());

		xAxis.setMarkerList(xAxisMarkers);

		// set axes
		axisModel.setAxis(xAxis);
		axisModel.setAxis(yAxis);

		return axisModel;
	}


	//
	// DEPRECATED;
	// should be removed as fast s possible
	//

	public static final boolean PROCESS_HORIZONTAL = true;

	public static final boolean PROCESS_VERTICAL = false;

	public static DataTableAxisModel getDataTableModel()
	{
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED");
	}

	public static DataTableAxisModel getDataTableModel(final Collection values, final int rows, final int cols,
			final boolean cutRest)
	{
		return getDataTableModel(values, rows, cols, PROCESS_VERTICAL, cutRest);
	}

	/**
	 * Factorymethod.
	 * 
	 * @param values
	 * @param rows
	 * @param cols
	 * @param cutRest
	 * @return an Instance
	 */
	public static DataTableAxisModel getDataTableModel(final Collection values, final int rows, final int cols,
			final boolean direction, final boolean cutRest)
	{
		DataTableAxisModel result = null;

		if (values != null && !values.isEmpty())
		{
			final List<List> model = createTwoDimList(values, rows, cols, direction, cutRest);

			// DataTableModel cellModel = new CollectionDataTableModel(model,
			// true);
			final DataTableModel cellModel = new ColumnCollectionDataTableModel(model);
			// result = new DataTableAxisModelImpl(cellModel);
			result = createDataTableAxisModel(cellModel);
		}
		return result;
	}

	private static List<List> createTwoDimList(final Collection col, int rows, int cols, final boolean direction,
			final boolean cutRest)
	{
		if (cols == -1 && rows == -1)
		{
			throw new RuntimeException("Neither column nor row has an specified size");
		}
		if (col == null || col.isEmpty())
		{
			throw new RuntimeException("Can't create DataTableModel; got no values");
		}
		final int size = col.size();

		// calculate dynamic parameters if neccessary, cut the modulo part of the
		// Collection
		if (cols == -1)
		{
			cols = size / rows;
		}
		if (rows == -1)
		{
			rows = size / cols;
		}

		if (!cutRest && size > rows * cols)
		{
			rows++;
		}

		// prepare list of rows
		final List<List> result = new ArrayList(cols);

		// prepare list of columns
		for (int i = 0; i < cols; i++)
		{
			result.add(new ArrayList(rows));
		}

		final Iterator iter = col.iterator();

		// int max_y = (direction == PROCESS_VERTICAL) ? rows : cols;
		// int max_x = (direction == PROCESS_VERTICAL) ? cols : rows;
		//
		// for (int _x = 0; _x < max_x; _x++)
		// for (int _y = 0; _y < max_y; _y++)
		// result.get( (direction == PROCESS_VERTICAL) ? _x :
		// _y).add(iter.hasNext() ? iter.next() : null);

		// more code but more readable
		if (direction == PROCESS_VERTICAL)
		{
			// fill n:m matrix per line (from top to bottom)
			for (int _col = 0; _col < cols; _col++)
			{
				for (int _row = 0; _row < rows; _row++)
				{
					result.get(_col).add(iter.hasNext() ? iter.next() : null);
				}
			}
		}
		else
		{
			// fill n:m matrix per column (from left to right)
			for (int _row = 0; _row < rows; _row++)
			{
				for (int _col = 0; _col < cols; _col++)
				{
					result.get(_col).add(iter.hasNext() ? iter.next() : null);
				}
			}
		}

		return result;
	}

}
