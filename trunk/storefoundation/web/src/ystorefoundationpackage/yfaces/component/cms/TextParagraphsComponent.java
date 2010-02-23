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

import de.hybris.platform.cms.model.ParagraphContentModel;
import de.hybris.platform.cms.model.TextParagraphModel;

import java.util.List;

import org.codehaus.yfaces.component.YComponent;


/**
 * This component displays a list of paragraphs page.<br/>
 * <br/>
 * It is used for <code>"textParagraphsComponentTag.xhtml"</code>.<br/>
 */
public interface TextParagraphsComponent extends YComponent
{

	/**
	 * Sets the paragraph page. This page is is used to retrieve all available paragraphs.<br/>
	 * 
	 * @param page
	 */
	void setTextParagraphPage(ParagraphContentModel page);

	/**
	 * Returns the paragraph page. May be null.
	 * 
	 * @return {@link ParagraphContentModel}
	 */
	ParagraphContentModel getTextParagraphPage();

	/**
	 * Sets the text paragraph page by its id. This value is ignored when a text paragraph page was already set via
	 * {@link #setTextParagraphPage(ParagraphContentModel)}.
	 * 
	 * @param id
	 *           id of text paragraph page.
	 */
	void setTextParagraphPageId(String id);

	/**
	 * Returns the id of the current text paragraph page. May be null when no page was set.
	 * 
	 * @return id
	 */
	String getTextParagraphPageId();

	/**
	 * Sets the list of paragraphs which shall be displayed.
	 * 
	 * @param paragraphs
	 *           paragraphs to display
	 */
	void setTextParagraphs(List<TextParagraphModel> paragraphs);

	/**
	 * Returns the list of paragraphs which shall be displayed.
	 * 
	 * @return list of paragraphs
	 */
	List<TextParagraphModel> getTextParagraphs();

}
