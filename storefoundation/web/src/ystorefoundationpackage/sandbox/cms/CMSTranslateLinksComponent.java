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

import de.hybris.platform.cms.constants.CmsConstants;
import de.hybris.platform.cms.jalo.Website;

import java.io.IOException;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * This component translates the cms specific links within its body to real links.
 * 
 * 
 */
public class CMSTranslateLinksComponent extends BaseComponent
{
	private static final Logger log = Logger.getLogger(CMSTranslateLinksComponent.class.getName());

	public static final String PK_MATCHER = "{pk}";

	protected String replaceLinks(final String source, final Website website, final HttpServletResponse response)
	{
		final StringWriter out = new StringWriter();
		try
		{
			int lastPos = 0;
			final Pattern linkExpr = Pattern.compile(CmsConstants.COMPONENT_LINK_PATTERN, Pattern.MULTILINE | Pattern.DOTALL
					| Pattern.CASE_INSENSITIVE);
			final Matcher m = linkExpr.matcher(source);
			while (m.find())
			{
				out.write(source.substring(lastPos, m.start()));
				String linkPK = m.group(1);
				while (linkPK.endsWith("/"))
				{
					linkPK = linkPK.substring(0, linkPK.length() - 1);
				}
				final String linkText = m.group(2);

				if (website != null)
				{
					String targetLinkURL = website.getItemViewURLPreview();
					if (targetLinkURL.indexOf(PK_MATCHER) > -1)
					{
						targetLinkURL = response.encodeURL(targetLinkURL.substring(0, targetLinkURL.indexOf(PK_MATCHER)).concat(linkPK)
								.concat(targetLinkURL.substring(targetLinkURL.indexOf(PK_MATCHER) + PK_MATCHER.length())));
					}
					else
					{
						log.warn("itemViewURLPreview is not yet implemented!");
					}
					out.write("<a href=\"".concat(targetLinkURL).concat("\">").concat(linkText).concat("</a>"));
				}
				else
				{
					out.write(linkText);
				}
				lastPos = m.end();
			}
			out.write(source.substring(lastPos, source.length()));
			return out.toString();
		}
		catch (final PatternSyntaxException e)
		{
			throw new RuntimeException(e);
		}
	}


	ResponseWriter old;
	StringWriter out;

	@Override
	public void encodeBegin(final FacesContext ctx) throws IOException
	{
		//System.out.println( "replacing links encoding begining" );
		old = ctx.getResponseWriter();
		out = new StringWriter();
		final ResponseWriter j = ctx.getRenderKit().createResponseWriter(out, null, null);
		ctx.setResponseWriter(j);
	}

	@Override
	public void encodeEnd(final FacesContext ctx) throws IOException
	{

		//System.out.println( "replacing links encoding end "+out );
		ctx.setResponseWriter(old);
		final Website website = getWebsite();

		final HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();

		old.write(replaceLinks(out.toString(), website, response));
	}

	@Override
	public String getFamily()
	{
		return "de.hybris.cms";
	}


}
