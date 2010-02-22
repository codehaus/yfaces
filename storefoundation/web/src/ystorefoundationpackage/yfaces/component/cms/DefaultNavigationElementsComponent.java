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
import de.hybris.yfaces.component.AbstractYComponent;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>NavigationElementsComponent</code> interface.
 */
public class DefaultNavigationElementsComponent extends AbstractYComponent implements NavigationElementsComponent
{
	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(DefaultNavigationElementsComponent.class);

	private List<NavigationElementModel> elements = null;

	private transient NavigationElementModel rootElement = null;
	private transient String rootId = null;

	@Override
	public void validate()
	{
		if (this.elements == null)
		{
			this.elements = Collections.EMPTY_LIST;

			if (this.rootElement == null && this.rootId != null)
			{
				final CmsService cmsService = YStorefoundation.getRequestContext().getPlatformServices().getCmsService();

				final WebsiteModel ws = cmsService.getSessionStore().getWebsite();
				this.rootElement = cmsService.findNavigationElementBy(ws, this.rootId);
				if (this.rootElement == null)
				{
					if (LOG.isInfoEnabled())
					{
						LOG.info("no navigation element with code '" + this.rootId + "' found, will not render it.");
					}
				}
			}

			if (this.rootElement != null)
			{
				this.elements = this.rootElement.getChildren();
			}
		}
	}

	public List<NavigationElementModel> getNavigationElements()
	{
		return this.elements;
	}

	public void setNavigationElements(final List<NavigationElementModel> elements)
	{
		this.elements = elements;
	}

	public NavigationElementModel getRootNavigationElement()
	{
		return this.rootElement;
	}

	public void setRootNavigationElement(final NavigationElementModel root)
	{
		this.rootElement = root;
	}

	public void setRootNavigationElementId(final String id)
	{
		this.rootId = id;
	}

}
