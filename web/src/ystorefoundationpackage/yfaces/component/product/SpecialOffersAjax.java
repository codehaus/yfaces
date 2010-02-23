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

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


public class SpecialOffersAjax extends HttpServlet
{

	private static final long serialVersionUID = -2971735824634330655L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SpecialOffersAjax.class);

	@Override
	@SuppressWarnings("boxing")
	public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html;charset=utf-8");
		final PrintWriter out = response.getWriter();
		try
		{
			final int first = Integer.parseInt(request.getParameter("first"));
			final int last = Integer.parseInt(request.getParameter("last"));
			final List<ProductModel> products = this.createDefaultProducts(request.getParameter("categoryName"));
			if (!products.isEmpty())
			{
				if (products.size() >= (last - first + 1))
				{
					final List<ProductModel> subProducts = products.subList(first - 1, last);
					final StringBuilder links = new StringBuilder(products.size() + "|");
					for (final ProductModel p : subProducts)
					{
						links.append(p.getThumbnail().getUrl() + "|" + response.encodeURL(createExternLink(p)) + "|");
					}
					out.print(links);
				}
			}
		}
		catch (final NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}
		out.close();
	}

	private List<ProductModel> createDefaultProducts(final String categoryCode)
	{
		CategoryModel category = null;
		List<ProductModel> result = Collections.EMPTY_LIST;
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final CatalogModel cat = reqCtx.getSessionContext().getCatalog();
		final CatalogVersionModel catVer = reqCtx.getPlatformServices().getCatalogService().getSessionCatalogVersion(cat.getId());
		category = reqCtx.getPlatformServices().getCategoryService().getCategory(catVer, categoryCode);
		if (category != null)
		{
			result = reqCtx.getProductManagement().findAllByCategory(category, false, null, true);
		}
		return result;
	}

	private String createExternLink(final ProductModel product)
	{
		return YStorefoundation.getRequestContext().getURLFactory().createExternalForm(product);
	}
}
