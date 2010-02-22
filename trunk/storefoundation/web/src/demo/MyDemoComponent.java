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

import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;


/**
 * 
 * 
 */
public interface MyDemoComponent extends YComponent
{
	public String getFormField();

	public void setFormField(String field);

	public String getResult();

	public void setResult(String result);

	public YComponentEventHandler<MyDemoComponent> getSubmitEvent();
}
