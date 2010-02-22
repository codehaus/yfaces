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
package demo;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 * 
 */
public class ComponentDemoBean1
{
	public ProductModel getDemoProduct()
	{
		ProductModel result = null;
		try
		{
			result = YStorefoundation.getRequestContext().getPlatformServices().getProductService().getProduct("HW1100-0025");
		}
		catch (final AmbiguousIdentifierException e)
		{
			//ignore if more products can be found
		}
		return result;
	}

}
