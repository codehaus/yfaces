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

import javax.faces.model.SelectItem;


/**
 * Minor improvements of standard {@link SelectItem}.<br/>
 * - implements {@link Comparable} by label <br/>
 * - performance issues (suppress value.toString() when no label is available)<br/>
 * 
 * 
 */
public class SfSelectItem<T> extends SelectItem implements Comparable<SfSelectItem>
{
	/**
	 * Constructor. Uses the passed value and creates an anonymous label.
	 * 
	 * @param value
	 *           selectable value
	 */
	public SfSelectItem(final T value)
	{
		this(value, null);
	}

	/**
	 * Constructor. Simply uses passed value and label for selection.
	 * 
	 * @param value
	 *           Value
	 * @param label
	 *           Label
	 */
	public SfSelectItem(final T value, final String label)
	{
		super();
		if (value != null)
		{
			super.setValue(value);
		}

		//default SelectItem would do a value.toString() 
		super.setLabel(label != null ? label : "???");
	}


	@Override
	public T getValue()
	{
		return (T) super.getValue();
	}


	/**
	 * Implements {@link Comparable}; uses label for comparison.
	 * 
	 * @param selectItem
	 *           {@link SelectItem} which shall be compared
	 * @return compare result
	 */
	public int compareTo(final SfSelectItem selectItem)
	{
		return this.getLabel().compareTo(selectItem.getLabel());
	}


	@Override
	public String toString()
	{
		return "{" + getValue() + "->" + getLabel() + "}";
	}




}
