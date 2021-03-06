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
package ystorefoundationpackage.yfaces.component.product;

import org.codehaus.yfaces.component.YModel;


public interface ScrollProductsComponent extends YModel
{

	public int getScrollSize();

	public void setScrollSize(int scrollSize);

	public int getAuto();

	public void setAuto(int auto);

	public String getTitle();

	public void setTitle(String title);

	public String getCategoryName();

	public void setCategoryName(String categoryName);

}
