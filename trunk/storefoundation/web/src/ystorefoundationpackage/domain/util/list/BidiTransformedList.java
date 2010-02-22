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

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;

import org.apache.commons.collections.Transformer;


/**
 * List which transforms its values bidirectional. In difference to apaches
 * <code>org.apache.commons.collections.list.TransformedList</code> this implementation supports both directions and,
 * the most important fact, transforms while getting/setting and not only while adding an element.<br/>
 * <br/> {@link Transformer} classes must be stateless as they aren't serializable.
 * 
 * 
 */
public class BidiTransformedList<S, D> extends AbstractList<D> implements Serializable
{
	private List<S> sourceList = null;

	private transient Transformer sourceToDestTransformer = null;
	private transient Transformer destToSourceTransformer = null;


	/**
	 * Constructor. Creates a new instance with a source list and a {@link Transformer} which is used for source to
	 * destination transformation.
	 * 
	 * @param sourceList
	 *           source list
	 * @param sourceToDest
	 *           {@link Transformer} for source to destination
	 */
	public BidiTransformedList(final List<S> sourceList, final Transformer sourceToDest)
	{
		this(sourceList, sourceToDest, null);
	}

	/**
	 * Constructor. Creates a new instance with a {@link Transformer} for both directions.
	 * 
	 * @param sourceList
	 *           source list
	 * @param sourceToDest
	 *           source to destination transformer
	 * @param destToSource
	 *           destination to source transformer
	 */
	public BidiTransformedList(final List<S> sourceList, final Transformer sourceToDest, final Transformer destToSource)
	{
		super();
		this.sourceList = sourceList;
		this.sourceToDestTransformer = sourceToDest;
		this.destToSourceTransformer = destToSource;

		this.sourceTransformerClass = sourceToDestTransformer.getClass();
		this.destTransformerClass = (destToSourceTransformer != null) ? destToSourceTransformer.getClass() : null;

	}



	@Override
	public D get(final int index)
	{
		return (D) this.sourceToDestTransformer.transform(sourceList.get(index));
	}

	@Override
	public D set(final int index, final D element)
	{
		final D old = this.get(index);
		this.sourceList.set(index, (S) this.destToSourceTransformer.transform(element));
		return old;
	}

	@Override
	public void add(final int index, final D element)
	{
		this.sourceList.add(index, (S) this.destToSourceTransformer.transform(element));
	}


	@Override
	public int size()
	{
		return this.sourceList.size();
	}

	private Class sourceTransformerClass = null;
	private Class destTransformerClass = null;

	private void readObject(final ObjectInputStream in) throws Exception
	{
		in.defaultReadObject();
		this.sourceToDestTransformer = (sourceTransformerClass != null) ? (Transformer) sourceTransformerClass.newInstance() : null;
		this.destToSourceTransformer = (destTransformerClass != null) ? (Transformer) destTransformerClass.newInstance() : null;
	}

}
