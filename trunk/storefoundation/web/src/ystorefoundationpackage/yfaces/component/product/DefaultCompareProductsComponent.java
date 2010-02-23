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
package ystorefoundationpackage.yfaces.component.product;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.RowCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.RotatedDataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.datatable.ext.cell.ClassificationCellConverter;
import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;



/**
 * Implementation of the <code>CompareProductsComponent</code> interface.
 */
public class DefaultCompareProductsComponent extends AbstractYComponent implements CompareProductsComponent
{
	private List<ProductModel> productList = null;
	//private List<? super Item> attributes = null;
	private List attributes = null;
	private boolean rotated = false;

	private YComponentEventHandler<CompareProductsComponent> ehRotateTable = null;

	private transient CategoryModel catg = null;
	private transient DataTableAxisModel model = null;

	/**
	 * This event gets fired when the user tries to rotate the table and view it.
	 */
	public static class RotateCompareTableEvent extends DefaultYComponentEventListener<CompareProductsComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<CompareProductsComponent> event)
		{
			final CompareProductsComponent cmp = event.getComponent();
			cmp.setRotated(cmp.getRotated() ^ true);

			((DefaultCompareProductsComponent) cmp).model = null;
		}
	}

	/**
	 * Constructor.
	 */
	public DefaultCompareProductsComponent()
	{
		super();
		this.ehRotateTable = super.createEventHandler(new RotateCompareTableEvent());
	}

	public List<ProductModel> getProductList()
	{
		return this.productList;
	}

	public void setCompareAttributes(final CategoryModel catg)
	{
		this.catg = catg;
	}

	public void setCompareAttributes(final List attributes)
	{
		this.attributes = attributes;
	}

	public void setProductList(final List<ProductModel> products)
	{
		this.productList = products;

		// reset model
		this.model = null;
	}


	public YComponentEventHandler<CompareProductsComponent> getRotateTableEvent()
	{
		return this.ehRotateTable;
	}

	/**
	 * Implementation of the <code>DataTableCellConverter</code> interface.
	 */
	public static class ProductCellConverter implements DataTableCellConverter<Object>
	{

		public Object getConvertedCellValue(final Object sourceCellValue)
		{
			return ((ProductModel) sourceCellValue).getName() + " <br>(" + ((ProductModel) sourceCellValue).getCode() + ")";
		}
	}

	public DataTableAxisModel getCompareTable()
	{
		if (this.model == null && getProductList().size() > 0)
		{
			//DataTableModel  
			final DataTableModel tmProducts = new RowCollectionDataTableModel(getProductList());
			model = DataTableFactory.createDataTableAxisModel(tmProducts);

			//create classificationcolumns
			for (Object attrib : attributes)
			{
				final DataTableAxisMarker marker = model.getYAxis().getMarkerAt(0).copy();

				if (attrib instanceof ClassAttributeAssignmentModel)
				{
					attrib = ((ClassAttributeAssignmentModel) attrib).getClassificationAttribute();
				}

				if (attrib instanceof ClassificationAttributeModel)
				{
					final ClassificationAttributeModel classAttrib = (ClassificationAttributeModel) attrib;
					marker.setCellConverter(new ClassificationCellConverter(classAttrib));
					marker.setTitle(classAttrib.getName());

				}
				else
				{
					throw new StorefoundationException("Unsupported attribute of type " + attrib.getClass().getName());
				}
			}

			//remove first row and use it for markertitles
			model.getXAxis().setTitleMarker(model.getYAxis().removeMarkerAt(0));
			model.getXAxis().getTitleMarker().setCellConverter(new ProductCellConverter());

			if (this.rotated)
			{
				this.model = new RotatedDataTableAxisModel(model);
			}

		}
		return model;
	}

	@Override
	public void validate()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		if (this.attributes == null && this.catg == null)
		{
			this.catg = reqCtx.getSessionContext().getCategory();
		}

		if (this.attributes == null)
		{
			this.attributes = new ArrayList();
		}

		if (this.catg != null)
		{
			this.attributes.addAll(JaloBridge.getInstance().findAllAssignmentsByCategory(catg,
					JaloBridge.CLASSIFICATION_ATTR_COMPAREABLE));
		}

		if (this.productList == null)
		{
			if (this.catg != null)
			{
				this.productList = reqCtx.getProductManagement().findAllByCategory(this.catg, false, null, true);
			}
			else
			{
				this.productList = Collections.EMPTY_LIST;
			}

		}

	}

	public boolean getRotated() // NOPMD
	{
		return rotated;
	}

	public void setRotated(final boolean rotated)
	{
		this.rotated = rotated;
	}




}
