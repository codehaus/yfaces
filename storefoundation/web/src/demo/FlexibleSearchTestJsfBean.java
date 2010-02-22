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
package demo;


import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.cms.jalo.Website;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import ystorefoundationpackage.Localized;
import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.RowCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.domain.util.list.FlexibleSearchBufferedList;
import ystorefoundationpackage.faces.SfSelectItem;
import ystorefoundationpackage.faces.SfSelectItemGroup;


public class FlexibleSearchTestJsfBean implements Serializable
{
	private static class _ItemConverter implements Converter
	{
		public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
		{
			return JaloSession.getCurrentSession().getItem(PK.parse(arg2));
		}

		public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
		{
			return ((Item) arg2).getPK().toString();
		}
	}

	private static final long serialVersionUID = 1234567;

	private transient SfSelectItemGroup<User> userSelector = null;
	private transient SfSelectItemGroup<Language> languageSelector = null;
	private transient SfSelectItemGroup<Currency> currencySelector = null;
	private transient SfSelectItemGroup<Website> websiteSelector = null;

	private transient DataTableAxisModel searchResultTable = null;

	private transient String error = null;

	private long time = 0;

	//serializable stuff
	private String searchTerm = null;
	private User selectedUser = null;
	private Currency selectedCurrency = null;
	private Language selectedLanguage = null;
	private Website selectedWebsite = null;


	public FlexibleSearchTestJsfBean()
	{
		this.selectedUser = JaloSession.getCurrentSession().getSessionContext().getUser();
		this.selectedCurrency = JaloSession.getCurrentSession().getSessionContext().getCurrency();
		this.selectedLanguage = JaloSession.getCurrentSession().getSessionContext().getLanguage();

		this.selectedWebsite = CmsManager.getInstance().getActiveWebsite();

		if (this.selectedWebsite == null)
		{
			this.selectedWebsite = CmsManager.getInstance().getWebsites().iterator().next();
		}
	}

	/**
	 * SearchTerm.
	 * 
	 * @param term
	 *           Searchterm
	 */
	public void setSearchTerm(final String term)
	{
		this.searchTerm = term;
		this.error = null;
		this.searchResultTable = null;
	}

	/**
	 * SearchTerm.
	 * 
	 * @return Searchterm.
	 */
	public String getSearchTerm()
	{
		return this.searchTerm;
	}

	public String getError()
	{
		return this.error;
	}

	public long getTime()
	{
		return this.time;
	}



	/**
	 * @return A Selector for the {@link User}
	 */
	public SfSelectItemGroup<User> getUserSelector()
	{
		if (this.userSelector == null)
		{
			this.userSelector = createSingleSelector(UserManager.getInstance().getAllUsers(), User.UID);
			this.userSelector.setSelectedValue(this.selectedUser);
			this.userSelector.setConverter(new _ItemConverter());
		}
		return userSelector;
	}

	public SfSelectItemGroup<Website> getWebsiteSelector()
	{
		if (this.websiteSelector == null)
		{
			this.websiteSelector = createSingleSelector(CmsManager.getInstance().getWebsites(), Website.CODE);
			this.websiteSelector.setSelectedValue(selectedWebsite);
			this.websiteSelector.setConverter(new _ItemConverter());
		}
		return this.websiteSelector;
	}

	/**
	 * @return A Selector for the {@link Language}
	 */
	public SfSelectItemGroup<Language> getLanguageSelector()
	{
		if (this.languageSelector == null)
		{
			this.languageSelector = createSingleSelector(C2LManager.getInstance().getAllLanguages(), Language.ISOCODE);
			this.languageSelector.setSelectedValue(this.selectedLanguage);
			this.languageSelector.setConverter(new _ItemConverter());
		}
		return languageSelector;
	}

	/**
	 * @return A Selector for the {@link Currency}
	 */
	public SfSelectItemGroup<Currency> getCurrencySelector()
	{
		if (this.currencySelector == null)
		{
			this.currencySelector = createSingleSelector(C2LManager.getInstance().getAllCurrencies(), Currency.ISOCODE);
			this.currencySelector.setSelectedValue(this.selectedCurrency);
			this.currencySelector.setConverter(new _ItemConverter());
		}
		return currencySelector;
	}

	/**
	 * Creates a SingleSelector.
	 * 
	 * @param <T>
	 * @param values
	 * @param labelQualifier
	 */
	private <T extends Item> SfSelectItemGroup<T> createSingleSelector(final Collection<T> values, final String labelQualifier)
	{
		final SfSelectItemGroup<T> result = new SfSelectItemGroup();
		for (final T item : values)
		{
			result.addSelectItem(new SfSelectItem(item, Localized.getValue(item, labelQualifier)));
		}
		return result;
	}



	public DataTableAxisModel getSearchResult()
	{
		if (this.searchResultTable == null)
		{
			final String term = this.searchTerm;
			this.selectedUser = this.getUserSelector().getSelectedValue();
			this.selectedLanguage = this.getLanguageSelector().getSelectedValue();
			this.selectedCurrency = this.getCurrencySelector().getSelectedValue();
			this.selectedWebsite = this.getWebsiteSelector().getSelectedValue();

			try
			{
				if (term != null && term.length() > 1)
				{
					final String substrg = term.substring(0, term.toLowerCase().indexOf("from"));
					final int count = substrg.split(",").length;
					final Class[] resultClasses = new Class[count];
					for (int i = 0; i < count; i++)
					{
						resultClasses[i] = String.class;
					}

					final SessionContext ctx = JaloSession.getCurrentSession().createSessionContext();
					ctx.setUser(selectedUser);
					ctx.setLanguage(selectedLanguage);
					ctx.setCurrency(selectedCurrency);
					CmsManager.getInstance().setActiveWebsite(this.selectedWebsite);

					this.time = System.currentTimeMillis();

					final List result = new FlexibleSearchBufferedList(term, null, resultClasses, 300);
					((FlexibleSearchBufferedList) result).setSearchContextAttributes(ctx);
					final DataTableModel flexSearch = new RowCollectionDataTableModel(result);

					this.time = System.currentTimeMillis() - time;

					this.searchResultTable = DataTableFactory.createDataTableAxisModel(flexSearch);
				}
				else
				{
					this.searchResultTable = null;
				}
			}
			catch (final Exception e)
			{
				this.error = e.getMessage();
			}
		}
		return this.searchResultTable;
	}



}
