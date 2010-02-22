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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.List;



/**
 * This components provides the search function.
 */
public interface QuickSearchComponent extends YComponent
{

	public String getSearchTerm();

	public void setSearchTerm(String searchTerm);

	public Boolean getIsSortAscending();

	public void setIsSortAscending(Boolean ascending);

	public String getSortOrder();

	public void setSortOrder(String searchOrder);

	public List<ProductModel> getSearchResultList();

	public void setSearchResultList(List<ProductModel> searchResultList);

	public YComponentEventHandler<QuickSearchComponent> getSearchEvent();

}
