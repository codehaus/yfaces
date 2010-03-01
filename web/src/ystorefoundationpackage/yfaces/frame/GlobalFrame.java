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

	private CategoryTreeComponent categoryTreeCmp = null;
	private QuickSearchComponent quickSearchCmp = null;
	private ChooseLanguageComponent chooseLanguageCmp = null;
	private AdBannerComponent adBannerCmp = null;
	private LoginComponent loginCmp = null;
	private ChooseCurrencyComponent chooseCurrencyCmp = null;
	private ChooseCatalogComponent chooseCatalogCmp = null;
	private ShowHistoryComponent showHistoryCmp = null;
	private ProductsQuickViewComponent productsQuickViewCmp = null;

	public GlobalFrame()
	{
		super();
	}

	/**
	 * @return {@link YModelBinding} for {@link ChooseLanguageComponent}
	 */
	public ChooseLanguageComponent getChooseLanguageComponent()
	{
		return this.chooseLanguageCmp;
	}
	
	public void  setChooseLanguageComponent(ChooseLanguageComponent model)
	{
		this.chooseLanguageCmp = model;
	}


	/**
	 * @return {@link YModelBinding} for {@link ChooseCurrencyComponent}
	 */
	public ChooseCurrencyComponent getChooseCurrencyComponent()
	{
		return this.chooseCurrencyCmp;
	}
	
	public void setChooseCurrencyComponent(ChooseCurrencyComponent model)
	{
		this.chooseCurrencyCmp = model;
	}
	
	
	

	/**
	 * @return {@link YModelBinding} for {@link QuickSearchComponent}
	 */
	public QuickSearchComponent getQuickSearchComponent()
	{
		if (this.quickSearchCmp == null) {
			this.quickSearchCmp = createQuickSearchComponent();
		}
		return this.quickSearchCmp;
	}
	
	public void setQuickSearchComponent(QuickSearchComponent model)
	{
		this.quickSearchCmp = model;
	}


	/**
	 * @return {@link YModelBinding} for {@link AdBannerComponent}
	 */
	public AdBannerComponent getAdBannerComponent()
	{
		if (this.adBannerCmp == null) {
			this.adBannerCmp = createAdBannerComponent();
		}
		return this.adBannerCmp;
	}
	
	public void setAdBannerComponent(AdBannerComponent model)
	{
		this.adBannerCmp = model;
	}


	/**
	 * @return {@link YModelBinding} for {@link LoginComponent}
	 */
	public LoginComponent getLoginComponent()
	{
		if (this.loginCmp == null) {
			this.loginCmp = createLoginComponent();
		}
		return this.loginCmp;
	}
	
	public void setLoginComponent(LoginComponent model)
	{
		this.loginCmp = model;
	}

//	public YModelBinding<LoginComponent> getLoginComponent()
//	{
//		return this.loginCmp;
//
//	}


	/**
	 * @return {@link YModelBinding} for {@link ProductsQuickViewComponent}
	 */
	public ProductsQuickViewComponent getProductsQuickViewComponent()
	{
		return this.productsQuickViewCmp;
	}
	
	public void  setProductsQuickViewComponent(ProductsQuickViewComponent model)
	{
		this.productsQuickViewCmp = model;
	}


	/**
	 * @return {@link YModelBinding} for {@link ShowHistoryComponent}
	 */
	public ShowHistoryComponent getShowHistoryComponent()
	{
		if (this.showHistoryCmp == null) {
			this.showHistoryCmp = new DefaultShowHistoryComponent();
		}
		return this.showHistoryCmp;
	}
	
	public void setShowHistoryComponent(ShowHistoryComponent model)
	{
		this.showHistoryCmp = model;
	}

	

	/**
	 * @return {@link YModelBinding} for {@link ChooseCatalogComponent}
	 */
	public ChooseCatalogComponent getChooseCatalogComponent()
	{
		return this.chooseCatalogCmp;
	}
	
	public void setChooseCatalogComponent(ChooseCatalogComponent model)
	{
		this.chooseCatalogCmp = model;
	}


	/**
	 * @return {@link YModelBinding} for {@link CategoryTreeComponent}
	 */
	public CategoryTreeComponent getCategoryTreeComponent()
	{
		return this.categoryTreeCmp;
	}
	
	public void  setCategoryTreeComponent(CategoryTreeComponent model)
	{
		this.categoryTreeCmp = model;
	}


	@Override
	public void refresh()
	{
		super.refresh();
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged())
		{
			this.adBannerCmp = this.createAdBannerComponent();
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
