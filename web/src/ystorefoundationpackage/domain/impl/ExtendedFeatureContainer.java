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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.catalog.jalo.classification.ClassAttributeAssignment;
import de.hybris.platform.catalog.jalo.classification.ClassificationClass;
import de.hybris.platform.catalog.jalo.classification.util.Feature;
import de.hybris.platform.catalog.jalo.classification.util.FeatureContainer;
import de.hybris.platform.catalog.jalo.classification.util.TypedFeature;
import de.hybris.platform.variants.jalo.VariantProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Special version of a {@link FeatureContainer} which automatically includes base product features in case the owning
 * product is a {@link VariantProduct} and does not provide values for that feature.
 * 
 * 
 */
public class ExtendedFeatureContainer extends FeatureContainer
{
	private FeatureContainer baseContainer;

	public ExtendedFeatureContainer(final FeatureContainer original) throws CloneNotSupportedException
	{
		super(original);
	}

	private FeatureContainer getOrLoadBaseContainer()
	{
		if (this.baseContainer == null)
		{
			this.baseContainer = FeatureContainer.loadTyped(((VariantProduct) getProduct()).getBaseProduct(),
					new ArrayList<ClassAttributeAssignment>(getAssignmentsInternal()));
		}
		return this.baseContainer;
	}

	/**
	 * Replaces all empty features by their base product counterparts in case the ase product provides non-empty ones
	 */
	@Override
	public List<? extends Feature> getFeatures()
	{
		if (getProduct() instanceof VariantProduct)
		{
			final List<Feature> ret = new ArrayList<Feature>(super.getFeatures());
			final int s = ret.size();
			for (int i = 0; i < s; i++)
			{
				final Feature f = ret.get(i);
				if (f.isEmpty() && f instanceof TypedFeature)
				{
					final TypedFeature tf = (TypedFeature) f;
					if (getOrLoadBaseContainer().hasFeature(tf.getClassAttributeAssignment()))
					{
						final TypedFeature baseFeature = getOrLoadBaseContainer().getFeature(tf.getClassAttributeAssignment());
						if (!baseFeature.isEmpty())
						{
							ret.set(i, baseFeature);
						}
					}
				}
			}
			return ret;
		}
		else
		{
			return super.getFeatures();
		}
	}

	public List<String> getClassNames()
	{
		final List<String> ret = new ArrayList();
		for (final ClassificationClass cl : getClasses())
		{
			final String n = cl.getName();
			ret.add(n != null ? n : cl.getCode());
		}
		Collections.sort(ret);
		return ret;
	}
}
