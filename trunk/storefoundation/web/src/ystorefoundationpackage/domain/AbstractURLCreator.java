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

import java.net.URL;
import java.util.Collections;
import java.util.Map;


/**
 * Abstract Implementation of the {@link URLCreator}
 * 
 * @author Denny.Strietzbaum
 * 
 * @param <T>
 */
public abstract class AbstractURLCreator<T> implements URLCreator<T>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.url.URLCreator#createExternalForm(java.lang.Object, java.util.Map)
	 */
	public abstract String createExternalForm(T resource, Map<String, ?> params);


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.url.URLCreator#createExternalForm(java.lang.Object)
	 */
	public String createExternalForm(final T resource)
	{
		return this.createExternalForm(resource, Collections.EMPTY_MAP);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.sandbox.url.URLCreator#createURL(java.lang.Object, java.util.Map)
	 */
	public URL createURL(final T resource, final Map<String, ?> params)
	{
		URL result = null;
		try
		{
			result = new URL(this.createExternalForm(resource, params));
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.sandbox.url.URLCreator#createURL(java.lang.Object)
	 */
	public URL createURL(final T resource)
	{
		return this.createURL(resource, Collections.EMPTY_MAP);
	}

}
