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
package ystorefoundationpackage.faces;


import java.util.Comparator;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.YFacesException;


/**
 * A {@link Comparator} which expects {@link SelectItem} and compares the label.
 * 
 */
public class CompareSelectItemByLabel implements Comparator<SelectItem>
{

	public int compare(final SelectItem o1, final SelectItem o2)
	{
		final String s1 = o1.getLabel();
		final String s2 = o2.getLabel();

		if (s1 == null || s2 == null)
		{
			throw new YFacesException("Can't compare SelectItems without a label", new NullPointerException());
		}

		return o1.getLabel().compareTo(o2.getLabel());
	}

}
