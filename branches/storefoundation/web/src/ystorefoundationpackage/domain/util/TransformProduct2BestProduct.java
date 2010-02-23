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
package ystorefoundationpackage.domain.util;

import de.hybris.platform.core.model.product.ProductModel;

import org.apache.commons.collections.Transformer;

import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 * 
 */
public class TransformProduct2BestProduct implements Transformer
{

	public ProductModel transform(final Object input)
	{
		final ProductManagement pm = YStorefoundation.getRequestContext().getProductManagement();
		final ProductModel best = pm.getCheapestVariant((ProductModel) input);
		return best;
	}

}
