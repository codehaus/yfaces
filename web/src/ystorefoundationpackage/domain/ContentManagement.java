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

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.text.NumberFormat;
import java.util.List;



/**
 * Anything which is somehow content related. This includes CMS functionalities as well as localization and formating
 * options.
 */
public interface ContentManagement
{

	/**
	 * Returns a localized message for the passed message key. Respects any session sensitive options like current
	 * {@link java.util.Locale} etc.
	 * 
	 * @param msgKey
	 *           key for resource bundle
	 * @return localized Message
	 */
	String getLocalizedMessage(final String msgKey);

	/**
	 * Returns a localized message for the passed message key. Respects any session sensitive options like current
	 * {@link java.util.Locale} etc.
	 * 
	 * @param msgKey
	 *           key for resource bundle
	 * @param params
	 *           parameters which shall be used for formatting the message
	 * @return localized message
	 */
	String getLocalizedMessage(String msgKey, Object... params);

	NumberFormat getNumberFormat();

	NumberFormat getCurrencyNumberFormat();

	NumberFormat getCurrencyNumberFormat(CurrencyModel currency);

	/**
	 * Searches for all css resources.
	 * 
	 * @return list of all found css resources
	 */
	List<String> getCssResources();

}
