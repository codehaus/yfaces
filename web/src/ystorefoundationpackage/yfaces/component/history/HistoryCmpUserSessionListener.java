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
package ystorefoundationpackage.yfaces.component.history;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.SfSessionContext.CategoryChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.ProductChangeListener;
import ystorefoundationpackage.domain.SfSessionContext.UserChangeListener;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.GlobalFrame;


/**
 * Listener for the history component. Categories and products can be remembered by it. After the user logs out, the
 * history will be cleared.
 */
public class HistoryCmpUserSessionListener implements CategoryChangeListener, ProductChangeListener, UserChangeListener
{

	public void performUserChanged(final UserModel oldUser, final UserModel newUser)
	{
		if (oldUser != null && JaloBridge.getInstance().isAnonymous(newUser))
		{
			this.getShowHistoryComponent().resetHistory();
		}
	}

	public void performCategoryChanged(final CategoryModel oldCategory, final CategoryModel newCategory)
	{
		if (newCategory != null)
		{
			// XXX:
			//			final CategoryBean bean = Webfoundation.getInstance().getBeanFactory().getBean(
			//					SfRequestContext.getCurrentContext().getPlatformServices().getModelService().getSource(newCategory));
			//			this.getShowHistoryComponent().addHistoryEntry((ItemBean) bean);
			this.getShowHistoryComponent().addHistoryEntry(newCategory);
		}
	}

	public void performProductChanged(final ProductModel oldProduct, final ProductModel newProduct)
	{
		if (newProduct != null)
		{
			this.getShowHistoryComponent().addHistoryEntry(newProduct);
		}
	}

	private ShowHistoryComponent getShowHistoryComponent()
	{
		final ShowHistoryComponent result = YStorefoundation.getRequestContext().getPageContext().getOrCreateFrame(
				GlobalFrame.class).getShowHistoryComponent();
		return result;
	}

}
