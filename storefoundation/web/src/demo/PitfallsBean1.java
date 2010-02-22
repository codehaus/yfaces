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
package demo;

import java.util.Date;
import java.util.Locale;

import javax.faces.event.ActionEvent;


/**
 * Show difference between a ValueBinding and a Value.
 * 
 * 
 */
public class PitfallsBean1
{
	private Locale locale = Locale.ENGLISH;
	private Date date = null;

	public PitfallsBean1()
	{
		this.date = new Date(System.currentTimeMillis());
	}

	public void setLocale(final ActionEvent event)
	{
		final String id = (String) event.getComponent().getAttributes().get("LOCALE");
		this.locale = "de".equals(id) ? Locale.GERMAN : Locale.ENGLISH;
	}

	public Locale getLocale()
	{
		return this.locale;
	}

	public Date getDate()
	{
		return this.date;
	}
}
