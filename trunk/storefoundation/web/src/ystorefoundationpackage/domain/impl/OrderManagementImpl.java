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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.core.Constants;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
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
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;
import de.hybris.platform.voucher.model.VoucherModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.OrderManagement;
import ystorefoundationpackage.domain.PriceLeverage;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 */
public class OrderManagementImpl extends AbstractDomainService implements OrderManagement
{
	private static final Logger log = Logger.getLogger(OrderManagementImpl.class);

	private class GetPricesInternalKey extends SfCacheKey<ProductModel, Prices>
	{
		public GetPricesInternalKey(final ProductModel rawKey)
		{
			super(rawKey, log);
		}

		@Override
		public Prices createObject(final ProductModel param)
		{
			return OrderManagementImpl.this.getPricesInternal(param);
		}

		@Override
		public boolean getExpired() // NOPMD 
		{
			final SfSessionContext sessCtx = YStorefoundation.getRequestContext().getSessionContext();
			final LanguageModel lang = sessCtx.getLanguage();
			final CurrencyModel cur = sessCtx.getCurrency();

			final Map m = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

			final Object oldLang = m.get("LANGUAGE");
			final Object oldCur = m.get("CURRENCY");

			final boolean expired = !lang.equals(oldLang) || !cur.equals(oldCur);

			if (expired)
			{
				m.put("LANGUAGE", lang);
				m.put("CURRENCY", cur);
			}

			return expired;
		}
	}

	public static class PlaceOrderResultImpl implements PlaceOrderResult
	{
		protected int errorCode = PlaceOrderResult.UNKNOWN_ERROR;
		protected OrderModel orderBean = null;

		public PlaceOrderResultImpl(final int errorCode)
		{
			this.errorCode = errorCode;
		}

		public int getErrorCode()
		{
			return this.errorCode;
		}

		public boolean containsError(final int bitmask)
		{
			return (this.errorCode & bitmask) > 0;
		}

		public OrderModel getOrder()
		{
			return this.orderBean;
		}

	}

	public AddToCartResult addToCart(final CartModel cart, final ProductModel product, final long quantity, UnitModel unit)
	{
		if (unit == null)
		{
			final Collection<UnitModel> units = this.getPrices(product).getOrderableUnits();

			if (units.size() == 1)
			{
				unit = units.iterator().next();
			}
		}

		// check some pre-conditions
		AddToCartResult result = null;
		if (product.getVariantType() != null)
		{
			result = AddToCartResult.CHOOSE_VARIANT;
		}
		else if (quantity < 1)
		{
			result = AddToCartResult.INVALID_QUANTITY;
		}
		else if (unit == null)
		{
			result = AddToCartResult.CHOOSE_UNIT;
		}

		if (result == null)
		{
			try
			{
				getPlatformServices().getCartService().addToCart(cart, product, quantity, unit);
				this.updateCart(cart);
				result = quantity == 1 ? AddToCartResult.ADDED_ONE : AddToCartResult.ADDED_SEVERAL;
			}
			catch (final Exception e)
			{
				log.error(e);
				result = AddToCartResult.UNEXPECTED_ERROR;

				// in case a JaloPriceFacoryException is thrown during updateCart() the newly
				// added entry should be removed (or set to previous state of quantity)
				// therefore the entry should be returned by addToCart(...) _(same like in jalo)  
			}
		}
		return result;
	}

	public PlaceOrderResult placeOrder(final CartModel cart)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		int errorCode = 0;

		//order validity check
		if (cart.getDeliveryAddress() == null)
		{
			errorCode = +PlaceOrderResult.CHOOSE_DELIVERY_ADDRESS;
		}
		if (cart.getPaymentAddress() == null)
		{
			errorCode = +PlaceOrderResult.CHOOSE_PAYMENT_ADDRESS;
		}
		if (cart.getDeliveryMode() == null)
		{
			errorCode = +PlaceOrderResult.CHOOSE_DELIVERY_MODE;
		}
		if (cart.getPaymentMode() == null)
		{
			errorCode = +PlaceOrderResult.CHOOSE_PAYMENT_MODE;
		}

