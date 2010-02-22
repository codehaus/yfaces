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
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

import java.util.List;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.product.fv.FeatureValueSelectorComponent;
import ystorefoundationpackage.yfaces.component.search.QuickSearchComponent;


public class ComponentDemoBean3 extends AbstractYFrame
{
	private YComponentBinding<FeatureValueSelectorComponent> fvSelectorCmp = null;
	private YComponentBinding<QuickSearchComponent> quickSearchCmp = null;

	public ComponentDemoBean3()
	{
		super();
		this.fvSelectorCmp = super.createComponentBinding();
		this.quickSearchCmp = super.createComponentBinding();
	}

	public YComponentBinding<FeatureValueSelectorComponent> getFeatureValueSelectorComponent()
	{
		return this.fvSelectorCmp;
	}

	public YComponentBinding<QuickSearchComponent> getQuickSearchComponent()
	{
		return this.quickSearchCmp;
	}





	public List<ProductModel> getDemoProductList()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final List<ProductModel> result = reqCtx.getProductManagement().findAllByTerm(reqCtx.getSessionContext().getCatalog(),
				"Canon", null, true);
		return result;
	}


}
