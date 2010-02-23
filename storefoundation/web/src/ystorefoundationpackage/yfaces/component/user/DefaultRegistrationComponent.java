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

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>RegistrationComponent</code> interface.
 */
public class DefaultRegistrationComponent extends AbstractYComponent implements RegistrationComponent
{
	private static final long serialVersionUID = 8712436172843689193L;

	private String login;
	private String password;
	private String passwordRepeat;

	private List<? extends SelectItem> countries;

	private AddressModel address;

	private Boolean isPasswordOnly = null;
	private Boolean isAddressEnabled = null;

	private YComponentEventHandler<RegistrationComponent> ehRegister = null;

	/**
	 * Constructor.
	 */
	public DefaultRegistrationComponent()
	{
		super();
		this.ehRegister = super.createEventHandler(new RegistrationEvent());
	}

	@Override
	public void validate()
	{
		if (getCountries() == null)
		{
			setCountries(getCountriesDefault());
		}

		if (getAddress() == null)
		{
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
			setAddress(new AddressModel());
			getAddress().setCountry((CountryModel) getCountries().get(0).getValue());
			getAddress().setOwner(userSession.getUser());
		}

		if (getIsPasswordEnabled() == null)
		{
			this.setIsPasswordEnabled(Boolean.TRUE);
		}

		if (getIsAddressEnabled() == null)
		{
			this.setIsAddressEnabled(Boolean.TRUE);
		}

	}


	public String getLogin()
	{
		return login;
	}

	public void setLogin(final String login)
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(final String password)
	{
		this.password = password;
	}

	public String getPasswordRepeat()
	{
		return passwordRepeat;
	}

	public void setPasswordRepeat(final String passwordRepeat)
	{
		this.passwordRepeat = passwordRepeat;
	}

	public AddressModel getAddress()
	{
		return address;
	}

	public void setAddress(final AddressModel address)
	{
		this.address = address;
	}

	public List<? extends SelectItem> getCountries()
	{
		return countries;
	}

	public void setCountries(final List<? extends SelectItem> countries)
	{
		this.countries = countries;
	}

	public Boolean getIsPasswordEnabled()
	{
		return this.isPasswordOnly;
	}

	public void setIsPasswordEnabled(final Boolean isPasswordOnly)
	{
		this.isPasswordOnly = isPasswordOnly;
	}

	public Boolean getIsAddressEnabled()
	{
		return this.isAddressEnabled;
	}

	public void setIsAddressEnabled(final Boolean isAddressEnabled)
	{
		this.isAddressEnabled = isAddressEnabled;
	}



	public YComponentEventHandler<RegistrationComponent> getRegisterEvent()
	{
		return this.ehRegister;
	}


	/**
	 * Default value for available countries. All countries provided by the hybris platform.
	 */
	private List<SelectItem> getCountriesDefault()
	{
		final List<SelectItem> result = new ArrayList<SelectItem>();

		final Collection<CountryModel> countries = YStorefoundation.getRequestContext().getPlatformServices().getI18NService()
				.getAllCountries();
		for (final CountryModel country : countries)
		{
			result.add(new SelectItem(country, country.getName()));
		}
		return result;
	}


}
