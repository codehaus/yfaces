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

import javax.faces.context.FacesContext;

import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.component.AbstractYComponentContainer;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.address.DefaultShowAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ShowAddressComponent;
import ystorefoundationpackage.yfaces.component.order.DefaultOrderTableComponent;
import ystorefoundationpackage.yfaces.component.order.OrderTableComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultShowPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ShowPaymentComponent;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Renders the detail information of the selected order.
 * 
 */
public class OrderDetailFrame extends AbstractYComponentContainer {
	private static enum ADDRESS_TYPE {
		Delivery, Payment
	}

	private ShowAddressComponent showDeliveryAddressCmp = null;
	private ShowAddressComponent showPaymentAddressCmp = null;
	private ShowPaymentComponent showPaymentCmp = null;
	private OrderTableComponent orderTableCmp = null;

	public OrderDetailFrame() {
		super();
	}

	@Override
	public String getTitle() {
		final String title = YStorefoundation.getRequestContext()
				.getContentManagement().getLocalizedMessage("orderDetail",
						getOrderModel().getCode());
		return title;
	}

	public ShowAddressComponent getDeliveryShowAddressComponent() {
		if (this.showDeliveryAddressCmp == null) {
			this.showDeliveryAddressCmp = this
					.createShowAddressCmp(ADDRESS_TYPE.Delivery);
		}
		return this.showDeliveryAddressCmp;
	}

	public void setDeliveryShowAddressComponent(ShowAddressComponent cmp) {
		this.showDeliveryAddressCmp = cmp;
	}

	public ShowAddressComponent getPaymentShowAddressComponent() {
		if (this.showPaymentAddressCmp == null) {
			this.showPaymentAddressCmp = this
					.createShowAddressCmp(ADDRESS_TYPE.Payment);
		}
		return this.showPaymentAddressCmp;
	}

	public void setPaymentShowAddressComponent(ShowAddressComponent cmp) {
		this.showPaymentAddressCmp = cmp;
	}

	public ShowPaymentComponent getShowPaymentComponent() {
		if (this.showPaymentCmp == null) {
			this.showPaymentCmp = this.createShowPaymentCmp();
		}
		return this.showPaymentCmp;
	}

	public void setShowPaymentComponent(ShowPaymentComponent cmp) {
		this.showPaymentCmp = cmp;
	}

	public OrderTableComponent getOrderTableComponent() {
		if (this.orderTableCmp == null) {
			this.orderTableCmp = this.createOrderTableCmp();
		}
		return this.orderTableCmp;
	}

	public void setOrderTableComponent(OrderTableComponent cmp) {
		this.orderTableCmp = cmp;
	}

	/**
	 * @return {@link ShowAddressComponent}
	 */
	private ShowAddressComponent createShowAddressCmp(final ADDRESS_TYPE type) {
		final ShowAddressComponent result = new DefaultShowAddressComponent();
		result.getChooseAddressAsDeliveryEvent().setEnabled(false);
		result.getChooseAddressAsPaymentEvent().setEnabled(false);
		result.getCustomAddressEvent().setEnabled(false);
		result.getDeleteAddressEvent().setEnabled(false);
		result.getEditAddressEvent().setEnabled(false);

		final OrderModel order = this.getOrderModel();
		if (type == ADDRESS_TYPE.Delivery) {
			// result.setAddress(order.getDeliveryAddress());
			result.setAddress(order.getDeliveryAddress());
		}

		if (type == ADDRESS_TYPE.Payment) {
			// result.setAddress(order.getPaymentAddress());
			result.setAddress(order.getPaymentAddress());
		}
		return result;
	}

	/**
	 * Creates a {@link ShowPaymentComponent} for this
	 * {@link org.codehaus.yfaces.component.YComponentContainer}
	 * 
	 * @return {@link ShowPaymentComponent}
	 */
	private ShowPaymentComponent createShowPaymentCmp() {
		final ShowPaymentComponent result = new DefaultShowPaymentComponent();
		result.setPaymentInfo(this.getOrderModel().getPaymentInfo());
		result.getDeletePaymentEvent().setEnabled(false);
		result.getEditPaymentEvent().setEnabled(false);
		return result;
	}

	/**
	 * Creates a {@link OrderTableComponent} for this
	 * {@link org.codehaus.yfaces.component.YComponentContainer}
	 * 
	 * @return {@link OrderTableComponent}
	 */
	private OrderTableComponent createOrderTableCmp() {
		final OrderTableComponent result = new DefaultOrderTableComponent();
		result.setOrder(this.getOrderModel());
		return result;
	}

	private OrderModel getOrderModel() {
		OrderModel result = (OrderModel) super.getAttributes().get("orderid");
		if (result == null) {
			final String id = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap().get(
							"orderid");
			final UserModel user = YStorefoundation.getRequestContext()
					.getSessionContext().getUser();
			// result =
			// Webfoundation.getInstance().getServices().getOrderService().findById(Long.parseLong(id),
			// user);
			result = YStorefoundation.getRequestContext().getOrderManagement()
					.getOrder(user, id);

			if (result == null) {
				YStorefoundation.getRequestContext().getErrorHandler()
						.handleException(
								new YFacesException("Invalid order requested"));
				// throw new YFacesException("Invalid order requested");
			}

			super.getAttributes().put("orderid", result);
		}
		return result;
	}

}
