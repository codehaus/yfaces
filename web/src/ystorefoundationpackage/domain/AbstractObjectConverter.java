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


import java.util.Map;


/**
 * Abstract {@link ObjectConverter} base implementation.
 * 
 */
public abstract class AbstractObjectConverter<T> implements ObjectConverter<T>
{
	private T convertedObject = null;
	private String convertedString = null;

	public T convertIDToObject(final String id)
	{
		this.setConvertedString(id);
		this.convertedObject = null;
		return this.getConvertedObject();
	}

	public String convertObjectToID(final T object)
	{
		this.setConvertedObject(object);
		this.convertedString = null;
		return this.getConvertedString();
	}

	public T getConvertedObject()
	{
		if (this.convertedObject == null)
		{
			this.convertedObject = this.convertStringToObject();
		}
		return this.convertedObject;
	}

	public String getConvertedString()
	{
		if (this.convertedString == null)
		{
			this.convertedString = this.convertObjectToString();
		}
		return this.convertedString;
	}

	public void setConvertedObject(final T object)
	{
		this.convertedObject = object;
	}

	public void setConvertedString(final String string)
	{
		this.convertedString = string;
	}

	public void setProperties(final Map properties)
	{
		//nop per default
	}

	/**
	 * Concrete converter logic for String to Object.
	 * 
	 * @return object
	 */
	public abstract T convertStringToObject();

	/**
	 * Concrete converter logic for Object to String.
	 * 
	 * @return String
	 */
	public abstract String convertObjectToString();

}
