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
package ystorefoundationpackage.faces;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.log4j.Logger;


/**
 * Extends standard {@link SelectItemGroup} with some additionally functionality. These things are: - provide a
 * (pre)selected value - manage (autodetect) a converter (according the selected valueclass) - manage a
 * {@link Validator}
 * 
 * 
 */

//TODO: this class should implement list 
public class SfSelectItemGroup<V> extends SelectItemGroup
{
	public static class DummyValidator implements Validator
	{
		public void validate(final FacesContext context, final UIComponent component, final Object value) throws ValidatorException
		{
			// DOCTODO Document reason, why this block is empty
		}
	}

	private static DummyValidator DEFAULT_VALIDATOR = new DummyValidator();
	private static final Logger log = Logger.getLogger(SfSelectItemGroup.class);


	private List<SelectItem> selectItemsList = new ArrayList();
	private List<V> selectedValuesList = new ArrayList();

	private Converter converter = null;

	private Class valueClass = null;


	public SfSelectItemGroup()
	{
		super();
	}

	public SfSelectItemGroup(final Class<V> valueClass)
	{
		super();
		this.valueClass = valueClass;
	}


	/**
	 * Adds a SelectItem to this group.
	 * 
	 * @param si
	 *           SelectItem
	 */
	public void addSelectItem(final SelectItem si)
	{
		this.selectItemsList.add(si);
	}

	/**
	 * Returns available SelectItems.
	 */
	@Override
	public SelectItem[] getSelectItems()
	{
		return this.selectItemsList.toArray(new SelectItem[0]);
	}

	/**
	 * Sets available SelectItems.
	 */
	@Override
	public void setSelectItems(final SelectItem[] selectItems)
	{
		this.setSelectItems(Arrays.asList(selectItems));
	}

	/**
	 * Sets available SelectItems.
	 * 
	 * @param selectItems
	 */
	public void setSelectItems(final Collection<? extends SelectItem> selectItems)
	{
		this.selectItemsList = new ArrayList(selectItems);
	}


	/**
	 * Returns the Value of the selected SelectedItem. This getter/setter can be used by any SelectOne component.
	 */
	public V getSelectedValue()
	{
		return this.getSelectedValues()[0];
	}

	/**
	 * Returns the Values of the selected SelectItems. Be careful: you get only the values, not the SelectItems itself.
	 * This circumstance considers the JSF behaviour of the 'selected' Attribute which asks for the values and not the
	 * SelectItems.
	 * 
	 * This getter/setter can be used with any SelectMany component.
	 */
	public V[] getSelectedValues()
	{
		//per default set the first value as preselected
		if (this.selectedValuesList.isEmpty())
		{
			this.selectedValuesList.add((V) this.getSelectItems()[0].getValue());
		}

		//we want an array of type V;
		//this allows myfaces to detect the right converter
		//		V[] resultArrayClass = (V[])Array.newInstance(this.selectedValuesList.get(0).getClass(), 0);

		final V[] resultArrayClass = (V[]) Array.newInstance(getValueClass(), 0);

		return this.selectedValuesList.toArray(resultArrayClass);
	}

	/**
	 * Sets the selected SelectItem.<br/>
	 * Be careful: you must specify the value of the SelectItem, not the SelectItem itself. This getter/setter can be
	 * used by any SelectOne component.
	 * 
	 * @param selectedValue
	 */
	public void setSelectedValue(final V selectedValue)
	{
		//V[] resultArray = (V[])Array.newInstance(selectedValue.getClass(), 1);
		final V[] resultArray = (V[]) Array.newInstance(getValueClass(), 1);
		resultArray[0] = selectedValue;
		setSelectedValues(resultArray);
	}


	/**
	 * Sets the selected SelectItems<br/>
	 * . Be careful: you must specify the values of the SelectItems, not the SelectItems itself. This getter/setter can
	 * be used with any SelectMany component.
	 */
	public void setSelectedValues(final V[] selectedValues)
	{
		this.selectedValuesList = Arrays.asList(selectedValues);
		if (log.isDebugEnabled())
		{
			log.debug("Set selected Items for group:" + this.getLabel() + " selected values: " + Arrays.asList(selectedValues));
		}
	}


	/**
	 * Returns a default Converter which is detected according the class of the first selected value.
	 */
	public Converter getConverter()
	{
		Converter result = this.converter;
		if (converter == null)
		{
			//iterate over all values
			for (final SelectItem si : getSelectItems())
			{
				//until first non-null value is found
				if (si.getValue() != null)
				{
					//..whose class is used for converter creation
					result = FacesContext.getCurrentInstance().getApplication().createConverter(si.getValue().getClass());
				}
			}
		}
		return result;
	}

	/**
	 * Returns an appropriate JSF {@link Converter} for this Selector.
	 */
	public void setConverter(final Converter converter)
	{
		this.converter = converter;
	}

	/**
	 * Returns an appropriate JSF {@link Validator} for this Selector.
	 */
	public Validator getValidator()
	{
		return DEFAULT_VALIDATOR;
	}


	/**
	 * Internal. Detects the class of selectitemvalues.
	 * 
	 * @return Class
	 */
	private Class getValueClass()
	{
		if (this.valueClass == null)
		{
			//take objectclass when only null-values are available
			this.valueClass = Object.class;

			//otherwise take class of first non-null value
			for (final SelectItem selectItem : this.selectItemsList)
			{
				if (selectItem.getValue() != null)
				{
					valueClass = selectItem.getValue().getClass();
					break;
				}
			}
		}
		return this.valueClass;

	}


}
