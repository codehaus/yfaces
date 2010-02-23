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

import de.hybris.platform.core.model.user.AddressModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * Implementation of the <code>ListAddressComponent</code> interface.
 */
public class DefaultListAddressComponent extends AbstractYComponent implements ListAddressComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultListAddressComponent.class);

	private List<AddressModel> addressList = null;

	private ShowAddressComponent showAddressCmpTemplate = null;
	private YComponentEventHandler<ListAddressComponent> ehCreateAddr = null;

	/**
	 * Constructor.
	 */
	public DefaultListAddressComponent()
	{
		super();
		this.ehCreateAddr = super.createEventHandler();
		this.showAddressCmpTemplate = super.newInstance(YComponent.SHOW_ADDRESS.viewId);
	}

	@Override
	public void validate()
	{
		if (getAddressList() == null)
		{
			LOG.debug("No addressList specified; creating default one");
			setAddressList(getDefaultAddressList());
		}
	}

	public List<AddressModel> getAddressList()
	{
		return this.addressList;
	}

	public void setAddressList(final List<AddressModel> addressList)
	{
		this.addressList = addressList;
	}

	public ShowAddressComponent getShowAddressComponent()
	{
		return super.newInstance(this.getShowAddressComponentTemplate());
	}

	public ShowAddressComponent getShowAddressComponentTemplate()
	{
		return this.showAddressCmpTemplate;
	}

	public YComponentEventHandler<ListAddressComponent> getCreateAddressEvent()
	{
		return this.ehCreateAddr;
	}

	private List<AddressModel> getDefaultAddressList()
	{
		return new ArrayList<AddressModel>(YStorefoundation.getRequestContext().getSessionContext().getUser().getAddresses());
	}

	@Override
	public void refresh()
	{
		setAddressList(null);
	}

}
