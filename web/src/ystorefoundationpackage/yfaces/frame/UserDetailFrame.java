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
import org.codehaus.yfaces.component.YModelBinding;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.address.DefaultListAddressComponent;
import ystorefoundationpackage.yfaces.component.address.EditAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ListAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ShowAddressComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultEditCreditCardPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultEditPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultListPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.EditPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ListPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ShowPaymentComponent;
import ystorefoundationpackage.yfaces.component.user.ChangePasswordComponent;
import ystorefoundationpackage.yfaces.component.user.DefaultChangePasswordComponent;
import ystorefoundationpackage.yfaces.component.user.DefaultUserInfoComponent;
import ystorefoundationpackage.yfaces.component.user.UserInfoComponent;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.DebitPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;

/**
 * Renders the user information in detail, such as user addresses, and payment
 * information.
 * 
 */
public class UserDetailFrame extends AbstractYFrame {
	private static final long serialVersionUID = -1550283086759307248L;

	// Navigation targets
	private static final String NAV_USERDETAIL = "userDetailPage";
	private static final String NAV_ADDRESS_EDIT = "addressEditPage";

	private static final String NAV_PAYMENTINFO_EDIT = "paymentEditPage";

	private static final String USER_INFO_CHANGE_PASSWORD_ACTION = "changePasswordPage";
	private static final String USER_INFO_SHOW_ORDER_HISTORY_ACTION = "orderHistoryPage";

	private ListAddressComponent listAddressCmp = null;
	private ListPaymentComponent listPaymentCmp = null;
	private UserInfoComponent userInfoCmp = null;

	public UserDetailFrame() {
		super();
	}

	/**
	 * @return {@link YModelBinding} for {@link ListAddressComponent}
	 */
	public ListAddressComponent getListAddressComponent() {
		if (this.listAddressCmp == null) {
			this.listAddressCmp = this.createListAddressComponent();
		}
		return this.listAddressCmp;
	}

	public void setListAddressComponent(ListAddressComponent cmp) {
		this.listAddressCmp = cmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link ListPaymentComponent}
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
	 * @return {@link YModelBinding} for {@link UserInfoComponent}
	 */
	public UserInfoComponent getUserInfoComponent() {
		if (this.userInfoCmp == null) {
			this.userInfoCmp = this.createUserInfoComponent();
		}
		return this.userInfoCmp;
	}

	public void setUserInfoComponent(UserInfoComponent cmp) {
		this.userInfoCmp = cmp;
	}

	/**
	 * External {@link YEventListener} for {@link ListAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doCreateAddress(final YEvent<ListAddressComponent> event) {
		// retrieve EditAddressComponent from AddressEditFrame...
		final EditAddressComponent editCmp = getAddressEditFrame()
				.getEditAddressComponent();

		// ...and set empty address and return action
		editCmp.setAddress(null);
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditAddressEvent().getListener().setAction(
				NAV_USERDETAIL);
	}

	/**
	 * External {@link YEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doEditAddress(final YEvent<ShowAddressComponent> event) {
		// retrieve Address which shall be edited
		final ShowAddressComponent cmp = event.getComponent();
		final AddressModel address = cmp.getAddress();

		// retrieve EditAddressComponent from AddressEditFrame
		final EditAddressComponent editCmp = getAddressEditFrame()
				.getEditAddressComponent();
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditAddressEvent().getListener().setAction(
				NAV_USERDETAIL);
		editCmp.setAddress(address);
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
		editCmp.getSavePaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		getPaymentInfoEditFrame().setEditPaymentComponent(editCmp);
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
		editCmp.getSavePaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		getPaymentInfoEditFrame().setEditPaymentComponent(editCmp);
	}

	/**
	 * External {@link YEventListener} for {@link ShowPaymentComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doEditPaymentInfo(final YEvent<ShowPaymentComponent> event) {
		final ShowPaymentComponent cmp = event.getComponent();
		final PaymentInfoModel paymentInfo = cmp.getPaymentInfo();
		final EditPaymentComponent editCmp = this
				.createEditPaymentComponent(paymentInfo);

		editCmp.setPaymentInfo(paymentInfo);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(
				NAV_USERDETAIL);
		getPaymentInfoEditFrame().setEditPaymentComponent(editCmp);
	}

	public void doChangePassword(final YEvent<DefaultUserInfoComponent> event) {
		final ChangePasswordFrame frame = getChangePasswordFrame();
		final ChangePasswordComponent cmp = new DefaultChangePasswordComponent();
		cmp.getCancelChangePasswordEvent().getListener().setAction(
				NAV_USERDETAIL);
		frame.setChangePasswordComponent(cmp);
	}

	private ChangePasswordFrame getChangePasswordFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final ChangePasswordFrame frame = nextPage
				.getOrCreateFrame(ChangePasswordFrame.class);
		return frame;
	}

	/**
	 * Internal. Creates a {@link ListAddressComponent} for this frame.
	 * 
	 * @return {@link ListAddressComponent}
	 */
	private ListAddressComponent createListAddressComponent() {
		final ListAddressComponent result = new DefaultListAddressComponent();
		result.getCreateAddressEvent().getListener()
				.setAction(NAV_ADDRESS_EDIT);
		result.getCreateAddressEvent().getListener().setActionListener(
				super.createExpressionString("doCreateAddress"));

		result.getShowAddressComponentTemplate().getEditAddressEvent()
				.getListener().setAction(NAV_ADDRESS_EDIT);
		result.getShowAddressComponentTemplate().getEditAddressEvent()
				.getListener().setActionListener(
						super.createExpressionString("doEditAddress"));

		return result;
	}

