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

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>ShowAddressComponent</code> interface.
 */
public class DefaultShowAddressComponent extends AbstractYComponent implements ShowAddressComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultShowAddressComponent.class);

	private YComponentEventHandler<ShowAddressComponent> ehEditAddr = null;
	private YComponentEventHandler<ShowAddressComponent> ehDeleteAddr = null;
	private YComponentEventHandler<ShowAddressComponent> ehAsDeliveryAddr = null;
	private YComponentEventHandler<ShowAddressComponent> ehAsPaymentAddr = null;
	private YComponentEventHandler<ShowAddressComponent> ehCustom = null;


	private AddressModel addressBean = null;
	private Object owner = null;
	private Boolean defaultDeliveryAddress = null;
	private Boolean defaultPaymentAddress = null;


	/**
	 * This event gets fired when the user tries to delete the selected address.
	 */
	public static class DeleteAddressEvent extends DefaultYComponentEventListener<ShowAddressComponent>
	{
		private static final long serialVersionUID = 5749625285432117567L;

		@Override
		public String action()
		{
			YStorefoundation.getRequestContext().redirect(true);
			return null;
		}

		@Override
		public void actionListener(final YComponentEvent<ShowAddressComponent> event)
		{
			final ShowAddressComponent cmp = event.getComponent();
			final AddressModel address = cmp.getAddress();
			final ModelService ms = YStorefoundation.getRequestContext().getPlatformServices().getModelService();
			ms.remove(address);
			cmp.setAddress(null);
			//update user in session after the address is deleted
			ms.refresh(YStorefoundation.getRequestContext().getSessionContext().getUser());
		}
	}

	/**
	 * This event gets fired when the user tries to set the selected address as the default payment address.
	 */
	public static class ChooseAsPaymentAddressEvent extends DefaultYComponentEventListener<ShowAddressComponent>
	{
		private static final long serialVersionUID = -5147823576659380171L;


		@SuppressWarnings("unchecked")
		@Override
		public void actionListener(final YComponentEvent<ShowAddressComponent> event)
		{
			final ShowAddressComponent cmp = event.getComponent();
			final Object owner = cmp.getOwner();
			final AddressModel address = cmp.getAddress();
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			if (owner instanceof UserModel)
			{
				((UserModel) owner).setDefaultPaymentAddress(address);
				reqCtx.getPlatformServices().getModelService().save(owner);
				reqCtx.getSessionContext().getUser().setDefaultPaymentAddress(address);
			}
			else
			{
				((CartModel) owner).setPaymentAddress(address);
				reqCtx.getPlatformServices().getModelService().save(owner);
				// Webfoundation.getInstance().getServices().getCartService().saveOrUpdate((CartModel) owner);
			}
		}
	}

	/**
	 * This event gets fired when the user tries to set the selected address as the default delivery address.
	 */
	public static class ChooseAsDeliveryAddressEvent extends DefaultYComponentEventListener<ShowAddressComponent>
	{
		private static final long serialVersionUID = -982874318133922258L;


		@SuppressWarnings("unchecked")
		@Override
		public void actionListener(final YComponentEvent<ShowAddressComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final ShowAddressComponent cmp = event.getComponent();
			final Object owner = cmp.getOwner();
			final AddressModel address = cmp.getAddress();

			if (owner instanceof UserModel)
			{
				((UserModel) owner).setDefaultShipmentAddress(address);
				reqCtx.getPlatformServices().getModelService().save(owner);
				reqCtx.getSessionContext().getUser().setDefaultShipmentAddress(address);
			}
			else
			{
				((CartModel) owner).setDeliveryAddress(address);
				reqCtx.getPlatformServices().getModelService().save(owner);
				//service.getCartService().saveOrUpdate((CartModel) owner);
			}
		}
	}

	/**
	 * Constructor.
	 */
	public DefaultShowAddressComponent()
	{
		super();
		this.ehAsDeliveryAddr = super.createEventHandler(new ChooseAsDeliveryAddressEvent());
		this.ehAsPaymentAddr = super.createEventHandler(new ChooseAsPaymentAddressEvent());
		this.ehCustom = super.createEventHandler();
		this.ehDeleteAddr = super.createEventHandler(new DeleteAddressEvent());
		this.ehEditAddr = super.createEventHandler();

		this.ehCustom.setEnabled(false);
	}


	public DefaultShowAddressComponent(final YComponent template)
	{
		super();
		final ShowAddressComponent cmp = (ShowAddressComponent) template;
		this.ehAsDeliveryAddr = cmp.getChooseAddressAsDeliveryEvent();
		this.ehAsPaymentAddr = cmp.getChooseAddressAsPaymentEvent();
		this.ehCustom = cmp.getCustomAddressEvent();
		this.ehDeleteAddr = cmp.getDeleteAddressEvent();
		this.ehEditAddr = cmp.getEditAddressEvent();
	}


	@Override
	public void validate()
	{
		if (getAddress() == null)
		{
			setAddress(this.getDefaultAddress());
		}

		if (getOwner() == null)
		{
			//setOwner(this.getDefaultOwner());
			this.owner = YStorefoundation.getRequestContext().getSessionContext().getUser();
		}

		if (getDefaultDeliveryAddress() == null)
		{
			setDefaultDeliveryAddress(this.isDefaultDeliveryAddress(getAddress()));
		}

		if (getDefaultPaymentAddress() == null)
		{
			setDefaultPaymentAddress(this.isDefaultPaymentAddress(getAddress()));
		}
	}

	@Override
	public void refresh()
	{
		if (getAddress() != null)
		{
			getModelService().refresh(getAddress());
			//			setAddress((AddressBean) Webfoundation.getInstance().getBeanFactory().getUpdate(getAddress()));
		}
	}


	public AddressModel getAddress()
	{
		return this.addressBean;
	}

	public void setAddress(final AddressModel address)
	{
		this.addressBean = address;
	}


	public Object getOwner()
	{
		return this.owner;
	}

	public void setOwner(final Object target)
	{
		this.owner = target;
	}


	public Boolean getDefaultDeliveryAddress()
	{
		return this.defaultDeliveryAddress;
	}

	public void setDefaultDeliveryAddress(final Boolean isDefaultDelivery)
	{
		this.defaultDeliveryAddress = isDefaultDelivery;
	}

	public Boolean getDefaultPaymentAddress()
	{
		return this.defaultPaymentAddress;
	}

	public void setDefaultPaymentAddress(final Boolean isDefaultPayment)
	{
		this.defaultPaymentAddress = isDefaultPayment;
	}


	public YComponentEventHandler<ShowAddressComponent> getEditAddressEvent()
	{
		return this.ehEditAddr;
	}

	public YComponentEventHandler<ShowAddressComponent> getDeleteAddressEvent()
	{
		return this.ehDeleteAddr;
	}

	public YComponentEventHandler<ShowAddressComponent> getCustomAddressEvent()
	{
		return this.ehCustom;
	}

	public YComponentEventHandler<ShowAddressComponent> getChooseAddressAsDeliveryEvent()
	{
		return this.ehAsDeliveryAddr;
	}

	public YComponentEventHandler<ShowAddressComponent> getChooseAddressAsPaymentEvent()
	{
		return this.ehAsPaymentAddr;
	}






	//	private Object getDefaultOwner()
	//	{
	//		final SfSessionContext userSession = SfRequestContext.getCurrentContext().getSessionContext();
	//		return userSession.getUser();
	//	}

	private AddressModel getDefaultAddress()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		final UserModel user = userSession.getUser();
		final Collection<AddressModel> addresses = user.getAddresses();
		final AddressModel result = (addresses.isEmpty()) ? new AddressModel() : addresses.iterator().next();
		return result;
	}

	@SuppressWarnings("boxing")
	private Boolean isDefaultDeliveryAddress(final AddressModel bean)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		final UserModel user = userSession.getUser();
		final AddressModel defaultShippingAddress = user.getDefaultShipmentAddress();

		return Boolean.valueOf(bean.equals(defaultShippingAddress));
	}

	@SuppressWarnings("boxing")
	public Boolean isDefaultPaymentAddress(final AddressModel bean)
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		final UserModel user = userSession.getUser();
		final AddressModel defaultPaymentAddress = user.getDefaultPaymentAddress();

		return Boolean.valueOf(bean.equals(defaultPaymentAddress));
	}

	private ModelService getModelService()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
	}


}
