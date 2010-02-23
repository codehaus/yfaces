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
 * Stateful bidirectional converter for object <-> string.<br/> <br/> This converter manages both endings, the object
 * and the string.<br/> Each end can be given either via {@link #setConvertedObject(Object)} or
 * {@link #setConvertedString(String)}.<br/> <br/> At least one end must be given.<br/> In that case the other end is
 * calculated via an internal conversion mechanism.<br/> The conversion takes place lazily when the other end is
 * requested either via {@link #getConvertedObject()} or {@link #getConvertedString()}.<br/> <br/> When both ends are
 * given, no conversion takes place.<br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface ObjectConverter<T>
{
	/**
	 * Sets a String representation and returns the appropriate Object.
	 * 
	 * @param id
	 *           String representation of the requested object
	 * @return object which matches the id
	 */
	public T convertIDToObject(String id);

	/**
	 * Sets a Object representation and returns the appropriate String.
	 * 
	 * @param object
	 *           Object representation of the requested String
	 * @return String which matches the object
	 */
	public String convertObjectToID(T object);

	/**
	 * Returns the object representation.<br/>
	 * 
	 * @return object
	 */
	public T getConvertedObject();

	/**
	 * Sets the object representation.<br/> This one is used to calculate the String representation (when no one was
	 * set).<br/>
	 * 
	 * @param object
	 *           object to set
	 */
	public void setConvertedObject(T object);

	/**
	 * Returns the String representation.
	 * 
	 * @return string
	 */
	public String getConvertedString();

	/**
	 * Sets the String representation.<br/> This one is used to calculate the object representation (when no one was
	 * set)<br/>
	 * 
	 * @param string
	 *           string to set
	 */
	public void setConvertedString(String string);

	/**
	 * Sets various properties.<br/> Whether this is relevant and which properties are evaluated must be looked up at the
	 * concrete implementation.
	 * 
	 * @param properties
	 *           properties to set.
	 */
	public void setProperties(Map properties);

}
