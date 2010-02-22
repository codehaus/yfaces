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
package ystorefoundationpackage.yfaces.component.user;


import de.hybris.platform.core.model.user.UserModel;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;


/**
 * This component displays the general user information.
 */
public interface UserInfoComponent extends YComponent
{

	//events
	public YComponentEventHandler<UserInfoComponent> getChangePasswordEvent();

	public YComponentEventHandler<UserInfoComponent> getShowOrderHistoryEvent();

	//model
	public UserModel getUser();

	public void setUser(UserModel user);

}
