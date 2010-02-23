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

import java.util.List;

import org.codehaus.yfaces.component.YComponent;


/**
 * This component renders a list of navigation elements.<br/>
 * The elements may be passed in various ways.<br/>
 * <br/>
 * See <code>"navigationElementsComponentTag.xhtml"</code>.<br/>
 */
public interface NavigationElementsComponent extends YComponent
{
	/**
	 * Sets the id of the root navigation element.<br/>
	 * The children of the root element are used for this component.
	 * 
	 * @param id
	 *           id of root element
	 */
	void setRootNavigationElementId(String id);

	/**
	 * Sets a root navigation element.<br/>
	 * The children of this one are used for this component.<br/>
	 * A concrete {@link NavigationElementModel} always overrules a optionally set root navigation id.<br/>
	 * 
	 * @param root
	 *           root element
	 */
	void setRootNavigationElement(NavigationElementModel root);

	/**
	 * Returns the root navigation element. The children of this one are used for this component. May return null.
	 * 
	 * @return {@link NavigationElementModel}
	 */
	NavigationElementModel getRootNavigationElement();

	/**
	 * Sets the list of navigation elements which shall be rendered.
	 * 
	 * @param elements
	 *           list of {@link NavigationElementModel}
	 */
	void setNavigationElements(List<NavigationElementModel> elements);

	/**
	 * Returns the list of navigation elements which shall be rendered.
	 * 
	 * @return list of navigation elements
	 */
	List<NavigationElementModel> getNavigationElements();
}
