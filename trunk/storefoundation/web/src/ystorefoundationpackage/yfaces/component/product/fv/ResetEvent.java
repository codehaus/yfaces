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

import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;


/**
 * This event gets fired when the user tries to reset all feature values.
 */
public class ResetEvent extends DefaultYEventListener<FeatureValueSelectorComponent>
{

	@Override
	public void actionListener(final YEvent<FeatureValueSelectorComponent> event)
	{
		final FeatureValueSelectorComponent cmp = event.getComponent();
		cmp.setProductList(null);
		cmp.setFilteredProductList(null);
	}

}
