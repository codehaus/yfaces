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
package ystorefoundationpackage.domain.util.list;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.jalo.Item;

import java.util.List;

import org.apache.commons.collections.Transformer;

import ystorefoundationpackage.domain.YStorefoundation;


/**
 * *
 */
public class ModelWrapperList<T extends ItemModel> extends BidiTransformedList<Item, T> //extends AbstractList<T> implements Serializable
{

	private static Transformer BEAN_TO_ITEM = new Transformer()
	{
		public Object transform(final Object input)
		{
			return YStorefoundation.getRequestContext().getPlatformServices().getModelService().getSource(input);
		}
	};

	private static Transformer ITEM_TO_BEAN = new Transformer()
	{
		public Object transform(final Object input)
		{
			return YStorefoundation.getRequestContext().getPlatformServices().getModelService().get(input);
		}
	};

	public ModelWrapperList(final List source)
	{
		super(source, ITEM_TO_BEAN, BEAN_TO_ITEM);
	}
}
