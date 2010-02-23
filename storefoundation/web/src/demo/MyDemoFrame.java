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

import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YModelBinding;



public class MyDemoFrame extends AbstractYFrame
{
	private YModelBinding<MyDemoComponent> myDemoCmp = null;
	private YModelBinding<MyDemoComponentLW> myDemoCmpLW = null;

	public MyDemoFrame()
	{
		super();
		this.myDemoCmp = super.createComponentBinding();
		this.myDemoCmpLW = super.createComponentBinding();
	}

	public YModelBinding<MyDemoComponent> getMyDemoComponent()
	{
		return this.myDemoCmp;
	}

	public YModelBinding<MyDemoComponentLW> getMyDemoComponentLW()
	{
		return this.myDemoCmpLW;
	}


}
