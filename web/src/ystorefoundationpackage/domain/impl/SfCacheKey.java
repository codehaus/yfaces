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

import de.hybris.platform.cache2.ObjectCreator;
import de.hybris.platform.cache2.ObjectKey;

import org.apache.log4j.Logger;


/**
 * A special cache key which is used with an {@link de.hybris.platform.cache2.ObjectCacheManager} This cache key
 * implements {@link ObjectKey} as well as {@link ObjectCreator}.
 * 
 * @param <K>
 * @param <V>
 */
public abstract class SfCacheKey<K, V> implements ObjectKey<V>, ObjectCreator<V>
{

	private K param = null;
	private Logger log = null; // NOPMD
	private String classSignature = null;
	private String paramSignature = null;

	private String signature = null;

	public SfCacheKey(final K key)
	{
		this(key, null);
	}

	public SfCacheKey(final K param, final Logger log)
	{
		this.param = param;
		this.log = log;

		this.classSignature = this.getClass().getName();
		this.paramSignature = String.valueOf(param.hashCode());


		this.signature = classSignature + paramSignature;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cache2.ObjectCreator#createObject()
	 * 
	 * This call is only used for logging and delegates to an abstract method which is responsible for object creation.
	 */
	public V createObject()
	{
		if (log != null && log.isDebugEnabled())
		{
			log.debug("Create value for " + this.paramSignature);
		}
		return this.createObject(param);
	}

	/**
	 * Creates the object based on the passed parameter
	 * 
	 * @param param
	 *           used for object creation
	 * @return Object
	 */
	public abstract V createObject(K param);


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cache2.ObjectKey#getObjectCreator()
	 */
	public ObjectCreator<V> getObjectCreator()
	{
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cache2.ObjectKey#getExpired()
	 */
	public boolean getExpired() // NOPMD 
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.cache2.ObjectKey#getSignature()
	 */
	public String getSignature()
	{
		//the signature actually is used as key for the underlying cache implementation
		return this.signature;
	}


}
