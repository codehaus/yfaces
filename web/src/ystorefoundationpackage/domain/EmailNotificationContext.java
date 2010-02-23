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
package ystorefoundationpackage.domain;

import de.hybris.platform.core.model.c2l.LanguageModel;


/**
 * General context information for email notification.
 * 
 */
public interface EmailNotificationContext
{

	/**
	 * @return user name
	 */
	public String getUserName();

	/**
	 * Sets the user name.
	 * 
	 * @param userName
	 *           the user name to set.
	 */
	public void setUserName(String userName);

	/**
	 * @return store name
	 */
	public String getStoreName();

	/**
	 * Sets the store name.
	 * 
	 * @param storeName
	 *           store name to set.
	 */
	public void setStoreName(String storeName);

	/**
	 * @return favorite language for the user
	 */
	public LanguageModel getLanguage();

	/**
	 * Sets the favorite language for the user.
	 * 
	 * @param lang
	 *           language to set.
	 */
	public void setLanguage(LanguageModel lang);

	/**
	 * @return current date
	 */
	public String getDate();

	/**
	 * @return the code of the renderer template
	 */
	public String getRendererTemplate();

}
