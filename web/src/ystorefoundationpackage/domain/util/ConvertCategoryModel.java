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
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.category.model.CategoryModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.YStorefoundation;


public class ConvertCategoryModel extends ConvertItemModel<CategoryModel>
{

	private static final Logger log = Logger.getLogger(ConvertCategoryModel.class);

	private static final String ID_DELIMITER = "/";
	private static final String PARAM_CATALOGID = "catalogId";
	private static final String PARAM_CATALOG = "catalog";

	private CatalogModel catalog = null;
	private String catalogId = null;
	private String categoryId = null;

	private final ConvertCatalogModel catalogModelConverter = new ConvertCatalogModel();

	@Override
	public void setProperties(final Map properties)
	{
		super.setProperties(properties);
		setCatalog((CatalogModel) properties.get(PARAM_CATALOG));
		this.setCatalogId((String) properties.get(PARAM_CATALOGID));
		this.catalogModelConverter.setProperties(properties);
	}

	@Override
	public void setConvertedString(final String string)
	{
		super.setConvertedString(string);

		final String[] params = string.split(ID_DELIMITER);
		if (params.length == 2)
		{
			setCatalogId(params[0]);
			this.categoryId = params[1];
		}
		else
		{
			this.categoryId = params[0];
		}
	}

	@Override
	public String convertObjectToString()
	{
		final CategoryModel catg = getConvertedObject();
		String _catalogId = getCatalogId();
		String _categoryId = null;

		if (_catalogId == null)
		{
			if (getCatalog() != null)
			{
				_catalogId = catalogModelConverter.convertObjectToID(getCatalog());
			}
			else
			{
				final Category _category = getPlatformServices().getModelService().getSource(catg);
				final CatalogVersion cv = CatalogManager.getInstance().getCatalogVersion(_category);
				final CatalogModel cat = getPlatformServices().getModelService().get(cv.getCatalog());
				_catalogId = catalogModelConverter.convertObjectToID(cat);
			}
		}

		_categoryId = super.getValidIdOrPK(catg.getCode(), catg);
		final String result = _catalogId + ID_DELIMITER + _categoryId;
		return result;
	}

	@Override
	public CategoryModel convertStringToObject()
	{
		CategoryModel result = null;
		Collection<Category> catgs = Collections.EMPTY_LIST;

		if (getCatalog() == null && getCatalogId() == null)
		{
			catgs = CategoryManager.getInstance().getCategoriesByCode(this.categoryId);
		}
		else
		{
			final CatalogModel _catalog = getCatalog() == null ? catalogModelConverter.convertIDToObject(getCatalogId())
					: getCatalog();
			if (_catalog != null)
			{
				final CatalogVersionModel catVer = YStorefoundation.getRequestContext().getPlatformServices().getCatalogService()
						.getSessionCatalogVersion(_catalog.getId());
				final CatalogVersion _catVer = getPlatformServices().getModelService().getSource(catVer);
				catgs = _catVer.getCategories(this.categoryId);
			}
		}

		//no categories at all, then try category id as PK
		if (catgs == null || catgs.isEmpty())
		{
			final Category _catg = super.getItemByPK(this.categoryId);
			catgs = _catg != null ? Collections.singleton(_catg) : Collections.EMPTY_LIST;
		}

		if (catgs.size() != 1)
		{
			final String msg = (catgs.isEmpty()) ? "Found no category for id " + this.categoryId
					: "Found more than one category for id " + this.categoryId;
			log.error(msg);
		}

		if (!catgs.isEmpty())
		{
			result = getPlatformServices().getModelService().get(catgs.iterator().next());
		}
		else
		{
			log.error("Can't convert " + getConvertedString() + " to an appropriate object");
		}

		return result;
	}

	private PlatformServices getPlatformServices()
	{
		return YStorefoundation.getRequestContext().getPlatformServices();
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
