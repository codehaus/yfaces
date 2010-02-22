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
package ystorefoundationpackage.domain.util;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;


public class ConvertProductModel extends ConvertItemModel<ProductModel>
{

	private static final Logger log = Logger.getLogger(ProductModel.class);

	private static final String PARAM_CATALOG = "catalog";
	private static final String PARAM_CATALOGID = "catalogId";

	private static final String ID_DELIMITER = "/";

	private String catalogId = null;
	private CatalogModel catalog = null;

	private String productId = null;

	private final ConvertCatalogModel catalogConverter = new ConvertCatalogModel();

	@Override
	public void setProperties(final Map properties)
	{
		super.setProperties(properties);
		setCatalog((CatalogModel) properties.get(PARAM_CATALOG));
		setCatalogId((String) properties.get(PARAM_CATALOGID));
		this.catalogConverter.setProperties(properties);
	}

	@Override
	public void setConvertedString(final String id)
	{
		super.setConvertedString(id);

		final String[] params = id.split(ID_DELIMITER);
		if (params.length == 2)
		{
			setCatalogId(params[0]);
			this.productId = params[1];
		}
		else
		{
			this.productId = params[0];
		}
	}

	@Override
	public String convertObjectToString()
	{
		final ProductModel product = getConvertedObject();
		String _catalogId = getCatalogId();
		String _productId = null;

		if (_catalogId == null)
		{
			if (getCatalog() != null)
			{
				_catalogId = catalogConverter.convertObjectToID(getCatalog());
			}
			else
			{
				final Product _product = getModelService().getSource(product);
				final CatalogVersion cv = CatalogManager.getInstance().getCatalogVersion(_product);
				final CatalogModel cat = getModelService().get(cv.getCatalog());
				_catalogId = catalogConverter.convertObjectToID(cat);
			}
		}
		_productId = super.getValidIdOrPK(product.getCode(), product);
		final String result = _catalogId + ID_DELIMITER + _productId;
		return result;
	}

	@Override
	public ProductModel convertStringToObject()
	{
		ProductModel result = null;
		Collection<Product> products = null;
		final boolean catalogAvailable = getCatalog() != null || getCatalogId() != null;

		//when a catalog shall be used...
		if (catalogAvailable)
		{
			//...either by instance or id...
			final CatalogModel _catalog = getCatalog() == null ? this.catalogConverter.convertIDToObject(getCatalogId())
					: getCatalog();
			//...and the catalog can be used
			if (_catalog != null)
			{
				//...then request products from that catalog
				final CatalogVersionModel catVer = YStorefoundation.getRequestContext().getPlatformServices().getCatalogService()
						.getSessionCatalogVersion(_catalog.getId());
				final CatalogVersion _catVer = getModelService().getSource(catVer);
				products = _catVer.getProducts(this.productId);
			}
			//otherwise error and return
			else
			{
				log.error("Found no catalog for id " + getCatalogId());
			}
		}
		//when no catalog is used
		else
		{
			//request all available products by ID
			products = ProductManager.getInstance().getProductsByCode(this.productId);
		}

		//no products at all, then try product id as PK
		if (products == null || products.isEmpty())
		{
			final Product _product = super.getItemByPK(this.productId);
			products = _product != null ? Collections.singleton(_product) : Collections.EMPTY_LIST;
		}

		if (products.size() != 1)
		{
			final String msg = (products.isEmpty()) ? "Found no product for id " + this.productId
					: "Found more than one product for id " + this.productId;
			log.error(msg);
		}

		if (!products.isEmpty())
		{
			result = getModelService().get(products.iterator().next());
		}
		else
		{
			log.error("Can't convert " + getConvertedString() + " to an appropriate object");
		}

		return result;
	}

	private ModelService getModelService()
	{
		return YStorefoundation.getRequestContext().getPlatformServices().getModelService();
	}

	public CatalogModel getCatalog()
	{
		return catalog;
	}

	public void setCatalog(final CatalogModel catalog)
	{
		this.catalog = catalog;
	}

	public String getCatalogId()
	{
		return catalogId;
	}

	public void setCatalogId(final String catalogId)
	{
		this.catalogId = catalogId;
	}

}
