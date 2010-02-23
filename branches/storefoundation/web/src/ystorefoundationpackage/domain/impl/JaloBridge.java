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

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commons.jalo.CommonsManager;
import de.hybris.platform.commons.jalo.renderer.RendererTemplate;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.order.price.DiscountModel;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.deliveryzone.jalo.Zone;
import de.hybris.platform.deliveryzone.jalo.ZoneDeliveryMode;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.deliveryzone.model.ZoneModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.delivery.DeliveryMode;
import de.hybris.platform.jalo.order.payment.PaymentMode;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.jalo.order.price.Tax;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.variants.jalo.VariantsManager;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.voucher.jalo.Voucher;
import de.hybris.platform.voucher.jalo.VoucherManager;
import de.hybris.platform.voucher.model.VoucherModel;

import java.io.Writer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 *
 */

// Marker class for Jalo stuff
// anything here should be gone in future and replaced by services/models
//
// ##
// CartService#addToCart (...) 
// - no chance of detecting a possible type of failure (only a exception of same type)
// - no chance of getting the last added entry (which means: no chance of removing it when it contains invalid data)
//
// ##
// price calculation is not done with models; (always underlying jalo items) ; this means model always has to be saved before
//
// ##
// FlexibleSearchService
// - no unboxing of modelclasses to itemclasses when passing expected resultclasses
// - no generated modelconstants for attributes; for building queries jalo packages must be imported (item structure must be known)
// - performance: the whole!!! searchresult gets immediately converted into models
//
// ## Performance (general)
// - model serialization
// - full conversion of searchresults into models 
public class JaloBridge
{
	static JaloBridge instance = new JaloBridge();

	/**
	 * CategoryService? ProductService?
	 * <p>
	 * (or is it safe to call size on {@link de.hybris.platform.product.ProductService#getProducts(CategoryModel)}
	 * <p>
	 * Uses a non-jalo and non persistent member at Category item.
	 */
	public int getAllProductsCount(final CategoryModel category)
	{
		final Category catg = getItem(category);
		return catg.getAllProductsCount();
	}

	/**
	 * CatalogService?
	 * <p>
	 * Uses a non-jalo and non persistent member at Category item.
	 */
	public int getRootCategoriesCount(final CatalogVersionModel model)
	{
		final CatalogVersion catVer = getItem(model);
		return catVer.getRootCategoriesCount();
	}

	/**
	 * CartService?
	 * <p>
	 * Fetch already available OrderEntries according a given product.
	 */
	public List<AbstractOrderEntryModel> getCartEntriesByProduct(final CartModel cart, final ProductModel product)
	{
		final Cart _cart = getItem(cart);
		final Product _product = getItem(product);
		final List<?> _result = _cart.getEntriesByProduct(_product);
		final List<AbstractOrderEntryModel> result = YStorefoundation.getRequestContext().getPlatformServices().getModelService()
				.getAll(_result, new ArrayList());
		return result;
	}


	/**
	 * CartService? OrderService?
	 */
	public PaymentModeModel getPaymentMode(final String code)
	{
		final PaymentMode item = OrderManager.getInstance().getPaymentModeByCode(code);
		final PaymentModeModel result = getModel(item);
		return result;
	}

	/**
	 * CartService? OrderService?
	 */
	public List<DeliveryModeModel> getAllDeliveryModes()
	{
		final Collection<DeliveryMode> col = OrderManager.getInstance().getAllDeliveryModes();
		final List<DeliveryModeModel> result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getAll(
				col, new ArrayList());
		return result;
	}

	/**
	 * CartService? OrderService?
	 * 
	 * @param paymentMode
	 * @return list of supported delivery modes
	 */
	public List<DeliveryModeModel> getSupportedDeliveryModes(final PaymentModeModel paymentMode)
	{
		final PaymentMode pm = getItem(paymentMode);
		final Collection<DeliveryMode> deliveryModes = OrderManager.getInstance().getSupportedDeliveryModes(pm);

		final List<DeliveryModeModel> result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getAll(
				deliveryModes, new ArrayList());
		return result;
	}

