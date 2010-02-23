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
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEventHandler;
import de.hybris.yfaces.context.YPageContext;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>ChangePasswordComponent</code> interface.
 */
public class DefaultChangePasswordComponent extends AbstractYComponent implements ChangePasswordComponent
{
	private UserModel user = null;
	private String oldPW = null;
	private String newPW1 = null;
	private String newPW2 = null;
	private Boolean isCheckCurrentPW = null;
	private Boolean isCheckPWRepeat = null;

	private YComponentEventHandler<ChangePasswordComponent> ehChangePW = null;
	private YComponentEventHandler<ChangePasswordComponent> ehCancelChangePW = null;

	//constructor
	public DefaultChangePasswordComponent()
	{
		super();
		this.ehChangePW = super.createEventHandler(new ChangePasswordEvent());
		this.ehCancelChangePW = super.createEventHandler(new CancelChangePasswordEvent());
	}

	public String getOldPassword()
	{
		return this.oldPW;
	}

	public String getNewPassword1()
	{
		return this.newPW1;
	}

	public String getNewPassword2()
	{
		return this.newPW2;
	}

	public UserModel getUser()
	{
		return this.user;
	}

	public void setOldPassword(final String currentPassword)
	{
		this.oldPW = currentPassword;
	}

	public void setNewPassword1(final String newPW1)
	{
		this.newPW1 = newPW1;
	}

	public void setNewPassword2(final String newPW2)
	{
		this.newPW2 = newPW2;
	}

	public void setUser(final UserModel user)
	{
		this.user = user;
	}

	public Boolean getCheckOldPassword()
	{
		return this.isCheckCurrentPW;
	}

	public Boolean getCheckNewPasswordRepeat()
	{
		return this.isCheckPWRepeat;
	}

	public void setCheckOldPassword(final Boolean check)
	{
		this.isCheckCurrentPW = check;
	}

	public void setCheckNewPasswordRepeat(final Boolean check)
	{
		this.isCheckPWRepeat = check;
	}

	@Override
	public void validate()
	{
		if (getUser() == null)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			setUser(userSession.getUser());
		}

		if (getNewPassword1() == null)
		{
			setNewPassword1("");
		}

		if (getNewPassword2() == null)
		{
			setNewPassword2("");
		}

		if (getCheckNewPasswordRepeat() == null)
		{
			setCheckNewPasswordRepeat(Boolean.TRUE);
		}

		if (getCheckOldPassword() == null)
		{
			setCheckOldPassword(Boolean.FALSE);
		}
	}


	public YComponentEventHandler<ChangePasswordComponent> getChangePasswordEvent()
	{
		return this.ehChangePW;
	}

	public YComponentEventHandler<ChangePasswordComponent> getCancelChangePasswordEvent()
	{
		return this.ehCancelChangePW;
	}

	/**
	 * This event gets fired when the user cancels the change of the password. The previous page will be loaded after
	 * that.
	 */
	public static class CancelChangePasswordEvent extends DefaultYComponentEventListener<ChangePasswordComponent>
	{

		private static final long serialVersionUID = 277000831634724475L;

		@Override
		public String action()
		{
			final YPageContext pageCtx = YStorefoundation.getRequestContext().getPageContext();
			if (pageCtx != null && pageCtx.getPreviousPage() != null)
			{
				return pageCtx.getPreviousPage().getNavigationId();
			}
			else
			{
				return null;
			}
		}
	}
}