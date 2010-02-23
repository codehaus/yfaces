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
import de.hybris.platform.cms.model.TextParagraphModel;
import de.hybris.platform.cms.model.WebsiteModel;
import de.hybris.platform.core.model.ItemModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.yfaces.component.AbstractYModel;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>TextParagraphComponent</code> interface.
 */
public class DefaultTextParagraphComponent extends AbstractYModel implements TextParagraphComponent
{
	public static final Pattern pattern = Pattern.compile("href[ ]*=[ ]*\"(component://(.*?))\"");

	private TextParagraphModel textParagraph = null;
	private String textParagraphId = null;


	private transient String paragraphText = null;

	private transient WebsiteModel website = null;


	@Override
	public void validate()
	{
		final CmsService cmsService = YStorefoundation.getRequestContext().getPlatformServices().getCmsService();

		if (this.website == null)
		{
			this.website = cmsService.getSessionStore().getWebsite();
		}

		if (this.textParagraph == null && this.textParagraphId != null)
		{
			this.textParagraph = cmsService.findTextParagraphBy(website, textParagraphId);
		}
	}

	@Override
	public void refresh()
	{
		this.paragraphText = null;
	}



	public String getTextParagraphContent()
	{
		if (this.paragraphText == null)
		{
			final String text = (this.getTextParagraph() != null) ? this.getTextParagraph().getParagraphText() : "";
			this.paragraphText = this.parseTextParagraph(text);
		}
		return this.paragraphText;
	}

	public TextParagraphModel getTextParagraph()
	{
		return this.textParagraph;
	}

	public void setTextParagraph(final TextParagraphModel textParagraph)
	{
		this.textParagraph = textParagraph;
	}

	public void setTextParagraphId(final String id)
	{
		this.textParagraphId = id;
	}


	/**
	 * Creates and returns the text content for this component.<br/>
	 * Parses the content taken from {@link #getTextParagraph()} and replaces CMS links with valid http links.<br/>
	 * 
	 * @return String
	 */
	private String parseTextParagraph(final String paragraphText)
	{
		String result = "";
		if (paragraphText != null && paragraphText.trim().length() > 0)
		{
			final Matcher m = pattern.matcher(paragraphText);

			final StringBuffer sb = new StringBuffer();
			int c = 0;
			while (m.find())
			{
				final String link = this.translateHyperlink(m.group(2));
				sb.append(paragraphText.substring(c, m.start()));
				sb.append("href=\"" + link + "\"");
				c = m.end();
			}
			sb.append(paragraphText.substring(c));
			result = sb.toString();
		}
		return result;
	}

	/**
	 * Translates the CMS hyperlink into a valid http hyperlink.
	 * 
	 * @param id
	 *           cms link (e.g. component//12345)
	 * @return http link
	 */
	private String translateHyperlink(final String id)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final ItemModel resource = reqCtx.getConverterFactory().convertIdToObject(ItemModel.class, id);
		final String result = reqCtx.getURLFactory().createExternalForm(resource);

		return result;
	}



}
