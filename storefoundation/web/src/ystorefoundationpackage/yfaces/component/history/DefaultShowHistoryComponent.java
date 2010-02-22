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
package ystorefoundationpackage.yfaces.component.history;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;




public class DefaultShowHistoryComponent extends AbstractYComponent implements ShowHistoryComponent
{

	private static final long serialVersionUID = 4917636990440575619L;
	private static final String HISTORY_ENTRIES = "historyEntries";

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultShowHistoryComponent.class);

	private int historyEntrySize = 5;

	private transient List<ItemModel> historyEntries;
	private final Set<Class> filterClasses;

	public DefaultShowHistoryComponent()
	{
		super();
		this.filterClasses = new HashSet<Class>();
		this.filterClasses.add(ProductModel.class);
	}


	@Override
	public void refresh()
	{
		this.historyEntries = null;
	}

	public List<ItemModel> getHistoryEntries()
	{
		if (this.historyEntries == null)
		{
			this.historyEntries = getHistoryListInternal();
		}
		return this.historyEntries;
	}

	public void setHistoryEntries(final List<ItemModel> historyEntries)
	{
		//		this.historyEntries = historyEntries;
		this.setHistoryListInternal(historyEntries);
	}

	public boolean isEnabledProduct()
	{
		return this.filterClasses.contains(ProductModel.class);
	}

	public void setEnabledProduct(final boolean enabledProduct)
	{
		if (enabledProduct)
		{
			this.filterClasses.add(ProductModel.class);
		}
		else
		{
			this.filterClasses.remove(ProductModel.class);
		}
	}

	public boolean isEnabledCategory()
	{
		return this.filterClasses.contains(CategoryModel.class);
	}

	public void setEnabledCategory(final boolean enabledCategory)
	{
		if (enabledCategory)
		{
			this.filterClasses.add(CategoryModel.class);
		}
		else
		{
			this.filterClasses.remove(CategoryModel.class);
		}
	}

	public void addHistoryEntry(final ItemModel item)
	{
		if (isAllowed(item))
		{
			final List<ItemModel> entries = getHistoryEntries();
			//always remove the item (regardless whether it is element or not) 
			entries.remove(item);
			//and add entry at first position
			entries.add(0, item);

			while (entries.size() > this.historyEntrySize)
			{
				entries.remove(entries.size() - 1);
			}
		}
	}


	public int getHistoryEntrySize()
	{
		return this.historyEntrySize;
	}

	public void setHistoryEntrySize(final int historyEntrySize)
	{
		this.historyEntrySize = historyEntrySize;
	}

	public void addFilterClass(final Class clazz)
	{
		if (clazz.getClass().isAssignableFrom(ItemModel.class))
		{
			this.filterClasses.add(clazz);
		}
		else
		{
			LOG.error("Class [" + clazz.getName() + "] is not allowed to be added as a filter class");
		}
	}



	public void resetHistory()
	{
		this.setHistoryListInternal(new LinkedList<ItemModel>());
	}

	/**
	 * Check passed item against internal filterrules.
	 * 
	 * @param item
	 * @return true when allowed
	 */
	private boolean isAllowed(final ItemModel item)
	{
		for (final Class filterClass : this.filterClasses)
		{
			if (filterClass.isAssignableFrom(item.getClass()))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a default historylist scoped in the usersession.
	 * 
	 * @return history list
	 */
	private List<ItemModel> getHistoryListInternal()
	{
		List<ItemModel> result = (List) getSessionMap().get(HISTORY_ENTRIES);


		if (result == null)
		{
			this.setHistoryListInternal(result = new LinkedList<ItemModel>());
		}

		return result;
	}

	private void setHistoryListInternal(final List<ItemModel> entries)
	{
		getSessionMap().put(HISTORY_ENTRIES, entries);
	}

	private Map getSessionMap()
	{
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
	}


}
