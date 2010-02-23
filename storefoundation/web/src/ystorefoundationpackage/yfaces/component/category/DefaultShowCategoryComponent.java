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

import org.codehaus.yfaces.component.AbstractYComponent;

import de.hybris.platform.category.model.CategoryModel;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;




/**
 * Implementation of the <code>ShowCategoryComponent</code> interface.
 */
public class DefaultShowCategoryComponent extends AbstractYComponent implements ShowCategoryComponent
{

	private static final long serialVersionUID = 5971805762714587607L;

	private CategoryModel categoryBean = null;
	private String categoryCode = null;

	@Override
	public void validate()
	{
		if (this.categoryBean == null)
		{
			if (this.categoryCode != null)
			{
				this.categoryBean = YStorefoundation.getRequestContext().getPlatformServices().getCategoryService().getCategory(
						categoryCode);
			}
			else
			{
				this.categoryBean = YStorefoundation.getRequestContext().getSessionContext().getCategory();
			}
		}

	}


	public CategoryModel getCategory()
	{
		return this.categoryBean;
	}

	public void setCategory(final CategoryModel category)
	{
		this.categoryBean = category;
	}

	public String getCategoryCode()
	{
		return this.categoryCode;
	}


	public void setCategoryCode(final String code)
	{
		this.categoryCode = code;
	}


	public long getAllProductsCount()
	{
		final long result = JaloBridge.getInstance().getAllProductsCount(getCategory());
		return result;
	}

}
