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

import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;



public class MyDemoComponentImpl extends AbstractYModel implements MyDemoComponent
{
	private String formField = null;
	private String result = null;

	private YEventHandler<MyDemoComponent> ehSubmit = null;

	public static class MyDemoComponentAction extends DefaultYEventListener<MyDemoComponent>
	{
		@Override
		public void actionListener(final YEvent<MyDemoComponent> event)
		{
			final MyDemoComponent cmp = event.getComponent();
			cmp.setResult(cmp.getFormField().toLowerCase());
		}
	}

	public MyDemoComponentImpl()
	{
		super();
		this.ehSubmit = super.createEventHandler(new MyDemoComponentAction());
	}


	public String getFormField()
	{
		return this.formField;
	}

	public String getResult()
	{
		return this.result;
	}

	public YEventHandler<MyDemoComponent> getSubmitEvent()
	{
		return this.ehSubmit;
	}

	public void setFormField(final String field)
	{
		this.formField = field;
	}

	public void setResult(final String result)
	{
		this.result = result;
	}

}
