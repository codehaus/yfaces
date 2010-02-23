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
package ystorefoundationpackage.yfaces.component.user;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;

import de.hybris.platform.core.model.user.UserModel;



/**
 * This component makes it possible for the user to change the password.
 */
public interface ChangePasswordComponent extends YModel
{

	public UserModel getUser();

	public void setUser(UserModel user);

	public String getOldPassword();

	public void setOldPassword(String currentPassword);

	public String getNewPassword1();

	public void setNewPassword1(String newPW1);

	public String getNewPassword2();

	public void setNewPassword2(String newPW2);

	public Boolean getCheckOldPassword();

	public Boolean getCheckNewPasswordRepeat();

	public void setCheckOldPassword(Boolean check);

	public void setCheckNewPasswordRepeat(Boolean check);

	public YEventHandler<ChangePasswordComponent> getChangePasswordEvent();

	public YEventHandler<ChangePasswordComponent> getCancelChangePasswordEvent();

}
