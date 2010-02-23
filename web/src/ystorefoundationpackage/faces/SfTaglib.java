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


import org.apache.log4j.Logger;

import com.sun.facelets.tag.AbstractTagLibrary;


public class SfTaglib extends AbstractTagLibrary
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SfTaglib.class);

	/** Namespace used to import this library in Facelets pages */
	public static final String NAMESPACE = "http://hybris.com/jsf/storefoundation";

	public SfTaglib()
	{
		super(NAMESPACE);

		//Add taghandlers
		super.addTagHandler("loadBundle", SfLoadBundleTagHandler.class);

		super.addComponent("outputLink", HtmlYOutputLink.COMPONENT_TYPE, null);
		super.addComponent("graphicImage", HtmlYGraphicImage.COMPONENT_TYPE, null);
		super.addComponent("cmsItem", HtmlYCmsItem.COMPONENT_TYPE, null);
	}

}
