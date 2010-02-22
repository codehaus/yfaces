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
package ystorefoundationpackage;

import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.cms.LiveEditSession;
import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.cms.jalo.Store;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.util.HybrisInitFilter;
import de.hybris.platform.util.Utilities;
import de.hybris.platform.util.WebSessionFunctions;
import de.hybris.yfaces.context.YSessionContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractApplicationContext;


/**
 * Configured as Filter in the <code>web.xml</code>. See
 * <link>http://java.sun.com/j2ee/1.4/docs/tutorial/doc/Servlets8.html#wp64572</link> for futher information on the
 * concepts of filters.
 */
public class YStoreFoundationInitFilter extends HybrisInitFilter //CMSServletFilter
{
	private static final Logger log = Logger.getLogger(YStoreFoundationInitFilter.class);


	@Override
	public boolean doPreRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException
	{
		//get system initialization state
		boolean processRequest = this.isSystemInitialized(request) && !request.getParameterMap().containsKey("notinitialized");

		if (processRequest)
		{
			processRequest = super.doPreRequest(request, response);

			final JaloSession jaloSession = WebSessionFunctions.tryGetJaloSession(request.getSession());
			if (jaloSession != null)
			{
				this.doPreRequest(jaloSession);
			}
			else
			{
				log.warn("missing jalosession for request " + request + " - cannot select catalog versions");
			}
		}
		else
		{
			systemNotInitializedResponse(request, response, "/notinitialized.html");
		}
		return processRequest;
	}

	protected void doPreRequest(final JaloSession jaloSession)
	{
		final LiveEditSession liveEditSession = CmsManager.getInstance().getLiveEditSession();
		final Store activeStore = CmsManager.getInstance().getActiveStore();

		final boolean isPreviewModeEnabled = CatalogManager.getInstance().isActivatingPreviewMode(jaloSession);
		final boolean isLiveEditStarted = liveEditSession != null && liveEditSession.isNewSession();
		if (activeStore != null)
		{
			if (isPreviewModeEnabled)
			{
				log.info("Starting 'Preview' mode for store " + activeStore.getCode());
				this.resetYFacesSession();
			}

			if (isLiveEditStarted)
			{
				log.info("Starting 'LiveEdit' mode for store " + activeStore.getCode());
				this.resetYFacesSession();
			}

		}
		else
		{
			throw new StorefoundationException("There is no 'Store' instance in the system that matches the following: \n"
					+ "1) status should be 'online' \n"
					+ "2) URL pattern must match this webapplication (If you are not sure use '.*' as URL-pattern) \n"
					+ "3) At least 1 active catalog must be assigned to this store.\n"
					+ "if these parameters are set correctly the Storefoundation should be able to display this store.");
		}


		//some logging
		if (log.isDebugEnabled())
		{
			final String store = activeStore.getCode() + "(" + activeStore.getWebsite().getCode() + ")";

			final String liveEdit = liveEditSession != null ? "yes; initial:" + liveEditSession.isNewSession() : "no";
			final String versions = jaloSession.getAttribute(CatalogConstants.SESSION_CATALOG_VERSIONS).toString();

			final String msg = "Using: " + store + "; " + versions + " (previewmode:" + isPreviewModeEnabled + "; liveedit:"
					+ liveEdit + ")";

			log.debug(msg);
		}

	}

	/**
	 * Method for requests who are fired on a not initialized System.
	 * 
	 * @param request
	 *           {@link HttpServletRequest}
	 * @param response
	 *           {@link HttpServletResponse}
	 * @return true when request processing lifecycle shall be continued
	 * @throws ServletException
	 */
	protected boolean systemNotInitializedResponse(final HttpServletRequest request, final HttpServletResponse response,
			String target) throws ServletException
	{
		try
		{
			if (target != null)
			{
				target = request.getContextPath() + target;
				response.sendRedirect(target);
			}
			else
			{
				response.setContentType("text/html");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write("System not initialized");
				response.getWriter().close();
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Checks whether the system is initialized
	 * 
	 * @param request
	 *           {@link HttpServletRequest}
	 * @return true when initialized
	 */
	protected boolean isSystemInitialized(final HttpServletRequest request)
	{
		final Tenant t = Registry.getCurrentTenant();
		Boolean result = (Boolean) request.getSession().getAttribute(this.getClass().getName() + "_" + t.getTenantID());
		if (result == null)
		{
			result = Boolean.valueOf(Utilities.isSystemInitialized(Registry.getCurrentTenant().getDataSource()));
			request.getSession().setAttribute(this.getClass().getName() + "_" + t.getTenantID(), result);
			if (log.isDebugEnabled())
			{
				log.debug("Initial System initialization check: got " + result);
			}
		}
		return result.booleanValue();
	}



	private void resetYFacesSession()
	{
		final AbstractApplicationContext ctx = (AbstractApplicationContext) Registry.getApplicationContext();
		ctx.getBeanFactory().destroyScopedBean(YSessionContext.class.getSimpleName());
	}






}
