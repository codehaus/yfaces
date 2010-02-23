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
package ystorefoundationpackage.domain.util;

import de.hybris.platform.catalog.model.CatalogModel;

import java.util.Comparator;
import java.util.List;


/**
 * 
 */
public class CompareCatalogByStoreOrder implements Comparator<CatalogModel>
{
	private final List<CatalogModel> storeCatalogs;
	private Comparator alternative = null;

	public CompareCatalogByStoreOrder(final List<CatalogModel> storeCatalogs)
	{
		this.storeCatalogs = storeCatalogs;
		this.alternative = new CompareCatalogByName();
	}

	public int compare(final CatalogModel o1, final CatalogModel o2)
	{
		final int pos1 = this.storeCatalogs.indexOf(o1);
		final int pos2 = this.storeCatalogs.indexOf(o2);
		if (pos1 >= 0 && pos2 >= 0)
		{
			return pos1 - pos2;
		}
		else if (pos1 < 0 && pos2 >= 0)
		{
			return 1; // first on is not in store list -> must be greater
		}
		else if (pos1 >= 0 && pos2 < 0)
		{
			return -1; // second on is not in store list -> must be greater
		}
		else
		{
			return alternative.compare(o1, o2); // if both are not in store list compare by name
		}
	}
}
