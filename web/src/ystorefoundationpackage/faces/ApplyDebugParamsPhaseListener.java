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

import de.hybris.platform.cms.LiveEditSessionImpl;
import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import ystorefoundationpackage.StorefoundationConfig;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 */
public class ApplyDebugParamsPhaseListener implements PhaseListener
{

	private static final Logger log = Logger.getLogger(ApplyDebugParamsPhaseListener.class);

	private static final String XSD_TEMPLATE = "yfaces-components.xsd.vm";

	private static enum DEBUG_PARAMS
	{
		CHANGE_USER("pUser"), CHANGE_CART("pCart"), LIVE_EDIT("pLiveEdit"), COMPONENT("pComponents"), CLEAR("pClear"), RESTART(
				"pRestart"), ;

		private String paramName = null;

		private DEBUG_PARAMS(final String name)
		{
			this.paramName = name;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#afterPhase(javax.faces.event.PhaseEvent)
	 */
	public void afterPhase(final PhaseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.event.PhaseListener#beforePhase(javax.faces.event.PhaseEvent)
	 */
	public void beforePhase(final PhaseEvent arg0)
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
		return PhaseId.RESTORE_VIEW;
	}

	private final Set<String> requestParamSet = new LinkedHashSet<String>();

	public ApplyDebugParamsPhaseListener()
	{
		for (final DEBUG_PARAMS param : DEBUG_PARAMS.values())
		{
			this.requestParamSet.add(param.paramName);
		}
	}

	public void run()
	{
		if (this.isExecutable())
		{
			for (final DEBUG_PARAMS param : DEBUG_PARAMS.values())
			{
				final Map requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
				if (requestParams.get(param.paramName) != null)
				{
					switch (param)
					{
						case CHANGE_USER:
							processUser();
							break;
						case CHANGE_CART:
							processCart();
							break;
						case LIVE_EDIT:
							processLiveEdit();
							break;
						//						case COMPONENT:
						//							processComponents();
						//							break;
						case CLEAR:
							processClearAll();
							break;
						case RESTART:
							processPlatformRestart();
							break;
					}
				}
			}
		}
	}

	public boolean isExecutable()
	{
		boolean result = false;
		if (StorefoundationConfig.IS_DEVELOPERMODE.getBoolean())
		{
			final Map requestParams = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			result = requestParams.get("pEnable") != null;

		}
		return result;
	}


	/**
	 * Internal. Evaluates parameter {@link DEBUG_PARAMS#CHANGE_CART}. Expects a Productcode. <br/>
	 * Creates an initial cart with a Product given by the code.
	 */
	private void processCart()
	{
		final String value = getParameter(DEBUG_PARAMS.CHANGE_CART.paramName);
		final ProductModel product = YStorefoundation.getRequestContext().getPlatformServices().getProductService().getProduct(
				value);

		final CartModel cart = this.getUserSession().getCart();
		YStorefoundation.getRequestContext().getOrderManagement().addToCart(cart, product, 1, product.getUnit());

		log.info("Set an initial cart");
	}

	/**
	 * Internal. Evaluates parameter <code>DEBUG_PARAMS#CHANGE_USER</code>. Expects a userlogin. <br/>
	 * Sets the User given by the login.
	 */
	private void processUser()
	{
		final String value = getParameter(DEBUG_PARAMS.CHANGE_USER.paramName);
		final UserModel newUser = YStorefoundation.getRequestContext().getPlatformServices().getUserService().getUser(value);
		final UserModel oldUser = this.getUserSession().getUser();

		log.info("Change User from " + oldUser + " to " + newUser);

		this.getUserSession().setUser(newUser);
	}

	private void processLiveEdit()
	{
		final String _liveEdit = getParameter(DEBUG_PARAMS.LIVE_EDIT.paramName);
		final Boolean isLiveEdit = _liveEdit != null ? Boolean.valueOf(_liveEdit) : null;
		if (isLiveEdit != null)
		{
			CmsManager.getInstance().setLiveEditSession(
					isLiveEdit.booleanValue() ? new LiveEditSessionImpl((Map) FacesContext.getCurrentInstance().getExternalContext()
							.getRequestParameterMap()) : null);
		}
	}

	//	private void processComponents()
	//	{
	//		final String value = getParameter(DEBUG_PARAMS.COMPONENT.paramName);
	//		final List<String> params = Arrays.asList(value.split(","));
	//
	//		final String action = params.get(0);
	//
	//		if ("list".equals(action))
	//		{
	//			for (final Map.Entry<String, YComponentInfo> entry : YComponentRegistry.getInstance().getAllComponents().entrySet())
	//			{
	//				log.info(entry.getKey() + " -> " + entry.getValue().getURL());
	//			}
	//			return;
	//		}
	//
	//		if ("xsd".equals(action))
	//		{
	//			final VelocityContext ctx = new VelocityContext();
	//			ctx.put("components", YComponentRegistry.getInstance().getAllComponents().values());
	//
	//			try
	//			{
	//				final String template = StorefoundationConfig.XSD_SOURCE.getString();
	//				final String target = StorefoundationConfig.XSD_TARGET.getString();
	//
	//				Reader in = null;
	//				if (template != null)
	//				{
	//					in = new FileReader(template + "\\" + XSD_TEMPLATE);
	//				}
	//				else
	//				{
	//					in = new InputStreamReader(FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(
	//							"/WEB-INF/" + XSD_TEMPLATE), "UTF-8");
	//				}
	//
	//				Writer out = null;
	//				if (target != null)
	//				{
	//					out = new FileWriter(target + "\\" + XSD_TEMPLATE.substring(0, XSD_TEMPLATE.length() - 3));
	//				}
	//				else
	//				{
	//					final HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
	//							.getResponse();
	//					resp.setCharacterEncoding("UTF-8");
	//					resp.setContentType("text/xml");
	//
	//					out = resp.getWriter();
	//				}
	//
	//				Velocity.evaluate(ctx, out, "log", in);
	//				out.flush();
	//				out.close();
	//
	//			}
	//			catch (final Exception e)
	//			{
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		FacesContext.getCurrentInstance().responseComplete();
	//	}

	private void processClearAll()
	{
		final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		request.getSession().invalidate();
		FacesContext.getCurrentInstance().responseComplete();
		FacesContext.getCurrentInstance().renderResponse();
	}

	private void processPlatformRestart()
	{
		Registry.destroyAndForceStartup();
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
