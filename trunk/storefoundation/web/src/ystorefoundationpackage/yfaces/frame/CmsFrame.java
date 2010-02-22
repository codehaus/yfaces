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
package ystorefoundationpackage.yfaces.frame;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

import javax.faces.context.FacesContext;

import ystorefoundationpackage.yfaces.component.cms.DefaultTextParagraphsComponent;
import ystorefoundationpackage.yfaces.component.cms.TextParagraphsComponent;


/**
 * Renders the cms information.
 * 
 */
public class CmsFrame extends AbstractYFrame
{
	private YComponentBinding<TextParagraphsComponent> textParagraphsCmp = null;

	public CmsFrame()
	{
		super();
		this.textParagraphsCmp = super.createComponentBinding(this.createTextParagraphsComponent());
	}

	public YComponentBinding<TextParagraphsComponent> getTextParagraphsComponent()
	{
		return this.textParagraphsCmp;
	}

	private TextParagraphsComponent createTextParagraphsComponent()
	{
		final TextParagraphsComponent result = new DefaultTextParagraphsComponent();
		String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("cmsid");
		if (id == null)
		{
			id = "frontpage";
		}

		result.setTextParagraphPageId(id);

		return result;
	}

}
