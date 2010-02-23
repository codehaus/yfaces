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


import java.util.Collections;
import java.util.Map;

import org.codehaus.yfaces.YFacesException;


/**
 * Creates valid URLs for supported resources.<br/>
 * Similiar to a JNDI lookup, except that this Factory looks up for a registered {@link URLCreator} and requests the
 * creator for a URL.<br/>
 * The {@link URLCreator} itself doesn't do a simple lookup but performs some logic to creaze a valid URL.
 * 
 */
public abstract class URLFactory
{
	/**
	 * Returns a {@link URLCreator} which is registered under the passed Class.
	 * 
	 * @param <T>
	 *           type of the creatpr
	 * @param resourceClass
	 *           class-lookup of the requested creator
	 * @return {@link URLCreator}
	 */
	public abstract <T> URLCreator<T> getURLCreator(Class<? extends T> resourceClass);

	/**
	 * Returns a {@link URLCreator} which is registered under the passed ID.
	 * 
	 * @param <T>
	 *           type of the creator
	 * @param creatorId
	 *           id-lookup of the requested creator
	 * @return {@link URLCreator}
	 */
	public abstract <T> URLCreator<T> getURLCreator(String creatorId);


	/**
	 * Creates an externalform of the passed Resource. This is just the same as creating a URL and call
	 * {@link java.net.URL#toExternalForm()}
	 * 
	 * @param resource
	 *           the resource which shall be locateable
	 * @return String representation of the appropriate URL
	 */
	public String createExternalForm(final Object resource)
	{
		return this.createExternalForm(resource, Collections.EMPTY_MAP);
	}

	/**
	 * Creates an externalform of the passed Resource. This is just the same as creating a URL and call
	 * {@link java.net.URL#toExternalForm()}
	 * 
	 * @param resource
	 *           the resource which shall be locateable
	 * @param params
	 *           additional parameters
	 * 
	 * @param <T>
	 *           type of the resource
	 * 
	 * @return String representation of the appropriate URL
	 */
	public <T> String createExternalForm(final T resource, final Map<String, ?> params)
	{
		if (resource == null)
		{
			throw new YFacesException("'null' not permited as resource", new NullPointerException());
		}

		final URLCreator<T> creator = this.getURLCreator((Class<T>) resource.getClass());

		if (creator == null)
		{
			throw new YFacesException("No URLCreator found for " + resource + " (" + resource.getClass() + ")");
		}

		return creator.createExternalForm(resource, params);
	}

	/**
	 * Creates an externalform of the passed Resource. This is just the same as creating a URL and call
	 * {@link java.net.URL#toExternalForm()}
	 * 
	 * @param creatorId
	 *           The id of the creator which shall be used
	 * @param resource
	 *           the resource which shall be locateable
	 * @return String representation of the appropriate URL
	 */
	public String createExternalForm(final String creatorId, final Object resource)
	{
		return this.createExternalForm(creatorId, resource, Collections.EMPTY_MAP);
	}

	/**
	 * Creates an externalform of the passed Resource. This is just the same as creating a URL and call
	 * {@link java.net.URL#toExternalForm()}
	 * 
	 * @param creatorId
	 *           The id of the creator which shall be used
	 * @param resource
	 *           the resource which shall be locateable
	 * @param params
	 *           additional parameters
	 * 
	 * @param <T>
	 *           type of the resource
	 * 
	 * @return String representation of the appropriate URL
	 */
	public <T> String createExternalForm(final String creatorId, final T resource, final Map<String, ?> params)
	{
		if (resource == null)
		{
			throw new YFacesException("'null' not permited as resource", new NullPointerException());
		}

		final URLCreator<T> creator = this.getURLCreator(creatorId);

		if (creator == null)
		{
			throw new YFacesException("No URLCreator found for " + resource + " (" + resource.getClass() + ")");
		}

		return creator.createExternalForm(resource, params);

	}


}
