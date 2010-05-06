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

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponentContainer;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventListener;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;

import ystorefoundationpackage.Localized;
import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.URLFactory;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PaymentModes;
import ystorefoundationpackage.domain.OrderManagement.PlaceOrderResult;
import ystorefoundationpackage.yfaces.component.address.DefaultShowAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ListAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ShowAddressComponent;
import ystorefoundationpackage.yfaces.component.cart.SelectDeliveryModeComponent;
import ystorefoundationpackage.yfaces.component.cart.SelectPaymentModeComponent;
import ystorefoundationpackage.yfaces.component.order.DefaultOrderTableComponent;
import ystorefoundationpackage.yfaces.component.order.OrderTableComponent;
import ystorefoundationpackage.yfaces.component.payment.DefaultShowPaymentComponent;
import ystorefoundationpackage.yfaces.component.payment.ShowPaymentComponent;
import ystorefoundationpackage.yfaces.component.voucher.VoucherComponent;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

/**
 * Renders the summary information of the cart.
 * 
 */
public class SummaryFrame extends AbstractYComponentContainer {
	private static final Logger log = Logger.getLogger(SummaryFrame.class);

	private static final String NAV_ADDRESSLIST = "addressListPage";
	private static final String NAV_PAYMENTLIST = "paymentListPage";
	private static final String NAV_THIS_PAGE = "summaryPage";

	// name of the attribute which indicates whether the current
	// showAddressModel shows a delivery or payment address
	private static final String MODEL_ATTRIB_ADRESSTYPE = "ADDRESS_TYPE";

	private ShowAddressComponent showDeliveryAddressCmp = null;
	private ShowAddressComponent showPaymentAddressCmp = null;
	private ShowPaymentComponent showPaymentCmp = null;
	private OrderTableComponent orderTableCmp = null;
	private SelectDeliveryModeComponent selectDeliveryModeCmp = null;
	private VoucherComponent voucherCmp = null;

	private static final String PAYMENT_ADDRESS = "defaultPaymentAddress";
	private static final String DELIVERY_ADDRESS = "defaultShipmentAddress";

	public SummaryFrame() {
		super();
	}

	/**
	 * @return Binding for {@link ShowAddressComponent} (delivery address)
	 */
	public ShowAddressComponent getShowDeliveryAddressComponent() {
		if (this.showDeliveryAddressCmp == null) {
			this.showDeliveryAddressCmp = this
					.createShowDeliveryAddressComponent();
		}
		return this.showDeliveryAddressCmp;
	}

	public void setShowDeliveryAddressComponent(ShowAddressComponent cmp) {
		this.showDeliveryAddressCmp = cmp;
	}

	/**
	 * @return Binding for {@link ShowAddressComponent} (payment address)
	 */
	public ShowAddressComponent getShowPaymentAddressComponent() {
		if (this.showPaymentAddressCmp == null) {
			this.showPaymentAddressCmp = this
					.createShowPaymentAddressComponent();
		}
		return this.showPaymentAddressCmp;
	}

	public void setShowPaymentAddressComponent(ShowAddressComponent cmp) {
		this.showPaymentAddressCmp = cmp;
	}

	/**
	 * @return Binding for {@link SelectDeliveryModeComponent}
	 */
	public SelectDeliveryModeComponent getSelectDeliveryModeComponent() {
		return this.selectDeliveryModeCmp;
	}

	public void setSelectDeliveryModeComponent(SelectDeliveryModeComponent cmp) {
		this.selectDeliveryModeCmp = cmp;
	}

	/**
	 * @return Binding for {@link ShowPaymentComponent}
	 */
	public ShowPaymentComponent getShowPaymentComponent() {
		if (this.showPaymentCmp == null) {
			this.showPaymentCmp = this.createShowPaymentComponent();
		}
		return this.showPaymentCmp;
	}

	public void setShowPaymentComponent(ShowPaymentComponent cmp) {
		this.showPaymentCmp = cmp;
	}

	/**
	 * @return Binding for {@link VoucherComponent}
	 */
	public VoucherComponent getVoucherComponent() {
		return this.voucherCmp;
	}

