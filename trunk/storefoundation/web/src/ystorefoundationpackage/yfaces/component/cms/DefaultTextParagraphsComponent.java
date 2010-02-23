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
package ystorefoundationpackage.yfaces.component.cms;

import de.hybris.platform.cms.CmsService;
import de.hybris.platform.cms.model.ParagraphContentModel;
import de.hybris.platform.cms.model.ParagraphModel;
import de.hybris.platform.cms.model.TextParagraphModel;
import de.hybris.platform.cms.model.WebsiteModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehaus.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>TextParagraphsComponent</code> interface.
 */
public class DefaultTextParagraphsComponent extends AbstractYComponent implements TextParagraphsComponent
{
	private String pageId = null;
	private List<TextParagraphModel> paragraphs = null;

	private transient ParagraphContentModel page = null;
	private transient WebsiteModel website = null;

	@Override
	public void validate()
	{
		final CmsService cmsService = YStorefoundation.getRequestContext().getPlatformServices().getCmsService();
		//almost always a website is needed
		if (this.website == null)
		{
			this.website = cmsService.getSessionStore().getWebsite();
		}

		//when no paragraphs are available...
		if (this.paragraphs == null)
		{
			this.paragraphs = Collections.EMPTY_LIST;

			//...set page via pageid...
			if (this.page == null && this.pageId != null)
			{
				this.page = cmsService.findParagraphPageBy(website, this.pageId);
			}

			//...and/or take available page to retrieve paragraphs
			if (this.page != null)
			{
				this.paragraphs = new ArrayList<TextParagraphModel>();
				for (final ParagraphModel p : this.page.getParagraphs())
				{
					this.paragraphs.add((TextParagraphModel) p);
				}
				this.pageId = this.page.getCode();
			}
		}
	}

	public String getTextParagraphPageId()
	{
		return this.pageId;
	}

	public void setTextParagraphPageId(final String id)
	{
		this.pageId = id;
	}

	public ParagraphContentModel getTextParagraphPage()
	{
		return this.page;
	}

	public List<TextParagraphModel> getTextParagraphs()
	{
		return this.paragraphs;
	}

	public void setTextParagraphPage(final ParagraphContentModel page)
	{
		this.page = page;
	}

	public void setTextParagraphs(final List<TextParagraphModel> paragraphs)
	{
		this.paragraphs = paragraphs;
	}

}
