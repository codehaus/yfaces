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
package ystorefoundationpackage.domain.util.list;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * {@link SubsetBufferList} implementation for a flexible search.
 * 
 * @param <V>
 */
public class FlexibleSearchBufferedModelList<V> extends SubsetBufferList<V>
{
	private static final Logger log = Logger.getLogger(FlexibleSearchBufferedModelList.class);

	private static final int DEFAULT_BUFFER_SIZE = 200;

	private String query = null;

	//resultclasses of columns
	private List<Class<?>> resultClasses = null;

	//FlexibleSearch parameters
	private Map params = Collections.EMPTY_MAP;

	/**
	 * Constructor. Uses the default Buffersize given with {@link FlexibleSearchBufferedList#DEFAULT_BUFFER_SIZE}
	 * 
	 * @param query
	 *           The query which shall be buffered
	 */
	public FlexibleSearchBufferedModelList(final String query)
	{
		this(query, null, null, DEFAULT_BUFFER_SIZE);
	}

	public FlexibleSearchBufferedModelList(final String query, final Map params)
	{
		this(query, params, null, DEFAULT_BUFFER_SIZE);
	}

	public FlexibleSearchBufferedModelList(final String query, final Map params, final int bufferSize)
	{
		this(query, params, null, bufferSize);
	}


	public FlexibleSearchBufferedModelList(final String query, final Map params, final Class<?>[] resultClasses)
	{
		this(query, params, resultClasses, DEFAULT_BUFFER_SIZE);
	}


	/**
	 * Constructor.
	 * 
	 * @param query
	 *           The query which shall be buffered
	 * @param resultClasses
	 *           The resultclasses
	 * @param bufferSize
	 *           the buffersize
	 */
	public FlexibleSearchBufferedModelList(final String query, final Map params, final Class<?>[] resultClasses,
			final int bufferSize)
	{
		super(bufferSize);
		this.resultClasses = (resultClasses == null) ? (List) Arrays.asList(Item.class) : Arrays.asList(resultClasses);
		this.params = (params == null) ? Collections.EMPTY_MAP : params;
		this.query = query;
	}

	@Override
	protected List<V> fetchSubset(final int absoluteIndex, final int count)
	{
		final FlexibleSearchService flexService = YStorefoundation.getRequestContext().getPlatformServices()
				.getFlexibleSearchService();

		final FlexibleSearchQuery flexQuery = this.createQuery();
		flexQuery.setStart(absoluteIndex);
		flexQuery.setCount(count);
		flexQuery.setNeedTotal(false);

		return (List<V>) flexService.search(flexQuery).getResult();
	}

	@Override
	protected int fetchTotalSize()
	{
		final FlexibleSearchService flexService = YStorefoundation.getRequestContext().getPlatformServices()
				.getFlexibleSearchService();

		final FlexibleSearchQuery flexQuery = this.createQuery();
		flexQuery.setStart(0);
		flexQuery.setCount(0);
		flexQuery.setNeedTotal(true);

		final int result = flexService.search(flexQuery).getTotalCount();

		if (log.isDebugEnabled())
		{
			log.debug("SQL count query for:'" + query + "' returns: " + result);
		}
		return result;
	}

	private FlexibleSearchQuery createQuery()
	{
		final FlexibleSearchQuery flexQuery = new FlexibleSearchQuery(query);
		flexQuery.setResultClassList(this.resultClasses);
		flexQuery.addQueryParameters(this.params);
		flexQuery.setStart(0);
		flexQuery.setCount(0);
		flexQuery.setFailOnUnknownFields(false);
		flexQuery.setNeedTotal(false);

		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		flexQuery.setCatalogVersions(reqCtx.getPlatformServices().getCatalogService().getSessionCatalogVersions());
		flexQuery.setLanguage(reqCtx.getSessionContext().getLanguage());
		flexQuery.setUser(reqCtx.getSessionContext().getUser());

		return flexQuery;
	}
}
