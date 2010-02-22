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
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventListener;
import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

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


/**
 * Renders the user information in detail, such as user addresses, and payment information.
 * 
 */
public class UserDetailFrame extends AbstractYFrame
{
	private static final long serialVersionUID = -1550283086759307248L;

	//Navigation targets
	private static final String NAV_USERDETAIL = "userDetailPage";
	private static final String NAV_ADDRESS_EDIT = "addressEditPage";

	private static final String NAV_PAYMENTINFO_EDIT = "paymentEditPage";

	private static final String USER_INFO_CHANGE_PASSWORD_ACTION = "changePasswordPage";
	private static final String USER_INFO_SHOW_ORDER_HISTORY_ACTION = "orderHistoryPage";

	private YComponentBinding<ListAddressComponent> listAddressCmp = null;
	private YComponentBinding<ListPaymentComponent> listPaymentCmp = null;
	private YComponentBinding<UserInfoComponent> userInfoCmp = null;

	public UserDetailFrame()
	{
		super();
		this.listAddressCmp = super.createComponentBinding(this.createListAddressComponent());
		this.listPaymentCmp = super.createComponentBinding(this.createListPaymentComponent());
		this.userInfoCmp = super.createComponentBinding(this.createUserInfoComponent());
	}

	/**
	 * @return {@link YComponentBinding} for {@link ListAddressComponent}
	 */
	public YComponentBinding<ListAddressComponent> getListAddressComponent()
	{
		return this.listAddressCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link ListPaymentComponent}
	 */
	public YComponentBinding<ListPaymentComponent> getListPaymentComponent()
	{
		return this.listPaymentCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link UserInfoComponent}
	 */
	public YComponentBinding<UserInfoComponent> getUserInfoComponent()
	{
		return this.userInfoCmp;
	}

	/**
	 * External {@link YComponentEventListener} for {@link ListAddressComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doCreateAddress(final YComponentEvent<ListAddressComponent> event)
	{
		//retrieve EditAddressComponent from AddressEditFrame...
		final EditAddressComponent editCmp = getAddressEditFrame().getEditAddressComponent().getValue();

		//...and set empty address and return action
		editCmp.setAddress(null);
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditAddressEvent().getListener().setAction(NAV_USERDETAIL);
		getAddressEditFrame().getEditAddressComponent().setValue(editCmp);
	}

	/**
	 * External {@link YComponentEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doEditAddress(final YComponentEvent<ShowAddressComponent> event)
	{
		//retrieve Address which shall be edited
		final ShowAddressComponent cmp = event.getComponent();
		final AddressModel address = cmp.getAddress();

		//retrieve EditAddressComponent from AddressEditFrame
		final EditAddressComponent editCmp = getAddressEditFrame().getEditAddressComponent().getValue();
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditAddressEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.setAddress(address);
		getAddressEditFrame().getEditAddressComponent().setValue(editCmp);
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
		editCmp.getSavePaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		getPaymentInfoEditFrame().getEditPaymentComponent().setValue(editCmp);
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
		editCmp.getSavePaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		getPaymentInfoEditFrame().getEditPaymentComponent().setValue(editCmp);
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
		final PaymentInfoModel paymentInfo = cmp.getPaymentInfo();
		final EditPaymentComponent editCmp = this.createEditPaymentComponent(paymentInfo);

		editCmp.setPaymentInfo(paymentInfo);
		editCmp.getSavePaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		editCmp.getCancelEditPaymentInfoEvent().getListener().setAction(NAV_USERDETAIL);
		getPaymentInfoEditFrame().getEditPaymentComponent().setValue(editCmp);
	}

	public void doChangePassword(final YComponentEvent<DefaultUserInfoComponent> event)
	{
		final ChangePasswordFrame frame = getChangePasswordFrame();
		final ChangePasswordComponent cmp = new DefaultChangePasswordComponent();
		cmp.getCancelChangePasswordEvent().getListener().setAction(NAV_USERDETAIL);
		frame.getChangePasswordComponent().setValue(cmp);
	}

	private ChangePasswordFrame getChangePasswordFrame()
	{
		final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final ChangePasswordFrame frame = nextPage.getOrCreateFrame(ChangePasswordFrame.class);
		return frame;
	}

	/**
	 * Internal. Creates a {@link ListAddressComponent} for this frame.
	 * 
	 * @return {@link ListAddressComponent}
	 */
	private ListAddressComponent createListAddressComponent()
	{
		final ListAddressComponent result = new DefaultListAddressComponent();
		result.getCreateAddressEvent().getListener().setAction(NAV_ADDRESS_EDIT);
		result.getCreateAddressEvent().getListener().setActionListener(super.createExpressionString("doCreateAddress"));

		result.getShowAddressComponentTemplate().getEditAddressEvent().getListener().setAction(NAV_ADDRESS_EDIT);
		result.getShowAddressComponentTemplate().getEditAddressEvent().getListener().setActionListener(
				super.createExpressionString("doEditAddress"));

		return result;
	}

	/**
	 * Internal. Creates a {@link ListPaymentComponent} for this frame.
	 * 
	 * @return {@link ListPaymentComponent}
	 */
	private ListPaymentComponent createListPaymentComponent()
	{
		final ListPaymentComponent result = new DefaultListPaymentComponent();

		result.getCreateCreditCardEvent().getListener().setAction(NAV_PAYMENTINFO_EDIT);
		result.getCreateCreditCardEvent().getListener().setActionListener(
				super.createExpressionString("doCreateCreditCardPaymentInfo"));

		result.getCreateDebitEvent().getListener().setAction(NAV_PAYMENTINFO_EDIT);
		result.getCreateDebitEvent().getListener().setActionListener(super.createExpressionString("doCreateDebitPaymentInfo"));

		result.getShowPaymentComponentTemplate().getEditPaymentEvent().getListener().setAction(NAV_PAYMENTINFO_EDIT);
		result.getShowPaymentComponentTemplate().getEditPaymentEvent().getListener().setActionListener(
				super.createExpressionString("doEditPaymentInfo"));

		return result;
	}

	/**
	 * Internal. Creates a {@link UserInfoComponent} for this frame.
	 * 
	 * @return {@link UserInfoComponent}
	 */
	private UserInfoComponent createUserInfoComponent()
	{
		final UserInfoComponent userInfoCmp = new DefaultUserInfoComponent();

		final YComponentEventListener<UserInfoComponent> chgPwListener = userInfoCmp.getChangePasswordEvent().getListener();
		chgPwListener.setAction(USER_INFO_CHANGE_PASSWORD_ACTION);
		chgPwListener.setActionListener(super.createExpressionString("doChangePassword"));
		userInfoCmp.getShowOrderHistoryEvent().getListener().setAction(USER_INFO_SHOW_ORDER_HISTORY_ACTION);
		return userInfoCmp;
	}

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

		return result;
	}

	/**
	 * @return {@link PaymentInfoEditFrame} bound to the following page.
	 */
	public PaymentInfoEditFrame getPaymentInfoEditFrame()
	{
		final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final PaymentInfoEditFrame result = nextPage.getOrCreateFrame(PaymentInfoEditFrame.class);
		return result;
	}

	/**
	 * @return {@link AddressEditFrame} bound to the following page
	 */
	private AddressEditFrame getAddressEditFrame()
	{
		final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final AddressEditFrame result = nextPage.getOrCreateFrame(AddressEditFrame.class);
		return result;
	}

}
