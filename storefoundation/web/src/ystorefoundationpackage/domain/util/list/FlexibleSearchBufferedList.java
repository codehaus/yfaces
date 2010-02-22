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
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SearchResult;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * A {@link List} implementation for a for a FlexibleSearch.<br/>
 * The buffersize/pagesize defines the size of the internal buffer.<br/>
 * When an element is requested with an index outside the buffer, the queries absolute index gets recalculated.<br/>
 * The query gets fired again and the internal buffer gets filled.<br/>
 * <br/>
 * Overall, this allows working with very large resultsets.<br/>
 * 
 * 
 * 
 * @param <V>
 */
public class FlexibleSearchBufferedList<V> extends SubsetBufferList<V>
{
	private static final Logger log = Logger.getLogger(FlexibleSearchBufferedList.class);

	private static final int DEFAULT_BUFFER_SIZE = 200;

	private String query = null;

	//resultclasses of columns
	private List<Class<?>> resultClasses = null;

	//SessionContext is transient but important attributes are still serializable
	//they are hold within in array (for performance reason during de/serialization)
	private transient SessionContext ctx = null;
	private Object[][] ctxParams = null;

	//FlexibleSearch parameters
	private Map params = Collections.EMPTY_MAP;

	/**
	 * Constructor. Uses the default Buffersize given with {@link FlexibleSearchBufferedList#DEFAULT_BUFFER_SIZE}
	 * 
	 * @param query
	 *           The query which shall be buffered
	 */
	public FlexibleSearchBufferedList(final String query)
	{
		this(query, null, null, DEFAULT_BUFFER_SIZE);
	}

	public FlexibleSearchBufferedList(final String query, final Map params)
	{
		this(query, params, null, DEFAULT_BUFFER_SIZE);
	}

	public FlexibleSearchBufferedList(final String query, final Map params, final Class<?>[] resultClasses)
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
	public FlexibleSearchBufferedList(final String query, final Map params, final Class<?>[] resultClasses, final int bufferSize)
	{
		super(bufferSize);
		this.ctx = JaloSession.getCurrentSession().createSessionContext();
		this.resultClasses = (resultClasses == null) ? (List) Arrays.asList(Item.class) : Arrays.asList(resultClasses);
		this.params = (params == null) ? Collections.EMPTY_MAP : params;
		this.query = query;
	}

	@Override
	protected List<V> fetchSubset(final int absoluteIndex, final int count)
	{

		final SearchResult<V> result = FlexibleSearch.getInstance().search(this.ctx, query, this.params, this.resultClasses, false,
				true, absoluteIndex, count);
		return result.getResult();
	}

	@Override
	protected int fetchTotalSize()
	{
		final int result = FlexibleSearch.getInstance().search(this.ctx, query, this.params, this.resultClasses, true, false, 0, 0)
				.getTotalCount();

		if (log.isDebugEnabled())
		{
			log.debug("SQL count query for:'" + query + "' returns: " + result);
		}
		return result;
	}

	/**
	 * Uses the passed {@link SessionContext} as query context.<br/>
	 * Marks all available context attributes as "preservable" which just means in case of a Serialization all context
	 * attributes gets serialized as well.
	 * 
	 * @param ctx
	 *           {@link SessionContext}
	 */
	public void setSearchContextAttributes(final SessionContext ctx)
	{
		this.ctx = ctx;
		this.extractSerializeableContextAttributes(this.ctx.getAttributes().keySet());
	}

	/**
	 * Uses the passed {@link SessionContext} as query context.<br/>
	 * Mark all attributes which are given by name with second parameter as preservable.<br/>
	 * This means in case of serialization only these attribute values are serialized as well. During deserialization a
	 * standard context is created and refilled with these values.
	 * 
	 * @param ctx
	 *           {@link SessionContext}
	 * @param preserveAttributes
	 *           collection of attribute names whose values shall be serializeable
	 */
	public void setSearchContextAttributes(final SessionContext ctx, final Collection<String> preserveAttributes)
	{
		for (final String param : preserveAttributes)
		{
			this.ctx.setAttribute(param, ctx.getAttribute(param));
		}
		this.extractSerializeableContextAttributes(preserveAttributes);
	}


	private void extractSerializeableContextAttributes(final Collection<String> params)
	{
		this.ctxParams = new Object[params.size()][2];
		int i = 0;
		for (final String param : params)
		{
			this.ctxParams[i][0] = param;
			this.ctxParams[i][1] = this.ctx.getAttribute(param);
			i++;
		}
	}



	//
	// DeSerialization
	//
	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException
	{
		in.defaultReadObject();

		//take the current sessioncontext
		this.ctx = JaloSession.getCurrentSession().createSessionContext();

		//but when available overwrite/put some attributes 
		if (this.ctxParams != null)
		{
			for (final Object[] entry : this.ctxParams)
			{
				this.ctx.setAttribute((String) entry[0], entry[1]);
			}
		}
	}
	//	
	//	//
	//	// Serialization
	//	//
	//	private void writeObject(ObjectOutputStream aOutputStream) throws IOException
	//	{
	//		aOutputStream.defaultWriteObject();
	//		if (log.isDebugEnabled())
	//		{
	//			log.debug(logId + ": <- saving instance (serialization)");
	//		}
	//	}	


}
