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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.constants.CustomerReviewConstants;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>CustomerReviewComponent</code> interface.
 */
public class DefaultCustomerReviewComponent extends AbstractYComponent implements CustomerReviewComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultCustomerReviewComponent.class);

	private ProductModel product = null;
	private List<FormattedCustomerReview> allReviews = null;


	@Override
	public void validate()
	{
		// when no product is passed, take the session product
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}

		if (this.allReviews == null && this.product != null)
		{
			this.allReviews = this.createFormattedReviewsList(this.product);
		}

		if (this.product == null)
		{
			throw new StorefoundationException("No product available for review");
		}
	}

	@Override
	public List<FormattedCustomerReview> getAllReviews()
	{
		return this.allReviews;
	}

	/**
	 * Gets average rating using ProductService
	 * 
	 * @return The average rating for current product
	 */
	@Override
	public double getAverageRating()
	{
		double result = 0;
		if (product != null)
		{
			//result = Webfoundation.getInstance().getServices().getProductService().getAverageRating(productBean);
			result = getCustomerReviewService().getAverageRating(product).doubleValue();
			result = Math.round(result * 100.0) / 100.0;

		}
		return result;
	}

	/**
	 * Gets a List of Integers based on the current maximal rating and the average rating See getStarRenderList(double
	 * rating, double maxRating)
	 * 
	 * @return List of Integers
	 */
	@Override
	public List<Integer> getAverageRatingStars()
	{
		final double rating = getAverageRating();
		final double maxRating = CustomerReviewConstants.getInstance().MAXRATING;
		return createStarRenderList(rating, maxRating);
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}


	/**
	 * Gets a list of Integers used to display full, half and greyed stars for the graphical display (row of stars) of
	 * the rating. First element in the list is the left most star. The values are 0, 1, 2: - 0: Grey star - 1: Half
	 * filled star (indicating a decimal value in the range 0.25-0.75 - 2: Completely filled star
	 * 
	 * @param rating
	 *           The actual rating
	 * @param maxRating
	 *           The maximal possible rating
	 * @return List of Integers
	 */
	protected List<Integer> createStarRenderList(final double rating, final double maxRating)
	{
		final ArrayList<Integer> returnList = new ArrayList<Integer>(Collections.nCopies((int) (Math.floor(rating)), Integer
				.valueOf(2)));
		if (maxRating == Math.floor(rating))
		{
			return returnList;
		}
		if (rating - Math.floor(rating) < 0.25)
		{
			returnList.add(Integer.valueOf(0));
		}
		else if (rating - Math.floor(rating) < 0.75)
		{
			returnList.add(Integer.valueOf(1));
		}
		else
		{
			returnList.add(Integer.valueOf(2));
		}
		if (maxRating - Math.floor(rating) > 1)
		{
			returnList.addAll(Collections.nCopies((int) (maxRating - Math.floor(rating) - 1), Integer.valueOf(0)));
		}
		return returnList;
	}

	private List<FormattedCustomerReview> createFormattedReviewsList(final ProductModel product)
	{
		List<FormattedCustomerReview> result = null;
		if (product != null)
		{
			result = new ArrayList<FormattedCustomerReview>();

			final List<CustomerReviewModel> customerReviews = getCustomerReviewService().getAllReviews(product);

			for (final CustomerReviewModel review : customerReviews)
			{
				final List<Integer> starRenderList = createStarRenderList(review.getRating().doubleValue(), CustomerReviewConstants
						.getInstance().MAXRATING);
				result.add(new FormattedCustomerReview(review, starRenderList));
			}
		}
		else
		{
			result = Collections.EMPTY_LIST;
		}
		return result;
	}

	private CustomerReviewService getCustomerReviewService()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getCustomerReviewService();
	}



}
