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

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.faces.model.SelectItem;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.faces.CompareSelectItemByLabel;



/**
 * Implementation of the <code>ChooseLanguageComponent</code> interface.
 */
public class DefaultChooseLanguageComponent extends AbstractYComponent implements ChooseLanguageComponent
{

	private static final long serialVersionUID = -5831970714189561288L;

	private LanguageModel languageModel = null;
	private List<? extends SelectItem> languages = null;

	private YComponentEventHandler<ChooseLanguageComponent> ehChooseLanguage = null;

	public DefaultChooseLanguageComponent()
	{
		super();
		this.ehChooseLanguage = super.createEventHandler(new SaveLanguageAction());
	}

	public LanguageModel getLanguage()
	{
		return this.languageModel;
	}

	public void setLanguage(final LanguageModel language)
	{
		this.languageModel = language;
	}

	public List<? extends SelectItem> getAvailableLanguages()
	{
		return this.languages;
	}

	public void setAvailableLanguages(final List<? extends SelectItem> languages)
	{
		this.languages = languages;
	}

	@Override
	public void validate()
	{
		if (this.languageModel == null)
		{
			this.languageModel = YStorefoundation.getRequestContext().getSessionContext().getLanguage();

		}
		if (this.languages == null)
		{
			this.languages = getSystemLanguages();
		}
	}




	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged())
		{
			this.languages = null;
		}
	}

	public YComponentEventHandler<ChooseLanguageComponent> getSaveLanguageEvent()
	{
		return this.ehChooseLanguage;
	}


	/**
	 * This event gets fired when the user changes the language.
	 */
	public static class SaveLanguageAction extends DefaultYComponentEventListener<ChooseLanguageComponent>
	{

		private static final long serialVersionUID = 5349888260508346870L;

		@Override
		public void actionListener(final YComponentEvent<ChooseLanguageComponent> event)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			userSession.setLanguage(event.getComponent().getLanguage());
		}
	}

	private static List<SelectItem> getSystemLanguages()
	{
		final List<SelectItem> languages = new ArrayList<SelectItem>();
		final Collection<LanguageModel> _languages = YStorefoundation.getRequestContext().getPlatformServices().getI18NService()
				.getAllActiveLanguages();
		for (final LanguageModel language : _languages)
		{
			final String name = (language.getName() != null) ? language.getName() : "[" + language.getIsocode() + "]";
			languages.add(new SelectItem(language, name));
		}

		Collections.sort(languages, new CompareSelectItemByLabel());

		return languages;
	}

}
