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

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * 
 * 
 */
public class RequestBean
{
	private String applicationPath = null;
	private List<String> cssFiles = null;

	public String getApplicationPath()
	{
		if (this.applicationPath == null)
		{
			final HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
					.getRequest();
			final String result = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
					+ request.getContextPath();
			this.applicationPath = (result.endsWith("/")) ? result : result.concat("/");
		}
		return this.applicationPath;
	}

	public List<String> getCssResources()
	{
		if (this.cssFiles == null)
		{
			this.cssFiles = YStorefoundation.getRequestContext().getContentManagement().getCssResources();
			if (this.cssFiles.isEmpty())
			{
				this.cssFiles = new ArrayList();
				this.cssFiles.add("css/layout.css");
				this.cssFiles.add("css/style1/components.css");
				this.cssFiles.add("css/style1/basics.css");
			}
		}
		return this.cssFiles;

	}

}
