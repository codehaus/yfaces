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

import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.lucenesearch.jalo.LuceneIndex;
import de.hybris.platform.lucenesearch.jalo.LuceneSearchResult;
import de.hybris.platform.lucenesearch.jalo.LucenesearchManager;
import de.hybris.platform.util.Utilities;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.search.BooleanQuery;


/**
 * 
 * 
 */
public class LuceneSearchBufferedList<V> extends SubsetBufferList<V>
{
	private static final Logger log = Logger.getLogger(LuceneSearchBufferedList.class);

	private String term = null;
	private String orderBy = null;
	private String luceneIndex = null;
	private boolean isAscending = true;

	private transient List<Float> score = null;

	public LuceneSearchBufferedList(final String term, final String orderBy, final boolean isAscending)
	{
		super();
		this.term = term;
		this.orderBy = orderBy;
		this.isAscending = isAscending;
	}

	public void setLuceneIndex(final String index)
	{
		this.luceneIndex = index;
	}

	public String getLuceneIndex()
	{
		return this.luceneIndex;
	}

	public List<Float> getScoreList()
	{
		return this.score;
	}

	@Override
	protected List<V> fetchSubset(final int absoluteIndex, final int numberOfElements)
	{
		final LuceneIndex idx = this.getLuceneIndexInternal();

		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		BooleanQuery.setMaxClauseCount(4096);
		LuceneSearchResult.setMaxHitsThreshold(numberOfElements);
		//		SearchResult result = new StandardSearchResult( Collections.EMPTY_LIST, 0, 0, 0 );
		LuceneSearchResult result = null;
		try
		{
			result = idx.searchItems(ctx, term, null, orderBy, isAscending, absoluteIndex, numberOfElements);
			//this.maxHitsExceeded = ((LuceneSearchResult)result).isMaxHitsExceeded();
			//this.setMaxHits(((LuceneSearchResult)result).getTotalCount());
		}
		catch (final de.hybris.platform.lucenesearch.jalo.ParseException e)
		{
			log.error("LuceneSearchControl: " + e.getMessage());
			//	Commented out: exception is way too much low level to be thrown to screen
			//	throw new de.hybris.jakarta.ext.lucenesearch.jalo.ParseException( e );
		}
		catch (final Exception e)
		{
			log.error("LuceneSearchControl: " + e.getMessage() + " \n" + Utilities.getStackTraceAsString(e));
		}

		this.score = new FakeSizeList<Float>(result.getScores(), super.size(), Float.valueOf(0));

		return result.getResult();
	}

	@Override
	protected int fetchTotalSize()
	{
		final LuceneIndex idx = this.getLuceneIndexInternal();

		final SessionContext ctx = JaloSession.getCurrentSession().getSessionContext();
		Integer result = Integer.valueOf(-1);
		try
		{
			//TODO:
			//unluckily getTotalCount from lucenesearchresult doesn't behave like flexiblesearchresult
			//LuceneSearchResult:  total returns size before applying postqueryfilter (which filters out restricted prods)
			//FlexibleSearchResult:total returns correct size

			result = Integer.valueOf(idx.searchItems(ctx, term, null, null, true, 0, -1).getTotalCount());
		}
		catch (final de.hybris.platform.lucenesearch.jalo.ParseException e)
		{
			log.error("LuceneSearchControl: " + e.getMessage());
		}
		catch (final Exception e)
		{
			log.error("LuceneSearchControl: " + e.getMessage() + " \n" + Utilities.getStackTraceAsString(e));
		}
		return result.intValue();
	}

	private LuceneIndex getLuceneIndexInternal()
	{
		final LuceneIndex result = (this.luceneIndex == null) ? CmsManager.getInstance().getActiveStore().getLuceneIndex()
				: LucenesearchManager.getInstance().getLuceneIndex(luceneIndex);
		return result;
	}

}
