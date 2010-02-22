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
package ystorefoundationpackage.yfaces.component.catalog;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.List;

import ystorefoundationpackage.domain.YStorefoundation;




/**
 * Implementation of the <code>ChooseCatalogComponent</code> interface.
 */
public class DefaultChooseCatalogComponent extends AbstractYComponent implements ChooseCatalogComponent
{

	private static final long serialVersionUID = 7066003570353102836L;

	private List<CatalogModel> catalogs = null;
	private CatalogModel currentCatalog = null;

	@Override
	public void validate()
	{
		if (this.catalogs == null)
		{
			//this.catalogs = Webfoundation.getInstance().getServices().getCatalogService().findAll();
			this.catalogs = YStorefoundation.getRequestContext().getDefaultValues().getDefaultCatalogs();
		}
		if (this.currentCatalog == null)
		{
			this.currentCatalog = YStorefoundation.getRequestContext().getSessionContext().getCatalog();
		}
	}

	@Override
	public void refresh()
	{
		this.currentCatalog = YStorefoundation.getRequestContext().getSessionContext().getCatalog();
	}

	public List<CatalogModel> getCatalogs()
	{
		return this.catalogs;
	}

	public void setCatalogs(final List<CatalogModel> catalogs)
	{
		this.catalogs = catalogs;
	}

	public CatalogModel getCurrentCatalog()
	{
		return this.currentCatalog;
	}

	public void setCurrentCatalog(final CatalogModel currentCatalog)
	{
		this.currentCatalog = currentCatalog;
	}

}
