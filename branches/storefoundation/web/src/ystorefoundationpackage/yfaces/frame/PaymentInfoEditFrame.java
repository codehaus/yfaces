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
package ystorefoundationpackage.yfaces.frame;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;

import ystorefoundationpackage.yfaces.component.payment.EditPaymentComponent;

/**
 * Renders the page for the user to edit the selected payment information.
 * 
 */
public class PaymentInfoEditFrame extends AbstractYFrame {
	private static final long serialVersionUID = 4318010538297503458L;

	private EditPaymentComponent editPaymentCmp = null;

	@SuppressWarnings("unused")
	private static final Logger log = Logger
			.getLogger(PaymentInfoEditFrame.class);

	public EditPaymentComponent getEditPaymentComponent() {
		return this.editPaymentCmp;
	}

	public void setEditPaymentComponent(EditPaymentComponent cmp) {
		this.editPaymentCmp = cmp;
	}

}
