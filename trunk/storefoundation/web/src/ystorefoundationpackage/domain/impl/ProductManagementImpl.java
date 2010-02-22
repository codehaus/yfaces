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
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.constants.CategoryConstants;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms.model.StoreModel;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.link.Link;
import de.hybris.platform.variants.model.VariantAttributeDescriptorModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import ystorefoundationpackage.StorefoundationConfig;
import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.OrderManagement;
import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.util.CompareEnumerationByCode;
import ystorefoundationpackage.domain.util.list.FlexibleSearchBufferedModelList;


/**
 *
 */
public class ProductManagementImpl extends AbstractDomainService implements ProductManagement
{
	private static final Logger log = Logger.getLogger(ProductManagementImpl.class);

	private class GetCategoryPathKey extends SfCacheKey<CategoryModel, List<List<CategoryModel>>>
	{
		public GetCategoryPathKey(final CategoryModel category)
		{
			super(category, log);
		}

		@Override
		public List<List<CategoryModel>> createObject(final CategoryModel param)
		{
			return ProductManagementImpl.this.getCategoryPathInternalRecursive(param);
		}
	}

	private class GetCheapestVariantKey extends SfCacheKey<ProductModel, ProductModel>
	{
		public GetCheapestVariantKey(final ProductModel rawKey)
		{
			super(rawKey, log);
		}

		@Override
		public ProductModel createObject(final ProductModel param)
		{
			return ProductManagementImpl.this.getCheapestVariantInternal(param);
		}
	}

	private final ByTermProductFinder findAllByTermFlexibleStrategy = new FlexibleByTermProductFinder();
	private final ByTermProductFinder findAllByTermLuceneStrategy = new LuceneByTermProductFinder();
	private String productModelCode = null;

