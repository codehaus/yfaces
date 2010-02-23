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
package demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.event.ActionEvent;


/**
 * Rendertime vs. Compiletime.
 * 
 * 
 * 
 */
public class PitfallsBean2
{
	private List<String> rows = null;
	private int count = 0;

	private final String[] localizedRow =
	{ "row", "reihe" };
	private int langFlag = 0;

	public PitfallsBean2()
	{
		this.rows = expensiveRowCreationOperation();
	}

	public List<String> getRows()
	{
		return this.rows;
	}

	public void addElement(final ActionEvent e)
	{
		this.rows.add(localizedRow[langFlag] + count++);
	}

	public void reset(final ActionEvent e)
	{
		this.rows = expensiveRowCreationOperation();
		this.count = 0;
	}

	public void update(final ActionEvent e)
	{
		for (int i = 0; i < this.rows.size(); i++)
		{
			this.rows.set(i, this.rows.get(i).replace(localizedRow[langFlag], localizedRow[langFlag ^ 1]));
		}
	}



	/**
	 * JSF ActionListener: Switches the Language Note: Only for demo purposes this is done within this component.
	 * 
	 * @param e
	 */
	public void switchLanguage(final ActionEvent e)
	{
		this.langFlag = this.langFlag ^ 1;
	}


	private List<String> expensiveRowCreationOperation()
	{
		final String row = localizedRow[langFlag];
		return new ArrayList(Arrays.asList(row + count++, row + count++, row + count++));
	}

}
