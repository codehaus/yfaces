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

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.util.Config;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;
import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.domain.DefaultValues;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.LoginFrame;


/**
 * Implementation of the <code>LoginComponent</code> interface.
 */
public class DefaultLoginComponent extends AbstractYComponent implements LoginComponent
{
	private String login = null;
	private String password = null;

	private String successForward = null;
	private String errorForward = null;

	private int httpPort = 0;
	private int sslPort = 0;

	private YComponentEventHandler<LoginComponent> ehLogin = null;
	private YComponentEventHandler<LoginComponent> ehLogout = null;
	private YComponentEventHandler<LoginComponent> ehDemoLogin = null;
	private YComponentEventHandler<LoginComponent> ehRegister = null;
	private YComponentEventHandler<LoginComponent> ehForgotPW = null;

	/**
	 * Constructor.
	 */
	public DefaultLoginComponent()
	{
		super();
		httpPort = Config.getInt("tomcat.http.port", 80);
		sslPort = Config.getInt("tomcat.ssl.port", 443);

		this.ehDemoLogin = super.createEventHandler(new DemoLoginEvent());
		this.ehForgotPW = super.createEventHandler(new DefaultYComponentEventListener(NavigationOutcome.FORGOT_PW_PAGE.id));
		this.ehLogin = super.createEventHandler(new LoginEvent());
		this.ehLogout = super.createEventHandler(new LogoutEvent());
		this.ehRegister = super.createEventHandler(new RegisterEvent());

	}

	public String getLogin()
	{
		return this.login;
	}

	public String getPassword()
	{
		return this.password;
	}

	public void setLogin(final String login)
	{
		this.login = login;
	}

	public void setPassword(final String password)
	{
		this.password = password;
	}


	public YComponentEventHandler<LoginComponent> getLoginEvent()
	{
		return this.ehLogin;
	}

	public YComponentEventHandler<LoginComponent> getLogoutEvent()
	{
		return this.ehLogout;
	}

	public YComponentEventHandler<LoginComponent> getDemoLoginEvent()
	{
		return this.ehDemoLogin;
	}

	public YComponentEventHandler<LoginComponent> getRegisterEvent()
	{
		return this.ehRegister;
	}

	public YComponentEventHandler<LoginComponent> getForgotPasswordEvent()
	{
		return this.ehForgotPW;
	}

	/**
	 * This event gets fired when the user tries to log in.
	 */
	public static class LoginEvent extends DefaultYComponentEventListener<LoginComponent>
	{
		private transient LoginComponent cmp = null;
		private UserModel newUser = null;

		@SuppressWarnings("unchecked")
		@Override
		public void actionListener(final YComponentEvent<LoginComponent> event)
		{
			cmp = event.getComponent();
			//request user by login values
			newUser = this.getUser(cmp.getLogin(), cmp.getPassword());

			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			//when login isn't successful ...
			if (newUser == null)
			{
				YPageContext loginPage = YStorefoundation.getRequestContext().getPageContext();

				//... and it is the first try...
				if (cmp.getFrame().getPage().getPreviousPage() == null)
				{
					loginPage = cmp.getFrame().getPage().getConversationContext().getOrCreateNextPage();

				}

				final LoginFrame loginFrame = loginPage.getOrCreateFrame(LoginFrame.class);

				loginFrame.getLoginComponent().getValue().setLogin(cmp.getLogin());
				loginFrame.getLoginComponent().getValue().setPassword(cmp.getPassword());
				userSession.getMessages().pushInfoMessage("frames.loginFrame.loginInvalid");
			}
			else
			{
				userSession.setUser(newUser);
				userSession.getMessages().pushInfoMessage("frames.loginFrame.loginValid", newUser.getName());
			}
		}

		@Override
		public String action()
		{
			String result = cmp.getSuccessForward();

			if (newUser != null)
			{
				//previous or same page
				if (result == null)
				{
					final YPageContext page = cmp.getFrame().getPage().getPreviousPage();
					if (page != null)
					{
						YStorefoundation.getRequestContext().redirect(page, false);
					}
					else
					{
						YStorefoundation.getRequestContext().redirect(false);
					}
				}
			}
			else
			{
				result = cmp.getErrorForward();
			}
			return result;
		}

		private CustomerModel getUser(final String id, final String pw)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			UserModel result = null;
			try
			{
				result = reqCtx.getPlatformServices().getUserService().getUser(id);
				if (result instanceof CustomerModel)
				{
					final boolean pwCheck = JaloBridge.getInstance().checkPassword(result, pw);
					if (!pwCheck)
					{
						result = null;
					}
				}
			}
			catch (final Exception e)
			{
				//silent catch; password check not successful or user not found
			}

			return (CustomerModel) result;
		}
	}

	/**
	 * This event gets fired when the user logs out.
	 */
	public static class LogoutEvent extends DefaultYComponentEventListener<LoginComponent>
	{
		private String oldLangIso = null;

		@SuppressWarnings("unchecked")
		@Override
		public void actionListener(final YComponentEvent<LoginComponent> event)
		{
			final LoginComponent cmp = event.getComponent();
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			oldLangIso = userSession.getLanguage().getIsocode();
			final DefaultValues defaults = YStorefoundation.getRequestContext().getDefaultValues();
			final UserModel defaultUser = defaults.getDefaultCustomer();
			userSession.setUser(defaultUser);
			cmp.setSuccessForward(null);
		}

		@Override
		public String action()
		{
			YStorefoundation.getRequestContext().redirect("/index.jsf?lang=" + oldLangIso);
			return null;
		}
	}

	/**
	 * This event gets fired when the user wants to register.
	 */
	public static class RegisterEvent extends DefaultYComponentEventListener<LoginComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<LoginComponent> event)
		{
			final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
			convCtx.getOrCreateNextPage();
		}

		@Override
		public String action()
		{
			return "registrationPage";
		}
	}

	/**
	 * This event gets fired when the user has no account, but still wants to see what a logged in user can do. The
	 * default demo customer 'demo' will be used.
	 */
	public static class DemoLoginEvent extends LoginEvent
	{
		@Override
		public void actionListener(final YComponentEvent<LoginComponent> event)
		{
			final LoginComponent cmp = event.getComponent();
			cmp.setLogin("demo");
			cmp.setPassword("1234");
			super.actionListener(event);
		}
	}

	/**
	 * Returns {code}true{code}, if the current Session is not an anonymous session.
	 * 
	 * @return {code}true{code}, if the current Session is not an anonymous session
	 */
	public boolean isLoggedIn()
	{
		final boolean result = !JaloBridge.getInstance().isAnonymous(
				YStorefoundation.getRequestContext().getSessionContext().getUser());
		return result;
	}


	public String getErrorForward()
	{
		return this.errorForward;
	}

	public String getSuccessForward()
	{
		return this.successForward;
	}

	public void setErrorForward(final String forward)
	{
		this.errorForward = forward;
	}

	public void setSuccessForward(final String forward)
	{
		this.successForward = forward;
	}

	public int getHTTPPort()
	{
		return this.httpPort;
	}

	public int getSSLPort()
	{
		return this.sslPort;
	}

	public void setHTTPPort(final int port)
	{
		this.httpPort = port;
	}

	public void setSSLPort(final int port)
	{
		this.sslPort = port;
	}

}
