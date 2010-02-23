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
package ystorefoundationpackage.yfaces.frame;


import java.util.Collections;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentBinding;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventListener;

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.catalog.ChooseCatalogComponent;
import ystorefoundationpackage.yfaces.component.category.CategoryTreeComponent;
import ystorefoundationpackage.yfaces.component.history.DefaultShowHistoryComponent;
import ystorefoundationpackage.yfaces.component.history.ShowHistoryComponent;
import ystorefoundationpackage.yfaces.component.i18n.ChooseCurrencyComponent;
import ystorefoundationpackage.yfaces.component.i18n.ChooseLanguageComponent;
import ystorefoundationpackage.yfaces.component.misc.AdBannerComponent;
import ystorefoundationpackage.yfaces.component.misc.DefaultAdBannerComponent;
import ystorefoundationpackage.yfaces.component.misc.AdBannerComponent.AdBannerMetaData;
import ystorefoundationpackage.yfaces.component.product.ProductTableComponent;
import ystorefoundationpackage.yfaces.component.product.ProductTableSortEvent;
import ystorefoundationpackage.yfaces.component.product.ProductsQuickViewComponent;
import ystorefoundationpackage.yfaces.component.search.DefaultQuickSearchComponent;
import ystorefoundationpackage.yfaces.component.search.QuickSearchComponent;
import ystorefoundationpackage.yfaces.component.user.DefaultLoginComponent;
import ystorefoundationpackage.yfaces.component.user.LoginComponent;


/**
 * Renders the general information.
 * 
 */
public class GlobalFrame extends AbstractYFrame
{
	private static final long serialVersionUID = 8924906285775079218L;
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(GlobalFrame.class);

	private YComponentBinding<CategoryTreeComponent> categoryTreeCmp = null;
	private YComponentBinding<QuickSearchComponent> quickSearchCmp = null;
	private YComponentBinding<ChooseLanguageComponent> chooseLanguageCmp = null;
	private YComponentBinding<AdBannerComponent> adBannerCmp = null;
	private YComponentBinding<LoginComponent> loginCmp = null;
	private YComponentBinding<ChooseCurrencyComponent> chooseCurrencyCmp = null;
	private YComponentBinding<ChooseCatalogComponent> chooseCatalogCmp = null;
	private YComponentBinding<ShowHistoryComponent> showHistoryCmp = null;
	private YComponentBinding<ProductsQuickViewComponent> productsQuickViewCmp = null;

	public GlobalFrame()
	{
		super();
		this.categoryTreeCmp = super.createComponentBinding();
		this.quickSearchCmp = super.createComponentBinding(createQuickSearchComponent());
		this.chooseLanguageCmp = super.createComponentBinding();
		this.adBannerCmp = super.createComponentBinding(this.createAdBannerComponent());
		this.loginCmp = super.createComponentBinding(this.createLoginComponent());
		this.chooseCurrencyCmp = super.createComponentBinding();
		this.chooseCatalogCmp = super.createComponentBinding();
		this.showHistoryCmp = super.createComponentBinding((ShowHistoryComponent) new DefaultShowHistoryComponent());
		this.productsQuickViewCmp = super.createComponentBinding();
	}

	/**
	 * @return {@link YComponentBinding} for {@link ChooseLanguageComponent}
	 */
	public YComponentBinding<ChooseLanguageComponent> getChooseLanguageComponent()
	{
		return this.chooseLanguageCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link ChooseCurrencyComponent}
	 */
	public YComponentBinding<ChooseCurrencyComponent> getChooseCurrencyComponent()
	{
		return this.chooseCurrencyCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link QuickSearchComponent}
	 */
	public YComponentBinding<QuickSearchComponent> getQuickSearchComponent()
	{
		return this.quickSearchCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link AdBannerComponent}
	 */
	public YComponentBinding<AdBannerComponent> getAdBannerComponent()
	{
		return this.adBannerCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link LoginComponent}
	 */
	public YComponentBinding<LoginComponent> getLoginComponent()
	{
		return this.loginCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link ProductsQuickViewComponent}
	 */
	public YComponentBinding<ProductsQuickViewComponent> getProductsQuickViewComponent()
	{
		return this.productsQuickViewCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link ShowHistoryComponent}
	 */
	public YComponentBinding<ShowHistoryComponent> getShowHistoryComponent()
	{
		return this.showHistoryCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link ChooseCatalogComponent}
	 */
	public YComponentBinding<ChooseCatalogComponent> getChooseCatalogComponent()
	{
		return this.chooseCatalogCmp;
	}

	/**
	 * @return {@link YComponentBinding} for {@link CategoryTreeComponent}
	 */
	public YComponentBinding<CategoryTreeComponent> getCategoryTreeComponent()
	{
		return this.categoryTreeCmp;
	}

	@Override
	public void refresh()
	{
		super.refresh();
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged())
		{
			getAdBannerComponent().setValue(this.createAdBannerComponent());
		}
	}

	private LoginComponent createLoginComponent()
	{
		final LoginComponent result = new DefaultLoginComponent();
		result.setErrorForward(NavigationOutcome.LOGIN_PAGE.id);
		result.setSuccessForward(null);
		return result;
	}

	private QuickSearchComponent createQuickSearchComponent()
	{
		final QuickSearchComponent result = new DefaultQuickSearchComponent();

		//a new listener is created; the default one does the search
		final YComponentEventListener<QuickSearchComponent> listener = new DefaultYComponentEventListener<QuickSearchComponent>();
		listener.setAction("searchResultPage");
		listener.setActionListener(super.createExpressionString("doQuickSearch"));

		result.getSearchEvent().addCustomListener(listener);

		return result;
	}

	private AdBannerComponent createAdBannerComponent()
	{
		final AdBannerComponent result = new DefaultAdBannerComponent();
		//		final Locale locale = SfRequestContext.getCurrentContext().getSessionContext().getLocale();
		//		final String suffix = Locale.GERMAN.equals(locale) ? "_de" : "";

		final String suffix = YStorefoundation.getRequestContext().getSessionContext().getLanguage().getIsocode();

		final AdBannerMetaData flashBanner = result.addBanner("hybris", "images/banners/storefoundation_banner_hybris.swf", "");
		result.addBanner("voucher", "images/banners/banner_voucher_" + suffix + ".gif", null);
		result.addBanner("sale", "images/banners/banner_sale_" + suffix + ".gif", null);

		flashBanner.getParameters().put("name", "storefoundation_banner_hybris");
		flashBanner.getParameters().put("quality", "high");
		flashBanner.getParameters().put("width", "197");
		flashBanner.getParameters().put("height", "120");
		flashBanner.getParameters().put("bgcolor", "#1246c8");

		return result;
	}

	/**
	 * External {@link YComponentEventListener} for {@link QuickSearchComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doQuickSearch(final YComponentEvent<QuickSearchComponent> event)
	{
		final QuickSearchComponent cmp = event.getComponent();

		//initialize ProductTableComponent at SearchResultFrame
		final SearchResultFrame frame = cmp.getFrame().getPage().getOrCreateFrame(SearchResultFrame.class);
		final ProductTableComponent ptCmp = frame.getProductTableComponent().getValue();
		//...set productlist
		ptCmp.setProductList(cmp.getSearchResultList());

		ptCmp.setClassAttributeAssignments(Collections.EMPTY_LIST);

		//...set searchterm for sorting
		((ProductTableSortEvent) ptCmp.getSortEvent().getListener()).setTerm(cmp.getSearchTerm());

	}

}
