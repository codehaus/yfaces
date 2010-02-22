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
 * Manages available {@link ObjectConverter} instances.<br/>
 * Converters may be retrieved by ID or by class.<br/>
 * 
 */
public abstract class ConverterFactory
{
	/**
	 * Returns a {@link ObjectConverter} according the passed class.
	 * 
	 * @param <T>
	 *           type of item
	 * @param clazz
	 *           class for converter lookup
	 * @return {@link ObjectConverter}
	 */
	public abstract <T extends Object> ObjectConverter<T> createConverter(Class<T> clazz);

	/**
	 * Returns a {@link ObjectConverter} according the passed id.
	 * 
	 * @param <T>
	 *           type of item
	 * @param converterId
	 *           id for converter lookup
	 * @return {@link ObjectConverter}
	 */
	public abstract <T extends Object> ObjectConverter<T> createConverter(String converterId);


	/**
	 * Calls {@link ConverterFactory#createConverter(Class)} and uses the class of the passed Item.
	 * 
	 * @param <T>
	 *           type of item
	 * @param item
	 *           Item whose class is used for converter lookup
	 * @return {@link ObjectConverter}
	 */
	public <T extends Object> ObjectConverter<T> createConverter(final T item)
	{
		return createConverter((Class<T>) item.getClass());
	}

	/**
	 * Stateless conversion from Item to ID.
	 * 
	 * @return converted Item as ID String
	 */
	public <T extends Object> String convertObjectToId(final T object)
	{
		return createConverter((Class<T>) object.getClass()).convertObjectToID(object);
	}

	/**
	 * Stateless conversion from ID to Item.
	 * 
	 * @param <T>
	 *           Type of resultitem
	 * @param clazz
	 *           expected Itemclass
	 * @param id
	 *           ID
	 * @return Item
	 */
	public <T extends Object> T convertIdToObject(final Class<T> clazz, final String id)
	{
		return createConverter(clazz).convertIDToObject(id);
	}


}
