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


import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;


public class RotatedDataTableAxisModel extends DataTableAxisModelImpl
{
	public RotatedDataTableAxisModel(final DataTableModel boundedModel)
	{
		super(boundedModel, null);
	}

	public RotatedDataTableAxisModel(final DataTableModel boundedModel, final String id)
	{
		super(boundedModel, id);
	}

	public RotatedDataTableAxisModel(final DataTableAxisModel wrappedModel)
	{
		this(wrappedModel.getDataTableModel(), wrappedModel.getId());
		super.setAxis(wrappedModel.getXAxis());
		super.setAxis(wrappedModel.getYAxis());

	}

	@Override
	public DataTableAxis getAxis(final AXIS_2D axisType)
	{
		return super.getAxis(axisType.invert());
	}




}
