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

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.model.product.ProductModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.Map.Entry;

import ystorefoundationpackage.Localized;
import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;


public class ProductFeaturesImpl implements ProductFeatures
{
	List<String> featureGroups = null;
	List<FormattedAttribute> featureList = null;
	Map<String, FormattedAttribute> featureMap = new HashMap<String, FormattedAttribute>();

	public ProductFeaturesImpl(final ProductModel product)
	{
		this(product, true);
	}

	public ProductFeaturesImpl(final ProductModel product, final boolean hideEmptyFeatures)
	{
		final Set<String> featGroupSet = new TreeSet<String>();
		final ClassificationService cs = YStorefoundation.getRequestContext().getPlatformServices().getClassificationService();
		final FeatureList features = cs.getFeatures(product);

		this.featureList = features.getFeatures().isEmpty() ? Collections.EMPTY_LIST : new ArrayList<Entry<String, String>>();

		for (final Feature feature : features)
		{
			if (hideEmptyFeatures && !feature.getValues().isEmpty())
			{
				final String attrName = feature.getName() != null ? feature.getName() : feature.getCode();
				final String attrValue = this.formatFeature(feature);
				final String attrId = feature.getClassAttributeAssignment().getClassificationAttribute().getCode();
				final FormattedAttribute attribute = new FormattedAttributeImpl(attrId, attrName, attrValue);
				featureMap.put(attrId, attribute);
				this.featureList.add(attribute);
			}
			featGroupSet.add(feature.getClassAttributeAssignment().getClassificationClass().getName());
		}
		this.featureGroups = new ArrayList(featGroupSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.domain.ProductManagement.ProductFeatures#getFeatureValueMap()
	 */
	@Override
	public Map<String, FormattedAttribute> getAttributesMap()
	{
		return this.featureMap;
	}

	private String formatFeature(final Feature feature)
	{
		final List<FeatureValue> featValues = feature.getValues();
		String result = "";
		if (!featValues.isEmpty())
		{
			final StringBuilder builder = new StringBuilder();
			final ClassAttributeAssignmentModel featAssignment = feature.getClassAttributeAssignment();
			for (final FeatureValue featValue : featValues)
			{
				final String _value = formatFeatureValue(feature, featValue);
				builder.append(_value);
				if (featAssignment.getRange().booleanValue())
				{
					builder.append(" - ");
				}
				else
				{
					builder.append(" ,");
				}
			}
			result = builder.substring(0, builder.length() - 2);
		}
		return result;
	}

	private String formatFeatureValue(final Feature feature, final FeatureValue featValue)
	{
		String result = "-";
		if (featValue != null)
		{
			final ClassAttributeAssignmentModel classAttribAssignment = feature.getClassAttributeAssignment();
			final ClassificationAttributeTypeEnum type = classAttribAssignment.getAttributeType();
			switch (type)
			{
				case BOOLEAN:
					result = Localized.getValue(((Boolean) featValue.getValue()));
					break;
				case ENUM:
					final ClassificationAttributeValueModel clavm = (ClassificationAttributeValueModel) featValue.getValue();
					result = (clavm.getName() == null ? clavm.getCode() : clavm.getName());
					break;
				case NUMBER:
					result = YStorefoundation.getRequestContext().getContentManagement().getNumberFormat()
							.format(featValue.getValue());
					break;
				case STRING:
					result = (String) featValue.getValue();
					break;
				case DATE:
					final Locale locale = YStorefoundation.getRequestContext().getSessionContext().getLocale();
					final TimeZone tz = YStorefoundation.getRequestContext().getPlatformServices().getI18NService()
							.getCurrentTimeZone();
					final DateFormat format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
					format.setCalendar(Calendar.getInstance(tz, locale));
					result = format.format((Date) featValue.getValue());
					break;
			}
			final ClassificationAttributeUnitModel unit = classAttribAssignment.getUnit();
			if (unit != null)
			{
				final String unitName = unit.getSymbol() != null ? unit.getSymbol() : unit.getCode();
				result = result + unitName;
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.yfaces.component.product.ProductDetailComponent.FormattedFeatures#getFeatureGroups()
	 */
	@Override
	public List<String> getFeatureGroups()
	{
		return this.featureGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.yfaces.component.product.ProductDetailComponent.FormattedFeatures#getFeatureValues()
	 */
	@Override
	public List<FormattedAttribute> getAttributesList()
	{
		return this.featureList;
	}

}
