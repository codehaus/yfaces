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
package ystorefoundationpackage.domain.impl;


import java.io.Serializable;

import ystorefoundationpackage.domain.FormattedAttribute;


/**
 * 
 */
public class FormattedAttributeImpl implements FormattedAttribute, Serializable
{
	private String id = null;
	private String name = null;
	private String value = null;

	public FormattedAttributeImpl(final String id, final String name, final String value)
	{
		this.id = id;
		this.name = name;
		this.value = value;
	}

	public String getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.name;
	}

	public String getValue()
	{
		return this.value;
	}

}