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

import de.hybris.platform.cms.LiveEditSession;
import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.cms.jalo.Website;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.util.Config;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringEscapeUtils;

import ystorefoundationpackage.domain.YStorefoundation;


public class HtmlYCmsItem extends UIComponentBase
{
	public static final String COMPONENT_TYPE = "hybris.HtmlYCmsItem";

	@Override
	public String getFamily()
	{
		return "de.hybris.cms";
	}

	@Override
	public String getRendererType()
	{
		return null;
	}


	@Override
	public void encodeBegin(final FacesContext ctx) throws IOException
	{
		if (this.isLiveEditEnabled())
		{
			ctx.getResponseWriter().write(createEncodeBeginContent());
		}
	}

	@Override
	public void encodeEnd(final FacesContext ctx) throws IOException
	{
		if (this.isLiveEditEnabled())
		{
			ctx.getResponseWriter().write("</span>");
		}
	}

	private String createEncodeBeginContent()
	{
		final LiveEditSession cmsSession = CmsManager.getInstance().getLiveEditSession();
		final Website website = CmsManager.getInstance().getActiveWebsite();

		final Item item = this.getItem();
		final String webroot = website.getFrontendURL();
		final String typeView = Config.getParameter("cms.typeview");
		final String onMouseOver = "\"onMouse_Item(event, this, '" + typeView + "');\"";
		final String onMouseOut = "\"onMouse_Item(event, this, '" + typeView + "');\"";

		final String edit = CmsManager.getInstance().getLocalizedString(cmsSession.getHmcLocale(), "editcontent");
		final String copy = CmsManager.getInstance().getLocalizedString(cmsSession.getHmcLocale(), "copycontent");

		final String _hmcCallbackEvent = StringEscapeUtils.escapeHtml(cmsSession.getHmcCallbackEvent());
		final String _hmcCallback = StringEscapeUtils.escapeHtml(cmsSession.getHmcCallback());
		final String onContextMenu = "return onContextMenu_Item(event, '" + edit + "', '" + copy + "','" + _hmcCallbackEvent
				+ "', '" + _hmcCallback + "', '" + item.getPK() + "', '" + webroot + "');";

		final String result = "<span onmouseover=" + onMouseOver + " onmouseout=" + onMouseOut + " oncontextmenu=\""
				+ onContextMenu + "\">";
		return result;
	}

	private boolean isLiveEditEnabled()
	{
		final LiveEditSession session = CmsManager.getInstance().getLiveEditSession();
		final Item item = getItem();
		final boolean result = (session != null && item != null);

		return result;
	}


	private Item getItem()
	{
		final Object value = getAttributes().get("item");

		Item result = null;
		if (value instanceof ItemModel)
		{
			result = YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(value);
		}
		else
		{
			if (value instanceof Item)
			{
				result = (Item) value;
			}
		}
		//		final Item result = (value instanceof ItemBean) ? ((ItemBean) value).getItem() : (value instanceof Item) ? (Item) value
		//				: null;

		return result;
	}

}
