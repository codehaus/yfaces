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
package ystorefoundationpackage.yfaces.component.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.order.AbstractOrder;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.codehaus.yfaces.component.AbstractYComponent;
import org.codehaus.yfaces.component.DefaultYComponentEventListener;
import org.codehaus.yfaces.component.YComponentEvent;
import org.codehaus.yfaces.component.YComponentEventHandler;

import ystorefoundationpackage.Localized;
import ystorefoundationpackage.datatable.ColumnCollectionDataTableModel;
import ystorefoundationpackage.datatable.DataTableModel;
import ystorefoundationpackage.datatable.ext.DataTableAxisModel;
import ystorefoundationpackage.datatable.ext.axes.DataTableAxisMarker;
import ystorefoundationpackage.datatable.ext.axes.DataTableFactory;
import ystorefoundationpackage.datatable.ext.cell.DataTableCellConverter;
import ystorefoundationpackage.domain.DefaultValues;
import ystorefoundationpackage.domain.SfRequestContext;
import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.faces.SfSelectItem;
import ystorefoundationpackage.faces.SfSelectItemGroup;


/**
 * Implementation of the <code>OrderHistoryComponent</code> interface.
 */
public class DefaultOrderHistoryComponent extends AbstractYComponent implements OrderHistoryComponent
{
	//used Localization keys
	private static final String LOCALIZATION_PRICE = Localized.WORD_PRICE.key;
	private static final String LOCALIZATION_STATE = Localized.WORD_STATE.key;
	private static final String LOCALIZATION_DATE = Localized.WORD_DATE.key;
	private static final String LOCALIZATION_ORDERID = "orderHistoryCmp.orderid";

	private static final String LOCALIZATION_ALL = Localized.WORD_ALL.key;


	private String sortColumnName = COLUMN_DATE;
	private boolean isSortSscending = true;

	private String orderCode = null;
	private EnumerationValueModel orderState = null;

	private List<OrderModel> orderList = null;
	private UserModel user = null;


	private transient DataTableAxisModel table = null;
	private transient SfSelectItemGroup orderStateSelector = null;

	/**
	 * This event gets fired when the user tries to view the orders under the selected status.
	 */
	public static class OrderStateSelectEvent extends DefaultYComponentEventListener<OrderHistoryComponent>
	{
		@Override
		public void actionListener(final YComponentEvent<OrderHistoryComponent> event)
		{
			final OrderHistoryComponent cmp = event.getComponent();
			final EnumerationValueModel selected = cmp.getOrderStateSelector().getSelectedValue();
			cmp.setOrderState(selected);
			((DefaultOrderHistoryComponent) cmp).table = null;
			((DefaultOrderHistoryComponent) cmp).orderList = null;
		}
	}

	/**
	 * This event gets fired when the user tries to sort the order table.
	 */
	public static class SortOrderHistoryTableEvent extends DefaultYComponentEventListener<OrderHistoryComponent>
	{

		@Override
		public void actionListener(final YComponentEvent<OrderHistoryComponent> event)
		{
			final OrderHistoryComponent cmp = event.getComponent();
			((DefaultOrderHistoryComponent) cmp).table = null;
			((DefaultOrderHistoryComponent) cmp).orderList = null;
		}
	}

	private YComponentEventHandler<OrderHistoryComponent> ehSelectOrderState = null;
	private YComponentEventHandler<OrderHistoryComponent> ehSortOrderTable = null;

	public DefaultOrderHistoryComponent()
	{
		super();
		this.ehSelectOrderState = super.createEventHandler(new OrderStateSelectEvent());
		this.ehSortOrderTable = super.createEventHandler(new SortOrderHistoryTableEvent());
	}

	public List<OrderModel> getOrderList()
	{
		return this.orderList;
	}

	public void setOrderList(final List<OrderModel> orderList)
	{
		this.orderList = orderList;
	}

	public String getSortColumn()
	{
		return this.sortColumnName;
	}

	public void setSortColumn(final String sortColumn)
	{
		this.sortColumnName = sortColumn;
	}

	public String getOrderCode()
	{
		return orderCode;
	}

	public void setOrderCode(final String orderCode)
	{
		this.orderCode = orderCode;
	}

	public EnumerationValueModel getOrderState()
	{
		return orderState;
	}

	public void setOrderState(final EnumerationValueModel orderState)
	{
		this.orderState = orderState;
	}


