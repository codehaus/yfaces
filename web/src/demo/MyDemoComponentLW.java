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


import javax.faces.event.ActionEvent;

import org.codehaus.yfaces.component.AbstractYModel;



/**
 * 
 * 
 */
public class MyDemoComponentLW extends AbstractYModel
{
	private String field = null;
	private String result = null;


	public String getFormField()
	{
		return this.field;
	}

	public void setFormField(final String field)
	{
		this.field = field;
	}

	public String getResult()
	{
		return this.result;
	}

	public void setResult(final String result)
	{
		this.result = result;
	}

	public String doSubmit()
	{
		return null;
	}

	public void doSubmit(final ActionEvent e)
	{
		this.result = this.field.toLowerCase();
	}
}
