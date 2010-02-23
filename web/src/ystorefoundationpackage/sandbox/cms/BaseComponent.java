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

import de.hybris.platform.cms.jalo.CmsManager;
import de.hybris.platform.cms.jalo.Website;
import de.hybris.platform.core.PK;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


/**
 * Superclass to all JSF-Components.
 */
public abstract class BaseComponent extends UIComponentBase
{

	protected Item getItem(final String param)
	{
		final Object o = getAttributes().get(param);

		if (o instanceof String)
		{
			return JaloSession.getCurrentSession().getItem(PK.parse((String) o));
		}
		else if (o instanceof PK)
		{
			return JaloSession.getCurrentSession().getItem((PK) o);
		}
		else if (o instanceof Item)
		{
			return (Item) o;
		}
		else
		{
			return null;
		}
	}

	protected HttpSession getSession()
	{
		final HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		return session;
	}

	protected Website getWebsite()
	{
		return CmsManager.getInstance().getActiveWebsite();
	}


}
