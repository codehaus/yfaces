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
import de.hybris.yfaces.YFacesException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.ConverterFactory;
import ystorefoundationpackage.domain.ObjectConverter;


/**
 * Implements {@link ConverterFactory}.
 */
public class ConverterFactoryImpl extends ConverterFactory
{
	private static final Logger log = Logger.getLogger(ConverterFactoryImpl.class);

	private Map<Class<?>, Class<ObjectConverter>> class2ConverterMap = null;
	private Map<String, Class<ObjectConverter>> id2ConverterMap = null;

	/**
	 * Constructor.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public ConverterFactoryImpl()
	{
		super();
		this.class2ConverterMap = new ClassLookupMap<Class<ObjectConverter>>();
		this.id2ConverterMap = new HashMap<String, Class<ObjectConverter>>();
	}

	public void addConverter(final String converterId, final Class<ObjectConverter> convClass)
	{
		if (converterId == null)
		{
			throw new YFacesException(new NullPointerException("no id specified"));
		}

		if (convClass == null)
		{
			throw new YFacesException(new NullPointerException("no " + ObjectConverter.class.getSimpleName() + " specified"));
		}

		if (log.isDebugEnabled())
		{
			log.debug("Set " + ObjectConverter.class.getSimpleName() + " for '" + converterId + "': " + convClass.getName());
		}

		this.id2ConverterMap.put(converterId, convClass);
	}

	public void addConverter(final Class forClass, final Class<ObjectConverter> convClass)
	{
		if (forClass == null)
		{
			throw new YFacesException(new NullPointerException("no class specified"));
		}

		if (convClass == null)
		{
			throw new YFacesException(new NullPointerException("no " + ObjectConverter.class.getSimpleName() + " specified"));
		}

		if (log.isDebugEnabled())
		{
			log.debug("Set " + ObjectConverter.class.getSimpleName() + " for " + forClass.getSimpleName() + ".class: "
					+ convClass.getName());
		}

		this.class2ConverterMap.put(forClass, convClass);
	}

	/**
	 * Reconfigures this factory. Resets any previous configuration.
	 * 
	 * @param configs
	 */
	public void setConfiguration(final List<ConverterConfig> configs)
	{
		this.id2ConverterMap.clear();
		this.class2ConverterMap.clear();
		for (final ConverterConfig config : configs)
		{
			final Class<ObjectConverter> converterClass = config.getConverterClass();
			if (converterClass != null)
			{
				if (config.getId() != null)
				{
					this.addConverter(config.getId(), converterClass);
				}

				if (config.getForClass() != null)
				{
					this.addConverter(config.getForClass(), converterClass);
				}
			}
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> ObjectConverter<T> createConverter(final Class<T> clazz)
	{
		final Class converterClass = class2ConverterMap.get(clazz);
		return createConverterInternal(converterClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ObjectConverter<? extends Object> createConverter(final String converterId)
	{
		final Class converterClass = id2ConverterMap.get(converterId);
		return createConverterInternal(converterClass);
	}


	/**
	 * Creates an {@link ObjectConverter} by its classname.
	 */
	private <T extends Object> ObjectConverter<T> createConverterInternal(final Class<ObjectConverter<T>> converterClass)
	{
		ObjectConverter<T> result = null;
		if (converterClass != null)
		{
			try
			{
				result = converterClass.newInstance();
			}
			catch (final Exception e)
			{
				log.error(e.getMessage());
			}
		}
		return result;
	}

}