	public void setVoucherComponent(VoucherComponent cmp) {
		this.voucherCmp = cmp;
	}

	/**
	 * @return binding for {@link OrderTableComponent}
	 */
	public OrderTableComponent getOrderTableComponent() {
		if (this.orderTableCmp == null) {
			this.orderTableCmp = this.createOrderTableComponent();
		}
		return this.orderTableCmp;
	}

	public void setOrderTableComponent(OrderTableComponent cmp) {
		this.orderTableCmp = cmp;
	}

	/**
	 * External {@link YEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doChangePayment(final YEvent<ShowPaymentComponent> event) {
		final PaymentListFrame payListFrame = this.getPaymentListFrame();

		final ShowPaymentComponent spCmp = payListFrame
				.getListPaymentComponent().getShowPaymentComponent();

		spCmp.getCustomPaymentEvent().setEnabled(true);
		spCmp.getCustomPaymentEvent().getListener().setActionListener(
				super.createExpressionString("doChoosePayment"));
		spCmp.getCustomPaymentEvent().getListener().setAction(NAV_THIS_PAGE);
		spCmp.getCustomPaymentEvent().setName(Localized.ACTION_CHOOSE.key);

		// configure paymentModeComponent
		final SelectPaymentModeComponent pmc = payListFrame
				.getSelectPaymentModeComponent();
		pmc.getChooseAdvanceEvent().getListener().setActionListener(
				super.createExpressionString("doChooseAdvancedPayment"));
		pmc.getChooseAdvanceEvent().getListener().setAction(NAV_THIS_PAGE);
		pmc.getChooseInvoiceEvent().getListener().setActionListener(
				super.createExpressionString("doChooseInvoicePayment"));
		pmc.getChooseInvoiceEvent().getListener().setAction(NAV_THIS_PAGE);
	}

	public void doChooseAdvancedPayment(
			final YEvent<SelectPaymentModeComponent> event) {
		refreshPayment(PaymentModes.ADVANCE.getCode(), null);
		recalculateCart();
	}

	public void doChooseInvoicePayment(
			final YEvent<SelectPaymentModeComponent> event) {
		refreshPayment(PaymentModes.INVOICE.getCode(), null);
		recalculateCart();
	}

	/**
	 * External {@link YEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doChoosePayment(final YEvent<ShowPaymentComponent> event) {
		refreshPayment(null, event.getComponent().getPaymentInfo());

		// auto detect new values according current cart
		getShowPaymentComponent().setPaymentInfo(null);
		getShowPaymentComponent().setPaymentMode(null);

		recalculateCart();
	}

	private void refreshPayment(final String paymentModeCode,
			final PaymentInfoModel paymentInfo) {
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final CartModel cart = reqCtx.getSessionContext().getCart();
		reqCtx.getOrderManagement().updateCart(cart, paymentModeCode,
				paymentInfo);

	}

	/**
	 * External {@link YEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doChangeAddress(final YEvent<ShowAddressComponent> event) {
		final ShowAddressComponent cmp = event.getComponent();
		final String type = (String) cmp.getAttributes().get(
				MODEL_ATTRIB_ADRESSTYPE);

		// configure AddressListPage
		final AddressListFrame addressListFrame = getAddressListFrame();

		final ListAddressComponent laCmp = addressListFrame
				.getListAddressComponent();

		// and modify it (change some options; configure choose-action)
		laCmp.getShowAddressComponentTemplate()
				.getChooseAddressAsDeliveryEvent().setEnabled(false);
		laCmp.getShowAddressComponentTemplate()
				.getChooseAddressAsPaymentEvent().setEnabled(false);
		laCmp.getShowAddressComponentTemplate().getCustomAddressEvent()
				.setEnabled(true);
		laCmp.getShowAddressComponentTemplate().getCustomAddressEvent()
				.setName(Localized.ACTION_CHOOSE.key);
		laCmp.getShowAddressComponentTemplate().getCustomAddressEvent()
				.getListener().setAction(NAV_THIS_PAGE);
		laCmp.getShowAddressComponentTemplate().getCustomAddressEvent()
				.getListener().setActionListener(
						super.createExpressionString("doChooseAddress"));

		super.getAttributes().put(MODEL_ATTRIB_ADRESSTYPE, type);
	}

	/**
	 * External {@link YEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doChooseAddress(final YEvent<ShowAddressComponent> event) {
		final ShowAddressComponent cmp = event.getComponent();
		final String type = (String) super.getAttributes().get(
				MODEL_ATTRIB_ADRESSTYPE);

		if (!"defaultPaymentAddress".equals(type)
				&& !"defaultShipmentAddress".equals(type)) {
			throw new StorefoundationException(
					"Can't detect Type of address (payment or delivery?)");
		}

		final AddressModel adr = cmp.getAddress();
		final UserModel user = YStorefoundation.getRequestContext()
				.getSessionContext().getUser();

		if ("defaultPaymentAddress".equals(type)) {
			user.setDefaultPaymentAddress(adr);
			getPlatformServices().getModelService().save(user);
			getShowPaymentAddressComponent().setAddress(adr);
		} else if ("defaultShipmentAddress".equals(type)) {
			user.setDefaultShipmentAddress(adr);
			// Webfoundation.getInstance().getServices().getUserService().saveUser(user);
			getPlatformServices().getModelService().save(user);
			getShowDeliveryAddressComponent().setAddress(adr);

			recalculateCart();
		}

		if (log.isDebugEnabled()) {
			log.debug("Set " + type + " for " + user.getName() + " -> "
					+ adr.getFirstname() + " " + adr.getLastname());
		}
	}

	// after the delivery address or payment is changed,
	// the cart must be recalculated because of the different delivery cost
	private void recalculateCart() {
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final AddressModel deliveryAddress = reqCtx.getSessionContext()
				.getUser().getDefaultShipmentAddress();
		final CartModel currentCart = reqCtx.getSessionContext().getCart();
		final PaymentModeModel pm = currentCart.getPaymentMode();
		final DeliveryModeModel deliveryMode = currentCart.getDeliveryMode();
		if (deliveryMode != null) {
			final List<DeliveryModeModel> modes = reqCtx.getOrderManagement()
					.getSupportedDeliveryModes(pm, deliveryAddress);

			if (!modes.contains(deliveryMode)) {
				currentCart.setDeliveryMode(null);
			}
		}
		currentCart.setDeliveryAddress(deliveryAddress);

		reqCtx.getPlatformServices().getModelService().save(currentCart);
		reqCtx.getOrderManagement().updateCart(currentCart);

		// make sure that the order table component will generate a new order
		getOrderTableComponent().setOrder(null);
	}

	/**
	 * External {@link YEventListener} for {@link OrderTableComponent}
	 * 
	 * @param event
	 *            {@link YEvent}
	 */
	public void doPlaceOrder(final YEvent<OrderTableComponent> event) {
		final PlaceOrderResult result = (PlaceOrderResult) event
				.getAttributes().get(PlaceOrderResult.class.getName());

		if (result.getErrorCode() == 0) {
			final URLFactory uf = YStorefoundation.getRequestContext()
					.getURLFactory();
			final String path = uf.getURLCreator("orderstate")
					.createExternalForm(result.getOrder());
			YStorefoundation.getRequestContext().redirect(path);
		}
	}

