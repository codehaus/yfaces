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
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YModelBinding;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventListener;

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

	private YModelBinding<CategoryTreeComponent> categoryTreeCmp = null;
	private YModelBinding<QuickSearchComponent> quickSearchCmp = null;
	private YModelBinding<ChooseLanguageComponent> chooseLanguageCmp = null;
	private YModelBinding<AdBannerComponent> adBannerCmp = null;
	private YModelBinding<LoginComponent> loginCmp = null;
	private YModelBinding<ChooseCurrencyComponent> chooseCurrencyCmp = null;
	private YModelBinding<ChooseCatalogComponent> chooseCatalogCmp = null;
	private YModelBinding<ShowHistoryComponent> showHistoryCmp = null;
	private YModelBinding<ProductsQuickViewComponent> productsQuickViewCmp = null;

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
	 * @return {@link YModelBinding} for {@link ChooseLanguageComponent}
	 */
	public YModelBinding<ChooseLanguageComponent> getChooseLanguageComponent()
	{
		return this.chooseLanguageCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link ChooseCurrencyComponent}
	 */
	public YModelBinding<ChooseCurrencyComponent> getChooseCurrencyComponent()
	{
		return this.chooseCurrencyCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link QuickSearchComponent}
	 */
	public YModelBinding<QuickSearchComponent> getQuickSearchComponent()
	{
		return this.quickSearchCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link AdBannerComponent}
	 */
	public YModelBinding<AdBannerComponent> getAdBannerComponent()
	{
		return this.adBannerCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link LoginComponent}
	 */
	public YModelBinding<LoginComponent> getLoginComponent()
	{
		return this.loginCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link ProductsQuickViewComponent}
	 */
	public YModelBinding<ProductsQuickViewComponent> getProductsQuickViewComponent()
	{
		return this.productsQuickViewCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link ShowHistoryComponent}
	 */
	public YModelBinding<ShowHistoryComponent> getShowHistoryComponent()
	{
		return this.showHistoryCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link ChooseCatalogComponent}
	 */
	public YModelBinding<ChooseCatalogComponent> getChooseCatalogComponent()
	{
		return this.chooseCatalogCmp;
	}

	/**
	 * @return {@link YModelBinding} for {@link CategoryTreeComponent}
	 */
	public YModelBinding<CategoryTreeComponent> getCategoryTreeComponent()
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
		final YEventListener<QuickSearchComponent> listener = new DefaultYEventListener<QuickSearchComponent>();
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
	 * External {@link YEventListener} for {@link QuickSearchComponent}
	 * 
	 * @param event
	 *           {@link YEvent}
	 */
	public void doQuickSearch(final YEvent<QuickSearchComponent> event)
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