		final List<VoucherModel> invalidVouchers = JaloBridge.getInstance().findInvalidVouchers(cart);

		if (!invalidVouchers.isEmpty())
		{
			errorCode = +PlaceOrderResult.INVALID_VOUCHERS;
		}

		final PlaceOrderResultImpl result = new PlaceOrderResultImpl(errorCode);

		if (errorCode == 0)
		{
			try
			{
				//redeem possible vouchers
				final List<String> voucherCodes = new ArrayList<String>(reqCtx.getPlatformServices().getVoucherService()
						.getAppliedVoucherCodes(cart));

				final OrderModel order = reqCtx.getPlatformServices().getOrderService().placeOrder(cart, null, null, null);
				order.setStatus(OrderStatus.CREATED);
				reqCtx.getPlatformServices().getModelService().save(order);
				result.orderBean = order;

				//create VoucherInvalidation for each voucher
				for (final String vCode : voucherCodes)
				{
					reqCtx.getPlatformServices().getVoucherService().createVoucherInvalidation(vCode, order);
				}
			}
			catch (final InvalidCartException e)
			{
				errorCode = +PlaceOrderResult.UNKNOWN_ERROR;
			}
			YStorefoundation.getRequestContext().getSessionContext().setCart(null);
		}
		return result;
	}

	@Override
	public boolean updateCart(final CartModel cartModel, String paymentMode, final PaymentInfoModel paymentInfo)
	{
		if (paymentMode == null)
		{
			paymentMode = PaymentModes.ADVANCE.getCode();
		}

		if (paymentInfo != null)
		{
			//final String code = ((PaymentInfo) getModelService().getSource(paymentInfo)).getComposedType().getCode();
			final String code = YStorefoundation.getRequestContext().getPlatformServices().getTypeService().getComposedType(
					paymentInfo.getClass()).getCode();

			if (code.equals(Constants.TYPES.CreditCardPaymentInfo))
			{
				paymentMode = PaymentModes.CREDIT.getCode();
			}
			else if (code.equals(Constants.TYPES.DebitPaymentInfo))
			{
				paymentMode = PaymentModes.DEBIT.getCode();
			}
		}

		final PaymentModeModel paymentModeModel = JaloBridge.getInstance().getPaymentMode(paymentMode);
		cartModel.setPaymentMode(paymentModeModel);
		cartModel.setPaymentInfo(paymentInfo);

		getModelService().save(cartModel);
		return updateCart(cartModel);
	}


	@Override
	public boolean updateCart(final CartModel cartModel)
	{
		// doing a model save to assure model properties are respected during calculation (pricefactory works with jalo)
		// can't be done here (as this can into saving an invalid cart state back (deleted entries etc.))
		// so unluckily cart saving (when necessary) has to be done manually 

		final boolean wasChanged = getPlatformServices().getCartService().calculateCart(cartModel);
		if (wasChanged)
		{
			getModelService().refresh(cartModel);
			YStorefoundation.getRequestContext().getSessionContext().setCart(cartModel);
		}
		return wasChanged;
	}


	@Override
	public OrderModel getOrder(final UserModel user, final String orderId)
	{
		OrderModel result = null;
		final String userPk = user.getPk().toString();
		final String query = "SELECT {o.pk} FROM {order as o} WHERE {o.pk} = '" + orderId + "' AND {o.user} = '" + userPk + "'";

		final FlexibleSearchService flexService = getPlatformServices().getFlexibleSearchService();

		final List<OrderModel> orders = (List) flexService.search(query).getResult();

		if (orders != null && orders.size() == 1)
		{
			result = orders.get(0);
		}
		return result;
	}


	@Override
	public List<DeliveryModeModel> getSupportedDeliveryModes(final PaymentModeModel paymentMode, final AddressModel deliveryAddress)
	{
		final List<DeliveryModeModel> result = new ArrayList(JaloBridge.getInstance().getSupportedDeliveryModes(paymentMode));
		if (deliveryAddress != null)
		{
			final String deliveryCountry = deliveryAddress.getCountry().getIsocode();

			for (final Object mode : result)
			{
				final ZoneDeliveryModeModel zoneMode = (ZoneDeliveryModeModel) mode;
				final List<ZoneModel> zones = JaloBridge.getInstance().getZones(zoneMode);

				boolean foundCountry = false;
				searchZones: for (final ZoneModel zone : zones)
				{
					for (final CountryModel country : zone.getCountries())
					{
						if (country.getIsocode().equals(deliveryCountry))
						{
							foundCountry = true;
							continue searchZones;
						}
					}
				}
				if (!foundCountry)
				{
					result.remove(mode);
				}
			}
		}
		return result;
	}

	@Override
	public List<PriceLeverage> getDisounts(final AbstractOrderModel order)
	{
		final List<PriceLeverage> result = new ArrayList<PriceLeverage>();
		final List<DiscountValue> discounts = order.getGlobalDiscountValues();
		for (final DiscountValue dv : discounts)
		{
			result.add(new PriceLeverageImpl(dv));
		}
		return result;
	}

	@Override
	public List<PriceLeverage> getTaxes(final AbstractOrderModel order)
	{
		final List<PriceLeverage> result = new ArrayList<PriceLeverage>();
		final Collection<TaxValue> taxes = order.getTotalTaxValues();
		for (final TaxValue tv : taxes)
		{
			result.add(new PriceLeverageImpl(tv));
		}
		return result;
	}

	@Override
	public List<PriceLeverage> getTaxes(final AbstractOrderEntryModel entry)
	{
		final List<PriceLeverage> result = new ArrayList<PriceLeverage>();
		final Collection<TaxValue> taxes = entry.getTaxValues();
		for (final TaxValue tv : taxes)
		{
			result.add(new PriceLeverageImpl(tv));
		}
		return result;
	}

	@Override
	public List<OrderModel> getAllOrders(final UserModel user, final String orderCode,
			final Collection<EnumerationValueModel> orderState, final String sortBy, final boolean sortAscending)
	{
		if (user == null)
		{
			throw new NullPointerException("No user specified");
		}

		final Map parameter = new HashMap();
		String query = "select {o:pk} from {Order as o LEFT JOIN OrderStatus AS stat ON {o:status}={stat:pk}  } "
				+ "where {o:user} = ?user ";
		parameter.put("user", user);

		// code
		if (orderCode != null)
		{
			query += " and {o:code}  LIKE ?code ";
			parameter.put("code", "%" + orderCode + "%");
		}

		if (orderState != null && !orderState.contains(null) && !orderState.isEmpty())
		{
			String pkList = "";
			for (final EnumerationValueModel _state : orderState)
			{
				pkList = pkList + _state.getPk().toString() + ",";
			}
			query += " and {o:status} IN (?status) ";
			parameter.put("status", pkList.substring(0, pkList.length() - 1));
		}


		// add order by criteria and direction
		if (sortBy != null)
		{
			query += " ORDER BY ";
			query += AbstractOrder.STATUS.equalsIgnoreCase(sortBy) ? "{stat:" + EnumerationValue.NAME + "} " : "{o:" + sortBy + "} ";
			if (!sortAscending)
			{
				query += " DESC ";
			}
			else
			{
				query += " ASC ";
			}
		}

		if (log.isDebugEnabled())
		{
			log.debug("Created Orderquery:" + query);
		}

		final FlexibleSearchService flexService = YStorefoundation.getRequestContext().getPlatformServices()
				.getFlexibleSearchService();

		final List<OrderModel> result = (List) flexService.search(query, parameter).getResult();
		return result;
	}

	@Override
	public Prices getPrices(final ProductModel product)
	{
		final Prices result = (Prices) getRequestCache().fetch(new GetPricesInternalKey(product));
		//result = new PricesImpl(product);
		return result;
	}


	protected Prices getPricesInternal(final ProductModel product)
	{
		return new PricesImpl(product);
	}


}