	@SuppressWarnings("unchecked")
	private AddressModel detectAddress(final UserModel userBean,
			final String type, final String fallback) {
		AddressModel address = type.equals(DELIVERY_ADDRESS) ? userBean
				.getDefaultShipmentAddress() : userBean
				.getDefaultPaymentAddress();

		if (address == null) {
			address = fallback.equals(DELIVERY_ADDRESS) ? userBean
					.getDefaultShipmentAddress() : userBean
					.getDefaultPaymentAddress();
			if (address == null) {
				final Collection<AddressModel> addressList = YStorefoundation
						.getRequestContext().getSessionContext().getUser()
						.getAddresses();
				address = addressList.isEmpty() ? null : addressList.iterator()
						.next();
			}
		}

		return address;
	}

	/**
	 * Creates and initializes a {@link ShowPaymentComponent} for this Frame.
	 * 
	 * @return {@link ShowPaymentComponent}
	 */
	private ShowPaymentComponent createShowPaymentComponent() {
		final ShowPaymentComponent result = new DefaultShowPaymentComponent();
		result.getDeletePaymentEvent().setEnabled(false);
		result.getEditPaymentEvent().setEnabled(false);
		result.getCustomPaymentEvent().setEnabled(true);
		result.getCustomPaymentEvent().getListener().setAction(NAV_PAYMENTLIST);
		result.getCustomPaymentEvent().getListener().setActionListener(
				super.createExpressionString("doChangePayment"));
		result.getCustomPaymentEvent().setName(Localized.ACTION_CHANGE.key);

		return result;
	}