	public void init()
	{
		this.productModelCode = getPlatformServices().getTypeService().getComposedType(ProductModel.class).getCode();
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.domain.ProductManagement#getFilteredCategories(java.util.List)
	 */
	@Override
	public List<CategoryModel> getFilteredCategories(final List<CategoryModel> categories)
	{
		List<CategoryModel> result = categories;

		if (StorefoundationConfig.CATEGORYFILTER_ENABLED.getBoolean())
		{
			if (!categories.isEmpty())
			{
				final boolean isHideClassificationClasses = true;
				result = new ArrayList<CategoryModel>(categories.size());

				//iterate over all Categories...
				for (final CategoryModel cat : categories)
				{
					//final boolean isEmpty = cat.getAllProductsCount() == 0;
					final int productsCount = JaloBridge.getInstance().getAllProductsCount(cat);
					final boolean isEmpty = productsCount == 0;

					//...and add those who're not empty and are not classificationclasses
					if (!isEmpty && !(isHideClassificationClasses && cat instanceof ClassificationClassModel))
					{
						result.add(cat);
					}
				}
			}
		}
		return result;
	}

	public List<List<CategoryModel>> getCategoryPath(final CategoryModel catg)
	{
		getUserSessionCache().fetch(new GetCategoryPathKey(catg));
		final List<List<CategoryModel>> result = (List) getUserSessionCache().fetch(new GetCategoryPathKey(catg));
		return result;
	}


	public List<List<CategoryModel>> getCategoryPath(ProductModel product)
	{
		if (product instanceof VariantProductModel)
		{
			product = ((VariantProductModel) product).getBaseProduct();
		}
		final Collection<CategoryModel> superCategories = product.getSupercategories();

		final List<List<CategoryModel>> result = new ArrayList();
		for (final CategoryModel category : superCategories)
		{
			final List<List<CategoryModel>> path = getCategoryPath(category);
			result.addAll(path);
		}
		return result;
	}


	/**
	 * Internal. Recursive helper.
	 */
	private List<List<CategoryModel>> getCategoryPathInternalRecursive(final CategoryModel catg)
	{
		List<List<CategoryModel>> result = null;

		//get filtered list of supercategories 
		final Collection<CategoryModel> superCats = this.getFilteredCategories(catg.getSupercategories());

		//finish recursive down when no more super cats are available 
		if (superCats.isEmpty())
		{
			//and simply return a single path with a single Category
			result = new ArrayList();
			final List<CategoryModel> subList = new ArrayList<CategoryModel>();
			subList.add(catg);
			result.add(subList);
		}
		else
		{
			result = new ArrayList<List<CategoryModel>>();

			//for each supercategory...
			for (final CategoryModel superCatg : superCats)
			{
				//...request the navigation path (recursive)
				final List<List<CategoryModel>> superCatgPathes = getCategoryPathInternalRecursive(superCatg);

				//...and add the category itself as last one to each available navigation path 
				for (final List<CategoryModel> superCatPath : superCatgPathes)
				{
					superCatPath.add(catg);
				}

				//...and put the recursive result into the current result
				result.addAll(superCatgPathes);
			}
		}

		return result;
	}

	@Override
	public ProductModel getCheapestVariant(final ProductModel product)
	{
		final ProductModel result = (ProductModel) getRequestCache().fetch(new GetCheapestVariantKey(product));
		return result;
	}


	@Override
	public List<ProductModel> findAllByCategory(final CategoryModel category, final boolean includeSubCategories,
			final String sortBy, final boolean sortAsc)
	{
		if (category == null)
		{
			throw new NullPointerException("No category specified");
		}

		// search for products. A CategoryProductRelation must exist where the given category is the source and the product is the target.
		final Map params = new HashMap();
		final StringBuilder query = new StringBuilder();
		query.append("SELECT {p:").append(Item.PK).append("} ");
		//query.append("FROM {").append(TypeManager.getInstance().getComposedType(Product.class).getCode()).append(" AS p ");
		query.append("FROM {").append(this.productModelCode).append(" AS p ");
		query.append("JOIN ").append(CategoryConstants.Relations.CATEGORYPRODUCTRELATION).append(" AS l ");
		query.append("ON {l:").append(Link.TARGET).append("}={p:").append(Item.PK).append("} } ");
		query.append("WHERE ").append("{l:").append(Link.SOURCE).append("}");
		if (includeSubCategories)
		{
			params.put("cat", category);
			query.append(" IN ( ?cat ");
			final Collection<CategoryModel> allSubCategories = category.getAllSubcategories();
			if (!allSubCategories.isEmpty())
			{
				params.put("allSubCategories", allSubCategories);
				query.append(", ?allSubCategories ");
			}
			query.append(") ");
		}
		else
		{
			params.put("cat", category);
			query.append(" = ?cat ");
		}
		// add orderbycriteria and order direction
		if (sortBy != null)
		{
			query.append(" ORDER BY {p:").append(sortBy).append("} ");
			if (!sortAsc)
			{
				query.append(" DESC ");
			}
			else
			{
				query.append(" ASC ");
			}
		}

		//	final List<Product> _result = new FlexibleSearchBufferedList<Product>(query.toString(), params, new Class[]
		//	{ Product.class }, 1000);
		//	final List<ProductModel> result = new ModelWrapperList<ProductModel>(_result);

		final List<ProductModel> result = new FlexibleSearchBufferedModelList<ProductModel>(query.toString(), params, 60);
		return result;
	}



	@Override
	public List<ProductModel> findAllByTerm(final CatalogModel catalog, final String term, final String orderBy,
			final boolean isAscending)
	{
		final StoreModel store = YStorefoundation.getRequestContext().getPlatformServices().getCmsService().getSessionStore();
		final ByTermProductFinder strategy = (store.getLuceneIndex() != null) ? findAllByTermLuceneStrategy
				: findAllByTermFlexibleStrategy;
		final List<ProductModel> result = strategy.findAllByTerm(catalog, term, orderBy, isAscending);
		return result;
	}

	public List<FormattedAttribute> getVariantAttributeInfoList(final ProductModel product)
	{
		List<FormattedAttribute> result = Collections.EMPTY_LIST;
		if (product instanceof VariantProductModel)
		{
			result = new ArrayList<FormattedAttribute>();
			final PlatformServices ps = YStorefoundation.getRequestContext().getPlatformServices();
			final VariantTypeModel typeModel = ((VariantProductModel) product).getBaseProduct().getVariantType();
			for (final VariantAttributeDescriptorModel vad : typeModel.getVariantAttributes())
			{
				//request id
				final String _id = vad.getQualifier();

				//request name (qualifier as fallback)
				final String _name = vad.getName();

				//request value
				Object _value = null;
				try
				{
					_value = JaloBridge.getInstance().getVariantAttribute((VariantProductModel) product, vad.getQualifier());
					if (_value instanceof HybrisEnumValue)
					{
						final EnumerationValueModel ev = ps.getTypeService().getEnumerationValue((HybrisEnumValue) _value);
						_value = ev.getName() != null ? ev.getName() : ev.getCode();
					}
				}
				catch (final Exception e)
				{
					_value = "[" + vad.getName() + "]";
					log.error(e.getMessage());
				}

				result.add(new FormattedAttributeImpl(_id, _name, _value != null ? _value.toString() : ""));
			}
		}
		else
		{
			if (product.getVariantType() != null)
			{
				result = new ArrayList<FormattedAttribute>();
				final VariantTypeModel vt = product.getVariantType();
				for (final VariantAttributeDescriptorModel d : vt.getVariantAttributes())
				{
					result.add(new FormattedAttributeImpl(d.getQualifier(), d.getName(), null));
				}
			}
		}

		return result;
	}


	public Map<EnumerationValueModel, List<ProductReferenceModel>> getAllProductReferences(final ProductModel productModel)
	{
		// sorted result map
		final Map<EnumerationValueModel, List<ProductReferenceModel>> result = new TreeMap<EnumerationValueModel, List<ProductReferenceModel>>(
				new CompareEnumerationByCode());

		// put each available reference under appropriate key
		final Collection<ProductReferenceModel> allReferences = productModel.getProductReferences();
		for (final ProductReferenceModel reference : allReferences)
		{
			final boolean activeReference = reference.getActive().booleanValue();
			if (activeReference)
			{
				final EnumerationValueModel type = getPlatformServices().getTypeService().getEnumerationValue(
						reference.getReferenceType());
				List<ProductReferenceModel> groupedReferences = result.get(type);
				if (groupedReferences == null)
				{
					result.put(type, groupedReferences = new ArrayList<ProductReferenceModel>());
				}
				groupedReferences.add(reference);
			}
		}
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.domain.ProductManagement#getFeatures(de.hybris.platform.core.model.product.ProductModel)
	 */
	@Override
	public ProductFeatures getFeatures(final ProductModel product)
	{
		final ProductFeatures result = new ProductFeaturesImpl(product);
		return result;
	}


	protected ProductModel getCheapestVariantInternal(ProductModel product)
	{
		ProductModel result = product;

		if (product instanceof VariantProductModel)
		{
			product = ((VariantProductModel) product).getBaseProduct();
		}

		if (product.getVariantType() != null)
		{
			final Collection<VariantProductModel> col = product.getVariants();
			result = this.getCheapestVariantInternal(col);
		}

		return result;
	}

	/**
	 * @param col
	 */
	private ProductModel getCheapestVariantInternal(final Collection<VariantProductModel> col)
	{
		final OrderManagement om = YStorefoundation.getRequestContext().getOrderManagement();
		double pMin = Double.MAX_VALUE;
		ProductModel result = null;
		final Iterator<VariantProductModel> ite = col.iterator();
		while (ite.hasNext())
		{
			final ProductModel p = ite.next();
			final Prices pricing = om.getPrices(p);
			if (pricing.isAvailable())
			{
				final double price = pricing.getDefaultPricing().getPriceValue();
				if (price < pMin)
				{
					pMin = price;
					result = p;
				}
			}
		}
		return result;
	}
}
