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
package ystorefoundationpackage.yfaces.component.cart;

import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.faces.SfSelectItem;
import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * Implementation of the <code>SelectDeliveryModeComponent</code> interface.
 */
public class DefaultSelectDeliveryModeComponent extends AbstractYModel implements SelectDeliveryModeComponent
{

	private List<DeliveryModeModel> deliveryModes = null;
	private List<DeliveryModeModel> supportedModes = null;
	private CartModel cart = null;

	private DeliveryModeModel selectedMode = null;

	private transient SfSelectItemGroup<DeliveryModeModel> selector = null;

	/**
	 * This event gets fired when the user tries to select the delivery mode for the order.
	 */
	public static class SelectDeliveryModeEvent extends DefaultYEventListener<SelectDeliveryModeComponent>
	{
		@Override
		public void actionListener(final YEvent<SelectDeliveryModeComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			final SelectDeliveryModeComponent cmp = event.getComponent();
			userSession.getMessages().pushInfoMessage("deliveryModeChanged");
			final DeliveryModeModel selected = cmp.getDeliveryModeSelector().getSelectedValue();
			final CartModel cb = cmp.getCart();
			cb.setDeliveryAddress(userSession.getUser().getDefaultShipmentAddress());
			cb.setDeliveryMode(selected);
			cmp.setSelectedDeliveryMode(selected);

			reqCtx.getPlatformServices().getModelService().save(cb);
			reqCtx.getOrderManagement().updateCart(cb);

			//XXX: is this necessary anymore?
			userSession.getPropertyHandler().setPropertyChanged(SfSessionContext.CART, true);
		}

		@Override
		public void valueChangeListener(final YEvent<SelectDeliveryModeComponent> event)
		{
			final SelectDeliveryModeComponent cmp = event.getComponent();
			final DeliveryModeModel selected = (DeliveryModeModel) ((ValueChangeEvent) event.getFacesEvent()).getNewValue();
			cmp.getDeliveryModeSelector().setSelectedValue(selected);
			this.actionListener(event);
		}

	}

	private YEventHandler<SelectDeliveryModeComponent> ehSelectMode = null;

	public DefaultSelectDeliveryModeComponent()
	{
		super();
		this.ehSelectMode = super.createEventHandler(new SelectDeliveryModeEvent());
	}

	@Override
	public void validate()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		if (this.cart == null)
		{
			this.cart = userSession.getCart();
		}

		if (this.deliveryModes == null)
		{
			final List<DeliveryModeModel> modes = JaloBridge.getInstance().getAllDeliveryModes();
			this.setAvailableDeliveryModes(modes);
		}

		if (this.supportedModes == null)
		{
			final PaymentModeModel pm = userSession.getCart().getPaymentMode();
			final AddressModel deliveryAddress = userSession.getUser().getDefaultShipmentAddress();
			final List<DeliveryModeModel> modes = YStorefoundation.getRequestContext().getOrderManagement()
					.getSupportedDeliveryModes(pm, deliveryAddress);
			setSupportedDeliveryModes(modes);
		}

		final DeliveryModeModel dm = getCart().getDeliveryMode();
		this.setSelectedDeliveryMode(dm);

	}

	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isCartChanged() || session.isLanguageChanged())
		{
			this.supportedModes = null;
		}
	}

	public List<DeliveryModeModel> getAvailableDeliveryModes()
	{
		return this.deliveryModes;
	}

	public void setAvailableDeliveryModes(final List<DeliveryModeModel> list)
	{
		this.deliveryModes = list;
	}

	public List<DeliveryModeModel> getSupportedDeliveryModes()
	{
		return this.supportedModes;
	}

	public void setSupportedDeliveryModes(final List<DeliveryModeModel> list)
	{
		this.supportedModes = list;
	}

	public DeliveryModeModel getSelectedDeliveryMode()
	{
		return this.selectedMode;
	}

	public void setSelectedDeliveryMode(final DeliveryModeModel selected)
	{
		this.selectedMode = selected;
	}

	public CartModel getCart()
	{
		return this.cart;
	}

	public void setCart(final CartModel cart)
	{
		this.cart = cart;
	}

	public SfSelectItemGroup<DeliveryModeModel> getDeliveryModeSelector()
	{
		if (this.selector == null)
		{
			this.selector = createSelector();
		}
		return this.selector;
	}

	public YEventHandler<SelectDeliveryModeComponent> getSelectDeliveryModeEvent()
	{
		return this.ehSelectMode;
	}

	private SfSelectItemGroup<DeliveryModeModel> createSelector()
	{
		final SfSelectItemGroup<DeliveryModeModel> result = new SfSelectItemGroup<DeliveryModeModel>();
		final Set supported = new HashSet(getSupportedDeliveryModes());
		DeliveryModeModel firstValidMode = null;
		for (final DeliveryModeModel dm : this.getAvailableDeliveryModes())
		{
			final String label = dm.getName() != null ? dm.getName() : dm.getCode();
			final SelectItem s = new SfSelectItem<DeliveryModeModel>(dm, label);
			s.setDisabled(!supported.contains(dm));
			result.addSelectItem(s);
			if (firstValidMode == null && !s.isDisabled())
			{
				firstValidMode = dm;
			}
		}
		//only if this mode is allowed, it will be selected
		if (supported.contains(getSelectedDeliveryMode()))
		{
			result.setSelectedValue(getSelectedDeliveryMode());
		}
		else
		{
			final CartModel c = getCart();
			if (c.getDeliveryAddress() != null)
			{
				result.setSelectedValue(firstValidMode);
				this.setSelectedDeliveryMode(firstValidMode);
				c.setDeliveryMode(firstValidMode);
				YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(c);
				YStorefoundation.getRequestContext().getOrderManagement().updateCart(c);
			}
		}
		return result;
	}


}
