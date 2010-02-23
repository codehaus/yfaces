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
package ystorefoundationpackage.faces;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.codehaus.yfaces.YFacesException;

import ystorefoundationpackage.domain.ConverterFactory;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 */
public class ApplyQueryParamsPhaseListener implements PhaseListener
{
	private static enum SESSION_PARAMS
	{
		LANGUAGE("lang"), CURRENCY("currency"), CATEGORY("categoryid"), PRODUCT("productid"), ORDER("orderid"), CATALOG("catalogid"), ;
		private String paramName = null;

		private SESSION_PARAMS(final String name)
		{
			this.paramName = name;
		}
	}

	private final Set<String> requestParamSet = new LinkedHashSet<String>();

	public ApplyQueryParamsPhaseListener()
	{
		for (final SESSION_PARAMS param : SESSION_PARAMS.values())
		{
			this.requestParamSet.add(param.paramName);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(final PhaseEvent arg0)
	{
		//this.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(final PhaseEvent arg0)
	{
		this.run();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#getPhaseId()
	 */
	public PhaseId getPhaseId()
	{
		//APPLY_REQUEST_VALUES would not work for GET requests
		return PhaseId.RESTORE_VIEW;
	}

	public void run()
	{
		for (final SESSION_PARAMS param : SESSION_PARAMS.values())
		{
			if (getParameter(param.paramName) != null)
			{
				switch (param)
				{
					case CATALOG:
						setCatalog();
						break;
					case CATEGORY:
						setCategory();
						break;
					case LANGUAGE:
						setLanguage();
						break;
					case PRODUCT:
						setProduct();
						break;
					case CURRENCY:
						setCurrency();
						break;
				}
			}
		}
	}


	private void setLanguage()
	{
		final String langIso = getParameter(SESSION_PARAMS.LANGUAGE.paramName);
		final LanguageModel newLang = YStorefoundation.getRequestContext().getPlatformServices().getI18NService().getLanguage(
				langIso);
		getUserSession().setLanguage(newLang);
	}

	private void setCurrency()
	{
		final String cur = getParameter(SESSION_PARAMS.CURRENCY.paramName);
		final CurrencyModel newCur = YStorefoundation.getRequestContext().getPlatformServices().getI18NService().getCurrency(cur);
		getUserSession().setCurrency(newCur);
	}

	private void setCatalog()
	{
		final String id = getParameter(SESSION_PARAMS.CATALOG.paramName);
		final CatalogModel catalog = (CatalogModel) convertIdToItem(id, CatalogModel.class);
		getUserSession().setCatalog(catalog);

	}

	private void setCategory()
	{
		final String pk = getParameter(SESSION_PARAMS.CATEGORY.paramName);
		final CategoryModel category = (CategoryModel) convertIdToItem(pk, CategoryModel.class);
		getUserSession().setCategory(category);
	}

	private void setProduct()
	{
		final String id = getParameter(SESSION_PARAMS.PRODUCT.paramName);
		final ProductModel product = (ProductModel) convertIdToItem(id, ProductModel.class);
		getUserSession().setProduct(product);
	}


	private Object convertIdToItem(final String id, final Class targetClass)
	{
		Object result = null;
		final ConverterFactory conFac = YStorefoundation.getRequestContext().getConverterFactory();
		result = conFac.createConverter(targetClass).convertIDToObject(id);
		if (result == null)
		{
			//JSF spec 11.3 says that exceptions thrown in PhaseListeners are swallowed
			//thats why the errorhandler for lifecycle exceptions gets instantiated manually here
			YStorefoundation.getRequestContext().getErrorHandler().handleException(
					new YFacesException("The requested Page is not available"));

		}
		return result;
	}

	private SfSessionContext getUserSession()
	{
		return YStorefoundation.getRequestContext().getSessionContext();

	}

	private String getParameter(final String key)
	{
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}


}
