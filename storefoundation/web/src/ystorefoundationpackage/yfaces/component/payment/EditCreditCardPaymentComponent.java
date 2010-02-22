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
package ystorefoundationpackage.yfaces.component.payment;

import java.util.List;

import javax.faces.model.SelectItem;


/**
 * This component is specially for the user to edit the credit card information.
 */
public interface EditCreditCardPaymentComponent extends EditPaymentComponent
{
	public List<? extends SelectItem> getAvailableTypes();

	public void setAvailableTypes(List<? extends SelectItem> creditCardTypes);

	public List<? extends SelectItem> getAvailableYears();

	public void setAvailableYears(List<? extends SelectItem> availableYears);

	public List<? extends SelectItem> getAvailableMonths();

	public void setAvailableMonths(List<? extends SelectItem> availableMonths);

}
