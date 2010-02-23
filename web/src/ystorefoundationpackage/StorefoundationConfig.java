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
package ystorefoundationpackage;

import de.hybris.platform.util.Config;


/**
 * Quick access to various configuration properties.
 */
public enum StorefoundationConfig
{
	/** Enables some developer only options like debug query parameters */
	IS_DEVELOPERMODE("storefoundation.developermode.enabled"),

	XSD_SOURCE("storefoundation.developermode.xsd.source"), //

	XSD_TARGET("storefoundation.developermode.xsd.target"),

	/** When true HTML comments were rendered for better debugging **/
	HTML_DEBUG_ENABLED("storefoundation.developermode.htmlDebugEnabled"),

	CATEGORYFILTER_ENABLED("storefoundation.categoryservice.filtercategories"),

	;

	private String key = null;
	private String def = null;

	private StorefoundationConfig(final String key)
	{
		this.key = key;
	}

	private StorefoundationConfig(final String key, final String def)
	{
		this.key = key;
		this.def = def;
	}

	public String getString()
	{
		return Config.getString(key, def);
	}

	public boolean getBoolean() // NOPMD 
	{
		return Config.getBoolean(key, def != null ? Boolean.parseBoolean(def) : false);
	}

}
