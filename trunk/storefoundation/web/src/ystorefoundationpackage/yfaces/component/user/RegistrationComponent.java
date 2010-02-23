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

import de.hybris.platform.core.model.user.AddressModel;

import java.util.List;

import javax.faces.model.SelectItem;

import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentEventHandler;



/**
 * This component makes it possible for the user to register in StoreFoundation.
 */
public interface RegistrationComponent extends YComponent
{
	public String getLogin();

	public void setLogin(String login);

	public String getPassword();

	public void setPassword(String password);

	public String getPasswordRepeat();

	public void setPasswordRepeat(String passwordRepeat);

	public List<? extends SelectItem> getCountries();

	public void setCountries(List<? extends SelectItem> countries);

	public AddressModel getAddress();

	public void setAddress(AddressModel address);

	public Boolean getIsPasswordEnabled();

	public void setIsPasswordEnabled(Boolean isPasswordEnabled);

	public Boolean getIsAddressEnabled();

	public void setIsAddressEnabled(Boolean isAddressEnabled);

	public YComponentEventHandler<RegistrationComponent> getRegisterEvent();
}
