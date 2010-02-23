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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>ProductQuickViewComponent</code> interface.
 */
public class DefaultProductQuickViewComponent extends AbstractYComponent implements ProductQuickViewComponent
{

	private static final long serialVersionUID = -4245833325373759326L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultProductQuickViewComponent.class);

	//current product
	private ProductModel product = null;
	private String productCode = null;
	private String nothingToDisplayMessage = null;

	//best product
	private ProductModel bestProduct = null;

	@Override
	public void validate()
	{
		if (this.product == null && this.productCode != null)
		{
			try
			{
				this.product = YStorefoundation.getRequestContext().getPlatformServices().getProductService().getProduct(
						this.productCode);
			}
			catch (final AmbiguousIdentifierException e)
			{
				//ignore if more products can be found
			}
			catch (final UnknownIdentifierException e)
			{
				log.warn("no product can be found with code [" + this.productCode + "]");
			}
		}

		if (this.nothingToDisplayMessage != null && !this.nothingToDisplayMessage.contains("{0}"))
		{
			this.nothingToDisplayMessage = this.nothingToDisplayMessage + " {0}";
		}

	}

	public String getProductCode()
	{
		return this.productCode;
	}

	public void setProductCode(final String code)
	{
		this.productCode = code;
	}

	public String getNothingFoundMsgFormat()
	{
		return this.nothingToDisplayMessage;
	}

	public void setNothingFoundMsgFormat(final String message)
	{
		this.nothingToDisplayMessage = message;
	}

	public ProductModel getBestProduct()
	{
		if (this.bestProduct == null)
		{
			this.bestProduct = YStorefoundation.getRequestContext().getProductManagement().getCheapestVariant(this.product);
		}
		return this.bestProduct;
	}

	public ProductModel getProduct()
	{
		return this.product;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

}
