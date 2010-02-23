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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.datatable.ColumnCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.datatable.ext.cell.ClassificationCellConverter;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.domain.util.TransformProduct2BestProduct;
import ystorefoundationpackage.domain.util.list.BidiTransformedList;
import ystorefoundationpackage.domain.util.list.LuceneSearchBufferedList;



/**
 * Implementation of the <code>ProductTableComponent</code> interface.
 */
public class DefaultProductTableComponent extends AbstractYComponent implements ProductTableComponent
{
	private static final Logger log = Logger.getLogger(DefaultProductTableComponent.class);

	private static final String TBLCOL_SELECT = "select";
	private static final String TBLCOL_SCORE = "score";
	private static final String TBLCOL_PRODUCT = "products";
	private static final String TBLCOL_PRICE = "price";

	private static final String TBLCOL_FEATURE_PREFIX = "feature";

	private static final String TBL_MARKERGROUP_FEATURES = "features";


	private List<ProductModel> productList = null;
	private List<ClassAttributeAssignmentModel> classAttributeAssignmentList = null;

	private boolean classificationColumnEnabled = true;
	private boolean selectColumnEnabled = true;
	private boolean priceColumnEnabled = true;
	private int classificationColumnCount = 3;
	private int visibleRowCount = 10;

	//sort properties
	private String sortColumnName = "name";
	private boolean isSortAscending = true;

	private transient DataTableAxisModel tableModel = null;
	private Boolean[] checkboxes = null;
	private transient List<ProductModel> selecteProducts = null;

	private YComponentEventHandler<ProductTableComponent> ehCompare = null;
	private YComponentEventHandler<ProductTableComponent> ehSortByName = null;

	/**
	 * Constructor.
	 */
	public DefaultProductTableComponent()
	{
		super();
		this.ehCompare = super.createEventHandler(new ProductTableCompareEvent());
		this.ehSortByName = super.createEventHandler(new ProductTableSortEvent(""));
	}


	public List<ProductModel> getProductList()
	{
		return this.productList;
	}

	public boolean isClassificationColumnEnabled()
	{
		return this.classificationColumnEnabled;
	}

	public boolean isSelectColumnEnabled()
	{
		return this.selectColumnEnabled;
	}

	public boolean isPriceColumnEnabled()
	{
		return this.priceColumnEnabled;
	}

	public void setClassificationColumnEnabled(final boolean enabled)
	{
		this.classificationColumnEnabled = enabled;
	}

	public void setSelectColumnEnabled(final boolean enabled)
	{
		this.selectColumnEnabled = enabled;
	}

	public void setPriceColumnEnabled(final boolean enabled)
	{
		this.priceColumnEnabled = enabled;
	}

	public void setProductList(final List<ProductModel> productList)
	{
		this.productList = productList;

		this.tableModel = null;
	}


	public List<ClassAttributeAssignmentModel> getClassAttributeAssignments()
	{
		return this.classAttributeAssignmentList;
	}

	public void setClassAttributeAssignments(final List<ClassAttributeAssignmentModel> assignments)
	{
		this.classAttributeAssignmentList = assignments;
	}

	public int getClassificationColumnCount()
	{
		return this.classificationColumnCount;
	}

	public void setClassificationColumnCount(final int count)
	{
		this.classificationColumnCount = count;
	}

	public int getVisibleRowCount()
	{
		return this.visibleRowCount;
	}

	public void setVisibleRowCount(final int rowCount)
	{
		this.visibleRowCount = rowCount;
	}

	public String getSortColumn()
	{
		return this.sortColumnName;
	}

	public boolean getSortAscending() // NOPMD
	{
		return this.isSortAscending;
	}

	public void setSortAscending(final boolean asc)
	{
		this.isSortAscending = asc;
	}

	public void setSortColumn(final String name)
	{
		this.sortColumnName = name;
	}

	//transient
	public List<ProductModel> getSelectedProducts()
	{
		if (this.selecteProducts == null)
		{
			if (selectColumnEnabled)
			{
				if (this.productList.size() != this.checkboxes.length)
				{
					throw new StorefoundationException("Size of available products (" + this.productList.size()
							+ ") doesn't match selectable size (" + this.checkboxes.length + ")");
				}
				this.selecteProducts = new ArrayList<ProductModel>();
				for (int i = 0; i < this.checkboxes.length; i++)
				{
					if (this.checkboxes[i].booleanValue())
					{
						this.selecteProducts.add(this.productList.get(i));
					}
				}
			}
		}
		return this.selecteProducts;
	}

	//transient
	public DataTableAxisModel getTableModel()
	{
		if (this.tableModel == null)
		{
			this.tableModel = this.getDefaultTableModel();
		}

		return this.tableModel;
	}


	public YComponentEventHandler<ProductTableComponent> getCompareEvent()
	{
		return this.ehCompare;
	}

	public YComponentEventHandler<ProductTableComponent> getSortEvent()
	{
		return this.ehSortByName;
	}


	@Override
	public void validate()
	{
		if (getProductList() == null)
		{
			setProductList(this.getDefaultProductList());
		}

		if (getClassAttributeAssignments() == null)
		{
			final List<ClassAttributeAssignmentModel> attributes = this.getDefaultClassAttributeAssignments();
			setClassAttributeAssignments(attributes);
		}

	}

