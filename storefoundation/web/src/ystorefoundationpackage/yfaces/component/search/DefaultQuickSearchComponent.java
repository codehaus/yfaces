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
package ystorefoundationpackage.yfaces.component.search;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>QuickSearchComponent</code> interface.
 */
public class DefaultQuickSearchComponent extends AbstractYComponent implements QuickSearchComponent
{

	private static final long serialVersionUID = 8712436172983689453L;

	/**
	 * This event gets fired when the user tries to do a search.
	 */
	public static class QuickSearchEvent extends DefaultYComponentEventListener<QuickSearchComponent>
	{

		@Override
		public void actionListener(final YComponentEvent<QuickSearchComponent> event)
		{
			final QuickSearchComponent cmp = event.getComponent();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(DefaultQuickSearchComponent.class.getName(),
					cmp.getSearchTerm());

			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final CatalogModel catalog = reqCtx.getSessionContext().getCatalog();
			final String term = cmp.getSearchTerm();
			final List<ProductModel> productList = reqCtx.getProductManagement().findAllByTerm(catalog, term, null, true);
			cmp.setSearchResultList(productList);
		}

	}


	private String searchTerm = null;
	private Boolean isAscending = null;
	private String sortOrder = null;
	private List<ProductModel> searchResultList = null;

	private YComponentEventHandler<QuickSearchComponent> ehSearch = null;

	public DefaultQuickSearchComponent()
	{
		super();
		this.isAscending = Boolean.TRUE;
		this.sortOrder = "name";
		this.searchResultList = Collections.EMPTY_LIST;
		this.ehSearch = super.createEventHandler(new QuickSearchEvent());
	}

	@Override
	public void validate()
	{
		if (getSearchTerm() == null)
		{
			final String term = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
					DefaultQuickSearchComponent.class.getName());
			setSearchTerm(term != null ? term : "");
		}
	}

	public String getSearchTerm()
	{
		return searchTerm;
	}

	public void setSearchTerm(final String searchTerm)
	{
		this.searchTerm = searchTerm;
	}

	public Boolean getIsSortAscending()
	{
		return this.isAscending;
	}

	public String getSortOrder()
	{
		return this.sortOrder;
	}

	public List<ProductModel> getSearchResultList()
	{
		return this.searchResultList;
	}

	public void setIsSortAscending(final Boolean ascending)
	{
		this.isAscending = ascending;
	}

	public void setSortOrder(final String searchOrder)
	{
		this.sortOrder = searchOrder;
	}

	public void setSearchResultList(final List<ProductModel> searchResultList)
	{
		this.searchResultList = searchResultList;
	}

	public YComponentEventHandler<QuickSearchComponent> getSearchEvent()
	{
		return this.ehSearch;
	}


}
