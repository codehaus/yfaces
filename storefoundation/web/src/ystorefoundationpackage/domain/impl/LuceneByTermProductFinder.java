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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.product.Product;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.util.list.LuceneSearchBufferedList;
import ystorefoundationpackage.domain.util.list.ModelWrapperList;


/**
 *
 */
public class LuceneByTermProductFinder implements ProductManagement.ByTermProductFinder
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(LuceneByTermProductFinder.class);

	public interface LuceneIndexConst
	{
		String ARTICLE_ID = "id";

		String ARTICLE_NAME = "name";

		String ARTICLE_TEXT = "text";

		String ARTICLE_KEYWORDS = "keywords";
	}



	@Override
	public List<ProductModel> findAllByTerm(final CatalogModel catalog, final String term, final String orderBy,
			final boolean isAscending)
	{
		final String _term = this.optimizeQuery(term);
		return new ModelWrapperList<ProductModel>(new LuceneSearchBufferedList<Product>(_term, orderBy, isAscending));
	}

	public List<ProductModel> findAllByTerm(final String luceneIndex, final String term, final String orderBy,
			final boolean isAscending)
	{
		final String _term = this.optimizeQuery(term);
		final LuceneSearchBufferedList<Product> result = new LuceneSearchBufferedList<Product>(_term, orderBy, isAscending);
		result.setLuceneIndex(luceneIndex);
		return new ModelWrapperList<ProductModel>(result);
	}



	private String optimizeQuery(final String query)
	{
		if (query == null)
		{
			return "";
		}
		if ("~".equals(query.trim()))
		{
			return "";
		}
		else
		// query term optimization here:
		{
			final StringBuilder rewritten = new StringBuilder();
			for (final StringTokenizer st = new StringTokenizer(query, " ", false); st.hasMoreTokens();)
			{
				final String token = st.nextToken();
				// no field opertor ':' -> dispatch to index fields
				if (token.indexOf(':') == -1)
				{
					// no other lucene specific syntax used -> prepend and append '*' and '~'
					if (token.indexOf('~') == -1 && token.indexOf('?') == -1 && token.indexOf('*') == -1 && token.indexOf('+') == -1
							&& token.indexOf('-') == -1 && token.indexOf('(') == -1 && token.indexOf('[') == -1
							&& token.indexOf('\"') == -1)
					{
						// ID
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_ID).append(":").append(token).append("*");
						// NAME
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_NAME).append(":").append(token);
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_NAME).append(":*").append(token).append("*");
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_NAME).append(":").append(token).append("~");
						// keywords
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_KEYWORDS).append(":").append(token);
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_KEYWORDS).append(":").append(token).append("~");
						// text
						rewritten.append(" ").append(LuceneIndexConst.ARTICLE_TEXT).append(":").append(token);
					}
					else
					{
						final String prefix = token.charAt(0) == '+' ? "+" : (token.charAt(0) == '-' ? "-" : null);
						// ID
						rewritten.append(" ").append(prefix != null ? prefix : "").append(LuceneIndexConst.ARTICLE_ID).append(":")
								.append(prefix != null ? token.substring(1) : token);
						// NAME
						rewritten.append(" ").append(prefix != null ? prefix : "").append(LuceneIndexConst.ARTICLE_NAME).append(":")
								.append(prefix != null ? token.substring(1) : token);
						// keywords
						rewritten.append(" ").append(prefix != null ? prefix : "").append(LuceneIndexConst.ARTICLE_KEYWORDS)
								.append(":").append(prefix != null ? token.substring(1) : token);
						// text
						rewritten.append(" ").append(prefix != null ? prefix : "").append(LuceneIndexConst.ARTICLE_TEXT).append(":")
								.append(prefix != null ? token.substring(1) : token);
					}
				}
				// leave token unchanged 
				else
				{
					rewritten.append(" ").append(token);
				}
			}
			return rewritten.toString();
		}
	}

}
