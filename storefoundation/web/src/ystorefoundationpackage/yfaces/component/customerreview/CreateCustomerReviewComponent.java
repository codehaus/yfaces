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

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;


/**
 * This component allows the logged-in user to write review for the product.
 */
public interface CreateCustomerReviewComponent extends YModel
{
	/**
	 * Gets the comment from the user review
	 * 
	 * @return A string containing the comment
	 */
	CustomerReviewModel getCustomerReview();

	/**
	 * Sets the comment for the customer review
	 */
	void setCustomerReview(CustomerReviewModel comment);

	/**
	 * Gets the product from the user review
	 * 
	 * @return A ProductBean representing the product
	 */
	ProductModel getProduct();

	/**
	 * Gets the event handler for CreateCustomerReviewTag.xhtml
	 * 
	 * @return A CreateCustomerReviewComponent
	 */
	YEventHandler<CreateCustomerReviewComponent> getSendReviewEvent();

}
