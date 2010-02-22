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

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;



public class MyDemoFrame extends AbstractYFrame
{
	private YComponentBinding<MyDemoComponent> myDemoCmp = null;
	private YComponentBinding<MyDemoComponentLW> myDemoCmpLW = null;

	public MyDemoFrame()
	{
		super();
		this.myDemoCmp = super.createComponentBinding();
		this.myDemoCmpLW = super.createComponentBinding();
	}

	public YComponentBinding<MyDemoComponent> getMyDemoComponent()
	{
		return this.myDemoCmp;
	}

	public YComponentBinding<MyDemoComponentLW> getMyDemoComponentLW()
	{
		return this.myDemoCmpLW;
	}


}
