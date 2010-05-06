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

import org.codehaus.yfaces.component.AbstractYComponentContainer;

import ystorefoundationpackage.yfaces.component.product.tellfriend.TellAFriendComponent;

/**
 * Renders the page for the user to recommend a product to the friends with
 * email.
 * 
 */
public class TellAFriendFrame extends AbstractYComponentContainer {

	private static final long serialVersionUID = 5064312503612109254L;

	private TellAFriendComponent tellFriendCmp = null;

	public TellAFriendFrame() {
		super();
	}

	public TellAFriendComponent getTellAFriendComponent() {
		return this.tellFriendCmp;
	}

	public void setTellAFriendComponent(TellAFriendComponent cmp) {
		this.tellFriendCmp = cmp;
	}

}