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

import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;


public interface DataTableAxisModel extends DataTableModel
{
	//concrete part
	public DataTableAxis getAxis(AXIS_2D axisType);

	public List<DataTableAxisMarker> getValueMarkers(AXIS_2D axisType);

	public DataTableAxisMarker getTitleMarker(AXIS_2D axisType);

	public DataTableAxisMarker getMarkerById(AXIS_2D axisType, String id);

	public String getId();

	public void setId(String id);

	public DataTableModel getDataTableModel();

	public Object getSourceValueAt(int x, int y);

	public boolean setSourceValueAt(int x, int y, Object sourceValue);

	//abstract part
	public DataTableAxis getXAxis();

	public DataTableAxis getYAxis();

	public List<DataTableAxisMarker> getColumns();

	public List<DataTableAxisMarker> getRows();

	public DataTableAxisMarker getColumnById(String id);

	public DataTableAxisMarker getRowById(String id);

	public DataTableAxisMarker getTitleRow();

	public DataTableAxisMarker getTitleColumn();

}
