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

import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.util.ComparePaymentInfoByCode;



/**
 * Implementation of the <code>ListPaymentComponent</code> interface.
 */
public class DefaultListPaymentComponent extends AbstractYComponent implements ListPaymentComponent
{
	private static final Logger log = Logger.getLogger(DefaultListPaymentComponent.class);

	private List<PaymentInfoModel> paymentInfoList = null;
	private ShowPaymentComponent showPaymentComponentTemplate = null;

	/**
	 * This event gets fired when the user tries to create a new credit card payment mode.
	 */
	public static class CreateCreditCardPaymentInfoEvent extends DefaultYComponentEventListener<ListPaymentComponent>
	{
		private static final long serialVersionUID = 2018905854746258167L;

		@Override
		public void actionListener(final YComponentEvent<ListPaymentComponent> event)
		{
			// DOCTODO Document reason, why this block is empty			
		}
	}

	/**
	 * This event gets fired when the user tries to create a new debit payment mode.
	 */
	public static class CreateDebitPaymentInfoEvent extends DefaultYComponentEventListener<ListPaymentComponent>
	{
		private static final long serialVersionUID = -8602941366977434262L;

		@Override
		public void actionListener(final YComponentEvent<ListPaymentComponent> event)
		{
			// DOCTODO Document reason, why this block is empty			
		}
	}

	private YComponentEventHandler<ListPaymentComponent> ehCreateCreditCard = null;
	private YComponentEventHandler<ListPaymentComponent> ehDebit = null;

	public DefaultListPaymentComponent()
	{
		super();
		this.ehCreateCreditCard = super.createEventHandler(new CreateCreditCardPaymentInfoEvent());
		this.ehDebit = super.createEventHandler(new CreateDebitPaymentInfoEvent());

		this.showPaymentComponentTemplate = super.newInstance(YComponent.SHOW_PAYMENT.viewId);
	}



	@Override
	public void validate()
	{
		if (getPaymentInfoList() == null)
		{
			log.debug("No paymentInfoList specified; creating default one");
			setPaymentInfoList(getDefaultPaymentInfoList());
		}
	}


	/**
	 * Returns the PaymentInfo list this model holds.
	 * 
	 * @return the PaymentInfo list this model holds.
	 */
	public List<PaymentInfoModel> getPaymentInfoList()
	{
		return paymentInfoList;
	}

	/**
	 * Sets the PaymentInfo list this model holds.
	 * 
	 * @param paymentInfoList
	 *           the PaymentInfo list this model should hold.
	 */
	public void setPaymentInfoList(final List<PaymentInfoModel> paymentInfoList)
	{
		this.paymentInfoList = paymentInfoList;
	}

	public ShowPaymentComponent getShowPaymentComponent()
	{
		return super.newInstance(getShowPaymentComponentTemplate());
	}

	public ShowPaymentComponent getShowPaymentComponentTemplate()
	{
		return this.showPaymentComponentTemplate;
	}

	public YComponentEventHandler<ListPaymentComponent> getCreateCreditCardEvent()
	{
		return this.ehCreateCreditCard;
	}

	public YComponentEventHandler<ListPaymentComponent> getCreateDebitEvent()
	{
		return this.ehDebit;
	}

	private List<PaymentInfoModel> getDefaultPaymentInfoList()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		final List<PaymentInfoModel> payments = new ArrayList<PaymentInfoModel>(userSession.getUser().getPaymentInfos());
		Collections.sort(payments, new ComparePaymentInfoByCode());
		return payments;
	}

	@Override
	public void refresh()
	{
		setPaymentInfoList(null);
	}

}
