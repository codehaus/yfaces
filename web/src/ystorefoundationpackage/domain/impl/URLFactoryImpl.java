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

import de.hybris.platform.util.collections.ClassLookupMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFacesException;

import ystorefoundationpackage.domain.URLCreator;
import ystorefoundationpackage.domain.URLFactory;


/**
 * Implementation of a {@link URLFactory}.
 */
public class URLFactoryImpl extends URLFactory
{
	private static final Logger log = Logger.getLogger(URLFactoryImpl.class);


	private Map<String, URLCreator> idToCreatorMap = null;
	private Map<Class<?>, URLCreator> classToCreatorMap = null;

	/**
	 * Contructor.
	 */
	public URLFactoryImpl()
	{
		super();
		this.idToCreatorMap = new HashMap<String, URLCreator>();
		this.classToCreatorMap = new ClassLookupMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.sandbox.url.ResourceFactory#createResource(java.lang.String,
	 * java.lang.Object, java.util.Map)
	 */
	@Override
	public <T> URLCreator<T> getURLCreator(final String creatorId)
	{

		return this.idToCreatorMap.get(creatorId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.sandbox.url.URLFactory#getURLCreator(java.lang.Class)
	 */
	@Override
	public <T> URLCreator<T> getURLCreator(final Class<? extends T> resource)
	{
		return this.classToCreatorMap.get(resource);
	}

	/**
	 * Adds a {@link URLCreator} for a given ID.
	 * 
	 * @param creatorId
	 *           the id
	 * @param creator
	 *           the {@link URLCreator} to add
	 */
	public void addURLCreator(final String creatorId, final URLCreator<?> creator)
	{
		if (creatorId == null)
		{
			throw new YFacesException(new NullPointerException("no id specified"));
		}

		if (creator == null)
		{
			throw new YFacesException(new NullPointerException("no " + URLCreator.class.getSimpleName() + " specified"));
		}

		if (log.isDebugEnabled())
		{
			final String msg = this.idToCreatorMap.containsKey(creatorId) ? "[update]" : "[init]";
			log.debug("Added " + URLCreator.class.getSimpleName() + msg + ": '" + creatorId + "' -> "
					+ creator.getClass().getSimpleName());
		}

		this.idToCreatorMap.put(creatorId, creator);

	}

	/**
	 * Adds a {@link URLCreator} for a given class.
	 * 
	 * @param forClass
	 *           the class
	 * @param creator
	 *           the {@link URLCreator}
	 */
	public void addURLCreator(final Class<?> forClass, final URLCreator<?> creator)
	{
		if (forClass == null)
		{
			throw new YFacesException(new NullPointerException("no class specified"));
		}

		if (creator == null)
		{
			throw new YFacesException(new NullPointerException("no " + URLCreator.class.getSimpleName() + " specified"));
		}

		if (log.isDebugEnabled())
		{
			final String msg = this.classToCreatorMap.containsKey(forClass) ? "[update]" : "[init]";
			log.debug("Added " + URLCreator.class.getSimpleName() + msg + ": " + forClass.getSimpleName() + " -> "
					+ creator.getClass().getSimpleName());
		}

		this.classToCreatorMap.put(forClass, creator);
	}

	/**
	 * Reconfigures this factory. Resets any previous configuration.
	 * 
	 * @param configs
	 */
	public void setConfiguration(final List<URLCreatorConfig> configs)
	{
		this.idToCreatorMap.clear();
		this.classToCreatorMap.clear();
		for (final URLCreatorConfig config : configs)
		{
			final URLCreator creator = config.getURLCreator();
			if (creator != null)
			{
				if (config.getId() != null)
				{
					this.addURLCreator(config.getId(), creator);
				}

				if (config.getForClass() != null)
				{
					this.addURLCreator(config.getForClass(), creator);
				}
			}
		}
	}

}
