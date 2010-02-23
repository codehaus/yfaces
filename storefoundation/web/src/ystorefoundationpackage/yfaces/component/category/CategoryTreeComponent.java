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
package ystorefoundationpackage.yfaces.component.category;

import de.hybris.platform.category.model.CategoryModel;

import org.apache.myfaces.custom.tree2.TreeModel;
import org.codehaus.yfaces.component.YModel;



/**
 * This component renders all categories of the selected catalog in the tree mode.
 */
public interface CategoryTreeComponent extends YModel
{
	CategoryModel getCategory();

	void setCategory(CategoryModel category);

	TreeModel getCategoryTree();
}
