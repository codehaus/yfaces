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

import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;



public class MyDemoComponentImpl extends AbstractYComponent implements MyDemoComponent
{
	private String formField = null;
	private String result = null;

	private YComponentEventHandler<MyDemoComponent> ehSubmit = null;

	public static class MyDemoComponentAction extends DefaultYComponentEventListener<MyDemoComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<MyDemoComponent> event)
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

	public YComponentEventHandler<MyDemoComponent> getSubmitEvent()
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
