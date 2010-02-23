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
import java.util.Map;


/**
 * Creates locators for any kind of resources.
 * 
 * @param <T>
 *           The type of the Resource
 * 
 * @author Denny.Strietzbaum
 */
public interface URLCreator<T>
{
	/**
	 * Creates a URL for the given resource.
	 * 
	 * @param resource
	 *           Resource which shall be located via a URL
	 * @param params
	 *           additional params
	 * 
	 * @return URL
	 */
	public URL createURL(T resource, Map<String, ?> params);

	/**
	 * Creates a URL for the given resource.
	 * 
	 * @param resource
	 *           Resource which shall be located via a URL
	 * 
	 * @return URL
	 */
	public URL createURL(T resource);

	/**
	 * Creates an external URL representation. This is equal to create a URL and call {@link URL#toExternalForm()}.
	 * 
	 * @param resource
	 *           Resource which shall be located via a URL
	 * @param params
	 *           additional Paramaters
	 * 
	 * @return String
	 */
	public String createExternalForm(T resource, Map<String, ?> params);

	/**
	 * Creates an external URL representation. This is equal to create a URL and call {@link URL#toExternalForm()}.
	 * 
	 * @param resource
	 *           Resource which shall be located via a URL
	 * 
	 * @return String
	 */
	public String createExternalForm(T resource);

}
