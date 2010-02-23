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
package ystorefoundationpackage.sandbox.cms;

import de.hybris.platform.cms.jalo.AbstractCMSItem;
import de.hybris.platform.cms.jalo.Template;
import de.hybris.platform.cms.jalo.Website;
import de.hybris.platform.cms.web.CMSRequestHandler;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.util.Config;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


/**
 * If the website is viewed as preview from the HMC, this component encloses its body with a red frame. If the user
 * clicks right a context menu is shown that allows to open a certain set of HMC editors. These include an editor to the
 * CMS item that represents the surrounded view component and an HMC-Text-editor that allows to edit the xhtml template
 * of the view component.
 * 
 * 
 */

public class CMSEditableComponent extends BaseComponent
{


	protected String getTemplate()
	{
		final Object n = getAttributes().get("template");
		if (!(n instanceof String) && !(n instanceof Template))
		{
			throw new IllegalArgumentException(
					"Attribute Template is not of Type java.lang.String or de.hybris.platform.cms.jalo.Template but of type "
							+ (n != null ? n.getClass() : null) + ".");
		}
		String template = "";
		if (n instanceof String)
		{
			template = (String) n;
		}
		else
		{
			template = ((Template) n).getCode();
		}
		return template;
	}

	protected String getStart()
	{
		final HttpSession session = getSession();


		final Item item = getItem("item");
		final Website website = getWebsite();


		//System.out.println( "session="+session+" hmccallback="+CMSRequestHandler.getHMCCallbackWindow(session)+" item="+item );	
		final String template = getTemplate();
		final String webroot = website.getFrontendURL();


		final StringWriter stw = new StringWriter();
		final PrintWriter out = new PrintWriter(stw);
		final String code = item instanceof AbstractCMSItem ? ((AbstractCMSItem) item).getCode() : String.valueOf(item);
		out.println("<!-- start CMSEditable: " + code + " template:" + template + " -->");

		if (CMSRequestHandler.getHMCCallbackWindow(session) != null
				&& (item != null || CMSRequestHandler.canEditTemplates(session)))
		{

			final String typeView = Config.getParameter("cms.typeview");
			final String onMouseOver;
			final String onMouseOut;
			if (typeView != null && "border".equals(typeView))
			{
				onMouseOver = "\"event.cancelBubble=true;return showBorder(this);\"";
				onMouseOut = "\"event.cancelBubble=true;return removeBorder(this);\"";
			}
			else
			{
				onMouseOver = "\"event.cancelBubble=true;return highlight(this);\"";
				onMouseOut = "\"event.cancelBubble=true;return lowlight(this);\"";
			}

			final StringBuilder onContextMenu = new StringBuilder();
			onContextMenu.append("showCMSMenu( document.getElementById('cmscontextmenu'), event);");

			if (CMSRequestHandler.canEditTemplates(session) && template != null)
			{
				onContextMenu.append("addTemplateLink(document.getElementById('cmscontextmenu'), '"
						+ CMSRequestHandler.getLocalizedString(session, "edittemplate") + "', '"
						+ CMSRequestHandler.getHMCCallbackEvent(session) + "', '" + CMSRequestHandler.getHMCCallbackWindow(session)
						+ "', '" + template + "', '" + webroot + "');");
			}
			if (item != null)
			{
				onContextMenu.append("addItemLinks(document.getElementById('cmscontextmenu'), '"
						+ CMSRequestHandler.getLocalizedString(session, "editcontent") + "', '"
						+ CMSRequestHandler.getLocalizedString(session, "copycontent") + "','"
						+ CMSRequestHandler.getHMCCallbackEvent(session) + "', '" + CMSRequestHandler.getHMCCallbackWindow(session)
						+ "', '" + item.getPK().toString() + "', '" + webroot + "');");
			}
			onContextMenu.append("event.cancelBubble=true;return false;");

			out.println("<span onmouseover=" + onMouseOver + " onmouseout=" + onMouseOut + " oncontextmenu=\""
					+ onContextMenu.toString() + "\">");
		}

		return stw.toString();
	}

	protected String getEnd()
	{
		final Item item = getItem("item");
		final String template = getTemplate();

		final StringWriter stw = new StringWriter();
		final PrintWriter out = new PrintWriter(stw);
		final String code = item instanceof AbstractCMSItem ? ((AbstractCMSItem) item).getCode() : String.valueOf(item);

		out.println("<!-- end CMSEditable: " + code + " template:" + template + " -->");

		if (CMSRequestHandler.getHMCCallbackWindow(getSession()) != null
				&& (item != null || CMSRequestHandler.canEditTemplates(getSession())))
		{
			out.println("</span>");
		}
		return stw.toString();
	}


	/**
	 * Method that is invoked when the opening tag is handled.
	 */
	@Override
	public void encodeBegin(final FacesContext ctx) throws IOException
	{
		//System.out.println( "encoding begining" );
		ctx.getResponseWriter().write(getStart());
	}

	/**
	 * Method that is invoked when the closing tag is handled.
	 */
	@Override
	public void encodeEnd(final FacesContext ctx) throws IOException
	{
		//System.out.println( "encoding end " );
		ctx.getResponseWriter().write(getEnd());
	}


	/**
	 * Gets the component family.
	 */
	@Override
	public String getFamily()
	{
		return "de.hybris.cms";
	}

}
