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
package ystorefoundationpackage.sandbox;

import de.hybris.platform.util.Config;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.apache.myfaces.shared_tomahawk.util.MessageUtils;


public class CompositeMessageBundle extends AbstractMap
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(CompositeMessageBundle.class);

	private transient final Set entry = new HashSet();
	private static List<String> bundles = new ArrayList<String>();
	private static Pattern pattern = Pattern.compile(".*\\.messagebundle[0-9]");
	private static final String STANDARD_BUNDLE = "ystorefoundationpackage.YStoreFoundation";

	public CompositeMessageBundle()
	{
		super();
		bundles.add(STANDARD_BUNDLE);
		final Map<String, String> params = Config.getAllParameters();
		for (final Map.Entry<String, String> entry : params.entrySet())
		{
			if (pattern.matcher(entry.getKey()).matches())
			{
				bundles.add(entry.getValue());
			}
		}
	}


	@Override
	public Object get(final Object arg0)
	{
		return getMessage((String) arg0);
	}


	@Override
	public Set entrySet()
	{
		/*
		 * ResourceBundle bundle; String value; Enumeration<String> keys; String key;
		 * 
		 * for (Iterator it = bundles.iterator(); it.hasNext();) { String bundlename = (String) it.next(); bundle =
		 * ResourceBundle.getBundle(bundlename,FacesContext.getCurrentInstance().getViewRoot().getLocale()); keys =
		 * bundle.getKeys(); while (keys.hasMoreElements()) { key = keys.nextElement();
		 * entryset.add(bundle.getObject(key)); }
		 * 
		 * }
		 */
		return entry;
	}

	/**
	 * Retrieve a localized Resourcestring.
	 * 
	 * @param msgKey
	 *           Resourcekey.
	 * @return localized String
	 */

	public String getMessage(final String msgKey)
	{
		String msg = null;
		ResourceBundle bundle = null;

		for (final Iterator it = bundles.iterator(); it.hasNext();)
		{
			final String bundlename = (String) it.next();
			bundle = ResourceBundle.getBundle(bundlename, FacesContext.getCurrentInstance().getViewRoot().getLocale());
			if (msg == null || msg.equals(msgKey))
			{
				msg = MessageUtils.getMessage(bundle, msgKey, null).getDetail();
			}
		}
		return msg;
	}

	/**
	 * Retrieve a localized Resourcestring. Uses the UIViewRoot Locale or, when null, the servers default one. Uses the
	 * previously set ResourceBundle.
	 * 
	 * @param msgKey
	 *           ResourceKey
	 * @param params
	 *           Optional number of Messageparameters
	 * @return Localized String
	 */
	public String getMessage(final String msgKey, final Object... params)
	{
		String msg = null;
		ResourceBundle bundle = null;

		for (final Iterator it = bundles.iterator(); it.hasNext();)
		{
			final String bundlename = (String) it.next();
			bundle = ResourceBundle.getBundle(bundlename, FacesContext.getCurrentInstance().getViewRoot().getLocale());
			if (msg == null || msg.equals(msgKey))
			{
				msg = MessageUtils.getMessage(bundle, msgKey, params).getDetail();
			}
		}
		return (msg == null) ? "???" + msg + "???" : msg;
	}


	/**
	 * Add a simple Message globaly to our Application.
	 * 
	 * @param msgKey
	 *           Resourcekey
	 */
	public FacesMessage addMessageToApplication(final String msgKey)
	{
		return addMessageToComponent(null, msgKey, (Object[]) null);
	}

	/**
	 * Add a Message globaly to our Application.
	 * 
	 * @param msgKey
	 * @param params
	 */
	public FacesMessage addMessageToApplication(final String msgKey, final Object... params)
	{
		return addMessageToComponent(null, msgKey, params);
	}

	/**
	 * Add a Message to an UIComponent.
	 * 
	 * @param component
	 *           Component or null for all available ones (Application)
	 * @param msgKey
	 * @param params
	 */
	public FacesMessage addMessageToComponent(final UIComponent component, final String msgKey, final Object... params)
	{
		final FacesMessage msg = createFacesMessage(FacesMessage.SEVERITY_INFO, msgKey, params);
		addMessageToComponent(component, msg);
		return msg;
	}

	/**
	 * Add a Message to an UIComponent.
	 * 
	 * @param component
	 *           Component or null for all available ones (Application)
	 */
	private void addMessageToComponent(final UIComponent component, final FacesMessage facesMessage)
	{
		final String id = (component != null) ? component.getId() : null;
		FacesContext.getCurrentInstance().addMessage(id, facesMessage);
	}

	/**
	 * Helper method to create a FacesMessage.
	 */
	private FacesMessage createFacesMessage(final Severity severity, final String msgKey, final Object... params)
	{
		final String localizedMsg = getMessage(msgKey, params);
		return new FacesMessage(severity, localizedMsg, localizedMsg);
	}

	/**
	 * Assure this FasesMessage survives a redirect.
	 * 
	 * @param msg
	 *           global Message
	 */
	public void makeMessageRedirectable(final FacesMessage msg)
	{
		makeMessageRedirectable(null, msg);
	}

	/**
	 * Assure this FacesMessage survives a redirect.
	 * 
	 * @param component
	 *           Component the FacesMessage is bound to.
	 * @param msg
	 */
	public void makeMessageRedirectable(final UIComponent component, final FacesMessage msg)
	{
		final Map messages = handleRedirectMessages(3);
		final String id = (component != null) ? component.getId() : null;
		if (!messages.containsKey(id))
		{
			messages.put(id, new ArrayList());
		}
		final List l = (List) messages.get(id);
		l.add(msg);
	}

	/**
	 * Restores all FacesMessage which should survive the redirect. Normaly there's no need to call this method because
	 * it's currently done in our PhaseTracker in RESTORE_VIEW Lifecycle.
	 */
	public void restoreRedirectableMessages()
	{
		final Map messages = handleRedirectMessages(1);
		if (messages != null)
		{
			final Iterator entries = messages.entrySet().iterator();
			while (entries.hasNext())
			{
				final Map.Entry entry = (Map.Entry) entries.next();
				final String clientId = (String) entry.getKey();
				final Iterator iterMsgs = ((List) entry.getValue()).iterator();
				while (iterMsgs.hasNext())
				{
					FacesContext.getCurrentInstance().addMessage(clientId, (FacesMessage) iterMsgs.next());
				}
			}
			handleRedirectMessages(4);
		}
	}

	//1: get
	//3: getOrCreate
	//4: delete
	private Map handleRedirectMessages(final int state)
	{
		Map result = null;
		final Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		if ((state & 1) == 1)
		{
			result = (Map) sessionMap.get("MESSAGESOVERREDIRECT");
		}

		if (state == 3 && result == null)
		{
			result = new HashMap();
			sessionMap.put("MESSAGESOVERREDIRECT", result);
		}

		if (state == 4)
		{
			sessionMap.put("MESSAGESOVERREDIRECT", null);
		}
		return result;
	}

	//	public static CompositeMessageBundle getInstance() {
	//		return JSFUtils.getJsfBean(CompositeMessageBundle.class);
	//	}
}
