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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Abstract list implementation which internally uses a buffered subset instead of holding all data same time.
 * <p>
 * Very useful when dealing with huge amount of data. The client just sees a general list with a regular size. However,
 * the size actually returned is not the real amount of loaded elements.
 * <p>
 * Existing instances of elements are hold by an internal buffer which represents a subset of whole list. Whenever an
 * element is requested whose index is below/above that ones of the subset buffer, the buffer gets refreshed. In that
 * case {@link #fetchSubset(int, int)} is called (with absolute element index and number of requested elements) and the
 * result is taken as the new buffer.
 * 
 * @param <V>
 */
public abstract class SubsetBufferList<V> extends AbstractList<V> implements Serializable
{
	private static final Logger log = Logger.getLogger(SubsetBufferList.class);

	private transient List<V> buffer = null;
	private transient int sizeValue = -1;

	//false after fresh construction, reset or deserialization
	//remove transient?
	private transient boolean sizeDetected = false;

	private int baseIndex = -1;

	private int bufferSize = -1;

	/**
	 * Constructor. Uses a default subsetbuffer size of 200.
	 */
	public SubsetBufferList()
	{
		this(200);
	}

	/**
	 * Constructor.
	 * 
	 * @param subsetBufferSize
	 *           size of internal subset buffer; size of preloaded elements
	 */
	public SubsetBufferList(final int subsetBufferSize)
	{
		super();
		this.bufferSize = subsetBufferSize;
		this.buffer = new ArrayList<V>(this.bufferSize);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datamodel.PageByPageData#getData(int)
	 */
	@Override
	public V get(final int index)
	{
		//separate passed index (absolute) into base and offset
		final int base = index / bufferSize;
		final int offset = index % bufferSize;

		if (log.isDebugEnabled())
		{
			log.debug("Requesting element at index:" + index + " (current page:" + baseIndex + "; calculated page:" + base
					+ "; calculated index:" + offset);
		}

		//buffer is null after reset or deserialization
		if (base != baseIndex || this.buffer == null)
		{
			this.setBaseIndex(base);
		}

		return this.buffer.get(offset);
	}

	//	/**
	//	 * Returns all buffered data.
	//	 * @return List with buffered data.
	//	 */
	//	public List<V> getBuffer() {
	//		return this.buffer;
	//	}


	/**
	 * Sets the absolute index to the given one. After that calls {@link #fetchSubset(int, int)} to update the internally
	 * hold buffer.
	 * 
	 * @param index
	 *           pageindex
	 */
	private void setBaseIndex(final int index)
	{
		//set new pageindex
		this.baseIndex = index;

		//detect number of elements to request 
		final int _count = ((index * bufferSize + bufferSize) > size()) ? size() % bufferSize : bufferSize;

		if (log.isDebugEnabled())
		{
			log.debug("Fetching new page data; index:" + index + "; count:" + _count);
		}

		//update pagebuffer
		this.buffer = this.fetchSubset(baseIndex * bufferSize, _count);
	}

	/**
	 * @return absolute index
	 */
	public int getBaseIndex()
	{
		return this.baseIndex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see hybris.faces.model.datamodel.PageByPageData#getTotalCount()
	 */
	@Override
	public int size()
	{
		if (!this.sizeDetected)
		{
			this.sizeValue = fetchTotalSize();
			this.sizeDetected = true;
		}
		return this.sizeValue;
	}

	/**
	 * Fetches subset data.
	 * <p>
	 * Gets called whenever a list element is requested which isn't element of the current subset.
	 * 
	 * @param absoluteIndex
	 *           start index
	 * @param numberOfElements
	 *           number of requested elements
	 * @return List subset
	 */
	protected abstract List<V> fetchSubset(int absoluteIndex, int numberOfElements);

	/**
	 * Fetches the total size of the list.
	 * <p>
	 * This method is called only once during lifetime of this instance. The result is taken as return for a
	 * {@link #size()} call. When an instance gets deserialized, this method gets called again.<br/>
	 * 
	 * @return size
	 */
	protected abstract int fetchTotalSize();



}
