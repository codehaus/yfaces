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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;
import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 *
 */
public class SubmitEvent extends DefaultYComponentEventListener<FeatureValueSelectorComponent>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.yfaces.component.DefaultYComponentEventListener#actionListener(de.hybris.yfaces.component.YComponentEvent
	 * )
	 */
	@Override
	public void actionListener(final YComponentEvent<FeatureValueSelectorComponent> event)
	{
		final FeatureValueSelectorComponent cmp = event.getComponent();
		final List<ProductModel> filteredProducts = this.getFilteredProducts(cmp);
		cmp.setFilteredProductList(filteredProducts);
		if (cmp.getFunnelMode())
		{
			cmp.setProductList(filteredProducts);
		}
	}

	private List<ProductModel> getFilteredProducts(final FeatureValueSelectorComponent cmp)
	{
		final List<ProductModel> products = cmp.getProductList();
		final List<ProductModel> result = new ArrayList<ProductModel>();

		for (final ProductModel product : products)
		{
			final ProductFeatures features = YStorefoundation.getRequestContext().getProductManagement().getFeatures(product);
			final Map<String, FormattedAttribute> featureMap = features.getAttributesMap();

			boolean matchesFilter = true;
			final Iterator<SfSelectItemGroup> iter = cmp.getSelectorList().iterator();
			while (iter.hasNext() && matchesFilter)
			{
				final SfSelectItemGroup selectGroup = iter.next();
				final String selectedValue = (String) selectGroup.getSelectedValue();

				if (!"null".equals(selectedValue))
				{
					final String id = selectGroup.getDescription();
					final FormattedAttribute attrib = featureMap.get(id);
					matchesFilter = attrib != null && selectedValue.equals(attrib.getValue());
				}
			}
			if (matchesFilter)
			{
				result.add(product);
			}
		}
		return result;

	}

}