	/**
	 * CartService? OrderService?
	 */
	public List<ZoneModel> getZones(final ZoneDeliveryModeModel zoneDeliveryMode)
	{
		final ZoneDeliveryMode zoneMode = getItem(zoneDeliveryMode);
		final Collection<Zone> zones = zoneMode.getZones();

		final List<ZoneModel> result = getModelService().getAll(zones, new ArrayList());
		return result;
	}

	public List<DiscountModel> getDiscounts(final String code)
	{
		final Collection<?> discount = OrderManager.getInstance().getDiscountsByCode(code);
		final List<DiscountModel> result = getModelService().getAll(discount, new ArrayList());
		return result;
	}

	public List<TaxModel> getTaxes(final String code)
	{
		final Collection<Tax> taxes = OrderManager.getInstance().getTaxesByCode(code);
		final List<TaxModel> result = getModelService().getAll(taxes, new ArrayList());
		return result;
	}


	public List<VariantProductModel> getVariantProductByAttributeValues(final ProductModel baseProduct,
			final Map<String, String> filter)
	{
		final Product base = getItem(baseProduct);
		final Collection<Product> _result = (Collection) VariantsManager.getInstance().getVariantProductByAttributeValues(base,
				filter);

		final List<VariantProductModel> result = getModelService().getAll(_result, new ArrayList());
		return result;

	}

	/**
	 * Redeems the voucher for the cart
	 * <p>
	 * <b>WARNING!</b> <br>
	 * If some methods for checking voucher availability are called before this method, all these methods have to be in
	 * one synchronize block! Suggested synchronization object is cart.
	 * </p>
	 * 
	 * @param cb
	 *           cart for which the voucher will be redeemed
	 * @param voucherCode
	 *           voucher code to be redeemed
	 * @return null if the voucher has been redeemed, or error code
	 */
	public String redeemVoucher(final CartModel cb, final String voucherCode)
	{
		final Voucher voucher = VoucherManager.getInstance().getVoucher(voucherCode);
		final Cart cart = getModelService().getSource(cb);
		final User user = getModelService().getSource(YStorefoundation.getRequestContext().getSessionContext().getUser());
		String redeemResult = null;

		//is the voucher valid?
		if (voucher == null)
		{
			redeemResult = "voucherError.notFound";
		}
		//no errors, then try to redeem the given voucher for this cart
		else
		{
			try
			{
				if (voucher.redeem(voucherCode, cart))
				{
					cart.setCalculated(false);
					YStorefoundation.getRequestContext().getOrderManagement().updateCart(cb);
				}
				//the voucher code cannot be redeemed, and we need a reason
				else
				{
					//check restrictions
					if (!voucher.isReservable(voucherCode, cart))
					{
						redeemResult = "voucherError.onlyOneAllowed";
					}
					else if (!voucher.isApplicable(cart))
					{
						redeemResult = "voucherError.restricted";
					}
					//check number of previously redeemed vouchers (depends on orders, not an carts)
					else if (!voucher.isReservable(voucherCode, user))
					{
						redeemResult = "voucherError.noMoreAvailable";
					}
					else
					{
						redeemResult = "voucherError.notFound";
					}
				}
			}
			catch (final JaloPriceFactoryException e)
			{
				//already checked, so this should not happen
				e.printStackTrace();
			}
		}

		return redeemResult;
	}

	/**
	 * Searches for all invalid vouchers for the given cart
	 * 
	 * @param cart
	 *           cart to be checked
	 * @return list of all invalid vouchers {@link VoucherModel}, or {@link java.util.Collections#EMPTY_LIST} if no
	 *         invalid voucher can be found
	 */
	public List<VoucherModel> findInvalidVouchers(final CartModel cart)
	{
		final List<Voucher> _result = new ArrayList<Voucher>();
		final VoucherManager vm = VoucherManager.getInstance();
		final Cart _cart = getModelService().getSource(cart);

		for (final String code : vm.getAppliedVoucherCodes(_cart))
		{
			final Voucher voucher = vm.getVoucher(code);
			if (!voucher.isReservable(code, _cart.getUser()))
			{
				_result.add(voucher);
				try
				{
					voucher.release(code, _cart);
				}
				catch (final JaloPriceFactoryException e)
				{
					e.printStackTrace();
				}
			}
		}
		List<VoucherModel> result = null;
		if (_result.isEmpty())
		{
			result = Collections.EMPTY_LIST;
		}
		else
		{
			result = new ArrayList<VoucherModel>();
			getModelService().getAll(_result, result);
		}
		return result;
	}

