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
package ystorefoundationpackage.datatable.ext.axes;


import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.keyvalue.DefaultMapEntry;
import org.apache.log4j.Logger;

import ystorefoundationpackage.datatable.DataTableException;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;


/**
 * Similar to {@link DataTableAxisMarkerImpl} except that this implementation additional implements a Map (allows nice
 * usage within EL)
 * 
 * 
 */
public class DataTableAxisMarkerMap extends DataTableAxisMarkerImpl implements Map<Object, Object>
{

	private static final Logger log = Logger.getLogger(DataTableAxisMarkerMap.class);

	private static Map<String, Method> READ_METHODS = new HashMap<String, Method>();
	static
	{
		try
		{
			final PropertyDescriptor[] descriptors = Introspector.getBeanInfo(DataTableAxisMarker.class).getPropertyDescriptors();
			for (final PropertyDescriptor descriptor : descriptors)
			{
				READ_METHODS.put(descriptor.getName(), descriptor.getReadMethod());
			}

		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	//holds a transient entryset 
	private transient Set<Map.Entry<Object, Object>> entrys = null;


	transient volatile Set<Object> keys = null;


	/**
	 * Constructs a new {@link DataTableAxisMarker}
	 * 
	 * @param model
	 *           The used {@link ystorefoundationpackage.datatable.DataTableModel}
	 * @param axis
	 *           The axis which this marker shall belong to
	 * @param accessIndex
	 *           the source-accessindex at the bounded {@link ystorefoundationpackage.datatable.DataTableModel}
	 */
	public DataTableAxisMarkerMap(final DataTableAxisModel model, final DataTableAxis axis, final int accessIndex)
	{
		super(model, axis, accessIndex);
	}

	/**
	 * Constructs a new {@link DataTableAxisMarker}
	 * 
	 * @param id
	 *           The id of this model
	 * @param model
	 *           The used {@link ystorefoundationpackage.datatable.DataTableModel}
	 * @param axis
	 *           The axis which this marker shall belong to
	 * @param accessIndex
	 *           the source-accessindex at the bounded {@link ystorefoundationpackage.datatable.DataTableModel}
	 */
	public DataTableAxisMarkerMap(final String id, final DataTableAxisModel model, final DataTableAxis axis, final int accessIndex)
	{
		super(id, model, axis, accessIndex);
	}


	/**
	 * Returns the concrete value for the given axis.
	 * 
	 * @param axisIndex
	 *           Axisindex
	 * @return Appropriate TableCellValue
	 */
	public Object get(final Integer axisIndex)
	{
		return super.getCellAt(axisIndex.intValue());
	}

	/**
	 * Returns the concrete value for the given axis.
	 * 
	 * @param otherAxis
	 *           The axis
	 * @return Appropriate TableCellValue
	 */
	public Object get(final DataTableAxisMarker otherAxis)
	{
		return super.getCellAt(otherAxis);
	}

	/**
	 * Returns the concrete value for the given axis which is referenced by id.
	 * 
	 * @param key
	 *           Axisid
	 * @return cellvalue
	 */
	public Object get(final String key)
	{
		return super.getCellAt(key);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Object get(final Object key)
	{
		//get by other axismarker
		if (key instanceof DataTableAxisMarker)
		{
			return get((DataTableAxisMarker) key);
		}

		//check for getter of wrapped axismarker (needed for EL handling)
		final Method mRead = READ_METHODS.get(key);
		if (mRead != null)
		{
			try
			{
				log.debug("Delegate mapkey acces '" + key + "' to wrapped " + DataTableAxisMarker.class.getSimpleName());
				return mRead.invoke(this, (Object[]) null);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}

		//get by other axismarkers id
		if (key instanceof String)
		{
			return get((String) key);
		}

		throw new DataTableException("MapViewDataTableAxis doesn't support this key:" + key.getClass() + "(" + key + ")");
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.webfoundation.datatable.ext.axes.DataTableAxisMarkerImpl#copy()
	 */
	@Override
	public DataTableAxisMarker copy()
	{
		final DataTableAxisMarker result = new DataTableAxisMarkerMap(super.getDataTableAxisModel(), super.getAxis(), super
				.getAccessIndex());
		((DataTableAxisImpl) super.getAxis()).addMarker(result);
		return result;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#entrySet()
	 */
	public Set<Entry<Object, Object>> entrySet()
	{
		if (entrys == null)
		{
			log.warn("PERF: CREATING ENTRYSET FOR MAPVIEWAXIS! (can be very expensive)");
			entrys = (this.getAxis().getType() == DataTableAxis.AXIS_2D.X_AXIS) ? this.createEntrySetForColumn()
					: createEntrySetForRow();
		}
		return entrys;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#keySet()
	 */
	public Set<Object> keySet()
	{
		if (keys == null)
		{
			keys = new AbstractSet<Object>()
			{
				@Override
				public Iterator<Object> iterator()
				{
					return new Iterator<Object>()
					{
						private final Iterator<Entry<Object, Object>> i = entrySet().iterator();

						public boolean hasNext()
						{
							return i.hasNext();
						}

						public Object next()
						{
							return i.next().getKey();
						}

						public void remove()
						{
							i.remove();
						}
					};
				}

				@Override
				public int size()
				{
					return DataTableAxisMarkerMap.this.size();
				}

				@Override
				public boolean contains(final Object k)
				{
					return DataTableAxisMarkerMap.this.containsKey(k);
				}
			};
		}
		return keys;
	}

	/**
	 * Creates an EntrySet for a Column-representation.
	 */
	protected Set<Entry<Object, Object>> createEntrySetForColumn()
	{
		final int maxRows = this.getDataTableAxisModel().getRowCount();
		final LinkedHashSet result = new LinkedHashSet<Entry>();
		for (int i = 0; i < maxRows; i++)
		{
			result.add(new DefaultMapEntry(Integer.valueOf(i), this.get(Integer.valueOf(i))));
		}
		return result;
	}

	protected Set<Entry<Object, Object>> createEntrySetForRow()
	{
		final int maxCols = this.getDataTableAxisModel().getColumnCount();
		final LinkedHashSet result = new LinkedHashSet<Entry>();
		for (int i = 0; i < maxCols; i++)
		{
			result.add(new DefaultMapEntry(Integer.valueOf(i), this.get(Integer.valueOf(i))));
		}
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#clear()
	 */
	public void clear()
	{
		throw new DataTableException(new UnsupportedOperationException());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(final Object key)
	{
		throw new DataTableException(new UnsupportedOperationException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#containsValue(java.lang.Object)
	 */
	public boolean containsValue(final Object value)
	{
		throw new DataTableException(new UnsupportedOperationException());
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty()
	{
		return size() == 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public Object put(final Object key, final Object value)
	{
		throw new DataTableException(new UnsupportedOperationException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(final Map t)
	{
		throw new DataTableException(new UnsupportedOperationException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(final Object key)
	{
		throw new DataTableException(new UnsupportedOperationException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#size()
	 */
	public int size()
	{
		return entrySet().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Map#values()
	 */
	public Collection values()
	{
		throw new DataTableException(new UnsupportedOperationException());
	}



}
