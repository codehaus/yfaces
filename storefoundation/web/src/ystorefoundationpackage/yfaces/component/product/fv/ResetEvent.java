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
package ystorefoundationpackage.yfaces.component.product.fv;

import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;


/**
 * This event gets fired when the user tries to reset all feature values.
 */
public class ResetEvent extends DefaultYComponentEventListener<FeatureValueSelectorComponent>
{

	@Override
	public void actionListener(final YComponentEvent<FeatureValueSelectorComponent> event)
	{
		final FeatureValueSelectorComponent cmp = event.getComponent();
		cmp.setProductList(null);
		cmp.setFilteredProductList(null);
	}

}
