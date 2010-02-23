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

import org.codehaus.yfaces.context.YRequestContext;



/**
 * A Storefoundation specific request singleton which provides access to various domain specific methods and services.
 * Actually {@link YStorefoundation#getRequestContext()} is the base of all calls to get access to domain logic.
 */
public interface SfRequestContext extends YRequestContext
{

	/**
	 * @return {@link DefaultValues}
	 */
	public DefaultValues getDefaultValues();

	/**
	 * @return {@link ConverterFactory}
	 */
	public ConverterFactory getConverterFactory();

	/**
	 * @return {@link URLFactory}
	 */
	public URLFactory getURLFactory();


	/**
	 * A Collection of platform specific services.
	 * 
	 * @return {@link PlatformServices}
	 */
	public PlatformServices getPlatformServices();

	/**
	 * Product specific domain services.
	 * 
	 * @return {@link ProductManagement}
	 */
	public ProductManagement getProductManagement();

	/**
	 * Content specific domain services.
	 * 
	 * @return {@link ContentManagement}
	 */
	public ContentManagement getContentManagement();

	/**
	 * Order specific domain services.
	 * 
	 * @return {@link OrderManagement}
	 */
	public OrderManagement getOrderManagement();

	/**
	 * Mail specific domain services.
	 * 
	 * @return {@link MailManagement}
	 */
	public MailManagement getMailManagement();



	@Override
	public SfSessionContext getSessionContext();


}
