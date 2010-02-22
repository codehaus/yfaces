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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>ProductsQuickViewComponent</code> interface.
 */
public class DefaultProductsQuickViewComponent extends AbstractYComponent implements ProductsQuickViewComponent
{

	private static final long serialVersionUID = 318822258711448813L;

	private static final Logger log = Logger.getLogger(DefaultProductsQuickViewComponent.class);

	private List<ProductModel> products = null;
	private String categoryCode = "topseller";
	private String catalogCode = null;

	private CategoryModel catg = null;
	private CatalogModel catalog = null;

	private String title = null;

	private int productCount = 3;
	private int rows = -1;
	private int columns = 1;

	public void setTitle(final String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return this.title;
	}


	public void setCategoryCode(final String catg)
	{
		this.categoryCode = catg;
	}

	public void setCategory(final CategoryModel catg)
	{
		this.catg = catg;
	}

	public void setCatalog(final CatalogModel catalog)
	{
		this.catalog = catalog;
	}

	public void setCatalogCode(final String catalogCode)
	{
		this.catalogCode = catalogCode;
	}

	public int getProductCount()
	{
		return this.productCount;
	}


	public void setProductCount(final int count)
	{
		this.productCount = count;
	}

	public List<ProductModel> getProductList()
	{
		return this.products;
	}

	public void setProductList(final List<ProductModel> products)
	{
		this.products = products;
	}


	@Override
	public void validate()
	{
		if (this.products == null)
		{
			this.products = createDefaultProductList();
		}
	}


	public DataTableAxisModel getProductTable()
	{
		final List pl = getProductList();
		if (pl == null)
		{
			return null;
		}


		DataTableAxisModel dataTableModel = null;

		final int rows = getRows();
		final int columns = getColumns();
		dataTableModel = DataTableFactory.getDataTableModel(pl, rows, columns, false);
		if (dataTableModel != null)
		{
			dataTableModel.setId("productsQuickViewComponent");
		}
		return dataTableModel;
	}


	private List<ProductModel> createDefaultProductList()
	{
		CategoryModel category = this.catg;
		List<ProductModel> result = Collections.EMPTY_LIST;
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		if (category == null)
		{
			if (this.catalog == null && this.catalogCode != null)
			{
				this.catalog = reqCtx.getPlatformServices().getCatalogService().getCatalog(catalogCode);
				if (this.catalog == null)
				{
					log.error("No catalog with code '" + this.catalogCode + "' found");
				}
			}
			else
			{
				this.catalog = reqCtx.getSessionContext().getCatalog();
			}

			final CatalogVersionModel catVer = reqCtx.getPlatformServices().getCatalogService().getSessionCatalogVersion(
					catalog.getId());
			try
			{
				category = reqCtx.getPlatformServices().getCategoryService().getCategory(catVer, categoryCode);
			}
			catch (final SystemException e)
			{
				log.warn("Error fetching category '" + categoryCode + "'");
				this.title = "category " + categoryCode + " not found";
			}
		}

		if (category != null)
		{
			this.title = category.getName();

			final List<ProductModel> products = reqCtx.getProductManagement().findAllByCategory(category, false, null, true);

			result = this.selectRandomProducts(products, getProductCount());

		}
		return result;

	}

	public int getRows()
	{
		return this.rows;
	}

	public void setRows(final int rows)
	{
		this.rows = rows;
	}

	public int getColumns()
	{
		return this.columns;
	}

	public void setColumns(final int columns)
	{
		this.columns = columns;
	}

	private List<ProductModel> selectRandomProducts(final List<ProductModel> products, final int count)
	{
		List<ProductModel> result = null;

		final int size = products.size();

		if (count >= size)
		{
			result = products;
		}
		else
		{
			result = new ArrayList<ProductModel>();
			while (result.size() < count)
			{
				ProductModel p = products.get((int) (size * Math.random()));

				// never show variants, always use base products
				if (p instanceof VariantProductModel)
				{
					p = ((VariantProductModel) p).getBaseProduct();
				}

				// never show the same product twice
				if (!result.contains(p))
				{
					result.add(p);
				}
			}
		}
		return result;
	}

}
