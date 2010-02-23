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
package ystorefoundationpackage.domain;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Map;


/**
 *
 */
public interface ProductManagement
{
	/**
	 * Replaceable strategy for multiple installed search services (flexible, lucene etc)
	 */
	interface ByTermProductFinder
	{
		List<ProductModel> findAllByTerm(CatalogModel catalog, String term, String orderBy, boolean isAscending);
	}

	public interface ProductFeatures
	{
		List<String> getFeatureGroups();

		List<FormattedAttribute> getAttributesList();

		Map<String, FormattedAttribute> getAttributesMap();

	}


	/**
	 * Creates a list of possible navigation paths starting from the root category up to all possible parent categories
	 * of the specific product.
	 * 
	 * @param product
	 *           product for which the paths will be built
	 * @return list of navigation paths
	 */
	List<List<CategoryModel>> getCategoryPath(ProductModel product);


	List<List<CategoryModel>> getCategoryPath(CategoryModel catg);

	List<CategoryModel> getFilteredCategories(List<CategoryModel> catgories);

	/**
	 * Finds the cheapest product which is related to the passed one
	 * <p>
	 * Related means, if passed product is:
	 * <p>
	 * a) base product: result is cheapest VariantProdutModel<br/>
	 * b) variant product: result is cheapest VariantProdutModel of base product of passed variant<br/>
	 * c) other products: result is passed product itself <br/>
	 * 
	 * 
	 * @param product
	 *           product to find the best product for
	 * @return found {@link ProductModel}
	 */
	ProductModel getCheapestVariant(ProductModel product);

	/**
	 * Finds all products that match the passed criteria.<br/>
	 * 
	 * @param category
	 *           Product must be located in that category
	 * @param includeSubCategories
	 *           Sub-categories shall be included
	 * @param sortBy
	 *           sort by
	 * @param sortAsc
	 *           sort ascending
	 * @return list of products
	 */
	List<ProductModel> findAllByCategory(CategoryModel category, boolean includeSubCategories, String sortBy, boolean sortAsc);


	/**
	 * Finds all products that match the passed term.<br/>
	 * 
	 * @param term
	 *           term to lookup for
	 * @param orderBy
	 *           order criteria
	 * @param isAscending
	 *           sort ascending
	 * @return list of products
	 */
	List<ProductModel> findAllByTerm(CatalogModel catalog, String term, String orderBy, boolean isAscending);

	/**
	 * Finds available variant attributes for a given product.
	 * <p>
	 * Differs between three kind of products:
	 * <ul>
	 * <li>base product: Each VariantAttributeInfo is a possible attribute of available VariantProducts; 'value' is
	 * always null.</li>
	 * <li>variant product: Each VariantAttributeInfo represents an Attribute and it'S value</li>
	 * <li>normal product: an empty List is returned</li>
	 * </ul>
	 * 
	 * @param product
	 *           {@link ProductModel} to be evaluated
	 * @return list of {@link FormattedAttribute}
	 */
	List<FormattedAttribute> getVariantAttributeInfoList(ProductModel product);

	Map<EnumerationValueModel, List<ProductReferenceModel>> getAllProductReferences(final ProductModel productModel);

	ProductFeatures getFeatures(ProductModel product);

}