	/**
	 * Internal. Creates a {@link ListPaymentComponent} for this frame.
	 * 
	 * @return {@link ListPaymentComponent}
	 */
	private ListPaymentComponent createListPaymentComponent() {
		final ListPaymentComponent result = new DefaultListPaymentComponent();

		result.getCreateCreditCardEvent().getListener().setAction(
				NAV_PAYMENTINFO_EDIT);
		result.getCreateCreditCardEvent().getListener().setActionListener(
				super.createExpressionString("doCreateCreditCardPaymentInfo"));

		result.getCreateDebitEvent().getListener().setAction(
				NAV_PAYMENTINFO_EDIT);
		result.getCreateDebitEvent().getListener().setActionListener(
				super.createExpressionString("doCreateDebitPaymentInfo"));

		result.getShowPaymentComponentTemplate().getEditPaymentEvent()
				.getListener().setAction(NAV_PAYMENTINFO_EDIT);
		result.getShowPaymentComponentTemplate().getEditPaymentEvent()
				.getListener().setActionListener(
						super.createExpressionString("doEditPaymentInfo"));

		return result;
	}

	/**
	 * Internal. Creates a {@link UserInfoComponent} for this frame.
	 * 
	 * @return {@link UserInfoComponent}
	 */
	private UserInfoComponent createUserInfoComponent() {
		final UserInfoComponent userInfoCmp = new DefaultUserInfoComponent();

		final YEventListener<UserInfoComponent> chgPwListener = userInfoCmp
				.getChangePasswordEvent().getListener();
		chgPwListener.setAction(USER_INFO_CHANGE_PASSWORD_ACTION);
		chgPwListener.setActionListener(super
				.createExpressionString("doChangePassword"));
		userInfoCmp.getShowOrderHistoryEvent().getListener().setAction(
				USER_INFO_SHOW_ORDER_HISTORY_ACTION);
		return userInfoCmp;
	}

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

		return result;
	}

	/**
	 * @return {@link PaymentInfoEditFrame} bound to the following page.
	 */
	public PaymentInfoEditFrame getPaymentInfoEditFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final PaymentInfoEditFrame result = nextPage
				.getOrCreateFrame(PaymentInfoEditFrame.class);
		return result;
	}

	/**
	 * @return {@link AddressEditFrame} bound to the following page
	 */
	private AddressEditFrame getAddressEditFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final AddressEditFrame result = nextPage
				.getOrCreateFrame(AddressEditFrame.class);
		return result;
	}

}
