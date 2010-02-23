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

import de.hybris.platform.cache2.ObjectKey;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel;
import de.hybris.platform.category.jalo.Category;
import de.hybris.platform.category.jalo.CategoryManager;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms.model.StoreModel;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.c2l.Language;
import de.hybris.platform.jalo.order.Cart;
import de.hybris.platform.jalo.order.OrderManager;
import de.hybris.platform.jalo.order.payment.PaymentInfo;
import de.hybris.platform.jalo.order.payment.PaymentMode;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.collections.map.CaseInsensitiveMap;
import org.apache.log4j.Logger;

import ystorefoundationpackage.domain.DefaultValues;
import ystorefoundationpackage.domain.PlatformServices;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.PaymentModes;
import ystorefoundationpackage.domain.util.CompareCatalogByName;
import ystorefoundationpackage.domain.util.CompareCatalogByStoreOrder;


public class DefaultValuesImpl extends AbstractDomainService implements DefaultValues
{
	private final ObjectKey GET_DEFAULT_CATALOGS = new SfCacheKey("dummy", log)
	{
		@Override
		public Object createObject(final Object param)
		{
			return DefaultValuesImpl.this.getDefaultCatalogsInternal();
		}
	};

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultValuesImpl.class);

	public CategoryModel getDefaultCategory()
	{
		final Category catg = CategoryManager.getInstance().getAllRootCategories().iterator().next();
		final CategoryModel result = getModelService().get(catg);
		return result;
	}

	public CatalogModel getDefaultCatalog()
	{

		CatalogModel result = null;
		final String cookieCatalog = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap().get(
				"selectedCatalog");

		if (cookieCatalog != null)
		{
			try
			{
				result = YStorefoundation.getRequestContext().getPlatformServices().getCatalogService().getCatalog(cookieCatalog);
			}
			catch (final UnknownIdentifierException e)
			{
				log.error(e);
			}
		}

		if (result == null)
		{
			result = getDefaultCatalogs().get(0);
			//result = catService.findAll().get(0);
		}

		return result;
	}


	@Override
	public List<CatalogModel> getDefaultCatalogs()
	{
		final List<CatalogModel> result = (List) getUserSessionCache().fetch(GET_DEFAULT_CATALOGS);
		return result;
	}

	public CurrencyModel getDefaultCurrency()
	{
		final Currency cur = JaloSession.getCurrentSession().getSessionContext().getCurrency();
		final CurrencyModel result = getModelService().get(cur);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.domain.DomainServices#getDefaultCustomer()
	 */
	@Override
	public UserModel getDefaultCustomer()
	{
		final User user = UserManager.getInstance().getAnonymousCustomer();
		final UserModel result = getModelService().get(user);
		return result;
	}


	// TODO: cleanup this method!!
	public LanguageModel getDefaultLanguage()
	{
		final Iterator availableLocales = Collections.singletonList(Locale.ENGLISH).iterator();

		Language chosen = null;
		final JaloSession jaloSession = JaloSession.getCurrentSession();

		// first get all languages which are available within active catalogs
		final Map availableLanguages = new CaseInsensitiveMap();
		for (final Iterator iter = CatalogManager.getInstance().getSessionCatalogVersions(jaloSession).iterator(); iter.hasNext();)
		{
			final CatalogVersion cv = (CatalogVersion) iter.next();
			for (final Iterator iterator = cv.getLanguages().iterator(); iterator.hasNext();)
			{
				final Language l = (Language) iterator.next();
				if (l.isActive())
				{
					availableLanguages.put(l.getIsoCode(), l);
				}
			}
		}
		if (log.isDebugEnabled())
		{
			log.debug("available catalog languages are " + availableLanguages.keySet());
		}

		// try users session language
		//if (chosen == null)
		{
			final Language userLang = jaloSession.getUser().getSessionLanguage();
			if (userLang != null && userLang.isActive()
					&& (availableLanguages.isEmpty() || availableLanguages.containsValue(userLang)))
			{
				chosen = userLang;
				if (log.isDebugEnabled())
				{
					log
							.debug("user session language " + userLang.getIsoCode() + " from " + jaloSession.getUser().getUID()
									+ " chosen");
				}
			}
		}
		// now lets see what the browser might have requested
		if (chosen == null && !availableLanguages.isEmpty())
		{
			//for (Enumeration en = request.getLocales(); chosen == null && en.hasMoreElements();)
			for (final Iterator en = availableLocales; chosen == null && en.hasNext();)
			{
				//final Locale loc = (Locale) en.nextElement();
				final Locale loc = (Locale) en.next();
				final String lang = loc.getLanguage();
				final String country = loc.getCountry();
				// 1. try lang+country
				chosen = (Language) availableLanguages.get(country != null && country.length() > 0 ? lang + "_" + country : lang);
				// 2. try lang alone
				if (chosen == null && country != null && country.length() > 0)
				{
					chosen = (Language) availableLanguages.get(lang);
				}
			}
			if (log.isDebugEnabled() && chosen != null)
			{
				log.debug("accepted browser language " + chosen.getIsoCode() + " chosen");
			}
		}
		// try default locale of the system
		if (chosen == null && !availableLanguages.isEmpty())
		{
			// 3. fallback to default locale of the system
			final Locale loc = Locale.getDefault();
			final String lang = loc.getLanguage();
			final String country = loc.getCountry();
			// try lang+country again
			chosen = (Language) availableLanguages.get(country != null && country.length() > 0 ? lang + "_" + country : lang);
			// try lang alone again
			if (chosen == null && country != null && country.length() > 0)
			{
				chosen = (Language) availableLanguages.get(lang);
			}
			if (log.isDebugEnabled() && chosen != null)
			{
				log.debug("system default language " + chosen.getIsoCode() + " chosen");
			}
		}
		// try 'de' as a last resort :/
		if (chosen == null && !Locale.getDefault().equals(Locale.GERMAN) && !availableLanguages.isEmpty())
		{
			chosen = (Language) availableLanguages.get("de");
			if (log.isDebugEnabled() && chosen != null)
			{
				log.debug("language 'de' == " + chosen.getIsoCode() + " chosen");
			}
		}
		// just take one of the available languages now :(
		if (chosen == null && !availableLanguages.isEmpty())
		{
			chosen = (Language) ((Map.Entry) availableLanguages.entrySet().iterator().next()).getValue();
			if (log.isDebugEnabled() && chosen != null)
			{
				log.debug("first language " + chosen.getIsoCode() + " of available languages chosen");
			}
		}
		// nothing found since user got no session language and catalogs did not provide any language
		if (log.isDebugEnabled() && chosen == null)
		{
			log.debug("cannot determine any default language - tried user, catalog, browser and system default languages!");
		}

		final LanguageModel result = getModelService().get(chosen);
		return result;
	}




	/**
	 * Jalo based implementation.<br/>
	 * - Catalogs must be non-empty.<br/>
	 * - Catalogs must be active.<br/>
	 * Resultorder is defined by the Store.<br/>
	 * <br/>
	 * 
	 * @return List of {@link CatalogModel}
	 * 
	 * @see CatalogService#getAllCatalogs()
	 */
	private List<CatalogModel> getDefaultCatalogsInternal()
	{
		final PlatformServices platformServices = YStorefoundation.getRequestContext().getPlatformServices();
		List<CatalogModel> _result = Collections.EMPTY_LIST;
		final Collection<CatalogVersionModel> activeVersions = platformServices.getCatalogService().getSessionCatalogVersions();

		if (!activeVersions.isEmpty())
		{
			final StoreModel s = platformServices.getCmsService().getSessionStore();

			_result = new ArrayList(activeVersions.size());
			for (final CatalogVersionModel cv : activeVersions)
			{
				// skip empty catalog versions right here
				final int rootCategoriesCount = JaloBridge.getInstance().getRootCategoriesCount(cv);
				if (rootCategoriesCount == 0)
				{
					continue;
				}
				final CatalogModel c = cv.getCatalog();
				if (!_result.contains(c)
						&& !(s.getHideClassificationSystemTree().booleanValue() && c instanceof ClassificationSystemModel))
				{
					_result.add(c);
				}
			}

			// sort by catalog order in store
			if (s != null)
			{
				Collections.sort(_result, new CompareCatalogByStoreOrder(new ArrayList(s.getCatalogs())));
			}
			// sort alphabetical
			else
			{
				Collections.sort(_result, new CompareCatalogByName());
			}
		}
		return _result;
	}

	@Override
	public CartModel getDefaultCart()
	{
		final Cart cart = JaloSession.getCurrentSession().getCart();
		final User user = JaloSession.getCurrentSession().getUser();

		//initialize cart with useful defaults (payment)

		//when available, take first paymentinfo from user 
		final Collection<PaymentInfo> pinfos = user.getPaymentInfos();
		final PaymentInfo pinfo = (pinfos != null && !pinfos.isEmpty()) ? pinfos.iterator().next() : null;
		if (pinfo != null)
		{
			cart.setPaymentInfo(pinfo);
		}

		//take payment and deliveraddress from user
		cart.setDeliveryAddress(user.getDefaultDeliveryAddress());
		cart.setPaymentAddress(user.getDefaultPaymentAddress());

		//choose best matching paymentmode
		String paymentModeCode = PaymentModes.ADVANCE.getCode();
		if (pinfo != null)
		{
			final String code = pinfo.getComposedType().getCode();
			if (code.equals(Constants.TYPES.CreditCardPaymentInfo))
			{
				paymentModeCode = PaymentModes.CREDIT.getCode();
			}
			if (code.equals(Constants.TYPES.DebitPaymentInfo))
			{
				paymentModeCode = PaymentModes.DEBIT.getCode();
			}
		}

		try
		{
			final PaymentMode pmode = OrderManager.getInstance().getPaymentModeByCode(paymentModeCode);
			cart.setPaymentMode(pmode);
		}
		catch (final JaloItemNotFoundException e)
		{
			log.warn("**********************************");
			log.warn("*** 'default' payment mode with type " + paymentModeCode
					+ " not found, having no default payment mode in the system. you cannot place orders.");
			log.warn("**********************************");
			cart.setPaymentMode(null);
		}

		final CartModel result = getModelService().get(cart);
		return result;
	}


}
