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

import java.io.IOException;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.log4j.Logger;
import org.apache.myfaces.shared_impl.renderkit.html.HTML;
import org.apache.myfaces.shared_impl.renderkit.html.HtmlRendererUtils;


/**
 * A HtmlGraphicImage customization which works properly with the Hybris Mediaweb. Affected Attributes: - value The
 * standard behaviour always adds the Webapplicationpath if 'value' starts with a slash. This doens't work properly with
 * Hybris mediaweb (which is absolute).
 * 
 * This implementation supressed the JSF functionality and acts like the default img behaviour (means: a path starting
 * with slash is really absolute). To get the Faces behaviour back, just add '~' as first characters of your path.
 * (Similar to Unix for the homedir)
 * 
 * 
 * 
 */
public class HtmlYGraphicImage extends HtmlGraphicImage
{
	static private final Logger log = Logger.getLogger(HtmlYGraphicImage.class);

	public static final String COMPONENT_TYPE = "javax.faces.hybris.HtmlGraphicImage";

	/*
	 * Unlike the original component, this component renders itself. Renderercode is taken from the
	 * HtmlImageRenderer(Base) and modified for Hybris. The difference: If getValue returns a tilede as first char, the
	 * webapplicationpath will be inserted in front of the string, otherwiese the original value will be taken as URL.
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeEnd(javax.faces.context.FacesContext)
	 */
	@Override
	public void encodeEnd(final FacesContext facesContext) throws IOException
	{
		final ResponseWriter writer = facesContext.getResponseWriter();

		final String url = (String) super.getValue();

		final String src = (url.charAt(0) == '~') ? facesContext.getApplication().getViewHandler().getResourceURL(facesContext,
				url.substring(1)) : url;

		if (log.isDebugEnabled())
		{
			log.debug("Image URL: changed " + url + " to " + src);
		}

		if (url.length() > 0)
		{
			writer.startElement(HTML.IMG_ELEM, this);

			HtmlRendererUtils.writeIdIfNecessary(writer, this, facesContext);

			writer.writeURIAttribute(HTML.SRC_ATTR, facesContext.getExternalContext().encodeResourceURL(src), null);

			HtmlRendererUtils.renderHTMLAttributes(writer, this, HTML.IMG_PASSTHROUGH_ATTRIBUTES);

			writer.endElement(org.apache.myfaces.shared_impl.renderkit.html.HTML.IMG_ELEM);
		}
	}
}
