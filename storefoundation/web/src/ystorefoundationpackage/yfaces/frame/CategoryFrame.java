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
package ystorefoundationpackage.yfaces.frame;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.category.ListCategoryComponent;
import ystorefoundationpackage.yfaces.component.misc.BreadcrumbComponent;
import ystorefoundationpackage.yfaces.component.misc.DefaultBreadcrumbComponent;
import ystorefoundationpackage.yfaces.component.product.ProductTableComponent;
import ystorefoundationpackage.yfaces.component.product.fv.FeatureValueSelectorComponent;


/**
 * Renders the specific category.
 * 
 */
public class CategoryFrame extends AbstractYFrame
{

	private static final long serialVersionUID = -7278066698207691495L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CategoryFrame.class);

	private final YComponentBinding<FeatureValueSelectorComponent> fvCmp = super.createComponentBinding();
	private final YComponentBinding<ListCategoryComponent> listCatgCmp = super.createComponentBinding();
	private final YComponentBinding<ProductTableComponent> productTableCmp = super.createComponentBinding();
	private final YComponentBinding<BreadcrumbComponent> breadcrumbCmp = super.createComponentBinding(this
			.createBreadcrumbComponent());

	private BreadcrumbComponent createBreadcrumbComponent()
	{
		final BreadcrumbComponent cmp = new DefaultBreadcrumbComponent();
		cmp.setLeaf(YStorefoundation.getRequestContext().getSessionContext().getCategory());
		return cmp;
	}

	public YComponentBinding<FeatureValueSelectorComponent> getFeatureValueSelectorComponent()
	{
		return this.fvCmp;
	}

	public YComponentBinding<ListCategoryComponent> getListCategoryComponent()
	{
		return this.listCatgCmp;
	}

	public YComponentBinding<ProductTableComponent> getProductTableComponent()
	{
		return this.productTableCmp;
	}

	public YComponentBinding<BreadcrumbComponent> getBreadcrumbComponent()
	{
		return this.breadcrumbCmp;
	}

	public String getCategoryDescription()
	{
		return YStorefoundation.getRequestContext().getSessionContext().getCategory().getDescription();
	}

}