	@Override
	public void refresh()
	{
		this.validate();
	}


	public List<ProductModel> getDefaultProductList()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final CategoryModel catg = reqCtx.getSessionContext().getCategory();
		List<ProductModel> result = Collections.EMPTY_LIST;
		if (catg != null)
		{
			result = reqCtx.getProductManagement().findAllByCategory(catg, true, "name", true);
		}
		return result;
	}


	public List<ClassAttributeAssignmentModel> getDefaultClassAttributeAssignments()
	{
		List result = Collections.EMPTY_LIST;
		final CategoryModel catg = YStorefoundation.getRequestContext().getSessionContext().getCategory();

		if (catg != null)
		{
			Collection<ClassAttributeAssignmentModel> _result = JaloBridge.getInstance().findAllAssignmentsByCategory(catg,
					JaloBridge.CLASSIFICATION_ATTR_LISTABLE);

			//fallback when no listable attributes are defined
			if (_result.isEmpty())
			{
				_result = JaloBridge.getInstance().findAllAssignmentsByCategory(catg, JaloBridge.CLASSIFICATION_ATTR_ALL);

				if (!_result.isEmpty())
				{
					log.warn("Category " + catg.getCode() + " has ClassificationAttributes but no ones are set to 'listable'; "
							+ "just take the first three from all available ones");
				}

			}
			result = new ArrayList(_result);
		}

		return result;
	}


	public DataTableAxisModel getDefaultTableModel()
	{
		DataTableAxisModel result = null;

		//retrieve products
		final List<ProductModel> _products = getProductList();
		if (_products == null)
		{
			throw new RuntimeException("No products available for creating a productlist.");
		}

		if (_products.isEmpty())
		{
			result = DataTableFactory.createEmptyDataTableAxisModel();
		}
		else
		{
			//create DataTableModel; column-based
			final ColumnCollectionDataTableModel tmProducts = new ColumnCollectionDataTableModel();

			//we have a product column
			tmProducts.addColumn(_products);

			//we have a price column
			tmProducts.addColumn(new BidiTransformedList(_products, new TransformProduct2BestProduct()));

			//do we have a checkbox column?
			if (isSelectColumnEnabled())
			{
				this.checkboxes = new Boolean[tmProducts.getRowCount()];
				Arrays.fill(checkboxes, Boolean.FALSE);
				final List<Boolean> sCheckboxes = Arrays.asList(checkboxes);

				//add checkbox-column to model
				tmProducts.addColumn(sCheckboxes);
			}
			else
			{
				this.getCompareEvent().setEnabled(false);
			}

			boolean scoreColumnEnabled = false;

			//do we have a score column? 
			if (_products instanceof LuceneSearchBufferedList)
			{
				//add score-column to model
				final List scores = ((LuceneSearchBufferedList) _products).getScoreList();
				tmProducts.addColumn(scores);
				scoreColumnEnabled = true;
			}

			//create DataTableAxisModel based on previous DataTableModel
			result = DataTableFactory.createDataTableAxisModel(tmProducts);
			result.setId("productListModel");

			//set title of product and checkbox marker
			result.getXAxis().getMarkerAt(0).setId(TBLCOL_PRODUCT);
			result.getXAxis().getMarkerAt(0).setTitle("name");

			result.getXAxis().getMarkerAt(1).setId(TBLCOL_PRICE);
			result.getXAxis().getMarkerAt(1).setTitle("price");

			int dataTableIndex = 2;
			if (isSelectColumnEnabled())
			{
				//TODO: make checkboxenabled save
				result.getXAxis().getMarkerAt(dataTableIndex).setId(TBLCOL_SELECT);
				result.getXAxis().getMarkerAt(dataTableIndex).setTitle("-");
				dataTableIndex++;
			}

			if (scoreColumnEnabled && result.getXAxis().getMarkerList().size() > dataTableIndex)
			{
				result.getXAxis().getMarkerAt(dataTableIndex).setId(TBLCOL_SCORE);
				result.getXAxis().getMarkerAt(dataTableIndex).setTitle("score");
			}

			if (isClassificationColumnEnabled())
			{
				this.addClassificationColumns(result);
			}
		}

		return result;

	}

	private void addClassificationColumns(final DataTableAxisModel model)
	{
		final List<ClassAttributeAssignmentModel> assignments = getClassAttributeAssignments();
		final int count = getClassificationColumnCount();

		//add each classification attribute as additional marker
		for (int i = 0; i < assignments.size() && i < count; i++)
		{
			final DataTableAxisMarker marker = model.getXAxis().getMarkerById(TBLCOL_PRODUCT).copy();
			marker.setId(TBLCOL_FEATURE_PREFIX + i);
			marker.setCellConverter(new ClassificationCellConverter(assignments.get(i)));
			final ClassificationAttributeModel attrib = assignments.get(i).getClassificationAttribute();
			marker.setTitle(attrib.getName() != null ? attrib.getName() : attrib.getCode());
			model.getXAxis().addMarkerToGroup(TBL_MARKERGROUP_FEATURES, marker);
		}
	}

}
