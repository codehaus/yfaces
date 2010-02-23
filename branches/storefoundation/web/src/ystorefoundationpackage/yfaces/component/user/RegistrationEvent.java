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

import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.context.YPageContext;

import ystorefoundationpackage.domain.MailManagement;
import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.RegistrationInfoContext;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;



/**
 * This event gets fired when the user tries to register. An Email will be sent to notify the user if the registration
 * is successful.
 */
public class RegistrationEvent extends DefaultYEventListener<RegistrationComponent>
{
	private static final long serialVersionUID = 8712436172843689195L;

	private static final Logger log = Logger.getLogger(RegistrationEvent.class);

	private boolean success = false;

	private transient RegistrationComponent cmp = null;

	@Override
	public void actionListener(final YEvent<RegistrationComponent> event)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		cmp = event.getComponent();
		final SfSessionContext sessCtx = YStorefoundation.getRequestContext().getSessionContext();

		success = false;
		if (!cmp.getPassword().equals(cmp.getPasswordRepeat()))
		{
			sessCtx.getMessages().pushInfoMessage("password_donotmatch");
		}
		else
		{
			//create Customer
			final PlatformServices ps = reqCtx.getPlatformServices();
			UserModel curUser = null;
			try
			{
				curUser = ps.getUserService().getUser(cmp.getLogin());
			}
			catch (final UnknownIdentifierException e)
			{
				// silent catch
			}

			success = curUser == null;
			if (success)
			{
				final CustomerModel customer = new CustomerModel();
				final AddressModel adr = cmp.getAddress();

				customer.setUid(cmp.getLogin());

				//customer.setPassword(cmp.getPassword());
				customer.setName(adr.getFirstname() + " " + adr.getLastname());
				customer.setSessionLanguage(sessCtx.getLanguage());
				ps.getModelService().save(customer);


				// create Address
				reqCtx.getPlatformServices().getModelService().save(adr);

				customer.setAddresses(Collections.singletonList(adr));
				customer.setDefaultShipmentAddress(adr);
				customer.setDefaultPaymentAddress(adr);

				ps.getModelService().save(customer);
				ps.getUserService().setPassword(customer.getUid(), cmp.getPassword());
				// add customer to 'customergroup'
				addUserToCustomerGroup(customer);

				sessCtx.setUser(customer);

				final MailManagement mailTemplates = reqCtx.getMailManagement();

				final RegistrationInfoContext regCtx = mailTemplates.getRegistrationMailContext(customer);
				final String subject = reqCtx.getContentManagement().getLocalizedMessage("components.registrationCmp.emailSubject");
				final boolean send = mailTemplates.sendMail(customer, subject, regCtx);
				if (send)
				{
					log.info("information email sent for user " + sessCtx.getUser().getName() + " to "
							+ sessCtx.getUser().getDefaultShipmentAddress().getEmail());
				}
				else
				{
					log.error("Error sending registration mail");
					sessCtx.getMessages().pushInfoMessage("Error sending registration mail");
				}
			}
			if (!success)
			{
				sessCtx.getMessages().pushInfoMessage("frames.loginFrame.loginInvalid");
			}
		}
	}

	/**
	 * Adds the given customer to the customer group.
	 * 
	 * @param customer
	 *           the customer to be added to the customer group.
	 */
	private void addUserToCustomerGroup(final CustomerModel customer)
	{
		final PlatformServices ps = YStorefoundation.getRequestContext().getPlatformServices();
		final PrincipalGroupModel userGroup = ps.getUserService().getUserGroup(Constants.USER.CUSTOMER_USERGROUP);
		final Set<PrincipalModel> members = new HashSet<PrincipalModel>(userGroup.getMembers());
		members.add(customer);
		ps.getModelService().save(userGroup);
	}

	@Override
	public String action()
	{
		if (success)
		{
			final YPageContext page = cmp.getFrame().getPage().getPreviousPage();
			if (page != null)
			{
				YStorefoundation.getRequestContext().redirect(page, true);
			}
		}
		else
		{
			YStorefoundation.getRequestContext().redirect(true);
		}

		return null;
	}

}
