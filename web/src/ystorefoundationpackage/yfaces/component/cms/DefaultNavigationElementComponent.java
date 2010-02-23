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
package ystorefoundationpackage.yfaces.component.cms;

import de.hybris.platform.cms.CmsService;
import de.hybris.platform.cms.model.NavigationElementModel;
import de.hybris.platform.cms.model.WebsiteModel;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>NavigationElementComponent</code> interface.
 */
public class DefaultNavigationElementComponent extends AbstractYModel implements NavigationElementComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultNavigationElementComponent.class);

	private NavigationElementModel navElement = null;
	private String navElementId = null;

	private transient Object target = null;
	private WebsiteModel website = null;

	@Override
	public void validate()
	{
		final CmsService cmsService = YStorefoundation.getRequestContext().getPlatformServices().getCmsService();

		if (this.website == null)
		{
			this.website = cmsService.getSessionStore().getWebsite();
		}

		//when no navelement is available but an id ... 
		if (this.navElement == null && this.navElementId != null)
		{
			//...take the id for find navelement
			this.navElement = cmsService.findNavigationElementBy(website, this.navElementId);
		}
		else if (this.navElement != null)
		{
			//...otherwise set id only
			this.navElementId = this.navElement.getCode();
		}

		if (this.target == null)
		{
			this.target = (this.navElement.getContentURL() != null) ? this.navElement.getContentURL() : this.navElement.getContent();
		}

	}


	public Object getTarget()
	{
		return target;
	}

	public NavigationElementModel getNavigationElement()
	{
		return this.navElement;
	}

	public void setNavigationElement(final NavigationElementModel navigationElement)
	{
		this.navElement = navigationElement;
	}


	public void setNavigationElementId(final String id)
	{
		this.navElementId = id;
	}


	//	/**
	//	 * Creates the link target for the {@link NavigationElementBean}
	//	 */
	//	private String createTarget(final NavigationElementModel nav)
	//	{
	//		String result = nav.getContentURL();
	//		if (result == null)
	//		{
	//			//retrieve pageContent
	//			final Object value = nav.getContent();
	//			if (value != null)
	//			{
	//				result = SfRequestContext.getCurrentContext().getURLFactory().createExternalForm(value);
	//			}
	//			else
	//			{
	//				LOG.error("NavigationElement '" + nav.getCode() + "' is not linked with any content");
	//				target = "[undefined]";
	//			}
	//		}
	//		return result;
	//	}

}
