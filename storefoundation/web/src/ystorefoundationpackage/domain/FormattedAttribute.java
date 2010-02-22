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

/**
 * Information regarding a VariantAttribute.
 */
public interface FormattedAttribute
{
	/**
	 * Finds the id of the variant attribute.
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * Finds the name of the variant attribute.
	 * 
	 * @return name
	 */
	String getName();

	/**
	 * Finds the value of the variant attribute.
	 * 
	 * @return value
	 */
	String getValue();
}