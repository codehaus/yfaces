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
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.commons.validator.GenericValidator;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>ForgotPasswordComponent</code> interface.
 */
public class DefaultForgotPasswordComponent extends AbstractYComponent implements ForgotPasswordComponent
{
	private String email = null;
	private String login = null;

	private YComponentEventHandler<ForgotPasswordComponent> ehSendPW = null;

	/**
	 * This event gets fired when the user tries to get the new password by Email.
	 */
	public static class SendPasswordEvent extends DefaultYComponentEventListener<ForgotPasswordComponent>
	{
		private String errorMessage;
		private String errorParameter;

		@Override
		public void actionListener(final YComponentEvent<ForgotPasswordComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			final ForgotPasswordComponent cmp = event.getComponent();
			String email = cmp.getEmail();
			final String login = cmp.getLogin();
			UserModel user = null;

			if ((GenericValidator.isBlankOrNull(email) && GenericValidator.isBlankOrNull(login))
					|| (!GenericValidator.isBlankOrNull(email) && !GenericValidator.isBlankOrNull(login)))
			{
				errorMessage = "forgotPasswordEnterAny";
			}
			else if (!GenericValidator.isBlankOrNull(email))
			{
				// try to find user for email
				user = reqCtx.getMailManagement().getCustomerByEmail(email);

				if (user == null)
				{
					// couldn't find distinct user for the given email
					errorMessage = "forgotPasswordNoSuchEmail";
				}
			}
			else
			{
				// try to find user by login name
				try
				{
					user = reqCtx.getPlatformServices().getUserService().getUser(login);
				}
				catch (final UnknownIdentifierException uie)
				{
					// couldn't find user with the given login name
					errorMessage = "forgotPasswordNoSuchUser";
				}
			}

			if (user != null)
			{
				final String password = reqCtx.getPlatformServices().getUserService().getPassword(user.getUid());
				if (password != null)
				{
					// user found, password found, send email
					if (GenericValidator.isBlankOrNull(email))
					{
						email = user.getDefaultPaymentAddress().getEmail();
					}

					final String subject = reqCtx.getContentManagement().getLocalizedMessage("forgottenPasswordEmailSubject");
					final StringBuilder str = new StringBuilder();
					str.append("Your login is :" + user.getUid());
					str.append("\nYour password is:" + password);

					final boolean result = reqCtx.getMailManagement().sendMail(user, subject, str.toString());

					if (!result)
					{
						errorMessage = "errorWhileSendingEmail";
					}
				}
				else
				{
					errorMessage = "forgotPasswordPWNotReadable";
				}
			}
		}

		@Override
		public String action()
		{
			String result = "forgotPasswordSendPage";
			if (errorMessage != null)
			{
				final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
				userSession.getMessages().pushErrorMessage(errorMessage, errorParameter);
				result = null;
			}
			return result;
		}
	}

	public DefaultForgotPasswordComponent()
	{
		super();
		this.ehSendPW = super.createEventHandler(new SendPasswordEvent());
	}

	public String getLogin()
	{
		return this.login;
	}

	public void setLogin(final String name)
	{
		this.login = name;
	}


	public String getEmail()
	{
		return this.email;
	}

	public void setEmail(final String mail)
	{
		this.email = mail;
	}


	public YComponentEventHandler<ForgotPasswordComponent> getSendPasswordEvent()
	{
		return this.ehSendPW;
	}


}
