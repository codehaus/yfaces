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



import java.util.List;

import org.codehaus.yfaces.component.YComponent;


/**
 * This component lists all available reviews for the selected product.
 */
public interface CustomerReviewComponent extends YComponent
{

	/**
	 * Gets average rating using ProductService
	 * 
	 * @return The average rating for current product
	 */
	double getAverageRating();

	List<FormattedCustomerReview> getAllReviews();

	/**
	 * Gets a List of Integers based on the current maximal rating and the average rating See getStarRenderList(double
	 * rating, double maxRating)
	 * 
	 * @return List of Integers
	 */
	List<Integer> getAverageRatingStars();


}
