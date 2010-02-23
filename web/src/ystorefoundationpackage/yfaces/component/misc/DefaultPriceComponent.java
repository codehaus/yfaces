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
package ystorefoundationpackage.yfaces.component.misc;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.util.Utilities;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.DateTimeConverter;

import org.codehaus.yfaces.component.AbstractYModel;

import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxis.AXIS_2D;
import ystorefoundationpackage.domain.Price;
import ystorefoundationpackage.domain.PriceLeverage;
import ystorefoundationpackage.domain.Prices;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


public class DefaultPriceComponent extends AbstractYModel implements PriceComponent
{
	private static final Converter PRICE_CONVERTER = new PriceFormatter();
	private static final Converter DATERANGE_CONVERTER = new DatesFormatter();
	private static final Converter DISCOUNT_CONVERTER = new DiscountValueFormatter();

	private static final String PRICE_COLUMN = "price";
	private static final String DATERANGE_COLUMN = "daterange";
	private static final String QUANTITY_COLUMN = "quantity";

	private boolean showOldPrice = true;
	private String oldPriceOutputFormat = "{0}";
	private String priceOutputFormat = "{0}";
	private boolean showTaxes = true;
	private boolean showPriceTable = true;
	private transient String productCode = null;
	private transient ProductModel product = null;

	private transient Prices pricings = null;

	private transient DataTableAxisModel table = null;

	/**
	 * {@link Converter} for formatting price values. Is necessary as Hybris has it's own algorithms.
	 */
	private static class PriceFormatter implements Converter
	{

		public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
		{
			throw new UnsupportedOperationException();
		}

		public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
		{
			final String result = (arg2 != null) ? Utilities.getCurrencyInstance().format(arg2) : "";
			return result;
		}
	}

	/**
	 * {@link Converter} for formatting discount values.
	 */
	public static class DiscountValueFormatter implements Converter
	{
		public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
		{
			throw new UnsupportedOperationException();
		}

		public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			final PriceLeverage discount = (PriceLeverage) arg2;

			final String descr = discount.getName() != null ? discount.getName() : discount.getId();
			final String value = discount.isAbsolute() ? reqCtx.getContentManagement().getCurrencyNumberFormat().format(
					discount.getValue()) : discount.getValue() + "%";

			final String result = descr + "(" + value + ")";
			return result;
		}
	}

	public static class DatesFormatter extends DateTimeConverter
	{
		public DatesFormatter()
		{
			super();
			super.setType("date");
		}

		@Override
		public Object getAsObject(final FacesContext arg0, final UIComponent arg1, final String arg2) throws ConverterException
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public String getAsString(final FacesContext arg0, final UIComponent arg1, final Object arg2) throws ConverterException
		{
			if (!(arg2 instanceof Price))
			{
				throw new IllegalArgumentException("Need a " + Price.class + " instance");
			}

			final Price price = (Price) arg2;
			String result = "-";

			if (price.getValidFromDate() != null)
			{
				result = super.getAsString(arg0, arg1, price.getValidFromDate());
				if (price.getValidToDate() != null)
				{
					result = result + " - " + super.getAsString(arg0, arg1, price.getValidToDate());
				}
				else
				{
					result = "ab " + result;
				}
			}
			else
			{
				if (price.getValidToDate() != null)
				{
					result = "bis " + super.getAsString(arg0, arg1, price.getValidToDate());
				}
			}

			return result;
		}
	}


	@Override
	public void validate()
	{
		if (this.pricings == null)
		{
			if (this.product == null && this.productCode != null)
			{
				try
				{
					this.product = YStorefoundation.getRequestContext().getPlatformServices().getProductService().getProduct(
							this.productCode);
				}
				catch (final AmbiguousIdentifierException e)
				{
					//ignore if more products can be found
				}
			}

			if (this.product != null)
			{
				this.pricings = YStorefoundation.getRequestContext().getOrderManagement().getPrices(product);
			}
		}

		if (!this.oldPriceOutputFormat.contains("{0}"))
		{
			this.oldPriceOutputFormat = this.oldPriceOutputFormat + " {0}";
		}

		if (!this.priceOutputFormat.contains("{0}"))
		{
			this.priceOutputFormat = this.priceOutputFormat + " {0}";
		}
	}

	public void setProductCode(final String code)
	{
		this.productCode = code;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

	public boolean isShowOldPrice()
	{
		return this.showOldPrice;
	}

	public boolean isShowTaxes()
	{
		return this.showTaxes;
	}

	public void setShowOldPrice(final boolean enabled)
	{
		this.showOldPrice = enabled;
	}

	public String getOldPriceMsgFormat()
	{
		return this.oldPriceOutputFormat;
	}

	public void setOldPriceMsgFormat(final String prefix)
	{
		this.oldPriceOutputFormat = prefix;
	}

	public String getPriceMsgFormat()
	{
		return this.priceOutputFormat;
	}

	public void setPriceMsgFormat(final String prefix)
	{
		this.priceOutputFormat = prefix;
	}

	public void setShowTaxes(final boolean enabled)
	{
		this.showTaxes = enabled;
	}

	public boolean isShowPriceTable()
	{
		return this.showPriceTable;
	}

	public void setShowPriceTable(final boolean enabled)
	{
		this.showPriceTable = enabled;
	}

	public Price getDefaultPrice()
	{
		return this.pricings.getDefaultPricing();
	}

	public DataTableAxisModel getPriceTable()
	{
		if (this.table == null && this.pricings.getPricingList().size() > 1)
		{
			this.table = this.createPriceTable();
		}
		return this.table;
	}

	public Prices getPricings()
	{
		return this.pricings;
	}


	public Converter getPriceConverter()
	{
		return PRICE_CONVERTER;
	}

	public Converter getDateRangeConverter()
	{
		return DATERANGE_CONVERTER;
	}

	public Converter getDiscountConverter()
	{
		return DISCOUNT_CONVERTER;
	}


	private DataTableAxisModel createPriceTable()
	{
		final List<Price> pricingList = this.pricings.getPricingList();
		final DataTableAxisModel result = DataTableFactory.createDataTableAxisModel(pricingList, AXIS_2D.Y_AXIS, 3);

		//quantity column
		final DataTableAxis xAxis = result.getXAxis();
		final DataTableAxisMarker quantityMarker = xAxis.getMarkerAt(0);
		quantityMarker.setId(QUANTITY_COLUMN);

		//start- and enddate column
		final DataTableAxisMarker dateMarker = xAxis.getMarkerAt(1);
		dateMarker.setId(DATERANGE_COLUMN);

		//price column
		final DataTableAxisMarker priceMarker = xAxis.getMarkerAt(2);
		priceMarker.setId(PRICE_COLUMN);


		return result;
	}


}
