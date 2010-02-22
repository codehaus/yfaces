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
package ystorefoundationpackage.yfaces.component.misc;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.yfaces.component.YComponent;

import java.util.List;



/**
 * This component displays the navigation path to a given value. (Another meaning may be "click-path").
 */
public interface BreadcrumbComponent extends YComponent
{

	/**
	 * Returns the value which is used for breadcrumb creation.
	 * 
	 * @return breadcrumb value
	 */
	Object getLeaf();

	/**
	 * Sets the value which is used for breadcrumb creation
	 * 
	 * @param value
	 *           value to set.
	 */
	void setLeaf(Object value);

	boolean isLinksEnabled();

	void setLinksEnabled(boolean enabled);

	/**
	 * @return true when multiple navigation paths are supported.
	 */
	boolean isMultiBreadcrumbEnabled();

	/**
	 * @param enabled
	 *           true when multiple navigation paths shall be supported
	 */
	void setMultiBreadcrumbEnabled(boolean enabled);


	//	/**
	//	 * Returns a one ore more (depends on whether multiple breadcrumbs is enabled)
	//	 * Lists of {@link BreadcrumbItem}  
	//	 * @return List of Breadcrumb
	//	 */
	//	List<List<Map.Entry<String, String>>> getBreadcrumb();

	List<List<CategoryModel>> getCategoryBreadcrumb();
}
