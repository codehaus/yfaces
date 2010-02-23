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

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;


/**
 * 
 * 
 */
public class ComponentDemoBean2
{
	public CategoryModel getDemoCategory()
	{
		final CategoryModel result = YStorefoundation.getRequestContext().getPlatformServices().getCategoryService().getCategory(
				"HW1200");
		return result;
	}

	public List<ProductModel> getDemoProductList()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final SfSessionContext userSession = reqCtx.getSessionContext();
		final List<ProductModel> result = reqCtx.getProductManagement()
				.findAllByTerm(userSession.getCatalog(), "Canon", null, true);
		return result;
	}

	public Collection<ClassAttributeAssignment> getDemoClassAttributeAssignments()
	{
		final List<ClassAttributeAssignment> result = new ArrayList(JaloBridge.getInstance().findAllAssignmentsByCategory(
				getDemoCategory(), JaloBridge.CLASSIFICATION_ATTR_SEARCHABLE));
		return new ArrayList(result.subList(2, 5));
	}


}