	public UserModel getUser()
	{
		return this.user;
	}

	public void setUser(final UserModel user)
	{
		this.user = user;
	}

	public boolean isSortAscending()
	{
		return this.isSortSscending;
	}


	public void setSortAscending(final boolean isAscending)
	{
		this.isSortSscending = isAscending;
	}


	@Override
	public void validate()
	{
		if (this.orderList == null)
		{
			final SfRequestContext reqCtx = YStorefoundation.getRequestContext();
			if (this.user == null)
			{
				this.user = reqCtx.getSessionContext().getUser();
				if (this.user == null)
				{
					final DefaultValues defaults = reqCtx.getDefaultValues();
					this.user = defaults.getDefaultCustomer();
				}
			}

			//			this.orderList = Webfoundation.getInstance().getServices().getOrderService().findAllBy(user, orderCode,
			//					Arrays.asList(getOrderState()), getSortColumn(), isSortAscending());
			this.orderList = reqCtx.getOrderManagement().getAllOrders(user, orderCode, Arrays.asList(getOrderState()),
					getSortColumn(), isSortAscending());
		}
	}

	@Override
	public void refresh()
	{
		final SfSessionContext session = YStorefoundation.getRequestContext().getSessionContext();
		if (session.isLanguageChanged() || session.isCurrencyChanged())
		{
			this.table = null;
		}
	}

	public YComponentEventHandler<OrderHistoryComponent> getOrderStateSelectEvent()
	{
		return this.ehSelectOrderState;
	}

	public YComponentEventHandler<OrderHistoryComponent> getSortOrderHistoryTableEvent()
	{
		return this.ehSortOrderTable;
	}



	public DataTableAxisModel getOrderHistoryTable()
	{
		if (this.table == null)
		{
			this.table = createOrderHistoryTable();
		}
		return this.table;
	}


	public SfSelectItemGroup getOrderStateSelector()
	{
		if (this.orderStateSelector == null)
		{
			this.orderStateSelector = createOrderStateSelector();
		}
		return this.orderStateSelector;
	}



	private DataTableAxisModel createOrderHistoryTable()
	{
		final List<OrderModel> orderList = getOrderList();
		DataTableAxisModel tableView = null;

		if (orderList != null && !orderList.isEmpty())
		{
			//create tablemodel (one column) based on orderlist
			final DataTableModel tableModel = new ColumnCollectionDataTableModel(getOrderList());

			//create tabelview
			tableView = DataTableFactory.createDataTableAxisModel(tableModel);

			//span four columns based one tablemodel
			final DataTableAxisMarker marker = tableView.getXAxis().getMarkerList().get(0);
			marker.setId(COLUMN_DATE);
			marker.setTitle(LOCALIZATION_DATE);

			tableView.getXAxis().appendMarker(marker, COLUMN_CODE, LOCALIZATION_ORDERID);
			tableView.getXAxis().appendMarker(marker, COLUMN_STATE, LOCALIZATION_STATE, new DataTableCellConverter<String>()
			{
				@Override
				public String getConvertedCellValue(final Object sourceCellValue)
				{
					final OrderStatus status = ((OrderModel) sourceCellValue).getStatus();
					final String result = (status != null) ? YStorefoundation.getRequestContext().getPlatformServices()
							.getTypeService().getEnumerationValue(status).getName() : "";
					return result;
				}
			});
			tableView.getXAxis().appendMarker(marker, COLUMN_PRICE, LOCALIZATION_PRICE);
		}

		return tableView;
	}

	private SfSelectItemGroup createOrderStateSelector()
	{
		final SfRequestContext reqCtx = YStorefoundation.getRequestContext();

		final TypeService ts = reqCtx.getPlatformServices().getTypeService();
		final Collection<EnumerationValueModel> orderStates = (Collection) ts.getEnumerationType(AbstractOrder.ORDER_STATUS_TYPE)
				.getValues();

		final SfSelectItemGroup<EnumerationValueModel> result = new SfSelectItemGroup<EnumerationValueModel>();

		result.addSelectItem(new SfSelectItem(null, reqCtx.getContentManagement().getLocalizedMessage(LOCALIZATION_ALL)));
		for (final EnumerationValueModel value : orderStates)
		{
			result.addSelectItem(new SfSelectItem(value, value.getName()));
		}
		result.setSelectedValue(this.getOrderState());
		return result;
	}



}
