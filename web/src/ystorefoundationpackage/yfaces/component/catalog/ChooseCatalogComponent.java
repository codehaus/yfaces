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

import java.util.List;

import org.codehaus.yfaces.component.YModel;


/**
 * This component makes it possible for the user to change the catalog.
 */
public interface ChooseCatalogComponent extends YModel
{

	//no event for choose catalog component

	//model
	List<CatalogModel> getCatalogs();

	void setCatalogs(List<CatalogModel> catalogs);

	CatalogModel getCurrentCatalog();

	void setCurrentCatalog(CatalogModel catalog);

}
