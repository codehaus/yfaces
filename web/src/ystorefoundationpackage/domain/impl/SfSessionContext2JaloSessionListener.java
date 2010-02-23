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

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSession.LoginProperties;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.HashMap;
import java.util.Map;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.SfSessionContext.CurrencyChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.LanguageChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.UserChangeListener;


/**
 * Implements various YSessionContext listeners and updates the {@link JaloSession} whenever an appropriate
 * YSessionContext property was changed.
 * 
 */
public class SfSessionContext2JaloSessionListener implements UserChangeListener, LanguageChangeListener, CurrencyChangeListener
{

	/**
	 * Updates {@link JaloSession} with the new user.
	 * 
	 */
	public void performUserChanged(final UserModel oldUser, final UserModel newUser)
	{
		final User u = getItem(newUser);
		final Map loginProps = new HashMap();
		loginProps.put(LoginProperties.USER_PK, u.getPK());
		//yes, core does an additional PW check although the user is directly given
		//HOR-133 loginProps.put(LoginProperties.PASSWORD, u.getPassword());

		//the other way: via login and pw
		//loginProps.put( "user.principal", login );
		//loginProps.put( "user.credentials", password );

		try
		{
			JaloSession.getCurrentSession().transfer(loginProps, false);
		}
		catch (final Exception e)
		{
			throw new StorefoundationException(e);
		}

		if (JaloBridge.getInstance().isAnonymous(newUser))
		{
			JaloSession.getCurrentSession().removeCart();
		}

	}

	/**
	 * Updates {@link JaloSession} with the new currency.
	 * 
	 */
	public void performCurrencyChanged(final CurrencyModel oldCurrency, final CurrencyModel newCurrency)
	{
		final Currency cur = (Currency) getItem(newCurrency);
		JaloSession.getCurrentSession().getSessionContext().setCurrency(cur);
	}

	/**
	 * Updates {@link JaloSession} with the new language.
	 * 
	 */
	public void performLanguageChanged(final LanguageModel oldLanguage, final LanguageModel newLanguage)
	{
		final Language lang = (Language) getItem(newLanguage);
		JaloSession.getCurrentSession().getSessionContext().setLanguage(lang);
	}


	private <T> T getItem(final ItemModel model)
	{
		final ModelService modelService = (ModelService) Registry.getApplicationContext().getBean("modelService");
		return (T) modelService.getSource(model);
	}


}
