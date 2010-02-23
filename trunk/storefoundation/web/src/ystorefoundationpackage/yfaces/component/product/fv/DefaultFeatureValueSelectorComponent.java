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

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.mutable.MutableInt;
import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.Localized;
import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.ProductManagement.ProductFeatures;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.faces.SfSelectItem;
import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * Implementation of the <code>FeatureValueSelectorComponent</code> interface.
 */
public class DefaultFeatureValueSelectorComponent extends AbstractYComponent implements FeatureValueSelectorComponent
{
	private static final Logger log = Logger.getLogger(DefaultFeatureValueSelectorComponent.class);

	private Collection<ClassAttributeAssignmentModel> attributes = null;

	private CategoryModel category = null;
	private boolean funnelMode = true;
	private List<ProductModel> productList = null;
	private List<ProductModel> filteredProductList = null;
	private Integer selectorCount = null;
	private Boolean isIgnoreClassificationClass = null;

	private transient List<SfSelectItemGroup> selectorList = null;

	private YComponentEventHandler<FeatureValueSelectorComponent> ehSubmit = null;
	private YComponentEventHandler<FeatureValueSelectorComponent> ehReset = null;

	/**
	 * Constructor.
	 */
	public DefaultFeatureValueSelectorComponent()
	{
		super();
		this.ehSubmit = super.createEventHandler(new SubmitEvent());
		this.ehReset = super.createEventHandler(new ResetEvent());

	}

