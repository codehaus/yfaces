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


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.AbstractURLCreator;
import ystorefoundationpackage.domain.ConverterFactory;
import ystorefoundationpackage.domain.ObjectConverter;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * An {@link ystorefoundationpackage.domain.URLCreator} which locates an bean. The final URL contains of the following
 * parts: Absolute URL path will be taken from current Request Relative URL path will be taken from Attribute 'target'
 * (The 'target' value should end with an empty query-parameter.) The passed Item will be converted by requesting an
 * appropriate Converter from the {@link ystorefoundationpackage.domain.ConverterFactory} and the result will be added
 * to the URL.
 */
public class CreateModelURL<T extends Object> extends AbstractURLCreator<T>
{
	private static final Logger LOG = Logger.getLogger(CreateModelURL.class);

	private String target = null;

	public CreateModelURL()
	{
		super();
	}

	public CreateModelURL(final String target)
	{
		super();
		this.target = target;
	}

	/**
	 * Sets the target.<br/>
	 * This is the relative part and may or may not include query parameters.
	 * 
	 * @param target
	 *           Target
	 */
	public void setTarget(final String target)
	{
		this.target = target.charAt(0) == '/' ? target : "/" + target;
	}

	public String getTarget()
	{
		return this.target;
	}



	@Override
	public URL createURL(final T resource, final Map<String, ?> params)
	{
		final HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		final String file = createExternalForm(resource);

		URL result = null;
		try
		{
			final String url = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + file;
			result = new URL(url);
		}
		catch (final MalformedURLException e)
		{
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String createExternalForm(final T resource, final Map<String, ?> params)
	{
		if (this.target == null)
		{
			throw new StorefoundationException("No targetprefix for this URLCreator defined", new NullPointerException());
		}

		if (resource == null)
		{
			throw new StorefoundationException("Can't create URL for 'null'", new NullPointerException());
		}

		//request itemconverter
		final ConverterFactory conFac = YStorefoundation.getRequestContext().getConverterFactory();
		final ObjectConverter<Object> converter = conFac.createConverter((Object) resource);
		converter.setProperties(params);

		//get converted id
		final String convertedValue = converter.convertObjectToID(resource);

		final FacesContext fc = FacesContext.getCurrentInstance();
		final String _url = fc.getApplication().getViewHandler().getResourceURL(fc, getTarget() + convertedValue);

		//		final HttpServletRequest req = Webfoundation.getInstance().getExternalContext().getRequest();
		//		final HttpServletResponse resp = Webfoundation.getInstance().getExternalContext().getResponse();
		//
		//		final String path = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/";
		//		final String result = resp.encodeURL(path.concat(getTarget()).concat(itemId));

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Created ExternalURL: " + resource + " -> " + _url);
		}

		//return result;

		//result is a fully qualified URL string starting with a slash and without leading protocol://host:port
		return _url;
	}

}
