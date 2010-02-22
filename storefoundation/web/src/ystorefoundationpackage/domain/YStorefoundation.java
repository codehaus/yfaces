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
package ystorefoundationpackage.domain;

import de.hybris.yfaces.YFaces;


/**
 * @author Denny.Strietzbaum
 * 
 */
public class YStorefoundation
{
	public static SfRequestContext getRequestContext()
	{
		return (SfRequestContext) YFaces.getRequestContext();
	}

}
