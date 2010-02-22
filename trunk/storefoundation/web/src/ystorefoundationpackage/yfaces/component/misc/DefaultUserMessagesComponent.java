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

import de.hybris.yfaces.component.AbstractYComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ystorefoundationpackage.domain.UserMessage;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>UserMessagesComponent</code> interface.
 */
public class DefaultUserMessagesComponent extends AbstractYComponent implements UserMessagesComponent
{
	private List<UserMessage> messages = null;
	private transient Map<String, List<UserMessage>> messageMap = null;

	@Override
	public void validate()
	{
		if (this.messages == null)
		{
			this.messages = YStorefoundation.getRequestContext().getSessionContext().getMessages().popMessages();

			final String fatalError = YStorefoundation.getRequestContext().getErrorHandler().getErrorCause();
			if (fatalError != null)
			{
				this.messages.add(new UserMessage(fatalError, fatalError, fatalError));
			}
		}

	}

	@Override
	public void refresh()
	{
		this.messages = YStorefoundation.getRequestContext().getSessionContext().getMessages().popMessages();
	}



	public void clearMessages()
	{
		this.messages = new ArrayList<UserMessage>();
	}

	public List<UserMessage> getMessages()
	{
		return this.messages;
	}

	public Map<String, List<UserMessage>> getMessagesMap()
	{
		if (this.messageMap == null)
		{
			this.messageMap = new HashMap<String, List<UserMessage>>();
			for (final UserMessage msg : this.messages)
			{
				List<UserMessage> messages = messageMap.get(msg.getType());
				if (messages == null)
				{
					messages = new ArrayList<UserMessage>();
					messageMap.put(msg.getType(), messages);
				}
				messages.add(msg);
			}
		}
		return this.messageMap;
	}

	public void setMessages(final List<UserMessage> messages)
	{
		this.messages = messages;
		this.messageMap = null;
	}

}
