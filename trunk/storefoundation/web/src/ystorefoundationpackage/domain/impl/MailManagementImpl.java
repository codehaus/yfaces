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
package ystorefoundationpackage.domain.impl;

import de.hybris.platform.commons.jalo.renderer.RendererTemplate;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.mail.MailUtils;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.ChangePasswordContext;
import ystorefoundationpackage.domain.EmailNotificationContext;
import ystorefoundationpackage.domain.MailManagement;
import ystorefoundationpackage.domain.OrderInfoContext;
import ystorefoundationpackage.domain.RegistrationInfoContext;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.order.OrderTableComponentFormatter;


/**
 * 
 */
public class MailManagementImpl extends AbstractDomainService implements MailManagement
{
	private static final Logger log = Logger.getLogger(MailManagementImpl.class);

	private static final String CHANGE_PASSWORD_TEMPLATE = "sfChangePassword";
	private static final String ORDERINFO_TEMPLATE = "sfOrderInfo";
	private static final String REGISTRATION_TEMPLATE = "sfRegistrationInfo";

	public boolean sendMail(final UserModel user, final String mailSubject, final EmailNotificationContext renderContext)
	{
		final List<RendererTemplateModel> templates = getRendererTemplates(renderContext.getRendererTemplate());
		if (!templates.isEmpty() && templates.size() > 1)
		{
			log.warn("more than one template found for [" + renderContext.getRendererTemplate() + "]");
		}
		final RendererTemplateModel template = templates.get(0);

		final boolean result = false;
		if (template != null)
		{
			final StringWriter mailMessage = new StringWriter();
			JaloBridge.getInstance().render(template, renderContext, mailMessage);
			this.sendMail(user, mailSubject, mailMessage.toString());
		}
		else
		{
			log.error("Error sending mail '" + mailSubject + "' to " + user.getDefaultShipmentAddress().getEmail());
			log.error("No email template '" + renderContext.getRendererTemplate() + "' available");
		}
		return result;
	}

	public boolean sendMail(final UserModel user, final String mailSubject, final String message)
	{
		try
		{
			final HtmlEmail email = (HtmlEmail) MailUtils.getPreConfiguredEmail();
			email.setCharset("utf-8");
			final String mailAdr = user.getDefaultShipmentAddress().getEmail();
			MailUtils.validateEmailAddress(mailAdr, "TO");

			email.addTo(mailAdr, user.getName());
			email.setSubject(mailSubject);
			email.setHtmlMsg(message);

			email.send();
		}
		catch (final EmailException e)
		{
			log.error("Error sending mail '" + mailSubject + "' to " + user.getDefaultShipmentAddress().getEmail());
			return false;
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return true;
	}

	//send an email to inform the user that he registered in store-foundation
	public RegistrationInfoContext getRegistrationMailContext(final UserModel user)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final RegistrationInfoContextImpl result = new RegistrationInfoContextImpl();
		result.setLanguage(reqCtx.getSessionContext().getLanguage());
		result.setUserName(user.getName());
		result.setLoginName(user.getUid());
		result.setStoreName(reqCtx.getContentManagement().getLocalizedMessage("global.word.storeName"));
		result.setEmailAddress(user.getDefaultShipmentAddress().getEmail());
		result.setRendererTemplate(REGISTRATION_TEMPLATE);
		return result;
	}

	//send email to inform the user
	public ChangePasswordContext getChangedPasswordMailContext(final UserModel user)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final ChangePasswordContextImpl ctx = new ChangePasswordContextImpl();
		ctx.setLanguage(reqCtx.getSessionContext().getLanguage());
		ctx.setUserName(user.getName());
		ctx.setStoreName(reqCtx.getContentManagement().getLocalizedMessage("global.word.storeName"));
		ctx.setRendererTemplate(CHANGE_PASSWORD_TEMPLATE);
		return ctx;
	}



	public OrderInfoContext getOrderMailContext(final UserModel user, final OrderModel order)
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final OrderTableComponentFormatter formatter = new OrderTableComponentFormatter();
		final OrderInfoContextImpl ctx = new OrderInfoContextImpl(order);
		ctx.setLanguage(reqCtx.getSessionContext().getLanguage());
		ctx.setUserName(user.getName());
		ctx.setStoreName(reqCtx.getContentManagement().getLocalizedMessage("global.word.storeName"));
		ctx.setSubtotalAmount(formatter.getFormattedSubTotal(order));
		ctx.setDeliveryCost(formatter.getFormattedDelivery(order));
		ctx.setPaymentCost(formatter.getFormattedPaymentTotal(order));
		ctx.setTaxAmount(StringUtils.join(formatter.getFormattedTaxes(order), " "));
		ctx.setDiscountInfo(StringUtils.join(formatter.getFormattedDiscounts(order), " "));
		ctx.setTotalAmount(formatter.getFormattedTotal(order));
		ctx.setRendererTemplate(ORDERINFO_TEMPLATE);

		//currency does not need to be set any longer
		ctx.setCurrency(reqCtx.getSessionContext().getCurrency().getIsocode().toUpperCase());

		return ctx;
	}

	@Override
	public UserModel getCustomerByEmail(final String email)
	{
		final String query = "select distinct {c:pk} from {Customer as c join Address as a on {a:owner}={c:pk}} where  {a:email} like ?email ";
		final Map queryParams = Collections.singletonMap("email", email);

		final FlexibleSearchService flexService = getPlatformServices().getFlexibleSearchService();
		final SearchResult<UserModel> searchResult = flexService.search(query, queryParams);

		UserModel result = null;
		if (searchResult.getCount() == 1)
		{
			result = searchResult.getResult().iterator().next();
		}
		return result;
	}


	private List<RendererTemplateModel> getRendererTemplates(final String code)
	{
		final FlexibleSearchService s = getPlatformServices().getFlexibleSearchService();
		final String type = getPlatformServices().getTypeService().getComposedType(RendererTemplateModel.class).getCode();
		final String query = "SELECT {" + Item.PK + "} " + "FROM {" + type + "} " + "WHERE {" + RendererTemplate.CODE
				+ "} = ?attrVal " + "ORDER BY {" + Item.PK + "} ASC"; // order by PK to guarantee stable fetch order
		final Map param = Collections.singletonMap("attrVal", code);
		final SearchResult<RendererTemplateModel> searchResult = s.search(query, param);

		return searchResult.getResult();
	}



}