	/**
	 * @return {@link ShowAddressComponent} for Payment
	 */
	private ShowAddressComponent createShowPaymentAddressComponent() {
		final ShowAddressComponent result = new DefaultShowAddressComponent();
		final AddressModel paymentAddress = detectAddress(YStorefoundation
				.getRequestContext().getSessionContext().getUser(),
				PAYMENT_ADDRESS, DELIVERY_ADDRESS);

		result.setAddress(paymentAddress);
		YStorefoundation.getRequestContext().getSessionContext().getCart()
				.setPaymentAddress(paymentAddress);

		// save type of this address (payment)
		result.getAttributes().put(MODEL_ATTRIB_ADRESSTYPE, PAYMENT_ADDRESS);

		this.prepareShowAddressComponent(result);

		return result;
	}

	/**
	 * @return {@link ShowAddressComponent} for Delivery
	 */
	private ShowAddressComponent createShowDeliveryAddressComponent() {
		final ShowAddressComponent result = new DefaultShowAddressComponent();

		final AddressModel deliveryAddress = detectAddress(YStorefoundation
				.getRequestContext().getSessionContext().getUser(),
				DELIVERY_ADDRESS, PAYMENT_ADDRESS);

		result.setAddress(deliveryAddress);
		YStorefoundation.getRequestContext().getSessionContext().getCart()
				.setDeliveryAddress(deliveryAddress);

		// save type of this address (delivery)
		result.getAttributes().put(MODEL_ATTRIB_ADRESSTYPE, DELIVERY_ADDRESS);

		this.prepareShowAddressComponent(result);

		return result;
	}

	private void prepareShowAddressComponent(final ShowAddressComponent cmp) {
		cmp.getDeleteAddressEvent().setEnabled(false);
		cmp.getChooseAddressAsDeliveryEvent().setEnabled(false);
		cmp.getChooseAddressAsPaymentEvent().setEnabled(false);
		cmp.getEditAddressEvent().setEnabled(false);
		cmp.getCustomAddressEvent().setEnabled(true);
		cmp.getCustomAddressEvent().setName(Localized.ACTION_CHANGE.key);
		cmp.getCustomAddressEvent().getListener().setAction(NAV_ADDRESSLIST);
		cmp.getCustomAddressEvent().getListener().setActionListener(
				super.createExpressionString("doChangeAddress"));
	}

	private OrderTableComponent createOrderTableComponent() {
		final OrderTableComponent result = new DefaultOrderTableComponent();
		final YEventListener<OrderTableComponent> listener = super
				.createComponentEventListener("doPlaceOrder");
		result.getPlaceOrderEvent().addCustomListener(listener);

		return result;
	}

	/**
	 * Managed property.
	 * 
	 * @return {@link AddressListFrame}
	 */
	private AddressListFrame getAddressListFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final AddressListFrame result = nextPage
				.getOrCreateFrame(AddressListFrame.class);
		return result;
	}

	private PaymentListFrame getPaymentListFrame() {
		final YConversationContext convCtx = YStorefoundation
				.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = convCtx.getOrCreateNextPage();
		final PaymentListFrame result = nextPage
				.getOrCreateFrame(PaymentListFrame.class);
		return result;
	}

	@Override
	public void refresh() {
		super.refresh();
		getShowPaymentComponent().setPaymentMode(null);
		getShowPaymentComponent().setPaymentInfo(null);
		getSelectDeliveryModeComponent().setSupportedDeliveryModes(null);
	}

	private PlatformServices getPlatformServices() {
		return YStorefoundation.getRequestContext().getPlatformServices();
	}

}
