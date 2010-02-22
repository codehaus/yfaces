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


/**
 * @author Denny.Strietzbaum
 * 
 */
public class CompareCatalogByName implements Comparator<CatalogModel>
{
	public int compare(final CatalogModel o1, final CatalogModel o2)
	{
		String name1 = o1.getName();
		if (name1 == null)
		{
			name1 = o1.getId();
		}

		String name2 = o2.getName();
		if (name2 == null)
		{
			name2 = o2.getId();
		}

		return name1.compareTo(name2);
	}

}
