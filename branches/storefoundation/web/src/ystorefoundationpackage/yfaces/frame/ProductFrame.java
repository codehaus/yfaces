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

import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYFrame;
import org.codehaus.yfaces.component.YModelBinding;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;

import ystorefoundationpackage.NavigationOutcome;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.component.customerreview.CustomerReviewComponent;
import ystorefoundationpackage.yfaces.component.misc.BreadcrumbComponent;
import ystorefoundationpackage.yfaces.component.misc.DefaultBreadcrumbComponent;
import ystorefoundationpackage.yfaces.component.product.ChooseVariantsComponent;
import ystorefoundationpackage.yfaces.component.product.DefaultProductDetailComponent;
import ystorefoundationpackage.yfaces.component.product.DefaultProductReferencesComponent;
import ystorefoundationpackage.yfaces.component.product.ProductDetailComponent;
import ystorefoundationpackage.yfaces.component.product.ProductReferencesComponent;
import ystorefoundationpackage.yfaces.component.product.ShowVariantProductsComponent;
import ystorefoundationpackage.yfaces.component.product.ProductReferencesComponent.ProductReferenceGroup;


/**
 * Renders the description of the specific product.
 * 
 */
public class ProductFrame extends AbstractYFrame
{
	private static final long serialVersionUID = 8123237357574493529L;
	private static final Logger log = Logger.getLogger(ProductFrame.class);

	private YModelBinding<ChooseVariantsComponent> chooseVariantsCmp = null;
	private YModelBinding<ShowVariantProductsComponent> showVariantProductsCmp = null;
	private List<YModelBinding<ProductReferencesComponent>> productReferencesCmpList = null;
	private YModelBinding<ProductDetailComponent> productDetailCmp = null;
	private YModelBinding<CustomerReviewComponent> customerReviewCmp = null;
	private YModelBinding<BreadcrumbComponent> breadcrumbCmp = null;

	public ProductFrame()
	{
		super();

		this.chooseVariantsCmp = super.createComponentBinding();
		this.showVariantProductsCmp = super.createComponentBinding();
		this.productDetailCmp = super.createComponentBinding(this.createProductDetailComponent());
		this.customerReviewCmp = super.createComponentBinding();
		this.breadcrumbCmp = super.createComponentBinding(this.createBreadcrumbComponent());

	}

	public YModelBinding<ChooseVariantsComponent> getChooseVariantsComponent()
	{
		return this.chooseVariantsCmp;
	}

	public YModelBinding<ShowVariantProductsComponent> getShowVariantProductsComponent()
	{
		return this.showVariantProductsCmp;
	}

	public YModelBinding<ProductDetailComponent> getProductDetailComponent()
	{
		return this.productDetailCmp;
	}

	public YModelBinding<CustomerReviewComponent> getCustomerReviewComponent()
	{
		return this.customerReviewCmp;
	}

	public YModelBinding<BreadcrumbComponent> getBreadcrumbComponent()
	{
		return this.breadcrumbCmp;
	}

