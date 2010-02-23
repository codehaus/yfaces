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
package ystorefoundationpackage.sandbox.cms;

import javax.faces.component.UIComponent;

import org.apache.myfaces.shared_impl.taglib.UIComponentTagBase;


/**
 * Helper class that bridges between a tag and a component.
 * 
 * 
 */
public class CMSEditableComponentTag extends UIComponentTagBase
{
	Object template, item;



	public Object getItem()
	{
		return item;
	}

	public void setItem(final Object item)
	{
		this.item = item;
	}

	public Object getTemplate()
	{
		return template;
	}

	public void setTemplate(final Object template)
	{
		this.template = template;
	}

	@Override
	public String getComponentType()
	{
		return "de.hybris.cms.CMSEditableComponent";
	}

	@Override
	protected void setProperties(final UIComponent component)
	{
		super.setProperties(component);
		component.getAttributes().put("template", template);
		component.getAttributes().put("item", item);
	}

	@Override
	public String getRendererType()
	{
		// renders itself
		return null;
	}

}
