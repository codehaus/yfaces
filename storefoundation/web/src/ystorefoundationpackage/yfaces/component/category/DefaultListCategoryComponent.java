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

import java.util.List;

import org.codehaus.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>ListCategoryComponent</code> interface.
 */
public class DefaultListCategoryComponent extends AbstractYComponent implements ListCategoryComponent
{

	private static final long serialVersionUID = 4699534144233667854L;

	private List<CategoryModel> categoryList = null;

	@Override
	public void validate()
	{
		if (this.categoryList == null)
		{
			CategoryModel categoryBean = YStorefoundation.getRequestContext().getSessionContext().getCategory();
			if (categoryBean == null)
			{
				categoryBean = YStorefoundation.getRequestContext().getDefaultValues().getDefaultCategory();
			}
			this.categoryList = YStorefoundation.getRequestContext().getProductManagement().getFilteredCategories(
					categoryBean.getCategories());
		}
	}


	public List<CategoryModel> getCategoryList()
	{
		return this.categoryList;
	}

	public void setCategoryList(final List<CategoryModel> categoryList)
	{
		this.categoryList = categoryList;
	}

}
