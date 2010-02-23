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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.ProductManagement;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.faces.SfSelectItemGroup;



/**
 * Implementation of the <code>ChooseVariantsComponent</code> interface.
 */
public class DefaultChooseVariantsComponent extends AbstractYModel implements ChooseVariantsComponent
{
	private static final Logger log = Logger.getLogger(DefaultChooseVariantsComponent.class);

	private static final long serialVersionUID = 8736573751347763592L;

	private ProductModel product = null;
	private List<ProductModel> variantProducts = null;
	private ProductModel baseProduct = null;
	private List<SfSelectItemGroup> attributesSelectorList = null;

	private YEventHandler<ChooseVariantsComponent> ehChooseVariants = null;

	//default constructor
	public DefaultChooseVariantsComponent()
	{
		super();
		this.ehChooseVariants = super.createEventHandler(new ShowVariantAction());
	}

	@Override
	public void validate()
	{
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}
		if (this.product != null)
		{
			if (this.baseProduct == null)
			{
				this.baseProduct = ((VariantProductModel) this.product).getBaseProduct();
			}
			if (this.attributesSelectorList == null)
			{
				this.attributesSelectorList = this.createAttributesSelectorList();
			}
			this.variantProducts = new ArrayList<ProductModel>(this.baseProduct.getVariants());
		}
	}

	@Override
	public void refresh()
	{
		this.attributesSelectorList = this.createAttributesSelectorList();
	}

	public YEventHandler<ChooseVariantsComponent> getShowVariantEvent()
	{
		return this.ehChooseVariants;
	}

	/**
	 * This event gets fired when the user tries to see the variant product with the selected attributes.
	 */
	public static class ShowVariantAction extends DefaultYEventListener<ChooseVariantsComponent>
	{

		private static final long serialVersionUID = 8988049388662500863L;

		@Override
		public void actionListener(final YEvent<ChooseVariantsComponent> event)
		{
			final ChooseVariantsComponent cmp = event.getComponent();
			final ProductModel variantProduct = cmp.getCurrentVariantProduct();
			final SfRequestContext sfCtx = YStorefoundation.getRequestContext();
			if (variantProduct != null)
			{
				final String url = sfCtx.getURLFactory().createExternalForm(variantProduct);
				sfCtx.redirect(url);

			}
			else
			{
				sfCtx.getSessionContext().getMessages().pushErrorMessage("variantnotavailable");
			}

		}
	}

	public ProductModel getCurrentVariantProduct()
	{
		final Collection<VariantProductModel> c = this.findMatchingVariants();
		if (c.isEmpty())
		{
			return null;
		}
		else
		{
			return c.iterator().next();
		}

	}

	public ProductModel getProduct()
	{
		return this.product;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}

	public List<ProductModel> getVariantProducts()
	{
		return this.variantProducts;
	}

	public List<SfSelectItemGroup> getAttributesSelectorList()
	{
		return this.attributesSelectorList;
	}


	private List<SfSelectItemGroup> createAttributesSelectorList()
	{
		final ProductManagement pm = YStorefoundation.getRequestContext().getProductManagement();

		// mapping: id/qualifier -> sorted set of available values
		final Map<String, Set<String>> idToValuesMap = new LinkedHashMap<String, Set<String>>();

		//first:
		//retrieve all variants
		final List<VariantProductModel> variants = new ArrayList<VariantProductModel>(this.baseProduct.getVariants());

		//for each variant...
		for (final ProductModel variant : variants)
		{
			//...retrieve variant attributes (localized name -> value)
			final List<FormattedAttribute> values = pm.getVariantAttributeInfoList(variant);

			//...for each variant attribute ...
			for (final FormattedAttribute vp : values)
			{
				//...update ID to Values mapping
				Set _idValuesSet = idToValuesMap.get(vp.getId());
				if (_idValuesSet == null)
				{
					idToValuesMap.put(vp.getId(), _idValuesSet = new TreeSet<String>());
				}
				_idValuesSet.add(vp.getValue());
			}
		}

		//second: 
		//create result
		final List<SfSelectItemGroup> result = new ArrayList<SfSelectItemGroup>();

		//all available variantattributes (baseproduct)
		final List<FormattedAttribute> allAttributes = pm.getVariantAttributeInfoList(this.baseProduct);

		//variantattributes and values for current product
		final List<FormattedAttribute> selectedValues = pm.getVariantAttributeInfoList(this.product);

		//for each possible variant attribute (id)...
		for (int i = 0; i < allAttributes.size(); i++)
		{
			final FormattedAttribute va = allAttributes.get(i);

			//...create a Selector
			final SfSelectItemGroup selector = new SfSelectItemGroup();
			final String label = va.getName();
			selector.setLabel(label != null ? label : "[" + va.getId() + "]");
			selector.setDescription(va.getId());

			//...and fill it with SelectItems (available values)
			final List<SelectItem> selectItems = new ArrayList<SelectItem>();
			final Set<String> values = idToValuesMap.get(va.getId());
			for (final String value : values)
			{
				if (value != null && value.trim().length() != 0)
				{
					selectItems.add(new SelectItem(value));
				}
				else
				{
					log.error("One of the variants of '" + this.product.getCode() + "' has no value for Attribute '" + va.getId()
							+ "'");
				}

			}
			selector.setSelectItems(selectItems);

			final FormattedAttribute selected = selectedValues.get(i);
			selector.setSelectedValue(selected.getValue());
			result.add(selector);
		}

		return result;
	}

	public ProductModel getBaseProduct()
	{
		return this.baseProduct;
	}


	private Collection<VariantProductModel> findMatchingVariants()
	{
		final Map<String, String> selected = new HashMap<String, String>();
		for (final SfSelectItemGroup<String> selectGroup : this.attributesSelectorList)
		{
			final String value = selectGroup.getSelectedValue();
			final String id = selectGroup.getDescription();
			selected.put(id, value);
		}

		final List<VariantProductModel> result = JaloBridge.getInstance().getVariantProductByAttributeValues(baseProduct, selected);
		return result;
	}
}
