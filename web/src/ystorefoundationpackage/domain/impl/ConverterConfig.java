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

import ystorefoundationpackage.domain.ObjectConverter;



/**
 * Single configuration element for {@link ConverterFactoryImpl} Summarizes all possible configurable parameters into
 * one class.<br/>
 * <br/>
 * There is no need to use this.<br/>
 * However when using configuration frameworks like Spring this class makes life much easier and gives a much more
 * readable configuration file.
 * 
 */
public class ConverterConfig
{
	private Class<ObjectConverter> converterClass = null;
	private String id = null;
	private Class forClass = null;

	public Class<ObjectConverter> getConverterClass()
	{
		return converterClass;
	}

	public void setConverterClass(final Class<ObjectConverter> converter)
	{
		this.converterClass = converter;
	}

	public String getId()
	{
		return id;
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	public Class getForClass()
	{
		return forClass;
	}

	public void setForClass(final Class forClass)
	{
		this.forClass = forClass;
	}




}
