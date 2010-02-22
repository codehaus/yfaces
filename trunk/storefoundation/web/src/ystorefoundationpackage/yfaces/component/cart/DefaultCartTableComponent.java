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
package ystorefoundationpackage.yfaces.component.cart;

import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.DefaultYComponentEventListener;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventHandler;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ystorefoundationpackage.StorefoundationException;
import ystorefoundationpackage.domain.FormattedAttribute;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.YStorefoundation;


/**
 * Implementation of the <code>CartTableComponent</code> interface.
 */
public class DefaultCartTableComponent extends AbstractYComponent implements CartTableComponent
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(DefaultCartTableComponent.class);


	private YComponentEventHandler<CartTableComponent> ehRemoveCartEntry = null;
	private YComponentEventHandler<CartTableComponent> ehUpdateCartEntry = null;
	private YComponentEventHandler<CartTableComponent> ehSortCartEntries = null;


	private static final long serialVersionUID = 8712436172843189173L;

	private String sortColumn = null;
	private boolean ascending = true;
	private CartModel cartBean = null;


	private transient String subTotal = null;
	private transient List<CartTableEntry> entries = null;

	/**
	 * Implementation of the <code>CartTableEntry</code> interface.
	 */
	public static class CartTableEntryImpl implements CartTableEntry
	{
		private AbstractOrderEntryModel cartEntryBean = null;
		private String basePrice = null;
		private String totalPrice = null;
		private String quantity = null;

		public CartTableEntryImpl(final AbstractOrderEntryModel bean)
		{
			this.cartEntryBean = bean;
			final CurrencyModel currency = bean.getOrder().getCurrency();
			final NumberFormat nf = YStorefoundation.getRequestContext().getContentManagement().getCurrencyNumberFormat(currency);
			this.totalPrice = nf.format(bean.getTotalPrice().doubleValue());
			this.basePrice = nf.format(bean.getBasePrice().doubleValue());

			this.quantity = String.valueOf(bean.getQuantity());
		}

		public String getFormattedBasePrice()
		{
			return this.basePrice;
		}

		public String getFormattedTotalPrice()
		{
			return this.totalPrice;
		}

		public String getQuantity()
		{
			return this.quantity;
		}

		public void setQuantity(final String quantity)
		{
			this.quantity = quantity;
		}

		public List<FormattedAttribute> getProductVariantValues()
		{
			return YStorefoundation.getRequestContext().getProductManagement().getVariantAttributeInfoList(
					this.cartEntryBean.getProduct());
		}

		public AbstractOrderEntryModel getSource()
		{
			return this.cartEntryBean;
		}

	}

	/**
	 * This event gets fired when the user tries to update the quantity of all cart entries. The entry gets removed when
	 * quantity is 0.
	 */
	public static class UpdateCartTableEntriesEvent extends DefaultYComponentEventListener<CartTableComponent>
	{
		private static final long serialVersionUID = 8712436172143689175L;

		@Override
		public void actionListener(final YComponentEvent<CartTableComponent> event)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			final CartTableComponent cmp = event.getComponent();
			final CartModel newCart = cmp.getCart();

			for (final CartTableEntry entry : cmp.getCartTableEntries())
			{
				try
				{
					final long newQuantity = Long.parseLong(entry.getQuantity());
					final AbstractOrderEntryModel _entry = entry.getSource();
					if (newQuantity > 0)
					{
						_entry.setQuantity(Long.valueOf(newQuantity));
						reqCtx.getPlatformServices().getModelService().save(_entry);
					}

					if (newQuantity == 0)
					{
						reqCtx.getPlatformServices().getModelService().remove(_entry);
					}
				}
				catch (final Exception e)
				{
					log.debug(e);
					reqCtx.getSessionContext().getMessages().pushErrorMessage("no_valid_quantity");
				}
			}
			reqCtx.getOrderManagement().updateCart(newCart);
		}

	}

	/**
	 * This event gets fired when the user tries to remove one entry from the cart. The entry is removed by its cart
	 * entry index passed as parameter to cart table model.
	 */
	public static class RemoveCartTableEntryEvent extends DefaultYComponentEventListener<CartTableComponent>
	{
		private static final long serialVersionUID = 8712436172143689175L;

		@Override
		public void actionListener(final YComponentEvent<CartTableComponent> event)
		{
			final CartTableEntry entry = (CartTableEntry) event.getFacesEvent().getComponent().getAttributes().get(
					ATTRIB_REMOVE_ENTRY);
			if (entry == null)
			{
				throw new StorefoundationException("Invalid CartTableEntry", new NullPointerException());
			}

			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			reqCtx.getPlatformServices().getModelService().remove(entry.getSource());
			reqCtx.getOrderManagement().updateCart(reqCtx.getSessionContext().getCart());
		}
	}

	public static class SortCartTableEvent extends DefaultYComponentEventListener<CartTableComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<CartTableComponent> event)
		{
			//CartTableComponent cmp = event.getActionComponent();
			log.error("Sorting is currently not supported");
		}
	}


	/**
	 * Constructor.
	 */
	public DefaultCartTableComponent()
	{
		super();
		this.ehRemoveCartEntry = super.createEventHandler(new RemoveCartTableEntryEvent());
		this.ehSortCartEntries = super.createEventHandler(new SortCartTableEvent());
		this.ehUpdateCartEntry = super.createEventHandler(new UpdateCartTableEntriesEvent());
	}

	@Override
	public void validate()
	{
		if (getSortColumn() == null)
		{
			setSortColumn("name");
		}

		if (getCart() == null)
		{
			setCart(YStorefoundation.getRequestContext().getSessionContext().getCart());
		}

	}

	@Override
	public void refresh()
	{
		//always refresh entries
		this.entries = null;
		this.cartBean = null;
		super.refresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.cart.CartTableComponent#getUpdateEvent()
	 */
	public YComponentEventHandler<CartTableComponent> getUpdateCartTableEntriesEvent()
	{
		return this.ehUpdateCartEntry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.cart.CartTableComponent#getRemoveEvent()
	 */
	public YComponentEventHandler<CartTableComponent> getRemoveCartTableEntryEvent()
	{
		return this.ehRemoveCartEntry;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see ystorefoundationpackage.faces.components.cart.CartTableComponent#getSortCartTableEvent()
	 */
	public YComponentEventHandler<CartTableComponent> getSortCartTableEvent()
	{
		return this.ehSortCartEntries;
	}


	public List<CartTableEntry> getCartTableEntries()
	{
		if (this.entries == null)
		{
			this.entries = new ArrayList<CartTableEntry>();
			final List<AbstractOrderEntryModel> entries = getCart().getEntries();
			for (final AbstractOrderEntryModel cartEntry : entries)
			{
				this.entries.add(new CartTableEntryImpl(cartEntry));
			}
		}
		return this.entries;
	}


	public String getSortColumn()
	{
		return sortColumn;
	}

	public void setSortColumn(final String sortColumn)
	{
		this.sortColumn = sortColumn;
	}

	public boolean isAscending()
	{
		return ascending;
	}

	public void setAscending(final boolean ascending)
	{
		this.ascending = ascending;
	}

	public CartModel getCart()
	{
		return cartBean;
	}

	public void setCart(final CartModel cartBean)
	{
		this.cartBean = cartBean;
	}

	public String getFormattedSubTotal()
	{
		if (this.subTotal == null)
		{
			final double subtotal = cartBean.getSubtotal().doubleValue();
			final NumberFormat nf = YStorefoundation.getRequestContext().getContentManagement().getCurrencyNumberFormat();
			//			final CurrencyModel cur = YStorefoundation.getRequestContext().getSessionContext().getCurrency();
			//			this.subTotal = Webfoundation.getInstance().getServices().getC2LService().getFormattedPrice(subtotal, cur);
			this.subTotal = nf.format(subtotal);
		}
		return this.subTotal;
	}





}