	public List<YModelBinding<ProductReferencesComponent>> getProductReferencesComponentList()
	{
		if (this.productReferencesCmpList == null)
		{
			this.productReferencesCmpList = createProductReferenceComponentList();
		}
		return this.productReferencesCmpList;
	}

	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged())
		{
			this.productReferencesCmpList = null;
		}
		super.refresh();
	}

	public String doTellAFriend()
	{
		String result = NavigationOutcome.TELLAFRIEND_PAGE.id;
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();
		if (JaloBridge.getInstance().isAnonymous(userSession.getUser()))
		{
			final YConversationContext convCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
			final YPageContext page = convCtx.getOrCreateNextPage();
			page.getOrCreateFrame(LoginFrame.class).getLoginComponent().getValue().setSuccessForward(result);
			page.getOrCreateFrame(GlobalFrame.class).getLoginComponent().setSuccessForward(result);
			result = NavigationOutcome.LOGIN_PAGE.id;
		}
		return result;
	}

	private BreadcrumbComponent createBreadcrumbComponent()
	{
		final BreadcrumbComponent cmp = new DefaultBreadcrumbComponent();
		cmp.setLeaf(YStorefoundation.getRequestContext().getSessionContext().getProduct());
		return cmp;
	}

	private List<YModelBinding<ProductReferencesComponent>> createProductReferenceComponentList()
	{
		log.debug("Creating initial " + ProductReferencesComponent.class.getName());

		//retrieve all ProductReferences for current product
		final ProductModel product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		//		final Map<EnumerationValueModel, List<ProductReferenceModel>> refs = Webfoundation.getInstance().getServices()
		//				.getProductService().getAllProductReferences(product);
		final Map<EnumerationValueModel, List<ProductReferenceModel>> refs = YStorefoundation.getRequestContext()
				.getProductManagement().getAllProductReferences(product);


		final List<YModelBinding<ProductReferencesComponent>> result = new ArrayList<YModelBinding<ProductReferencesComponent>>();

		//iterate over all referencetypes...
		for (final Map.Entry<EnumerationValueModel, List<ProductReferenceModel>> entry : refs.entrySet())
		{
			//...get EnumerationValue and List of ProductReferences 
			final EnumerationValueModel key = entry.getKey();
			final List<ProductReferenceModel> productReferences = entry.getValue();

			//...create a ProductReferenceComponent for each type
			final DefaultProductReferencesComponent cmp = new DefaultProductReferencesComponent();

			//...and initialize it according the type:

			//default: crossells-layout, no selection, no grouping
			cmp.setLayout(ProductReferencesComponent.TYPE_CROSSELLS);
			cmp.setHeadline(key.getName());


			//UPSELLS: upsells-layout, no selection, no grouping
			if ("UPSELLING".equals(key.getCode()))
			{
				cmp.setLayout(ProductReferencesComponent.TYPE_UPSELLS);
				cmp.setHeadline(YStorefoundation.getRequestContext().getContentManagement().getLocalizedMessage(
						"productReferencesCmp.upselling.headline"));
			}

			//CROSSELLS, ACCESSORIES: crossells-layout, selection, grouping by description 
			if ("CROSSELLING".equals(key.getCode()) || "ACCESSORIES".equals(key.getCode()))
			{
				cmp.setLayout(ProductReferencesComponent.TYPE_CROSSELLS);

				//process grouping
				for (final ProductReferenceModel _reference : productReferences)
				{
					final ProductReferenceGroup group = cmp.getOrCreateProductReferenceGroup(_reference.getDescription());
					group.getProductList().add(_reference.getTarget());
					group.setHeadline(_reference.getDescription());
					group.setSelectionEnabled(true);
				}
			}
			else
			{
				//process no grouping
				final ProductReferenceGroup group = cmp.getOrCreateProductReferenceGroup("dummy");
				for (final ProductReferenceModel _reference : productReferences)
				{
					group.getProductList().add(_reference.getTarget());
				}
			}


			//...create a binding for the component
			//TODO: REAPLCE CASTS
			final YModelBinding<ProductReferencesComponent> binding = super
					.createComponentBinding((ProductReferencesComponent) cmp);

			binding.setValue(cmp);

			//...and put the binding into resultlist
			result.add(binding);
		}
		return result;
	}

	private ProductDetailComponent createProductDetailComponent()
	{
		final SfSessionContext userSession = YStorefoundation.getRequestContext().getSessionContext();

		final ProductDetailComponent result = new DefaultProductDetailComponent();
		final boolean anonymousUser = JaloBridge.getInstance().isAnonymous(userSession.getUser());
		result.getTellAFriendEvent().setEnabled(!anonymousUser);
		result.getAddToWishListEvent().setEnabled(!anonymousUser);
		result.getTellAFriendEvent().getListener().setAction(super.createExpressionString("doTellAFriend"));
		return result;
	}

}
