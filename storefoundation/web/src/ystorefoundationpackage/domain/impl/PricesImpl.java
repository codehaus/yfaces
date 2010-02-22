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

import de.hybris.platform.cms.model.StoreModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.jalo.order.price.ProductPriceInformations;
import de.hybris.platform.jalo.order.price.TaxInformation;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.Price;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 * 
 */
public class PricesImpl implements Prices
{
	private static final Logger log = Logger.getLogger(PricesImpl.class);

	private static Price EMPTY_PRICE = new PriceImpl();

	private List<Price> pricingList = null;
	private Price defaultPricing = EMPTY_PRICE;
	private Set<UnitModel> orderableUnits = null;

	private Price bestValue = EMPTY_PRICE;
	private Price minimumAmount = EMPTY_PRICE;

	public PricesImpl(final ProductModel product)
	{
		this((Product) YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(product));
	}

	public PricesImpl(final Product product)
	{
		//some working values...
		//...Product and Currency item
		final Currency cur = JaloSession.getCurrentSession().getSessionContext().getCurrency();

		//net or gross (globally)
		final StoreModel store = YStorefoundation.getRequestContext().getPlatformServices().getCmsService().getSessionStore();
		final boolean _isNet = store.getShowNetPrices().booleanValue();

		final Price oldPricing = null;

		// TPL-1191: "old price" calculation removed; model still provides that functionality
		//Handle OLD PRICE (when available) 
		//		final Double oldPriceValue = StorefoundationManager.getInstance().getOldPrice(product);
		//		final Currency oldPriceCurrency = StorefoundationManager.getInstance().getOldPriceCurrency(product);
		//		if (oldPriceValue != null) 
		//		{
		//			double _oldPrice = oldPriceValue.doubleValue();
		//			if (oldPriceCurrency != null && !oldPriceCurrency.equals(cur)) 
		//			{
		//				_oldPrice = oldPriceCurrency.convert(cur, _oldPrice);
		//			}
		//			oldPricing = new PriceImpl(_oldPrice);
		//		}

		//get price informations (price rows)
		final Date forDate = Utilities.getDefaultCalendar().getTime();
		ProductPriceInformations pInfos = null;
		try
		{
			pInfos = product.getAllPriceInformations(forDate, _isNet);
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}

		this.defaultPricing = new PriceImpl();
		//are pricerows available?
		final Collection<PriceInformation> prices = pInfos.getPrices();
		final List<TaxInformation> taxes = new ArrayList(pInfos.getTaxes());
		this.pricingList = Collections.EMPTY_LIST;
		this.orderableUnits = new HashSet<UnitModel>();
		if (prices != null && !prices.isEmpty())
		{
			//... create PricingList
			this.pricingList = new ArrayList<Price>();
			//... for each price row ...			
			for (final PriceInformation pinfo : prices)
			{
				//...add a Price to PricingList
				final PriceImpl price = new PriceImpl(pinfo, cur, new ArrayList(pInfos.getDiscounts()), taxes);
				price.setOldPricing(oldPricing);
				this.pricingList.add(price);

				//				//Set as default price?
				//				boolean setAsDefault = (!this.defaultPricing.isAvailable()) || (price.getPriceValue() < this.defaultPricing.getPriceValue());
				//				if (setAsDefault)
				//					this.defaultPricing = price;

				if (price.getUnit() != null)
				{
					this.orderableUnits.add(price.getUnit());
				}

				//price for minimum amount
				if (this.minimumAmount.getAmount() >= price.getAmount())
				{
					this.minimumAmount = price;
				}

				//price for best value
				if (!this.bestValue.isAvailable() || price.getPriceValue() < this.bestValue.getPriceValue())
				{
					this.bestValue = price;
				}
			}
		}

		this.defaultPricing = this.bestValue;
		if (log.isDebugEnabled())
		{
			log.debug("Default Pricing for product " + product.getCode() + ": " + this.getDefaultPricing().toString());
		}

	}

	public List<Price> getPricingList()
	{
		return this.pricingList;
	}

	public void setPricingList(final List<Price> pricingList)
	{
		this.pricingList = pricingList;
	}

	public Price getDefaultPricing()
	{
		return this.defaultPricing;
	}

	public void setDefaultPricing(final Price defaultPricing)
	{
		this.defaultPricing = defaultPricing;
	}


	public Set<UnitModel> getOrderableUnits()
	{
		return this.orderableUnits;
	}

	public boolean isAvailable()
	{
		return !this.pricingList.isEmpty();
	}



	public Price getBestValuePrice()
	{
		return this.bestValue;
	}

	public Price getLowestQuantityPrice()
	{
		return this.minimumAmount;
	}

	@Override
	public String toString()
	{
		String result = super.toString();
		if (this.pricingList != null)
		{
			for (final Price price : this.pricingList)
			{
				result = result.concat(price.toString());
			}
		}
		return result;
	}

}
