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

import de.hybris.platform.core.model.enumeration.EnumerationValueModel;

import java.util.Comparator;


/**
 *
 */
public class CompareEnumerationByCode implements Comparator<EnumerationValueModel>
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(final EnumerationValueModel o1, final EnumerationValueModel o2)
	{
		return o1.getCode().compareTo(o2.getCode());
	}

}
