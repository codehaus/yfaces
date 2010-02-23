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
package ystorefoundationpackage.yfaces.component.misc;


import java.util.List;
import java.util.Map;

import org.codehaus.yfaces.component.YModel;

import ystorefoundationpackage.domain.UserMessage;



/**
 * This component displays some messages to inform the user what has happened and/or what can be done.
 */
public interface UserMessagesComponent extends YModel
{
	//
	// Standard JSF FacesMessage:
	// - is not redirectable (gets lost)
	// - always escapes (no links possible)

	Map<String, List<UserMessage>> getMessagesMap();

	List<UserMessage> getMessages();

	void setMessages(List<UserMessage> messages);

	void clearMessages();

}
