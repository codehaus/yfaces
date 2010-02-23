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

import java.util.List;

import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YModelBinding;
import org.codehaus.yfaces.component.YEvent;

import ystorefoundationpackage.domain.impl.LuceneByTermProductFinder;
import ystorefoundationpackage.yfaces.component.search.DefaultQuickSearchComponent;
import ystorefoundationpackage.yfaces.component.search.QuickSearchComponent;
import ystorefoundationpackage.yfaces.component.search.DefaultQuickSearchComponent.QuickSearchEvent;


/**
 * 
 * 
 */
public class ComponentDemoBean4 extends AbstractYFrame
{
	private YModelBinding<QuickSearchComponent> quickSearchCmp1 = null;
	private YModelBinding<QuickSearchComponent> quickSearchCmp2 = null;

	public static class CustomQuickSearchEvent2 extends QuickSearchEvent
	{
		@Override
		public void actionListener(final YEvent<QuickSearchComponent> event)
		{
			final QuickSearchComponent cmp = event.getComponent();
			final LuceneByTermProductFinder ps = new LuceneByTermProductFinder();

			final List<ProductModel> productList = ps.findAllByTerm("ystorefoundation", cmp.getSearchTerm(), null, true);
			cmp.setSearchResultList(productList);
		}
	}

	public ComponentDemoBean4()
	{
		super();
		//first component is the standard one
		this.quickSearchCmp1 = super.createComponentBinding((QuickSearchComponent) new DefaultQuickSearchComponent());

		//second component is the standard one but with some modified initial values
		final QuickSearchComponent cmp2 = new DefaultQuickSearchComponent();
		cmp2.getSearchEvent().setListener(new CustomQuickSearchEvent2());
		this.quickSearchCmp2 = super.createComponentBinding(cmp2);
	}


	public YModelBinding<QuickSearchComponent> getQuickSearchComponent1()
	{
		return this.quickSearchCmp1;
	}

	public YModelBinding<QuickSearchComponent> getQuickSearchComponent2()
	{
		return this.quickSearchCmp2;
	}


}
