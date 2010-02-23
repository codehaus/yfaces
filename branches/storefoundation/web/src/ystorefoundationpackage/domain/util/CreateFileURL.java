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
package ystorefoundationpackage.domain.util;


import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;

import ystorefoundationpackage.domain.AbstractURLCreator;


/**
 * Creates a valid http(s) URL for a file resource which is given relatively.<br/>
 * This {@link ystorefoundationpackage.domain.URLCreator} uses the current <code>HttpServletRequest</code> to retrieve
 * host information<br/.> The request is used to add the absolute part of the URL.<br/>
 * The response will be taken for encoding the URL.<br/>
 * 
 */
public class CreateFileURL extends AbstractURLCreator<String>
{
	private static final Logger log = Logger.getLogger(CreateFileURL.class);

	@Override
	public String createExternalForm(final String resource, final Map<String, ?> params)
	{
		if (resource == null)
		{
			throw new YFacesException("Can't create an URL for 'null'", new NullPointerException());
		}

		final FacesContext fctx = FacesContext.getCurrentInstance();
		final String contextPath = fctx.getExternalContext().getRequestContextPath();

		String result = null;
		if (!resource.startsWith(contextPath + "/"))
		{
			result = fctx.getApplication().getViewHandler().getResourceURL(fctx, resource);
		}
		else
		{
			result = fctx.getExternalContext().encodeResourceURL(resource);
		}

		if (log.isDebugEnabled())
		{
			log.debug("Created URL for '" + resource + "': " + result);
		}

		return result;
	}

}
