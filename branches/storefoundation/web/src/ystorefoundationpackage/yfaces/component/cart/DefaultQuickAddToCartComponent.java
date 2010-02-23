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
package ystorefoundationpackage.yfaces.component.cart;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.commons.validator.GenericValidator;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;


/**
 * Implementation of the <code>QuickAddToCartComponent</code> interface.
 */
public class DefaultQuickAddToCartComponent extends AbstractYModel implements QuickAddToCartComponent
{
	private static final long serialVersionUID = 8712436172843689183L;

	/**
	 * This event gets fired when the user tries to add the product using its code.
	 */
	public static class AddProductByCodeEvent extends DefaultYEventListener<QuickAddToCartComponent>
	{
		private static final long serialVersionUID = 8712436172843689185L;

		@Override
		public void actionListener(final YEvent<QuickAddToCartComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();
			final QuickAddToCartComponent cmp = event.getComponent();
			final String code = cmp.getProductCode();
			if (!GenericValidator.isBlankOrNull(code))
			{
				try
				{
					final ProductModel product = YStorefoundation.getRequestContext().getPlatformServices().getProductService()
							.getProduct(code.trim());
					final AddToCartResult state = reqCtx.getOrderManagement().addToCart(sessCtx.getCart(), product,
							cmp.getProductQuantity().longValue(), product.getUnit());

					switch (state)
					{
						case ADDED_ONE:
							sessCtx.getMessages().pushInfoMessage("addedtocart_one", product.getName());
							break;
						case ADDED_SEVERAL:
							sessCtx.getMessages().pushInfoMessage("addedtocart_many", cmp.getProductQuantity().toString(),
									product.getName());
							break;

						case CHOOSE_VARIANT:
							sessCtx.getMessages().pushInfoMessage("quickaddchoosevariant");
							String url = reqCtx.getURLFactory().createExternalForm(product);
							reqCtx.redirect(url);
							break;
						case CHOOSE_UNIT:
							sessCtx.getMessages().pushInfoMessage("quickaddmultipleunits");
							url = reqCtx.getURLFactory().createExternalForm(product);
							reqCtx.redirect(url, false);
							break;

						case INVALID_QUANTITY:
							sessCtx.getMessages().pushInfoMessage("no_valid_quantity");
							break;
						case NOT_ORDERABLE:
							sessCtx.getMessages().pushInfoMessage("quickaddcannotorder");
							break;
						case NOT_FOUND:
							sessCtx.getMessages().pushInfoMessage("quickaddnoproductfound");
							break;
						case UNEXPECTED_ERROR:
							sessCtx.getMessages().pushErrorMessage("unexpectedError");
							break;
					}
				}
				catch (final UnknownIdentifierException ue)
				{
					sessCtx.getMessages().pushInfoMessage("quickaddnoproductfound");
				}
				catch (final AmbiguousIdentifierException ae)
				{
					sessCtx.getMessages().pushInfoMessage("quickaddmorethanoneproduct");
				}

			}
		}
	}


	private String productCode;
	private Integer productQuantity;

	private YEventHandler<QuickAddToCartComponent> ehAddProductByCode = null;

	public DefaultQuickAddToCartComponent()
	{
		super();
		this.ehAddProductByCode = super.createEventHandler(new AddProductByCodeEvent());
	}


	public String getProductCode()
	{
		return productCode;
	}

	public void setProductCode(final String productCode)
	{
		this.productCode = productCode;
	}

	public Integer getProductQuantity()
	{
		return productQuantity;
	}

	public void setProductQuantity(final Integer productQuantity)
	{
		this.productQuantity = productQuantity;
	}

	public YEventHandler<QuickAddToCartComponent> getAddProductByCodeEvent()
	{
		return this.ehAddProductByCode;
	}

	@Override
	public void validate()
	{
		if (getProductQuantity() == null)
		{
			setProductQuantity(Integer.valueOf(1));
		}

	}


}
