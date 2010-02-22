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

import de.hybris.platform.cms.model.NavigationElementModel;
import de.hybris.yfaces.component.YComponent;


/**
 * This component renders a single {@link NavigationElementModel}.<br/>
 * 
 * See <code>navigationElementTag.xhtml</code>.
 * 
 */
public interface NavigationElementComponent extends YComponent
{
	/**
	 * Sets the navigation element by its id.
	 */
	void setNavigationElementId(String id);

	/**
	 * Sets the navigation element which shall be rendered.
	 * 
	 * @param navigationElement
	 *           navigation element to set.
	 */
	void setNavigationElement(NavigationElementModel navigationElement);

	/**
	 * Returns the navigation element which shall be rendered.
	 * 
	 * @return {@link NavigationElementModel}
	 */
	NavigationElementModel getNavigationElement();

	/**
	 * Returns the target of this NavigationElement.<br/>
	 * This is always a valid HTTP URL.<br/>
	 * The url is taken from {@link NavigationElementModel#getContentURL()}.<br/>
	 * When this value is null the url gets created for the page returned by {@link NavigationElementModel#getContent()}.
	 * 
	 * @return http url
	 */
	Object getTarget();
}
