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
package ystorefoundationpackage.domain;

import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.Collection;
import java.util.List;


/**
 * Anything which is somehow Order or Purchase related. This includes operation on cart, pricing, order related
 * addresses and payment modes, discounts (vouchers).
 */
public interface OrderManagement
{
	/**
	 * Defines all payment modes.
	 */
	static enum PaymentModes
	{
		INVOICE("invoice"), ADVANCE("advance"), CREDIT("creditcard"), DEBIT("debitentry");

		private String code = null;

		private PaymentModes(final String code)
		{
			this.code = code;
		}

		public String getCode()
		{
			return this.code;
		}
	}


	/**
	 * Defines all states which can happen when the user adds the product to cart.
	 */
	static enum AddToCartResult
	{
		CHOOSE_VARIANT, CHOOSE_UNIT, NOT_ORDERABLE, NOT_FOUND, INVALID_QUANTITY, UNEXPECTED_ERROR, ADDED_ONE, ADDED_SEVERAL;
	}

	/**
	 * Returned by of {@link OrderManagement#placeOrder(CartModel)}. Holds the successfully created {@link OrderModel} or
	 * provides information of possible errors.
	 */
	interface PlaceOrderResult
	{
		//bitmask of possible errors
		public static final int UNKNOWN_ERROR = 1 << 15;
		public static final int NO_ERROR = 0;
		public static final int CHOOSE_DELIVERY_ADDRESS = 1;
		public static final int CHOOSE_PAYMENT_ADDRESS = 2;
		public static final int CHOOSE_DELIVERY_MODE = 4;
		public static final int CHOOSE_PAYMENT_MODE = 8;
		public static final int INVALID_VOUCHERS = 16;

		/**
		 * Finds the error code
		 * 
		 * @return error code
		 */
		int getErrorCode();

		/**
		 * Checks if error exists
		 * 
		 * @param bitmask
		 *           bit mask which representing the error
		 * @return true if error exists
		 */
		boolean containsError(int bitmask);

		/**
		 * Finds the order
		 * 
		 * @return found order
		 */
		OrderModel getOrder();
	}

	/**
	 * Adds to cart given product and returns cart state
	 * 
	 * @param p
	 *           the product to add
	 * @param quantity
	 *           the quantity of products to add
	 * @param u
	 *           the product's unit
	 * 
	 * @return the cart state
	 */
	AddToCartResult addToCart(CartModel cart, ProductModel p, long quantity, UnitModel u);


	/**
	 * Updates the cart when necessary. This includes:
	 * <ul>
	 * <li>detect removed/invalid entries</li>
	 * <li>refresh cart/entries when isCalulated returns false</li>
	 * <li>call <code>SfSessionContext#setCart(CartModel)</code> with passed cart</li>
	 * </ul>
	 * 
	 * @param cartModel
	 *           cart to recalculate
	 * @return false when recalculation isn't necessary
	 */
	boolean updateCart(CartModel cartModel);

	boolean updateCart(CartModel cartModel, String paymentMode, PaymentInfoModel paymentInfo);

	PlaceOrderResult placeOrder(final CartModel cart);

	OrderModel getOrder(UserModel user, String orderId);

	List<DeliveryModeModel> getSupportedDeliveryModes(PaymentModeModel paymentMode, AddressModel address);

	/**
	 * Searches for all {@link OrderModel} under the given conditions.
	 * 
	 * @param user
	 *           the user who owns the order
	 * @param orderCode
	 *           code of order
	 * @param orderState
	 *           state of order
	 * @param sortBy
	 *           status to be sorted by
	 * @param sortAscending
	 *           sort ascending tag
	 * @return list of found {@link OrderModel}
	 */
	List<OrderModel> getAllOrders(UserModel user, String orderCode, Collection<EnumerationValueModel> orderState, String sortBy,
			boolean sortAscending);

	/**
	 * Returns the price for the passed product.
	 * 
	 * @param product
	 *           product to request the price for
	 * @return available {@link Prices}
	 */
	Prices getPrices(ProductModel product);


	/**
	 * Searches for all global discount values which are applied to on order level instead of order entry level.
	 * 
	 * @param order
	 *           the order which contains the discount values
	 * @return list of {@link PriceLeverage} containing the discount values.
	 */
	List<PriceLeverage> getDisounts(AbstractOrderModel order);

	/**
	 * Searches for all tax values of this order.
	 * 
	 * @param order
	 *           the order which contains the tax values
	 * @return list of {@link PriceLeverage} containing all tax values of this order.
	 */
	List<PriceLeverage> getTaxes(AbstractOrderModel order);

	/**
	 * Searches for all applied tax values.
	 * 
	 * @param entry
	 *           order entry which contains the tax values
	 * @return list of {@link PriceLeverage} containing all tax values on the given order entry
	 */
	List<PriceLeverage> getTaxes(AbstractOrderEntryModel entry);

}
