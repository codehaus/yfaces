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

import java.util.AbstractList;
import java.util.List;


/**
 * Wraps another List but fakes the total size. When {@link List#get(int)} requests an index beyond the wrapped list a
 * fakevalue is returned otherwise the call is delegated to the wrapped list.
 * 
 * 
 * 
 */
public class FakeSizeList<V> extends AbstractList
{
	private List<V> source = null;
	private V fakeValue = null;
	private int fakeSize = 0;

	/**
	 * Constructor.
	 * 
	 * @param source
	 *           source List
	 * @param fakeSize
	 *           desired size
	 */
	public FakeSizeList(final List source, final int fakeSize)
	{
		this(source, fakeSize, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *           source List.
	 * @param fakeSize
	 *           desired size
	 * @param fakeValue
	 *           fake value
	 */
	public FakeSizeList(final List source, final int fakeSize, final V fakeValue)
	{
		super();
		if (source == null)
		{
			throw new NullPointerException("No backed List specified");
		}
		this.source = source;
		this.fakeSize = fakeSize;
		this.fakeValue = fakeValue;
	}

	@Override
	public V get(final int index)
	{
		return index < source.size() ? (V) source.get(index) : fakeValue;
	}

	@Override
	public int size()
	{
		return this.fakeSize;
	}
}
