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
package ystorefoundationpackage.yfaces.component.product;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.datatable.ColumnCollectionDataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.domain.Price;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.OrderManagement.AddToCartResult;


/**
 * Implementation of the <code>ProductReferencesComponent</code> interface.
 */
public class DefaultProductReferencesComponent extends AbstractYModel implements ProductReferencesComponent
{
	private static final String TO_CART_LINK = "/pages/cartPage.jsf";

	private int type = -1;
	private String headline = null;

	private ProductModel product = null;

	private Map<Object, ProductReferenceGroup> productReferenceGroupMap = null;

	private YEventHandler<ProductReferencesComponent> ehAddSelectionToCart = null;


	private class ProductReferenceGroupImpl implements ProductReferenceGroup, Serializable
	{
		private List<ProductModel> productList = null;
		private List<Boolean> checkboxList = null;
		private boolean selectionEnabled = false;
		private String headline = null;
		private Object groupId = null;

		private transient DataTableAxisModel table = null;

		public ProductReferenceGroupImpl(final Object groupId)
		{
			this.groupId = groupId;
			this.productList = new ArrayList<ProductModel>();
		}

		public List<ProductModel> getProductList()
		{
			return this.productList;
		}

		public void setProductList(final List<ProductModel> products)
		{
			this.productList = products;
		}

		public boolean isSelectionEnabled()
		{
			return this.selectionEnabled;
		}

		public void setSelectionEnabled(final boolean enabled)
		{
			this.selectionEnabled = enabled;
		}

		public String getHeadline()
		{
			return headline;
		}

		public void setHeadline(final String headline)
		{
			this.headline = headline;
		}

		public Object getGroupId()
		{
			return this.groupId;
		}

		public DataTableAxisModel getTable()
		{
			if (this.table == null)
			{
				this.table = this.createTable();
			}
			return this.table;
		}

		public List<ProductModel> getSelectedProducts()
		{
			final List<ProductModel> result = new ArrayList<ProductModel>();
			for (int i = 0; i < this.productList.size(); i++)
			{
				if (this.checkboxList.get(i) == Boolean.TRUE)
				{
					result.add(this.productList.get(i));
				}
			}
			return result;
		}

		private DataTableAxisModel createTable()
		{
			DataTableAxisModel tableView = null;

			//retrieve products
			final List<ProductModel> _products = getProductList();
			if (_products == null)
			{
				throw new StorefoundationException("No products available");
			}

			if (_products.isEmpty())
			{
				tableView = DataTableFactory.createEmptyDataTableAxisModel();
			}
			else
			{
				final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

				final ColumnCollectionDataTableModel tableModel = new ColumnCollectionDataTableModel();

				//checkbox column (0)
				if (this.checkboxList == null)
				{
					final Boolean[] checkboxes = new Boolean[_products.size()];
					Arrays.fill(checkboxes, Boolean.TRUE);
					this.checkboxList = Arrays.asList(checkboxes);
				}
				tableModel.addColumn(this.checkboxList);

				//product column (1)
				tableModel.addColumn(_products);

				//price column (2)
				tableModel.addColumn(_products); //dummy column

				//price delta column (3)/formatted price delta column (4) 
				final double d2 = reqCtx.getOrderManagement().getPrices(DefaultProductReferencesComponent.this.getProduct())
						.getDefaultPricing().getPriceValue();

				final List<Double> priceDelta = new ArrayList<Double>();
				final List<String> formattedPriceDelta = new ArrayList<String>();
				for (final ProductModel product : getProductList())
				{
					final Price price = reqCtx.getOrderManagement().getPrices(product).getDefaultPricing();
					final Double delta2 = Double.valueOf(price.getPriceValue() - d2);
					final CurrencyModel currency = reqCtx.getSessionContext().getCurrency();
					final String formattedDelta2 = reqCtx.getContentManagement().getCurrencyNumberFormat(currency).format(
							delta2.doubleValue());

					priceDelta.add(delta2);
					formattedPriceDelta.add(formattedDelta2);
				}
				tableModel.addColumn(priceDelta);
				tableModel.addColumn(formattedPriceDelta);

				tableView = DataTableFactory.createDataTableAxisModel(tableModel);
			}

			return tableView;
		}


	}

	/**
	 * This event gets fired when the user tries to add the selected products to the cart.
	 */
	@SuppressWarnings("PMD.TooFewBranchesForASwitchStatement")
	public static class AddSelectionToCartEvent extends DefaultYEventListener<ProductReferencesComponent>
	{
		@Override
		public void actionListener(final YEvent<ProductReferencesComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final SfSessionContext sessCtx = reqCtx.getSessionContext();

			final ProductReferencesComponent cmp = event.getComponent();
			final List<ProductModel> selected = new ArrayList();
			for (final ProductReferenceGroup group : cmp.getProductReferenceGroups())
			{
				selected.addAll(group.getSelectedProducts());
			}

			int count = 0;
			for (final ProductModel product : selected)
			{
				final AddToCartResult state = reqCtx.getOrderManagement().addToCart(sessCtx.getCart(), product, 1, null);

				if (state == AddToCartResult.ADDED_ONE)
				{
					count++;
				}
				else
				{
					throw new YFacesException(cmp, "");
				}
			}

			final String resource = reqCtx.getURLFactory().createExternalForm(TO_CART_LINK);
			final String toCartLink = "<a href=\"" + resource + "\">"
					+ reqCtx.getContentManagement().getLocalizedMessage("frames.productFrame.toCart") + "</a>";

			if (count == 1)
			{
				sessCtx.getMessages().pushInfoMessage("addedtocart_one", toCartLink);
			}
			else
			{
				sessCtx.getMessages().pushInfoMessage("addedtocart_many", String.valueOf(count), toCartLink);
			}
		}
	}

	public DefaultProductReferencesComponent()
	{
		super();
		this.productReferenceGroupMap = new HashMap<Object, ProductReferenceGroup>();
		this.ehAddSelectionToCart = super.createEventHandler(new AddSelectionToCartEvent());
	}


	@Override
	public void validate()
	{
		if (this.product == null)
		{
			this.product = YStorefoundation.getRequestContext().getSessionContext().getProduct();
		}

		this.getAddSelectionToCartEvent().setEnabled(false);
		for (final ProductReferenceGroup group : this.getProductReferenceGroups())
		{
			if (group.isSelectionEnabled())
			{
				this.getAddSelectionToCartEvent().setEnabled(true);
				break;
			}

		}
	}


	public String getHeadline()
	{
		return this.headline;
	}


	public ProductModel getProduct()
	{
		return this.product;
	}

	public void setProduct(final ProductModel product)
	{
		this.product = product;
	}


	public int getLayout()
	{
		return this.type;
	}

	public void setHeadline(final String headline)
	{
		this.headline = headline;
	}


	public void setLayout(final int type)
	{
		this.type = type;
	}

	public Collection<ProductReferenceGroup> getProductReferenceGroups()
	{
		return new ArrayList<ProductReferenceGroup>(this.productReferenceGroupMap.values());
	}

	public ProductReferenceGroup getOrCreateProductReferenceGroup(final Object groupId)
	{
		ProductReferenceGroup result = this.productReferenceGroupMap.get(groupId);
		if (result == null)
		{
			result = new ProductReferenceGroupImpl(groupId);
			this.productReferenceGroupMap.put(groupId, result);
		}
		return result;
	}


	public YEventHandler<ProductReferencesComponent> getAddSelectionToCartEvent()
	{
		return this.ehAddSelectionToCart;
	}





}
