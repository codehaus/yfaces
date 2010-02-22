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
package ystorefoundationpackage.yfaces.component.product;

import de.hybris.yfaces.component.AbstractYComponent;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;


public class DefaultScrollProductsComponent extends AbstractYComponent implements ScrollProductsComponent
{

	private static final long serialVersionUID = 1382126047164116553L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultScrollProductsComponent.class);

	private int scrollSize = 3;

	private int auto = 5;

	private String title = "specialOffers";

	private String categoryName = "specialoffers";

	@Override
	public void validate()
	{
		//not so much to do now...
	}

	public int getScrollSize()
	{
		return this.scrollSize;
	}

	public void setScrollSize(final int scrollSize)
	{
		this.scrollSize = scrollSize;
	}

	public String getTitle()
	{
		return YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(this.title);
	}

	public void setTitle(final String title)
	{
		this.title = title;
	}

	public String getCategoryName()
	{
		return this.categoryName;
	}

	public void setCategoryName(final String categoryName)
	{
		this.categoryName = categoryName;
	}

	public int getAuto()
	{
		return this.auto;
	}

	public void setAuto(final int auto)
	{
		this.auto = auto;
	}

}
