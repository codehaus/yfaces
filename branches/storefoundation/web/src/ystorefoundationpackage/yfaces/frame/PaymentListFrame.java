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

import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventListener;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;

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
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;

/**
 * Renders all payment information of the user as a list.
 * 
 */
public class PaymentListFrame extends AbstractYFrame {

	private static final long serialVersionUID = 1L;

	private static final String NAV_PAYMENT_EDIT = "paymentEditPage";
	private static final String NAV_PAYMENT_LIST = "paymentListPage";

	private ListPaymentComponent listPaymentCmp = null;
	private SelectPaymentModeComponent selectPaymentModeCmp = null;

	public PaymentListFrame() {
		super();
	}

	/**
	 * @return {@link ListPaymentComponent}
	 */
	public ListPaymentComponent getListPaymentComponent() {
		if (this.listPaymentCmp == null) {
			this.listPaymentCmp = this.createListPaymentComponent();
		}
		return this.listPaymentCmp;
	}

	public void setListPaymentComponent(ListPaymentComponent cmp) {
		this.listPaymentCmp = cmp;
	}

	/**
	 * @return {@link SelectPaymentModeComponent}
	 */
	public SelectPaymentModeComponent getSelectPaymentModeComponent() {
		if (this.selectPaymentModeCmp == null) {
			this.selectPaymentModeCmp = new DefaultSelectPaymentModeComponent();
		}
		return this.selectPaymentModeCmp;
	}

	public void setSelectPaymentModeComponent(SelectPaymentModeComponent cmp) {
		this.selectPaymentModeCmp = cmp;
	}

	/**
	 * External {@link YEventListener} for {@link ShowPaymentComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doEditPaymentInfo(final YEvent<ShowPaymentComponent> event) {
		final ShowPaymentComponent cmp = event.getComponent();

		final PaymentInfoEditFrame frame = getPaymentEditFrame();

		final PaymentInfoModel paymentInfo = cmp.getPaymentInfo();
		final EditPaymentComponent editCmp = createEditPaymentComponent(paymentInfo);

		frame.setEditPaymentComponent(editCmp);
	}

	/**
	 * External {@link YEventListener} for {@link ListPaymentComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doCreateCreditCardPaymentInfo(
			final YEvent<ListPaymentComponent> event) {
		final EditPaymentComponent editCmp = new DefaultEditCreditCardPaymentComponent();
		getPaymentEditFrame().setEditPaymentComponent(editCmp);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(
				NAV_PAYMENT_LIST);
	}

	/**
	 * External {@link YEventListener} for {@link ListPaymentComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doCreateDebitPaymentInfo(
			final YEvent<ListPaymentComponent> event) {
		final EditPaymentComponent editCmp = new DefaultEditPaymentComponent();
		getPaymentEditFrame().setEditPaymentComponent(editCmp);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(
				NAV_PAYMENT_LIST);
	}

	/**
	 * Internal. Creates a {@link ListPaymentComponent} for this frame.
	 * 
	 * @return {@link ListPaymentComponent}
	 */
	private ListPaymentComponent createListPaymentComponent() {
		final ListPaymentComponent result = new DefaultListPaymentComponent();

		result.getShowPaymentComponent().getCustomPaymentEvent().setEnabled(
				false);
		result.getShowPaymentComponent().getEditPaymentEvent().setEnabled(true);
		result.getShowPaymentComponent().getEditPaymentEvent().getListener()
				.setAction(NAV_PAYMENT_EDIT);
		result.getShowPaymentComponent().getEditPaymentEvent().getListener()
				.setActionListener(
						super.createExpressionString("doEditPaymentInfo"));

		final YEventListener<ListPaymentComponent> listener = result
				.getCreateCreditCardEvent().getListener();
		listener.setAction(NAV_PAYMENT_EDIT);
		listener.setActionListener(super
				.createExpressionString("doCreateCreditCardPaymentInfo"));

		result.getCreateDebitEvent().getListener().setAction(NAV_PAYMENT_EDIT);
		result.getCreateDebitEvent().getListener().setActionListener(
				super.createExpressionString("doCreateDebitPaymentInfo"));

		return result;
	}

	/**
	 * Internal. Creates a {@link EditPaymentComponent} for this frame.
	 * 
	 * @param paymentInfo
	 * @return {@link EditPaymentComponent}
	 */
	private EditPaymentComponent createEditPaymentComponent(
			final PaymentInfoModel paymentInfo) {
		EditPaymentComponent result = null;

		if (paymentInfo instanceof CreditCardPaymentInfoModel) {
			result = new DefaultEditCreditCardPaymentComponent();
		} else {
			if (paymentInfo instanceof DebitPaymentInfoModel) {
				result = new DefaultEditPaymentComponent();
			} else {
				throw new StorefoundationException("");
			}
		}

		result.setPaymentInfo(paymentInfo);
		result.getSavePaymentInfoEvent().getListener().setAction(
				NAV_PAYMENT_LIST);
		return result;
	}

	private PaymentInfoEditFrame getPaymentEditFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final PaymentInfoEditFrame result = nextPage
				.getOrCreateFrame(PaymentInfoEditFrame.class);
		return result;
	}
}
