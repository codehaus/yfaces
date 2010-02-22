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

import javax.faces.component.html.HtmlOutputLink;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.URLCreator;
import ystorefoundationpackage.domain.URLFactory;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * A {@link HtmlOutputLink} which supports any Object as value.<br/>
 * The object is passed to the {@link URLFactory} which is responsible for creating a valid URL.<br/>
 * By default the {@link URLCreator} is detected automatically by the objectclass, however the creator can be specified
 * directly via Attribute "valuetype".<br/>
 * 
 * Attribute 'value' allows 'null'.<br/>
 * In that case the body is rendered but no link.
 * 
 * @author denny.strietzbaum
 */
public class HtmlYOutputLink extends HtmlOutputLink
{
	static private final Logger log = Logger.getLogger(HtmlYOutputLink.class);

	public static final String COMPONENT_TYPE = "javax.faces.hybris.HtmlOutputLink";

	private String _valuetype;

	public String getValuetype()
	{
		return _valuetype;
	}

	public void setValuetype(final String _type)
	{
		this._valuetype = _type;
	}


	//	@Override
	//	public Object getValue()
	//	{
	//		Object result = super.getValue();
	//
	//		if (result == null)
	//		{
	//			throw new YFacesException("No link target specified");
	//		}
	//
	//		final URLCreator creator = (_valuetype != null) ? SfRequestContext.getCurrentContext().getURLFactory().getURLCreator(
	//				_valuetype) : SfRequestContext.getCurrentContext().getURLFactory().getURLCreator(result.getClass());
	//
	//
	//		if (creator != null)
	//		{
	//			result = creator.createExternalForm(result, getAttributes());
	//		}
	//		else
	//		{
	//			log.warn("No " + URLCreator.class.getSimpleName() + " found for "
	//					+ (_valuetype != null ? _valuetype : result.getClass()));
	//			result = "???";
	//		}
	//
	//		if (log.isDebugEnabled())
	//		{
	//			log.debug("replaced [" + super.getValue() + "] with: " + result);
	//		}
	//
	//		return result;
	//	}

	@Override
	public Object getValue()
	{
		Object result = super.getValue();

		if (result != null)
		{
			final URLFactory urlFac = YStorefoundation.getRequestContext().getURLFactory();

			final URLCreator creator = (_valuetype != null) ? urlFac.getURLCreator(_valuetype) : urlFac.getURLCreator(result
					.getClass());

			if (creator != null)
			{
				result = creator.createExternalForm(result, getAttributes());
			}
			else
			{
				log.warn("No " + URLCreator.class.getSimpleName() + " found for "
						+ (_valuetype != null ? _valuetype : result.getClass()));
				result = "???";
			}

			if (log.isDebugEnabled())
			{
				log.debug("replaced [" + super.getValue() + "] with: " + result);
			}
		}
		else
		{
			result = "null";
		}

		return result;
	}

	@Override
	public void restoreState(final FacesContext context, final Object state)
	{
		final Object[] values = (Object[]) state;
		super.restoreState(context, values[0]);
		this._valuetype = (String) values[1];
	}

	@Override
	public Object saveState(final FacesContext context)
	{
		return new Object[]
		{ super.saveState(context), _valuetype };
	}

	@Override
	public void encodeBegin(final FacesContext context) throws IOException
	{
		super.encodeBegin(context);
	}

	@Override
	public void encodeEnd(final FacesContext context) throws IOException
	{
		super.encodeEnd(context);
	}


	@Override
	public void decode(final FacesContext context)
	{
		super.decode(context);
	}

	@Override
	public String getFamily()
	{
		//We use the HtmlOutputLink Family as we don't need a new Renderer
		return super.getFamily();
	}

	@Override
	protected Renderer getRenderer(final FacesContext context)
	{
		Renderer result = null;
		//always render the childs (when rendered=true)
		//but never render a link when value is null 
		if (super.getValue() != null)
		{
			result = super.getRenderer(context);
		}
		return result;
	}


}
