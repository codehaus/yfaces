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

import de.hybris.platform.cms.model.StoreModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.domain.ContentManagement;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 *
 */
public class ContentManagementImpl extends AbstractDomainService implements ContentManagement
{

	public String getLocalizedMessage(final String msgKey)
	{
		return getLocalizedMessage(msgKey, (Object[]) null);
	}

	@Override
	public String getLocalizedMessage(final String msgKey, final Object... params)
	{
		final ResourceBundle resourceBundle = getBundle();
		String result = null;
		try
		{
			result = resourceBundle.getString(msgKey);
			if (params != null && params.length > 0)
			{
				final Locale locale = YStorefoundation.getRequestContext().getSessionContext().getLocale();
				final MessageFormat mf = new MessageFormat(result, locale);
				result = mf.format(params);
			}
		}
		catch (final MissingResourceException e)
		{
			result = "[" + msgKey + "]";
		}
		return result;
	}

	@Override
	public NumberFormat getNumberFormat()
	{
		final Locale locale = YStorefoundation.getRequestContext().getSessionContext().getLocale();
		final NumberFormat result = NumberFormat.getNumberInstance(locale);
		return result;
	}

	@Override
	public NumberFormat getCurrencyNumberFormat()
	{
		return getCurrencyNumberFormat(YStorefoundation.getRequestContext().getSessionContext().getCurrency());
	}

	@Override
	public NumberFormat getCurrencyNumberFormat(final CurrencyModel currency)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final Locale locale = reqCtx.getSessionContext().getLocale();

		final NumberFormat result = JaloBridge.getInstance().getNumberFormat(locale, currency);
		return result;
	}

	public List<String> getCssResources()
	{
		final StoreModel store = YStorefoundation.getRequestContext().getPlatformServices().getCmsService().getSessionStore();
		final String value = store.getWebsite().getCssURL();
		List<String> result = Collections.EMPTY_LIST;
		if (value != null)
		{
			result = Arrays.asList(value.split(","));
		}
		return result;
	}

	private static final String BUNDLE_KEY = ContentManagementImpl.class.getName() + "_BUNDLE";

	private ResourceBundle getBundle()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final Locale locale = reqCtx.getSessionContext().getLocale();
		ResourceBundle result = (ResourceBundle) reqCtx.getAttributes().get(BUNDLE_KEY + String.valueOf(locale.hashCode()));
		if (result == null)
		{
			// detected fallback locales
			final Locale[] locales = reqCtx.getPlatformServices().getLocalizationService().getAllLocales(locale);

			// detect bundle
			final String bundle = FacesContext.getCurrentInstance().getApplication().getMessageBundle();

			// webapp classloader
			final ClassLoader loader = Thread.currentThread().getContextClassLoader();

			// load bundle
			result = reqCtx.getPlatformServices().getI18NService().getBundle(bundle, locales, loader);

			reqCtx.getAttributes().put(BUNDLE_KEY + String.valueOf(locale.hashCode()), result);
		}

		return result;
	}
}
