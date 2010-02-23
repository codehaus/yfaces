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
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.customerreview.CustomerReviewService;
import de.hybris.platform.customerreview.model.CustomerReviewModel;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;




/**
 * Implementation of the <code>CreateCustomerReviewComponent</code> interface.
 */
public class DefaultCreateCustomerReviewComponent extends AbstractYComponent implements CreateCustomerReviewComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultCreateCustomerReviewComponent.class.getName());
	/**
	 * 
	 */
	private static final long serialVersionUID = -5998892555376565665L;
	private ProductModel product = null;
	private CustomerReviewModel customerReview = null;

	private YComponentEventHandler<CreateCustomerReviewComponent> ehSendReview = null;

	@Override
	public void validate()
	{
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}

		if (this.customerReview == null)
		{
			this.customerReview = new CustomerReviewModel();
		}

		if (this.product == null)
		{
			throw new StorefoundationException("No product available for review");
		}
	}

	/**
	 * 
	 */
	public DefaultCreateCustomerReviewComponent()
	{
		super();
		this.ehSendReview = super.createEventHandler(new SendCustomerReviewAction());

	}

	/**
	 * {@inheritDoc}
	 */
	public CustomerReviewModel getCustomerReview()
	{
		return customerReview;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setCustomerReview(final CustomerReviewModel value)
	{
		this.customerReview = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public ProductModel getProduct()
	{
		return this.product;
	}

	/**
	 * {@inheritDoc}
	 */
	public YComponentEventHandler<CreateCustomerReviewComponent> getSendReviewEvent()
	{
		return this.ehSendReview;
	}

	public static class SendCustomerReviewAction extends DefaultYComponentEventListener<CreateCustomerReviewComponent>
	{
		private static final long serialVersionUID = 3247537269817124773L;
		private transient CustomerReviewModel customerReview = null;
		private boolean inputError = false;

		@Override
		public void actionListener(final YComponentEvent<CreateCustomerReviewComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

			customerReview = event.getComponent().getCustomerReview();
			if (customerReview.getComment().length() > 4000)
			{
				reqCtx.getContentManagement().getLocalizedMessage("components.createCustomerReviewCmp.tooLongCommentMessage");
				inputError = true;
				return;
			}
			final UserModel user = reqCtx.getSessionContext().getUser();

			final CreateCustomerReviewComponent cmp = event.getComponent();
			final CustomerReviewService customerReviewService = reqCtx.getPlatformServices().getCustomerReviewService();
			customerReviewService.updateCustomerReview(cmp.getCustomerReview(), user, cmp.getProduct());
		}

		@Override
		public String action()
		{
			final String result = inputError ? "createCustomerReviewPage" : "productDetailPageRedirect";
			return result;
		}
	}
}
