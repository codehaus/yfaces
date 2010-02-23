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
package ystorefoundationpackage.datatable.ext.cell;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;

import java.util.List;
import java.util.Map;

import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;


/**
 * 
 */
public class ClassificationCellConverter implements DataTableCellConverter<Object>
{
	public static final boolean FEATURREQUEST_BY_API = true;
	private ClassificationAttributeModel classAttr = null;

	/**
	 * Constructs a {@link ClassificationCellConverter} and decides according the class of the Parameter how the
	 * {@link ClassificationAttributeModel} is given.<br/>
	 * 
	 * @param param
	 *           The ClassificationAttribute, either directly or indirectly
	 */
	public ClassificationCellConverter(final ClassificationAttributeModel param)
	{
		this.classAttr = param;
	}

	public ClassificationCellConverter(final ClassAttributeAssignmentModel param)
	{
		this.classAttr = param.getClassificationAttribute();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datatable.DataTableCellConverter#getConvertedCellValue(java.lang.Object)
	 */
	public Object getConvertedCellValue(final Object cellvalue)
	{
		if (!(cellvalue instanceof ProductModel))
		{
			throw new UnsupportedOperationException("Cellvalue must be of type Productmodel");
		}
		final ProductModel product = (ProductModel) cellvalue;
		final String result = FEATURREQUEST_BY_API ? getValueByFeatureAPI(product) : getValueByQuery(product);

		return result;
	}

	/**
	 * Doesn't care of typed or untyped features.
	 * 
	 * @param item
	 */
	private String getValueByQuery(final ProductModel item)
	{
		final String classAttrPk = this.classAttr.getPk().toString();
		//build query to get available attributevalues ... 
		final String query = "SELECT {f.stringvalue}" + "FROM {productfeature as f " + "JOIN classattributeassignment as a "
				+ "ON {f.classificationattributeassignment} = {a.PK} " + "AND {a.classificationattribute} = " + classAttrPk + " "
				+ "AND {f.product} = " + item.getPk().getLongValue() + " " +
				//			"JOIN CategoryProductRelation as r " +
				//			"ON {r.source} IN (" + StringUtils.join(((LazyLoadItemList)catg.getAllSubcategories()).getPKList().iterator(), ',') +	") " +
				//			"AND {r.target} = {f.product}" +
				" } ";
		final List searchResult = FlexibleSearch.getInstance().search(query, Object.class).getResult();
		final String result = (searchResult.isEmpty()) ? " " : searchResult.iterator().next().toString();
		return result;
	}

	private String getValueByFeatureAPI(final ProductModel product)
	{
		final String key = "FEATURES " + product.getPk().toString();
		final Map map = YStorefoundation.getRequestContext().getAttributes();
		ProductFeatures features = (ProductFeatures) map.get(key);
		if (features == null)
		{
			features = YStorefoundation.getRequestContext().getProductManagement().getFeatures(product);
			map.put(key, features);
		}

		final FormattedAttribute attribute = features.getAttributesMap().get(this.classAttr.getCode());
		final String result = attribute != null ? attribute.getValue() : null;
		return result;
	}


}
