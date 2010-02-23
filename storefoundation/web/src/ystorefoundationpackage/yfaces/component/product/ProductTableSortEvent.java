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
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;

import ystorefoundationpackage.domain.OrderManagement;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;




/**
 * This event gets fired when the user tries to sort the listed products.
 */
public class ProductTableSortEvent extends DefaultYComponentEventListener<ProductTableComponent>
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(ProductTableSortEvent.class);

	private static final Comparator PRICE_COMP_ASC = new Comparator<ProductModel>()
	{
		public int compare(final ProductModel o1, final ProductModel o2)
		{
			final OrderManagement om = YStorefoundation.getRequestContext().getOrderManagement();
			final ProductManagement pm = YStorefoundation.getRequestContext().getProductManagement();
			final Prices pb1 = om.getPrices(pm.getCheapestVariant(o1));
			final Prices pb2 = om.getPrices(pm.getCheapestVariant(o2));
			final double diff = pb1.getDefaultPricing().getPriceValue() - pb2.getDefaultPricing().getPriceValue();

			return diff < 0.0 ? -1 : (diff > 0.0 ? 1 : 0);
		}
	};

	private static final Comparator PRICE_COMP_DESC = new Comparator<ProductModel>()
	{
		public int compare(final ProductModel o1, final ProductModel o2)
		{
			final OrderManagement om = YStorefoundation.getRequestContext().getOrderManagement();
			final ProductManagement pm = YStorefoundation.getRequestContext().getProductManagement();
			final Prices pb1 = om.getPrices(pm.getCheapestVariant(o1));
			final Prices pb2 = om.getPrices(pm.getCheapestVariant(o2));
			final double diff = pb1.getDefaultPricing().getPriceValue() - pb2.getDefaultPricing().getPriceValue();

			return diff < 0.0 ? 1 : (diff > 0.0 ? -1 : 0);
		}
	};


	private String term = null;

	public ProductTableSortEvent(final String term)
	{
		super();
		this.term = term;
	}


	@Override
	public void actionListener(final YComponentEvent<ProductTableComponent> event)
	{
		final ProductTableComponent cmp = event.getComponent();
		List<ProductModel> result = null;
		String sortBy = cmp.getSortColumn();
		final boolean sortAsc = cmp.getSortAscending();

		if ("price".equals(sortBy))
		{
			result = new ArrayList<ProductModel>(cmp.getProductList());
			Collections.sort(result, sortAsc ? PRICE_COMP_ASC : PRICE_COMP_DESC);
		}
		else
		{
			if ("score".equals(sortBy))
			{
				sortBy = null;
			}
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final CatalogModel catalog = reqCtx.getSessionContext().getCatalog();
			result = reqCtx.getProductManagement().findAllByTerm(catalog, this.term, sortBy, cmp.getSortAscending());

		}

		cmp.setProductList(result);
	}


	public String getTerm()
	{
		return term;
	}

	public void setTerm(final String term)
	{
		this.term = term;
	}


}
