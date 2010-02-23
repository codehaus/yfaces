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
package ystorefoundationpackage.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 */
public class UserMessages
{
	public static final String INFO_MESSAGE = "INFO_MESSAGE";
	public static final String ERROR_MESSAGE = "ERROR_MESSAGE";

	private List<UserMessage> messagesList = null;

	public UserMessages()
	{
		this.messagesList = new ArrayList<UserMessage>();
	}

	public UserMessage pushInfoMessage(final String summary)
	{
		return this.pushInfoMessage(summary, (Object[]) null);
	}

	public UserMessage pushInfoMessage(final String summary, final Object... params)
	{
		final UserMessage result = new UserMessage(INFO_MESSAGE);
		result.setSummary(summary, params);
		this.pushMessage(result);
		return result;
	}

	public UserMessage pushUnlocalizedInfoMessage(final String message)
	{
		final UserMessage result = new UserMessage(INFO_MESSAGE);
		result.setUnlocalizedSummary(message);
		this.pushMessage(result);
		return result;
	}

	public UserMessage pushErrorMessage(final String summary)
	{
		return this.pushErrorMessage(summary, (Object[]) null);
	}

	public UserMessage pushErrorMessage(final String summary, final Object... params)
	{
		final UserMessage result = new UserMessage(ERROR_MESSAGE);
		result.setSummary(summary, params);
		this.pushMessage(result);
		return result;
	}

	public UserMessage pushUnlocalizedErrorMessage(final String message)
	{
		final UserMessage result = new UserMessage(ERROR_MESSAGE);
		result.setUnlocalizedSummary(message);
		this.pushMessage(result);
		return result;
	}


	public void pushMessage(final UserMessage userMessage)
	{
		this.messagesList.add(userMessage);
	}


	public List<UserMessage> popInfoMessages()
	{
		return popMessages(INFO_MESSAGE);
	}

	public List<UserMessage> popErrorMessages()
	{
		return popMessages(ERROR_MESSAGE);
	}

	public List<UserMessage> popMessages(final String type)
	{
		final List<UserMessage> result = new ArrayList<UserMessage>();
		for (final Iterator<UserMessage> iter = this.messagesList.iterator(); iter.hasNext();)
		{
			final UserMessage msg = iter.next();
			if (msg.getType().equals(type))
			{
				result.add(msg);
				iter.remove();
			}
		}
		return result;
	}

	public List<UserMessage> popMessages()
	{
		final List<UserMessage> result = new ArrayList<UserMessage>(this.messagesList);
		this.messagesList.clear();
		return result;
	}



}
