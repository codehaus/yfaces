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

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import de.hybris.platform.core.model.user.UserModel;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>UserInfoComponent</code> interface.
 */
public class DefaultUserInfoComponent extends AbstractYComponent implements UserInfoComponent
{

	private static final long serialVersionUID = 8191493087187993625L;

	private UserModel user = null;

	private YComponentEventHandler<UserInfoComponent> ehChangePW = null;
	private YComponentEventHandler<UserInfoComponent> ehOrderHistory = null;

	@Override
	public void validate()
	{
		if (this.user == null)
		{
			this.user = YStorefoundation.getRequestContext().getSessionContext().getUser();
		}
	}

	//default constructor
	public DefaultUserInfoComponent()
	{
		super();
		this.ehChangePW = super.createEventHandler();
		this.ehOrderHistory = super.createEventHandler();
	}

	public UserModel getUser()
	{
		return this.user;
	}

	public void setUser(final UserModel user)
	{
		this.user = user;
	}

	public YComponentEventHandler<UserInfoComponent> getChangePasswordEvent()
	{
		return this.ehChangePW;
	}

	public YComponentEventHandler<UserInfoComponent> getShowOrderHistoryEvent()
	{
		return this.ehOrderHistory;
	}

}
