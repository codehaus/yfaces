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
package ystorefoundationpackage.yfaces.component.address;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;
import de.hybris.yfaces.context.YPageContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>EditAddressComponent</code> interface.
 */
public class DefaultEditAddressComponent extends AbstractYComponent implements EditAddressComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultEditAddressComponent.class);

	private AddressModel address = null;
	private List<? extends SelectItem> countries = null;

	private YComponentEventHandler<EditAddressComponent> ehEditAddr = null;
	private YComponentEventHandler<EditAddressComponent> ehCancelEditAddr = null;


	/**
	 * This event gets fired when the user tries to save the address changes.
	 */
	public static class SaveAddressEventListener extends DefaultYComponentEventListener<EditAddressComponent>
	{

		private static final long serialVersionUID = 888791411364716766L;

		@Override
		public void actionListener(final YComponentEvent<EditAddressComponent> event)
		{
			final AddressModel address = event.getComponent().getAddress();
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(address);
			//update user in session since the address is changed
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().refresh(
					YStorefoundation.getRequestContext().getSessionContext().getUser());
		}
	}

	/**
	 * This event gets fired when the user cancels the change of the address. The previous page will be loaded.
	 */
	public static class CancelEditAddressEventListener extends DefaultYComponentEventListener<EditAddressComponent>
	{

		private static final long serialVersionUID = -3073549927821788196L;

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

	/**
	 * Constructor.
	 */
	public DefaultEditAddressComponent()
	{
		super();
		this.ehEditAddr = super.createEventHandler(new SaveAddressEventListener());
		this.ehCancelEditAddr = super.createEventHandler(new CancelEditAddressEventListener());
	}


	@Override
	public void validate()
	{
		if (getAvailableCountries() == null)
		{
			setAvailableCountries(getCountriesDefault());
			LOG.debug("Created default countries (" + getAvailableCountries().size() + ")");
		}

		if (getAddress() == null)
		{
			//setAddress(getAddressDefault((CountryBean) getAvailableCountries().get(0).getValue()));
			setAddress(getAddressDefault((CountryModel) getAvailableCountries().get(0).getValue()));
			LOG.debug("Created default address (empty)");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.address.edit.AddressEditComponent#getAddress()
	 */
	public AddressModel getAddress()
	{
		return this.address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.address.edit.AddressEditComponent#getAvailableCountries()
	 */
	public List<? extends SelectItem> getAvailableCountries()
	{
		return this.countries;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.faces.components.address.edit.AddressEditComponent#setAddress(de.hybris.platform.webfoundation
	 * .bean.ItemBean)
	 */
	public void setAddress(final AddressModel address)
	{
		this.address = address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.faces.components.address.edit.AddressEditComponent#setAvailableCountries(java.util.List)
	 */
	public void setAvailableCountries(final List<? extends SelectItem> countries)
	{
		this.countries = countries;
	}


	public YComponentEventHandler<EditAddressComponent> getSaveAddressEvent()
	{
		return this.ehEditAddr;
	}

	public YComponentEventHandler<EditAddressComponent> getCancelEditAddressEvent()
	{
		return this.ehCancelEditAddr;
	}


	/**
	 * Default value for available countries. All countries provided by the hybris platform.
	 */
	private List<SelectItem> getCountriesDefault()
	{
		final List<SelectItem> _countries = new ArrayList<SelectItem>();
		//		for (final CountryBean country : Webfoundation.getInstance().getServices().getC2LService().getActiveCountries())
		//		{
		//			_countries.add(new SelectItem(country, country.getName()));
		//		}
		final Collection<CountryModel> countries = getI18NService().getAllCountries();
		for (final CountryModel country : countries)
		{
			_countries.add(new SelectItem(country, country.getName()));
		}
		return _countries;
	}

	/**
	 * Default value for Address. Empty Address; takes the first country (of all available ones) as preselected one and
	 * uses the current user as owner.
	 */
	private AddressModel getAddressDefault(final CountryModel preselectedCountry)
	{
		final AddressModel result = new AddressModel();
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		result.setCountry(preselectedCountry);
		result.setOwner(userSession.getUser());

		return result;
	}

	private I18NService getI18NService()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getI18NService();
	}

}
