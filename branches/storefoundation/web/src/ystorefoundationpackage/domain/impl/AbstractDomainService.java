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

import de.hybris.platform.cache2.ObjectCacheManager;
import de.hybris.platform.cache2.ObjectCacheManagerImpl;
import de.hybris.platform.cache2.SimpleObjectCache;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Map;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.YStorefoundation;


public class AbstractDomainService
{
	private PlatformServices platformServices = null;

	/**
	 * Shortcut for <code>SfRequestContext.getSessionContext().getPlatformServices().getModelService()</code>
	 * 
	 * @return {@link ModelService} as defined via spring
	 */
	protected ModelService getModelService()
	{
		return getPlatformServices().getModelService();
	}

	/**
	 * Shortcut for <code>SfRequestContext.getSessionContext().getPlatformServices()</code>
	 * 
	 * @return {@link PlatformServices} as defined via spring.
	 */
	public PlatformServices getPlatformServices()
	{
		if (this.platformServices == null)
		{
			this.platformServices = YStorefoundation.getRequestContext().getPlatformServices();
		}
		return this.platformServices;
	}

	public void setPlatformServices(final PlatformServices services)
	{
		this.platformServices = services;
	}


	private static final String CACHE_KEY = ObjectCacheManager.class.getName() + "_services";

	protected ObjectCacheManager getRequestCache()
	{
		final Map m = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
		ObjectCacheManager result = (ObjectCacheManager) m.get(CACHE_KEY);
		if (result == null)
		{
			m.put(CACHE_KEY, result = new ObjectCacheManagerImpl(new SimpleObjectCache()));
		}
		return result;
	}


	/**
	 * A cache which is bound to the current user. It gets reseted whenever the user changes (e.g. login or logout)
	 * 
	 * @return {@link ObjectCacheManager}
	 */
	protected ObjectCacheManager getUserSessionCache()
	{
		final Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		//pk of user which the cache is associated with
		final String pk = (String) sessionMap.get("USER_PK");
		//pk of current user
		final String curPk = JaloSession.getCurrentSession().getUser().getPK().toString();
		if (!curPk.equals(pk))
		{
			sessionMap.put("USER_PK", curPk);
			sessionMap.put(CACHE_KEY, null);
		}

		ObjectCacheManager result = (ObjectCacheManager) sessionMap.get(CACHE_KEY);
		if (result == null)
		{
			sessionMap.put(CACHE_KEY, result = new ObjectCacheManagerImpl(new SimpleObjectCache()));
		}

		return result;
	}

}
