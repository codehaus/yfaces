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
package ystorefoundationpackage.datatable;

public class DataTableException extends RuntimeException
{
	public DataTableException(final Throwable cause)
	{
		super(cause);
	}

	public DataTableException(final String message, final Throwable cause)
	{
		super(message, cause);
	}

	public DataTableException(final String message)
	{
		super(message);
	}

}
