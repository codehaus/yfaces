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

import de.hybris.platform.jalo.Item;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Quick access to various localized terms.
 */
public enum Localized
{
	ACTION_CHANGE("global.action.change"), //
	ACTION_CHOOSE("global.action.choose"), //

	WORD_ALL("global.word.all"), //
	WORD_STATE("global.word.state"), //
	WORD_PRICE("global.word.price"), //
	WORD_DATE("global.word.date"), //
	WORD_CHOOSE("global.word.choose"),

	WORD_TRUE("true"), //
	WORD_FALSE("false"), //
	WORD_NOT_AVAILABLE("n/a"), //

	PHRASE_ERROR("global.phrase.error"), ;

	public final String key;

	private Localized(final String key)
	{
		this.key = key;
	}

	public String value()
	{
		return YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(this.key);
	}

	public String value(final Object... params)
	{
		return YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(this.key, params);
	}

	public static String getValue(final boolean value)
	{
		return value ? Localized.WORD_TRUE.value() : Localized.WORD_FALSE.value();
	}

	public static String getValue(final Boolean value)
	{
		final String result = value != null ? getValue(value.booleanValue()) : WORD_NOT_AVAILABLE.value();
		return result;
	}


	public static String getValue(final Object object, final String attribute)
	{
		String result = null;
		try
		{
			result = (String) ((Item) object).getAttribute(attribute);
		}
		catch (final Exception e)
		{
			result = "[" + attribute + "]";
		}
		return result;
	}

}