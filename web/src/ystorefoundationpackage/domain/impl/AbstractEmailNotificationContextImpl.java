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

import de.hybris.platform.core.model.c2l.LanguageModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ystorefoundationpackage.domain.EmailNotificationContext;


/**
 * 
 * 
 */
public abstract class AbstractEmailNotificationContextImpl implements EmailNotificationContext
{

	private String userName = null;
	private String storeName = null;
	private LanguageModel language = null;
	private String rendererTemplate = null;


	/**
	 * @return the rendererTemplate
	 */
	public String getRendererTemplate()
	{
		return rendererTemplate;
	}

	/**
	 * @param rendererTemplate
	 *           the rendererTemplate to set
	 */
	public void setRendererTemplate(final String rendererTemplate)
	{
		this.rendererTemplate = rendererTemplate;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(final String userName)
	{
		this.userName = userName;
	}

	public String getStoreName()
	{
		return storeName;
	}

	public void setStoreName(final String storeName)
	{
		this.storeName = storeName;
	}

	public void setLanguage(final LanguageModel language)
	{
		this.language = language;
	}

	public LanguageModel getLanguage()
	{
		return this.language;
	}

	public String getDate()
	{
		SimpleDateFormat format;
		final String langIso = getLanguage().getIsocode();

		if ("de".equals(langIso))
		{
			format = new SimpleDateFormat("dd. MMM yyyy", Locale.GERMAN);
		}
		else
		{
			format = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
		}
		return format.format(new Date());
	}

}
