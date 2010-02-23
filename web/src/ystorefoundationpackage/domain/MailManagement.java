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

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;



/**
 * 
 */
public interface MailManagement
{
	/**
	 * Tries to find a customer with the given email address and returns the appropriate {@link UserModel}. If no
	 * distinct customer is found, null is returned (so returning null can also mean that there is more than one customer
	 * found for the given email address).
	 * 
	 * @param email
	 *           the email address to search for a customer
	 * @return found customer with the given email, or null if there is no distinct customer with that email address
	 */
	UserModel getCustomerByEmail(final String email);

	boolean sendMail(UserModel user, String mailSubject, EmailNotificationContext renderContext);

	boolean sendMail(UserModel user, String mailSubject, String message);

	RegistrationInfoContext getRegistrationMailContext(UserModel user);

	ChangePasswordContext getChangedPasswordMailContext(UserModel user);

	OrderInfoContext getOrderMailContext(UserModel user, OrderModel orderResult);


}
