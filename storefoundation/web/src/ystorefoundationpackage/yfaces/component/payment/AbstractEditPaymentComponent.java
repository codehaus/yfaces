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

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;
import org.codehaus.yfaces.context.YPageContext;

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

import ystorefoundationpackage.domain.YStorefoundation;



/**
 * General implementation for <code>EditPaymentComponent</code>
 */
public abstract class AbstractEditPaymentComponent extends AbstractYComponent implements EditPaymentComponent
{
	private static final long serialVersionUID = 5769978947928383354L;


	/**
	 * This event gets fired when the user tries to save the changes for the selected payment mode.
	 */
	public static class SavePaymentInfoEvent extends DefaultYComponentEventListener<EditPaymentComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<EditPaymentComponent> event)
		{
			final EditPaymentComponent cmp = event.getComponent();
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().save(cmp.getPaymentInfo());
			//update user in session after the payment info is changed
			YStorefoundation.getRequestContext().getPlatformServices().getModelService().refresh(
					YStorefoundation.getRequestContext().getSessionContext().getUser());
		}
	}

	/**
	 * This event gets fired when the user cancels the changes. The previous page will be loaded.
	 */
	public static class CancelEditPaymentInfoEvent extends DefaultYComponentEventListener<EditPaymentComponent>
	{

		private static final long serialVersionUID = 6455652696810898534L;

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

	private PaymentInfoModel paymentInfo;
	private YComponentEventHandler<EditPaymentComponent> ehSave = null;
	private YComponentEventHandler<EditPaymentComponent> ehCancelEdit = null;

	public AbstractEditPaymentComponent()
	{
		super();
		this.ehSave = createEventHandler(new SavePaymentInfoEvent());
		this.ehCancelEdit = createEventHandler(new CancelEditPaymentInfoEvent());
	}

	public YComponentEventHandler<EditPaymentComponent> getSavePaymentInfoEvent()
	{
		return this.ehSave;
	}

	public YComponentEventHandler<EditPaymentComponent> getCancelEditPaymentInfoEvent()
	{
		return this.ehCancelEdit;
	}

	/**
	 * Gets the {@link PaymentInfoModel} this model holds.
	 * 
	 * @return the PaymentInfoModel this model holds.
	 */
	public PaymentInfoModel getPaymentInfo()
	{
		return paymentInfo;
	}

	/**
	 * Sets the {@link PaymentInfoModel} this model holds.
	 * 
	 * @param paymentInfo
	 *           the PaymentInfoModel this model holds.
	 */
	public void setPaymentInfo(final PaymentInfoModel paymentInfo)
	{
		this.paymentInfo = paymentInfo;
	}

}
