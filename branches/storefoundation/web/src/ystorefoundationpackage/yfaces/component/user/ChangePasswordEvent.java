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


import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;

import ystorefoundationpackage.domain.ChangePasswordContext;
import ystorefoundationpackage.domain.MailManagement;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;


/**
 * This event gets fired when the user tries to change the password. An email will also be sent to notify the user if
 * the password is changed successfully.
 */
public class ChangePasswordEvent extends DefaultYEventListener<ChangePasswordComponent>
{

	private static final Logger log = Logger.getLogger(ChangePasswordEvent.class);

	private static final String LOC_PW_NOT_SAME = "user.changePasswordCmp.pwUnequal";
	private static final String LOC_PW_CHANGE = "components.changePasswordCmp.emailSubject";

	private static enum ERROR_CODE
	{
		WRONG_PASSWORD, WRONG_PASSWORD_REPETITION;
	}

	private ERROR_CODE error = null;

	@Override
	public String action()
	{
		//XXX: remove this as fast as possible
		final String result = (this.error == null) ? "userDetailPage" : null;
		return result;
	}


	@Override
	public void actionListener(final YEvent<ChangePasswordComponent> event)
	{
		this.error = null;
		final ChangePasswordComponent cmp = event.getComponent();
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		//check current password
		if (cmp.getCheckOldPassword().booleanValue()
				&& !JaloBridge.getInstance().checkPassword(cmp.getUser(), cmp.getOldPassword()))
		{
			this.error = ERROR_CODE.WRONG_PASSWORD;
			return;
		}

		//check new password repetition
		if (cmp.getCheckNewPasswordRepeat().booleanValue() && !cmp.getNewPassword1().equals(cmp.getNewPassword2()))
		{
			reqCtx.getSessionContext().getMessages().pushErrorMessage(LOC_PW_NOT_SAME);
			this.error = ERROR_CODE.WRONG_PASSWORD_REPETITION;
		}

		if (error == null)
		{
			reqCtx.getPlatformServices().getUserService().setPassword(cmp.getUser().getUid(), cmp.getNewPassword1());
			reqCtx.getPlatformServices().getModelService().save(cmp.getUser());

			final MailManagement mt = reqCtx.getMailManagement();
			final ChangePasswordContext changePwCtx = mt.getChangedPasswordMailContext(cmp.getUser());
			final String mailSubject = reqCtx.getContentManagement().getLocalizedMessage(LOC_PW_CHANGE);

			final boolean send = mt.sendMail(cmp.getUser(), mailSubject, changePwCtx);
			if (send)
			{
				log.info("Successfully send email '" + mailSubject + "' to " + cmp.getUser().getDefaultShipmentAddress().getEmail());
			}
			else
			{
				log.info("Error sending email '" + mailSubject + "' to " + cmp.getUser().getDefaultShipmentAddress().getEmail());
			}
		}

	}

}
