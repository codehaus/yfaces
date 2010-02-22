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
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.Collections;
import java.util.List;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>BreadcrumbComponent</code> interface.
 */
public class DefaultBreadcrumbComponent extends AbstractYComponent implements BreadcrumbComponent
{
	private Object value = null;
	private boolean linksEnabled = true;
	private boolean isMultiBreadEnabled = true;

	private transient List<List<CategoryModel>> catgBreadcrumb = null;

	@Override
	public void validate()
	{
		if (getLeaf() == null)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			value = userSession.getProduct();
			if (value == null)
			{
				value = userSession.getCategory();
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#getValue()
	 */
	public Object getLeaf()
	{
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#isLinksEnabled()
	 */
	public boolean isLinksEnabled()
	{
		return this.linksEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#isMultiBreadcrumbEnabled()
	 */
	public boolean isMultiBreadcrumbEnabled()
	{
		return this.isMultiBreadEnabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#setLinksEnabled(boolean)
	 */
	public void setLinksEnabled(final boolean enabled)
	{
		this.linksEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#setMultiBreadcrumbEnabled(boolean)
	 */
	public void setMultiBreadcrumbEnabled(final boolean enabled)
	{
		this.isMultiBreadEnabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.misc.BreadcrumbComponent#setValue(java.lang.Object)
	 */
	public void setLeaf(final Object value)
	{
		this.value = value;
	}


	public List<List<CategoryModel>> getCategoryBreadcrumb()
	{
		if (this.catgBreadcrumb == null)
		{
			if (this.value != null)
			{
				if (this.value instanceof CategoryModel)
				{
					this.catgBreadcrumb = YStorefoundation.getRequestContext().getProductManagement().getCategoryPath(
							(CategoryModel) value);

				}
				if (this.value instanceof ProductModel)
				{
					this.catgBreadcrumb = YStorefoundation.getRequestContext().getProductManagement().getCategoryPath(
							(ProductModel) value);
				}
			}
			else
			{
				this.catgBreadcrumb = Collections.EMPTY_LIST;
			}
		}
		return this.catgBreadcrumb;
	}





}
