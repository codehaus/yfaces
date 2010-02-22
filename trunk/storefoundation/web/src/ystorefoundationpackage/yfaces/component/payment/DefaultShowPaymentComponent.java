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
package ystorefoundationpackage.yfaces.component.payment;

import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PaymentModes;
import ystorefoundationpackage.domain.impl.JaloBridge;


/**
 * Implementation of the <code>ShowPaymentComponent</code> interface.
 */
public class DefaultShowPaymentComponent extends AbstractYComponent implements ShowPaymentComponent
{

	/**
	 * This event gets fired when the user tries to remove the selected payment mode.
	 */
	public class DeletePaymentInfoEvent extends DefaultYComponentEventListener<ShowPaymentComponent>
	{
		private static final long serialVersionUID = -9188215350741106605L;

		@Override
		public String action()
		{
			YStorefoundation.getRequestContext().redirect(true);
			return null;
		}

		@Override
		public void actionListener(final YComponentEvent<ShowPaymentComponent> event)
		{
			final PaymentInfoModel paymentInfo = event.getComponent().getPaymentInfo();
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().remove(paymentInfo);
			//update user in session after the payment info is deleted
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().refresh(
					YStorefoundation.getRequestContext().getSessionContext().getUser());
		}
	}

	private PaymentInfoModel pib = null;
	private PaymentModeModel type = null;
	private CartModel cart = null;
	private EnumerationValueModel paymentInfoType = null;

	private YComponentEventHandler<ShowPaymentComponent> ehDelete = null;
	private YComponentEventHandler<ShowPaymentComponent> ehEdit = null;
	private YComponentEventHandler<ShowPaymentComponent> ehCustom = null;


	public DefaultShowPaymentComponent()
	{
		super();
		this.ehCustom = super.createEventHandler();
		this.ehEdit = super.createEventHandler();
		this.ehDelete = super.createEventHandler(new DeletePaymentInfoEvent());

		this.ehCustom.setEnabled(false);

	}

	public DefaultShowPaymentComponent(final YComponent template)
	{
		super();
		final ShowPaymentComponent cmp = (ShowPaymentComponent) template;
		this.ehCustom = cmp.getCustomPaymentEvent();
		this.ehDelete = cmp.getDeletePaymentEvent();
		this.ehEdit = cmp.getEditPaymentEvent();
	}

	public void setCart(final CartModel cart)
	{
		this.cart = cart;
	}

	public CartModel getCart()
	{
		return this.cart;
	}


	public PaymentInfoModel getPaymentInfo()
	{
		return this.pib;
	}

	public void setPaymentInfo(final PaymentInfoModel paymentInfo)
	{
		this.pib = paymentInfo;
		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			final CreditCardType ccType = ((CreditCardPaymentInfoModel) pib).getType();
			this.paymentInfoType = YStorefoundation.getRequestContext().getPlatformServices().getTypeService().getEnumerationValue(
					ccType);
		}
	}

	public EnumerationValueModel getPaymentInfoType()
	{
		return this.paymentInfoType;
	}


	public PaymentModeModel getPaymentMode()
	{
		return this.type;
	}

	public void setPaymentMode(final PaymentModeModel mode)
	{
		this.type = mode;
	}

	public YComponentEventHandler<ShowPaymentComponent> getCustomPaymentEvent()
	{
		return this.ehCustom;
	}

	public YComponentEventHandler<ShowPaymentComponent> getDeletePaymentEvent()
	{
		return this.ehDelete;
	}

	public YComponentEventHandler<ShowPaymentComponent> getEditPaymentEvent()
	{
		return this.ehEdit;
	}


	@Override
	public void validate()
	{
		final CartModel cart = this.cart != null ? this.cart : YStorefoundation.getRequestContext().getSessionContext().getCart();

		//when no paymenttype was set...
		if (getPaymentMode() == null)
		{
			//...and no paymentInfo was set, take paymentInfo from current cart
			if (getPaymentInfo() == null)
			{
				//	setPaymentInfo(sCart.getPaymentInfo(cart));
				setPaymentInfo(cart.getPaymentInfo());
			}

			//...take paymentType from from current cart
			PaymentModeModel paymentMode = cart.getPaymentMode();

			//...or take first supported paymentType from models paymentInfo
			if (getPaymentInfo() != null || paymentMode == null)
			{
				paymentMode = this.detectPaymentMode(getPaymentInfo());
			}

			//finally set paymenttype
			setPaymentMode(paymentMode);
		}
	}

	@Override
	public void refresh()
	{
		// final EnumerationValueModel model = getPaymentInfoType(); // comment out to raise TPL-1215
		// final String name = model.getName();
		// System.out.println(name);

		if (getPaymentInfo() != null)
		{
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().refresh(getPaymentInfo());
		}
	}

	private PaymentModeModel detectPaymentMode(final PaymentInfoModel paymentInfo)
	{

		String result = PaymentModes.ADVANCE.getCode();

		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			result = PaymentModes.CREDIT.getCode();
		}
		else
		{
			if (paymentInfo instanceof DebitPaymentInfoModel)
			{
				result = PaymentModes.DEBIT.getCode();
			}
		}

		final PaymentModeModel _result = JaloBridge.getInstance().getPaymentMode(result);
		return _result;

	}


}
