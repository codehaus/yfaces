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

import org.codehaus.yfaces.component.YModel;

import de.hybris.platform.cms.model.TextParagraphModel;



/**
 * This component displays a single text paragraph.<br/>
 * CMS links are fully supported.<br/>
 * <br/>
 *It is used for <code>"textParagraphTag.xhtml"</code>.<br/>
 * <br/>
 */
public interface TextParagraphComponent extends YModel
{
	/**
	 * @return text paragraph
	 */
	TextParagraphModel getTextParagraph();

	/**
	 * Sets the text paragraph.
	 * 
	 * @param textParagraph
	 *           paragraph to set.
	 */
	void setTextParagraph(TextParagraphModel textParagraph);

	/**
	 * Sets the text paragraph by its id.
	 * 
	 * @param id
	 *           id of paragraph
	 */
	void setTextParagraphId(String id);

	/**
	 * Creates and returns the content of the text paragraph.<br/>
	 * This is simply the value {@link TextParagraphModel#getParagraphText()} but additionally parses the output for CMS
	 * links and replace it with valid HTML links.<br/>
	 * <br/>
	 * 
	 * @return content as String
	 */
	String getTextParagraphContent();
}
