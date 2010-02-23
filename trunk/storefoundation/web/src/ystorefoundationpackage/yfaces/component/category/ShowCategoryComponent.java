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

import org.codehaus.yfaces.component.YComponent;

import de.hybris.platform.category.model.CategoryModel;




/**
 * This component renders a single category in detail.
 */
public interface ShowCategoryComponent extends YComponent
{

	//no events needed for ShowCategoryComponent

	//model
	CategoryModel getCategory();

	void setCategory(CategoryModel category);

	void setCategoryCode(String code);

	String getCategoryCode();

	long getAllProductsCount();

}