	@SuppressWarnings("boxing")
	@Override
	public void validate()
	{
		if (getProductList() == null)
		{
			log.debug("No productlist specified; creating default one");
			setProductList(getDefaultProductList());
		}

		if (getIgnoreClassificationClass() == null)
		{
			log.debug("No ignoreClassificationClass specified; creating default value");
			setIgnoreClassificationClass(Boolean.FALSE);
		}

		if (getClassAttributeAssignments() == null)
		{
			log.debug("No assignments specified; creating default one");
			setClassAttributeAssignments(getSearchableCategoryAssignments());
		}

		if (getFeatureValuesSelectorCount() == null)
		{
			log.debug("No selectorCount specified; creating default one");
			setFeatureValuesSelectorCount(Integer.valueOf(getClassAttributeAssignments().size()));
		}

		if (getFilteredProductList() == null)
		{
			this.filteredProductList = getProductList();
		}

	}

	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		//reset selectors after language change
		if (session.isLanguageChanged())
		{
			this.selectorList = null;
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.selector.featureselector.FeatureSelectorModel#getSelectorList()
	 */
	public List<SfSelectItemGroup> getSelectorList()
	{
		if (this.selectorList == null)
		{
			this.selectorList = getSelectorListInternal();
		}
		return this.selectorList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.yfaces.component.product.fv.FeatureValueSelectorComponent#setSelectorList(java.util.List)
	 */
	//	@Override
	//	public void setSelectorList(final List<SfSelectItemGroup> selectorList)
	//	{
	//		this.selectorList = selectorList;
	//	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.dataprovider.ProductDataProvider#getProductList()
	 */
	public List<ProductModel> getProductList()
	{
		return this.productList;
	}

	public void setProductList(final List<ProductModel> productList)
	{
		this.productList = productList;
		this.selectorList = null;
	}


	public List<ProductModel> getFilteredProductList()
	{
		return this.filteredProductList;
	}


	public void setFilteredProductList(final List<ProductModel> productList)
	{
		this.filteredProductList = productList;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.selector.featureselector.FeatureSelectorModel#isFunnelMode()
	 */
	public boolean getFunnelMode() // NOPMD
	{
		return funnelMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.selector.featureselector.FeatureSelectorModel#setFunnelMode(boolean)
	 */
	public void setFunnelMode(final boolean funnelMode)
	{
		this.funnelMode = funnelMode;
	}


	public Boolean getIgnoreClassificationClass()
	{
		return this.isIgnoreClassificationClass;
	}

	public void setIgnoreClassificationClass(final Boolean ignore)
	{
		this.isIgnoreClassificationClass = ignore;
	}


	public Collection<ClassAttributeAssignmentModel> getClassAttributeAssignments()
	{
		return this.attributes;
	}

	public void setClassAttributeAssignments(final Collection<ClassAttributeAssignmentModel> attribs)
	{
		this.attributes = attribs;
	}



	public Integer getFeatureValuesSelectorCount()
	{
		return this.selectorCount;
	}


	public void setFeatureValuesSelectorCount(final Integer count)
	{
		this.selectorCount = count;
	}




	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.selector.featureselector.FeatureSelectorControl#getSubmitEvent()
	 */
	public YComponentEventHandler<FeatureValueSelectorComponent> getSubmitEvent()
	{
		return this.ehSubmit;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.selector.featureselector.FeatureSelectorControl#getResetEvent()
	 */
	public YComponentEventHandler<FeatureValueSelectorComponent> getResetEvent()
	{
		return this.ehReset;
	}


	/**
	 * Defaultvalue for productlist. This is a List of all Products of the current Category.
	 */
	private List<ProductModel> getDefaultProductList()
	{
		final CategoryModel catg = getCategoryInternal();

		final List<ProductModel> result = YStorefoundation.getRequestContext().getProductManagement().findAllByCategory(catg, true,
				"name", true);

		return result;
	}

	/**
	 * Defaultvalue for available assignments.
	 * 
	 * @return All assignments for thze current category
	 */
	private Collection<ClassAttributeAssignmentModel> getSearchableCategoryAssignments()
	{
		final CategoryModel catg = getCategoryInternal();
		final Collection<ClassAttributeAssignmentModel> result = JaloBridge.getInstance().findAllAssignmentsByCategory(catg,
				JaloBridge.CLASSIFICATION_ATTR_SEARCHABLE);

		return result;
	}

	private CategoryModel getCategoryInternal()
	{
		CategoryModel result = getCategory() != null ? getCategory() : YStorefoundation.getRequestContext().getSessionContext()
				.getCategory();
		if (result == null)
		{
			result = YStorefoundation.getRequestContext().getDefaultValues().getDefaultCategory();
		}
		return result;
	}

	/**
	 * Internal. Creates the final selectorlist.
	 * 
	 * @return List
	 */
	private List<SfSelectItemGroup> getSelectorListInternal()
	{
		final List<SfSelectItemGroup> result = new ArrayList();
		final List<ClassificationAttributeModel> attributes = new ArrayList<ClassificationAttributeModel>();

		int selectorGroups = getFeatureValuesSelectorCount().byteValue();
		final Iterator<ClassAttributeAssignmentModel> iter = getClassAttributeAssignments().iterator();
		while (iter.hasNext() && selectorGroups-- > 0)
		{
			attributes.add(iter.next().getClassificationAttribute());
		}

		// prepare data collection
		final Map<String, Map<String, MutableInt>> map = new LinkedHashMap<String, Map<String, MutableInt>>();
		final Map<String, String> groupNameMap = new HashMap<String, String>();
		for (final ClassificationAttributeModel attribute : attributes)
		{
			map.put(attribute.getCode(), new TreeMap());
			final String name = attribute.getName() != null ? attribute.getName() : attribute.getCode();
			groupNameMap.put(attribute.getCode(), name);
		}

		// TODO: break after 100 processed products due to performance reasons

		// start data collection
		final List<ProductModel> products = getProductList();
		for (final ProductModel product : products)
		{
			final ProductFeatures features = YStorefoundation.getRequestContext().getProductManagement().getFeatures(product);
			for (final ClassificationAttributeModel attribute : attributes)
			{
				final Map<String, MutableInt> m = map.get(attribute.getCode());
				final FormattedAttribute fAttrib = features.getAttributesMap().get(attribute.getCode());
				if (fAttrib != null)
				{
					final String attrValue = features.getAttributesMap().get(attribute.getCode()).getValue();
					MutableInt mInt = m.get(attrValue);
					if (mInt == null)
					{
						m.put(attrValue, mInt = new MutableInt());
					}
					mInt.increment();
				}
			}
		}

		for (final Map.Entry<String, Map<String, MutableInt>> entry : map.entrySet())
		{
			final Map<String, MutableInt> selectorValues = entry.getValue();

			final List<SelectItem> selectItemList = new ArrayList<SelectItem>();
			int maxOccurence = 0;
			for (final Map.Entry<String, MutableInt> selectorValue : selectorValues.entrySet())
			{
				maxOccurence = Math.max(maxOccurence, selectorValue.getValue().intValue());
				final String label = selectorValue.getKey() + " (" + selectorValue.getValue() + ")";
				final String value = selectorValue.getKey();
				final SelectItem si = new SfSelectItem(value, label);
				selectItemList.add(si);
			}

			boolean disableGroup = selectItemList.isEmpty();
			if (selectItemList.size() == 1 && products.size() == maxOccurence)
			{
				disableGroup = true;
			}
			else
			{
				//add a 'Please Choose' selectitem when necessary
				final SfSelectItem selectItem = new SfSelectItem("null", Localized.WORD_CHOOSE.value());
				selectItemList.add(0, selectItem);
			}

			// TODO: label mustn't be qualifier
			final SfSelectItemGroup group = new SfSelectItemGroup();
			group.setSelectItems(selectItemList);
			group.setLabel(groupNameMap.get(entry.getKey()));
			group.setDescription(entry.getKey());
			group.setDisabled(disableGroup);

			result.add(group);
		}

		return result;
	}


	public CategoryModel getCategory()
	{
		return category;
	}

	public void setCategory(final CategoryModel category)
	{
		this.category = category;
	}


}
