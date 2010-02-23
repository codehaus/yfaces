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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.validator.GenericValidator;

import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 */
public class FlexibleByTermProductFinder implements ProductManagement.ByTermProductFinder
{

	@Override
	public List<ProductModel> findAllByTerm(final CatalogModel catalog, final String term, String orderBy,
			final boolean isAscending)
	{
		final PlatformServices ps = YStorefoundation.getRequestContext().getPlatformServices();

		if (orderBy == null)
		{
			orderBy = Product.NAME;
		}
		final Map<String, Object> params = new HashMap<String, Object>();
		final String productTypeCode = ps.getTypeService().getComposedType(ProductModel.class).getCode();

		String query = "SELECT {p:" + Item.PK + "} ";
		query += "FROM {" + productTypeCode + " as p } ";
		query += "WHERE {p:" + Item.TYPE + "} NOT IN ( ?variantTypes ) ";

		final ComposedTypeModel variantType = ps.getTypeService().getComposedType(VariantProductModel.class);
		final List<ComposedTypeModel> ignoreTypes = new ArrayList();
		ignoreTypes.add(variantType);
		ignoreTypes.addAll(variantType.getAllSubTypes());

		params.put("variantTypes", ignoreTypes);

		// add where condition to match the search term
		final boolean empty = GenericValidator.isBlankOrNull(term);
		if (!empty)
		{
			query += " AND LOWER( CASE WHEN {" + Product.NAME + "} IS NULL THEN 'ZZZZ' ELSE {" + Product.NAME
					+ "} END ) like ?term OR  " + "LOWER( {" + Product.DESCRIPTION + "} ) like ?term ";
			params.put("term", "%" + term.toLowerCase() + "%");
		}
		// add orderby criteria and direction ( use PK as fallback if nothing is specified )
		if (Product.NAME.equalsIgnoreCase(orderBy)) // special case name which may be null
		{
			query += " ORDER BY CASE WHEN {" + Product.NAME + "} IS NULL THEN 'ZZZZ' ELSE {" + Product.NAME + "} END ";
		}
		else
		{
			query += " ORDER BY {p:" + (orderBy != null ? orderBy : Item.PK) + "} ";
		}
		if (!isAscending)
		{
			query += " DESC ";
		}
		else
		{
			query += " ASC ";
		}

		final CatalogVersionModel cvm = ps.getCatalogService().getSessionCatalogVersion(catalog.getId());
		final FlexibleSearchQuery fquery = new FlexibleSearchQuery(query, params);
		fquery.setCatalogVersions(Collections.singletonList(cvm));
		final List result = ps.getFlexibleSearchService().search(fquery).getResult();
		return result;
	}


}
