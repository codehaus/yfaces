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
package ystorefoundationpackage.yfaces.frame;

import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventListener;
import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.cart.DefaultSelectPaymentModeComponent;
import ystorefoundationpackage.yfaces.component.cart.SelectPaymentModeComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultEditCreditCardPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultEditPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultListPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.EditPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ListPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ShowPaymentComponent;


/**
 * Renders all payment information of the user as a list.
 * 
 */
public class PaymentListFrame extends AbstractYFrame
{

	private static final long serialVersionUID = 1L;

	private static final String NAV_PAYMENT_EDIT = "paymentEditPage";
	private static final String NAV_PAYMENT_LIST = "paymentListPage";

	private YComponentBinding<ListPaymentComponent> listPaymentCmp = null;
	private YComponentBinding<SelectPaymentModeComponent> selectPaymentModeCmp = null;

	public PaymentListFrame()
	{
		super();
		this.listPaymentCmp = super.createComponentBinding(this.createListPaymentComponent());
		this.selectPaymentModeCmp = super
				.createComponentBinding((SelectPaymentModeComponent) new DefaultSelectPaymentModeComponent());
	}

	/**
	 * @return {@link YComponentBinding} for {@link ListPaymentComponent}
	 */
	public YComponentBinding<ListPaymentComponent> getListPaymentComponent()
	{
		return this.listPaymentCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link SelectPaymentModeComponent}
	 */
	public YComponentBinding<SelectPaymentModeComponent> getSelectPaymentModeComponent()
	{
		return this.selectPaymentModeCmp;
	}

	/**
	 * External {@link YComponentEventListener} for {@link ShowPaymentComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doEditPaymentInfo(final YComponentEvent<ShowPaymentComponent> event)
	{
		final ShowPaymentComponent cmp = event.getComponent();

		final PaymentInfoEditFrame frame = getPaymentEditFrame();

		final PaymentInfoModel paymentInfo = cmp.getPaymentInfo();
		final EditPaymentComponent editCmp = createEditPaymentComponent(paymentInfo);

		frame.getEditPaymentComponent().setValue(editCmp);
	}

	/**
	 * External {@link YComponentEventListener} for {@link ListPaymentComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doCreateCreditCardPaymentInfo(final YComponentEvent<ListPaymentComponent> event)
	{
		final EditPaymentComponent editCmp = new DefaultEditCreditCardPaymentComponent();
		getPaymentEditFrame().getEditPaymentComponent().setValue(editCmp);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(NAV_PAYMENT_LIST);
	}

	/**
	 * External {@link YComponentEventListener} for {@link ListPaymentComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doCreateDebitPaymentInfo(final YComponentEvent<ListPaymentComponent> event)
	{
		final EditPaymentComponent editCmp = new DefaultEditPaymentComponent();
		getPaymentEditFrame().getEditPaymentComponent().setValue(editCmp);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(NAV_PAYMENT_LIST);
	}

	/**
	 * Internal. Creates a {@link ListPaymentComponent} for this frame.
	 * 
	 * @return {@link ListPaymentComponent}
	 */
	private ListPaymentComponent createListPaymentComponent()
	{
		final ListPaymentComponent result = new DefaultListPaymentComponent();

		result.getShowPaymentComponent().getCustomPaymentEvent().setEnabled(false);
		result.getShowPaymentComponent().getEditPaymentEvent().setEnabled(true);
		result.getShowPaymentComponent().getEditPaymentEvent().getListener().setAction(NAV_PAYMENT_EDIT);
		result.getShowPaymentComponent().getEditPaymentEvent().getListener().setActionListener(
				super.createExpressionString("doEditPaymentInfo"));

		final YComponentEventListener<ListPaymentComponent> listener = result.getCreateCreditCardEvent().getListener();
		listener.setAction(NAV_PAYMENT_EDIT);
		listener.setActionListener(super.createExpressionString("doCreateCreditCardPaymentInfo"));

		result.getCreateDebitEvent().getListener().setAction(NAV_PAYMENT_EDIT);
		result.getCreateDebitEvent().getListener().setActionListener(super.createExpressionString("doCreateDebitPaymentInfo"));

		return result;
	}

	/**
	 * Internal. Creates a {@link EditPaymentComponent} for this frame.
	 * 
	 * @param paymentInfo
	 * @return {@link EditPaymentComponent}
	 */
	private EditPaymentComponent createEditPaymentComponent(final PaymentInfoModel paymentInfo)
	{
		EditPaymentComponent result = null;

		if (paymentInfo instanceof CreditCardPaymentInfoModel)
		{
			result = new DefaultEditCreditCardPaymentComponent();
		}
		else
		{
			if (paymentInfo instanceof DebitPaymentInfoModel)
			{
				result = new DefaultEditPaymentComponent();
			}
			else
			{
				throw new StorefoundationException("");
			}
		}

		result.setPaymentInfo(paymentInfo);
		result.getSavePaymentInfoEvent().getListener().setAction(NAV_PAYMENT_LIST);
		return result;
	}

	private PaymentInfoEditFrame getPaymentEditFrame()
	{
		final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final PaymentInfoEditFrame result = nextPage.getOrCreateFrame(PaymentInfoEditFrame.class);
		return result;
	}
}
