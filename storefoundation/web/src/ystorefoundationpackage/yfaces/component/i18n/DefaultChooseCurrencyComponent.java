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
package ystorefoundationpackage.yfaces.component.i18n;

import de.hybris.platform.core.model.c2l.CurrencyModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.faces.CompareSelectItemByLabel;



/**
 * Implementation of the <code>ChooseCurrencyComponent</code> interface.
 */
public class DefaultChooseCurrencyComponent extends AbstractYComponent implements ChooseCurrencyComponent
{

	private static final long serialVersionUID = -2062499590823570517L;

	private CurrencyModel currencyBean = null;
	private List<? extends SelectItem> currencies = null;

	private YComponentEventHandler<ChooseCurrencyComponent> ehChooseCurrency = null;

	/**
	 * Constructor.
	 */
	public DefaultChooseCurrencyComponent()
	{
		super();
		this.ehChooseCurrency = super.createEventHandler(new SaveCurrencyAction());
	}

	@Override
	public void validate()
	{
		if (this.currencyBean == null)
		{
			this.currencyBean = getSystemDefaultCurrency();
		}
		if (this.currencies == null)
		{
			this.currencies = getSystemCurrencies();
		}
	}

	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged())
		{
			this.currencies = null;
		}
	}



	public static class SaveCurrencyAction extends DefaultYComponentEventListener<ChooseCurrencyComponent>
	{

		private static final long serialVersionUID = 3092333836836254495L;

		@Override
		public void actionListener(final YComponentEvent<ChooseCurrencyComponent> event)
		{
			final CurrencyModel selectedCurrency = event.getComponent().getCurrency();
			YStorefoundation.getRequestContext().getSessionContext().setCurrency(selectedCurrency);
		}
	}

	public CurrencyModel getCurrency()
	{
		return this.currencyBean;
	}

	public void setCurrency(final CurrencyModel currency)
	{
		this.currencyBean = currency;
	}

	public List<? extends SelectItem> getAvailableCurrencies()
	{
		return currencies;
	}

	public void setAvailableCurrencies(final List<? extends SelectItem> currencies)
	{
		this.currencies = currencies;
	}

	public YComponentEventHandler<ChooseCurrencyComponent> getSaveCurrencyEvent()
	{
		return this.ehChooseCurrency;
	}

	private CurrencyModel getSystemDefaultCurrency()
	{
		return YStorefoundation.getRequestContext().getSessionContext().getCurrency();
	}

	private List<SelectItem> getSystemCurrencies()
	{
		final List<SelectItem> currencies = new ArrayList<SelectItem>();
		final Collection<CurrencyModel> currencyModels = YStorefoundation.getRequestContext().getPlatformServices()
				.getI18NService().getAllCurrencies();
		for (final CurrencyModel currency : currencyModels)
		{
			currencies.add(new SelectItem(currency, currency.getName() != null ? currency.getName() : currency.getIsocode()));
		}

		Collections.sort(currencies, new CompareSelectItemByLabel());

		return currencies;
	}

}