	/**
	 * LocalizationService or I18NService?
	 * <p>
	 * Uses {@link de.hybris.platform.util.Utilities} to adjust the result according some {@link Currency} settings.
	 */
	public NumberFormat getNumberFormat(final Locale locale, final CurrencyModel currency)
	{
		final DecimalFormat result = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);

		//jalo adjusts NumberFormat according some settings at CurrencyItem (number of digits, currency symbol)
		final Currency cur = getItem(currency);
		Currency.adjustDigits(result, cur);
		Currency.adjustSymbol(result, cur);

		return result;
	}


	/**
	 * UserService?
	 */
	public boolean checkPassword(final UserModel user, final String password)
	{
		return ((User) getItem(user)).checkPassword(password);
	}

	/**
	 * UserService?
	 */
	public boolean isAnonymous(final UserModel user)
	{
		// standard check
		//		final User _user = getItem(user);
		//		return _user.isAnonymousCustomer();

		// custom check
		return Constants.USER.ANONYMOUS_CUSTOMER.equals(user.getUid());
	}

	/**
	 * CommonsService?
	 */
	public void render(final RendererTemplateModel template, final Object renderContext, final Writer mailMessage)
	{
		final RendererTemplate _template = getItem(template);
		CommonsManager.getInstance().render(_template, renderContext, mailMessage);
	}

	// Classification Stuff 
	// FeatureContainer doesn't support itemmodels
	//


	public static final int CLASSIFICATION_ATTR_SEARCHABLE = 1;
	public static final int CLASSIFICATION_ATTR_LISTABLE = 2;
	public static final int CLASSIFICATION_ATTR_COMPAREABLE = 4;
	public static final int CLASSIFICATION_ATTR_ALL = 8;

	public Collection<ClassAttributeAssignmentModel> findAllAssignmentsByCategory(final CategoryModel category, final int bitmask)
	{
		final Category catg = getItem(category);
		final Collection<ClassificationClass> classes = CatalogManager.getInstance().getClassificationClasses(catg);
		final Collection<ClassAttributeAssignment> result = new LinkedHashSet<ClassAttributeAssignment>();

		for (final ClassificationClass clazz : classes)
		{
			Collection<ClassAttributeAssignment> subresult = null;
			switch (bitmask)
			{
				case CLASSIFICATION_ATTR_SEARCHABLE:
					subresult = clazz.getSearchableAttributeAssignments();
					break;
				case CLASSIFICATION_ATTR_LISTABLE:
					subresult = clazz.getListableAttributeAssignments();
					break;
				case CLASSIFICATION_ATTR_COMPAREABLE:
					subresult = clazz.getComparableAttributeAssignments();
					break;
				case CLASSIFICATION_ATTR_ALL:
					subresult = clazz.getClassificationAttributeAssignments();
					break;
				default:
					throw new StorefoundationException("");
			}
			result.addAll(subresult);
		}

		final List<ClassAttributeAssignmentModel> _result = getModelService().getAll(result, new ArrayList());
		return _result;
	}


	/**
	 * Provides access to attributes of dynamically created variants (must be used for variants without model classes)
	 * 
	 * @param product
	 * @param qualifier
	 * @return attribute value of qualifier
	 * @throws JaloSecurityException
	 */
	public Object getVariantAttribute(final VariantProductModel product, final String qualifier) throws JaloSecurityException
	{
		final Item item = getItem(product);

		Object result = item.getAttribute(qualifier);

		if (result instanceof EnumerationValue)
		{
			result = getModelService().get(result);
		}
		return result;
	}

	private <T extends Item> T getItem(final ItemModel model)
	{
		final T result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(model);
		return result;
	}

	private <T extends ItemModel> T getModel(final Item item)
	{
		final T result = item != null ? (T) YStorefoundation.getRequestContext().getPlatformServices().getModelService().get(item)
				: null;
		return result;
	}

	private ModelService getModelService()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
	}

	public static JaloBridge getInstance()
	{
		return instance;
	}

}
