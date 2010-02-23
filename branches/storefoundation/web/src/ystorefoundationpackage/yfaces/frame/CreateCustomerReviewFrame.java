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

import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YModelBinding;

import ystorefoundationpackage.yfaces.component.customerreview.CreateCustomerReviewComponent;


/**
 * Renders the page for the user to evaluate and write a review for the selected product.
 * 
 */
public class CreateCustomerReviewFrame extends AbstractYFrame
{

	private static final long serialVersionUID = 7865042503932068434L;

	private YModelBinding<CreateCustomerReviewComponent> customerReviewCmp = null;

	public CreateCustomerReviewFrame()
	{
		super();
		this.customerReviewCmp = super.createComponentBinding();
	}

	public YModelBinding<CreateCustomerReviewComponent> getCreateCustomerReviewComponent()
	{
		return this.customerReviewCmp;
	}

}
