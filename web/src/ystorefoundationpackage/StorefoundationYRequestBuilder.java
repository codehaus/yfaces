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



import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.codehaus.yfaces.SpringYRequestContextBuilder;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


/**
 * A ServletContext Listener to initialize some global Application properties.
 * 
 */
public class StorefoundationYRequestBuilder extends SpringYRequestContextBuilder
{


	@Override
	protected WebApplicationContext createWebApplicationContext(final ServletContext ctx)
	{
		ConfigurableWebApplicationContext hybrisCtx = null;

		try
		{
			hybrisCtx = (ConfigurableWebApplicationContext) WebApplicationContextUtils.getRequiredWebApplicationContext(ctx);
			final WebApplicationContext yfacesCtx = super.createWebApplicationContext(ctx);

			((ConfigurableWebApplicationContext) yfacesCtx).getBeanFactory().copyConfigurationFrom(hybrisCtx.getBeanFactory());
			hybrisCtx.refresh();
		}
		catch (final IllegalStateException ex)
		{
			throw new StorefoundationException(ex);
		}

		return hybrisCtx;
	}



}
