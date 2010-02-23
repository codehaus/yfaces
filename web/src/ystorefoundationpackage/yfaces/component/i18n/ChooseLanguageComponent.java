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

import java.util.List;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * This component makes it possible for the user to change the language.
 */
public interface ChooseLanguageComponent extends YModel
{

	//model
	LanguageModel getLanguage();

	void setLanguage(LanguageModel language);

	/**
	 * Get all available languages in the system
	 * 
	 * @return List of SelectItems
	 */
	List<? extends SelectItem> getAvailableLanguages();

	void setAvailableLanguages(List<? extends SelectItem> languages);

	//events
	YEventHandler<ChooseLanguageComponent> getSaveLanguageEvent();

}
