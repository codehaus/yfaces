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
package ystorefoundationpackage.yfaces.component.customerreview;

import de.hybris.platform.customerreview.model.CustomerReviewModel;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 *
 */
public class FormattedCustomerReview implements Serializable
{
	private String comment = null;
	private String headline = null;
	private String rating = null;
	private Date creationTime = null;
	private String user = null;
	private List<Integer> starRenderList = null;

	public FormattedCustomerReview(final CustomerReviewModel reviewBean, final List<Integer> starRenderList)
	{
		this.comment = reviewBean.getComment();
		this.headline = reviewBean.getHeadline();
		this.rating = String.valueOf(reviewBean.getRating());
		this.creationTime = reviewBean.getCreationtime();
		this.user = reviewBean.getUser().getUid();
		this.starRenderList = starRenderList;
	}

	public String getComment()
	{
		return this.comment;
	}

	public String getHeadline()
	{
		return this.headline;
	}

	public String getRatingString()
	{
		return this.rating;
	}

	public Date getCreationTime()
	{
		return this.creationTime;
	}

	public List<Integer> getRatingStars()
	{
		return this.starRenderList;
	}

	public String getUser()
	{
		return this.user;
	}
}
