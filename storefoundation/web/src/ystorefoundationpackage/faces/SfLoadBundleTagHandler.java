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

import de.hybris.platform.util.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagAttributeException;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagHandler;


public class SfLoadBundleTagHandler extends TagHandler
{

	private static final Log log = LogFactory.getLog(SfLoadBundleTagHandler.class);

	private static class BundleMap implements Map<String, String>
	{
		private final ResourceBundle _bundle;
		private final boolean empty;
		private List<String> _values; // lazy

		BundleMap(final ResourceBundle bundle)
		{
			_bundle = bundle;
			empty = !_bundle.getKeys().hasMoreElements();
		}

		public String get(final Object key)
		{
			try
			{
				return _bundle.getString((String) key);
			}
			catch (final MissingResourceException e)
			{
				return "[" + key + "]";
			}
		}

		public boolean isEmpty()
		{
			return empty;
		}

		public boolean containsKey(final Object key)
		{
			try
			{
				return _bundle.getString((String) key) != null;
			}
			catch (final MissingResourceException e)
			{
				return false;
			}
		}

		public Collection<String> values()
		{
			if (_values == null)
			{
				final List<String> lst = new ArrayList<String>();
				for (final Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements();)
				{
					lst.add(_bundle.getString(enumer.nextElement()));
				}
				_values = Collections.unmodifiableList(lst);
			}
			return _values;
		}

		public int size()
		{
			return values().size();
		}

		public boolean containsValue(final Object value)
		{
			return values().contains(value);
		}

		public Set<Map.Entry<String, String>> entrySet()
		{
			final Set<Map.Entry<String, String>> set = new HashSet<Entry<String, String>>();
			for (final Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements();)
			{
				final String k = enumer.nextElement();
				set.add(new Map.Entry<String, String>()
				{
					public String getKey()
					{
						return k;
					}

					public String getValue()
					{
						return _bundle.getString(k);
					}

					public String setValue(final String value)
					{
						throw new UnsupportedOperationException(getClass().getName() + " UnsupportedOperationException");
					}
				});
			}
			return set;
		}

		public Set<String> keySet()
		{
			final Set<String> set = new HashSet<String>();
			for (final Enumeration<String> enumer = _bundle.getKeys(); enumer.hasMoreElements(); set.add(enumer.nextElement()))
			{
				// DOCTODO Document reason, why this block is empty
			}
			return set;
		}

		public String remove(final Object key)
		{
			throw new UnsupportedOperationException(getClass().getName() + " UnsupportedOperationException");
		}

		public void putAll(final Map<? extends String, ? extends String> t)
		{
			throw new UnsupportedOperationException(getClass().getName() + " UnsupportedOperationException");
		}

		public String put(final String key, final String value)
		{
			throw new UnsupportedOperationException(getClass().getName() + " UnsupportedOperationException");
		}

		public void clear()
		{
			throw new UnsupportedOperationException(getClass().getName() + " UnsupportedOperationException");
		}
	}


	private final TagAttribute _baseName;
	private final TagAttribute _var;

	public SfLoadBundleTagHandler(final TagConfig config)
	{
		super(config);
		_baseName = getRequiredAttribute("basename");
		_var = getRequiredAttribute("var");
	}

	public void apply(final FaceletContext ctx, final UIComponent arg1) throws IOException, FacesException, FaceletException,
			ELException
	{
		String baseName = null;
		try
		{
			final ResourceBundle bundle = Utilities.getResourceBundle(baseName = _baseName.getValue(ctx), Thread.currentThread()
					.getContextClassLoader());
			if (bundle != null)
			{
				final FacesContext faces = ctx.getFacesContext();
				faces.getExternalContext().getRequestMap().put(_var.getValue(ctx), new BundleMap(bundle));
			}
			else
			{
				log.error("Resource bundle '" + baseName + "' could not be found.");
			}
		}
		catch (final Exception e)
		{
			throw new TagAttributeException(tag, _baseName, e);
		}
	}
}
