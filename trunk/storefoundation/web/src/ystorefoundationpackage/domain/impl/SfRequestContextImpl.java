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

import de.hybris.yfaces.context.YRequestContextImpl;

import ystorefoundationpackage.domain.ContentManagement;
import ystorefoundationpackage.domain.ConverterFactory;
import ystorefoundationpackage.domain.DefaultValues;
import ystorefoundationpackage.domain.MailManagement;
import ystorefoundationpackage.domain.OrderManagement;
import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.URLFactory;



/**
 * A Storefoundation specific request singleton which provides access to various domain specific methods and services.
 * Actually {@link ystorefoundationpackage.domain.YStorefoundation#getRequestContext()} is the base of all calls to get
 * access to domain logic.
 */
public class SfRequestContextImpl extends YRequestContextImpl implements SfRequestContext
{
	private MailManagement mailTemplates = null;
	private DefaultValues defaultValues = null;

	private ConverterFactory converterFactory = null;
	private URLFactory urlFactory = null;
	private PlatformServices platformServices = null;
	private ProductManagement productManagement = null;
	private ContentManagement contentManagement = null;
	private OrderManagement orderManagement = null;

	/**
	 * @return the orderManagement
	 */
	public OrderManagement getOrderManagement()
	{
		return orderManagement;
	}

	/**
	 * @param orderManagement
	 *           the orderManagement to set
	 */
	public void setOrderManagement(final OrderManagement orderManagement)
	{
		this.orderManagement = orderManagement;
	}

	/**
	 * @return the contentManagement
	 */
	public ContentManagement getContentManagement()
	{
		return contentManagement;
	}

	/**
	 * @param contentManagement
	 *           the contentManagement to set
	 */
	public void setContentManagement(final ContentManagement contentManagement)
	{
		this.contentManagement = contentManagement;
	}

	/**
	 * @return the productManagement
	 */
	public ProductManagement getProductManagement()
	{
		return productManagement;
	}

	/**
	 * @param productManagement
	 *           the productManagement to set
	 */
	public void setProductManagement(final ProductManagement productManagement)
	{
		this.productManagement = productManagement;
	}

	/**
	 * @return the shopDefaults
	 */
	public DefaultValues getDefaultValues()
	{
		return defaultValues;
	}

	/**
	 * @param shopDefaults
	 *           the shopDefaults to set
	 */
	public void setDefaultValues(final DefaultValues shopDefaults)
	{
		this.defaultValues = shopDefaults;
	}

	/**
	 * @return the mailTemplates
	 */
	public MailManagement getMailManagement()
	{
		return mailTemplates;
	}

	/**
	 * @param mailTemplates
	 *           the mailTemplates to set
	 */
	public void setMailManagement(final MailManagement mailTemplates)
	{
		this.mailTemplates = mailTemplates;
	}


	/**
	 * @return the converterFactory
	 */
	public ConverterFactory getConverterFactory()
	{
		return converterFactory;
	}

	/**
	 * @param converterFactory
	 *           the converterFactory to set
	 */
	public void setConverterFactory(final ConverterFactory converterFactory)
	{
		this.converterFactory = converterFactory;
	}

	/**
	 * @return the urlFactory
	 */
	public URLFactory getURLFactory()
	{
		return urlFactory;
	}

	/**
	 * @param urlFactory
	 *           the urlFactory to set
	 */
	public void setURLFactory(final URLFactory urlFactory)
	{
		this.urlFactory = urlFactory;
	}


	public PlatformServices getPlatformServices()
	{
		return platformServices;
	}

	public void setPlatformServices(final PlatformServices platformServices)
	{
		this.platformServices = platformServices;
	}


	@Override
	public SfSessionContext getSessionContext()
	{
		return (SfSessionContext) super.getSessionContext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.yfaces.context.YRequestContextImpl#setSessionContext(de.hybris.yfaces.context.YSessionContext)
	 */
	public void setSessionContext(final SfSessionContext sessionContext)
	{
		super.setSessionContext(sessionContext);
	}


}
