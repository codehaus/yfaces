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

import de.hybris.platform.core.model.c2l.CurrencyModel;



/**
 * 
 * 
 */
public class DefaultPriceLeverage implements PriceLeverage
{
	private String id = null;
	private String name = null;
	private double value = 0d;
	private boolean absolute = true;
	private double appliedValue = 0d;
	private CurrencyModel appliedCurrency = null;
	private int type = PriceLeverage.TYPE_UNKNOWN;

	public void setValues(final String id, final double value, final boolean isAbsolute)
	{
		this.id = id != null ? id : "";
		this.value = value;
		this.absolute = isAbsolute;
	}


	public CurrencyModel getAppliedCurrency()
	{
		return this.appliedCurrency;
	}

	public double getAppliedValue()
	{
		return this.appliedValue;
	}

	public String getId()
	{
		return this.id;
	}

	public int getType()
	{
		return this.type;
	}

	public double getValue()
	{
		return this.value;
	}

	public boolean isAbsolute()
	{
		return this.absolute;
	}

	public boolean isRelative()
	{
		return !this.absolute;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	protected void setId(final String id)
	{
		this.id = id;
	}

	protected void setAbsolute(final boolean isAbsolute)
	{
		this.absolute = isAbsolute;
	}

	protected void setValue(final double value)
	{
		this.value = value;
	}

	protected void setAppliedValue(final double appliedValue)
	{
		this.appliedValue = appliedValue;
	}

	protected void setAppliedCurrency(final CurrencyModel currency)
	{
		this.appliedCurrency = currency;
	}

	protected void setType(final int type)
	{
		this.type = type;
	}


	@Override
	public String toString()
	{
		final String _code = this.id != null ? this.id : "";
		final String _cur = this.appliedCurrency != null ? this.appliedCurrency.getIsocode() : "???";
		final String _applied = String.valueOf(this.appliedValue) + _cur;

		final String _result = PriceLeverage.class.getSimpleName() + "(" + _code + "): " + value + (this.absolute ? _cur : "%")
				+ " = " + _applied;

		return _result;
	}

}
