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
package ystorefoundationpackage.yfaces.component.product.tellfriend;

import de.hybris.platform.commons.jalo.CommonsManager;
import de.hybris.platform.commons.jalo.renderer.RendererTemplate;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.util.mail.MailUtils;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.URLFactory;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.frame.TellAFriendFrame;


public class DefaultTellAFriendComponent extends AbstractYComponent implements TellAFriendComponent
{
	private static final long serialVersionUID = 3542659820552240335L;

	private static final Logger log = Logger.getLogger(DefaultTellAFriendComponent.class);

	private ProductModel product = null;
	private UserModel user = null;

	private String emailAddress = null;
	private String comment = null;

	private YComponentEventHandler<TellAFriendComponent> ehSendMail = null;

	/**
	 * default constructor
	 */
	public DefaultTellAFriendComponent()
	{
		super();
		this.ehSendMail = super.createEventHandler(new SendEmailAction());
	}

	@Override
	public void validate()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
		final SfSessionContext sessCtx = reqCtx.getSessionContext();

		if (this.product == null)
		{

			this.product = sessCtx.getProduct();
		}

		if (this.user == null)
		{
			this.user = sessCtx.getUser();
		}
		if (this.comment == null)
		{
			this.comment = reqCtx.getContentManagement().getLocalizedMessage("components.tellAFriendCmp.commentContent");
		}
	}

	public YComponentEventHandler<TellAFriendComponent> getSendEmailEvent()
	{
		return this.ehSendMail;
	}

	public static class SendEmailAction extends DefaultYComponentEventListener<TellAFriendComponent>
	{

		private static final long serialVersionUID = -3294253669480502365L;
		private transient TellAFriendComponent cmp = null;
		private boolean emailSent = false;

		@Override
		public void actionListener(final YComponentEvent<TellAFriendComponent> event)
		{
			cmp = event.getComponent();

			final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

			final TellAFriendFrame tellAFriendFrame = (TellAFriendFrame) cmp.getFrame();
			tellAFriendFrame.getTellAFriendComponent().getValue().setEmailAddress(cmp.getEmailAddress());
			tellAFriendFrame.getTellAFriendComponent().getValue().setComment(cmp.getComment());

			try
			{
				if (!sendHtmlEmail())
				{
					return;
				}
				emailSent = true;
				userSession.getMessages().pushInfoMessage("components.tellAFriendCmp.email_sent");
				log.info("tellAfriend email sent to " + cmp.getEmailAddress());
			}
			catch (final EmailException e)
			{
				userSession.getMessages().pushInfoMessage(e.getMessage());
				log.error("Error sending mail", e);
			}
		}

		@Override
		public String action()
		{
			if (emailSent)
			{
				return "productDetailPageRedirect";
			}
			else
			{
				return "tellAFriendPage";
			}
		}

		private boolean sendHtmlEmail() throws EmailException
		{
			final String toEmailAddress = cmp.getEmailAddress();
			final HtmlEmail htmlEmail = (HtmlEmail) MailUtils.getPreConfiguredEmail();
			htmlEmail.setCharset("utf-8");

			final String[] emailAddresses = toEmailAddress.split(";");
			final Iterator<String> iterEmailAddresses = Arrays.asList(emailAddresses).iterator();
			while (iterEmailAddresses.hasNext())
			{
				final String anEmailAddress = iterEmailAddresses.next();
				MailUtils.validateEmailAddress(anEmailAddress, "TO");
				htmlEmail.addTo(anEmailAddress);
			}

			final TellAFriendContext ctx = new TellAFriendContextImpl();
			ctx.setToEmailAddress(toEmailAddress);
			htmlEmail.setSubject(cmp.getProduct().getName());
			ctx.setUserName(cmp.getUser().getName());
			ctx.setProductName(cmp.getProduct().getName());
			ctx.setComment(cmp.getComment());

			final URLFactory urlFac = YStorefoundation.getRequestContext().getURLFactory();
			final String link = urlFac.getURLCreator(cmp.getProduct().getClass()).createURL(cmp.getProduct()).toString();
			ctx.setProductLink(link);

			final StringWriter mailMessage = new StringWriter();
			final RendererTemplate template = (RendererTemplate) CommonsManager.getInstance().getFirstItemByAttribute(
					RendererTemplate.class, RendererTemplate.CODE, "tellAFriend");
			if (template == null)
			{
				YStorefoundation.getRequestContext().getSessionContext().getMessages().pushErrorMessage(
						"components.tellAFriendCmp.templateNotFound");
				return false;
			}
			CommonsManager.getInstance().render(template, ctx, mailMessage);
			htmlEmail.setHtmlMsg(mailMessage.toString());
			htmlEmail.send();
			return true;
		}
	}

	public String getComment()
	{
		return this.comment;
	}

	public String getEmailAddress()
	{
		return this.emailAddress;
	}

	public ProductModel getProduct()
	{
		return this.product;
	}

	public UserModel getUser()
	{
		return this.user;
	}

	public void setComment(final String comment)
	{
		this.comment = comment;
	}

	public void setEmailAddress(final String emailAddress)
	{
		this.emailAddress = emailAddress;
	}

}
