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

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentEvent;
import de.hybris.yfaces.component.YComponentEventListener;
import de.hybris.yfaces.context.YConversationContext;
import de.hybris.yfaces.context.YPageContext;

import org.apache.log4j.Logger;

import ystorefoundationpackage.YComponent;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.yfaces.component.address.EditAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ListAddressComponent;
import ystorefoundationpackage.yfaces.component.address.ShowAddressComponent;


/**
 * Renders all addresses of the user as a list.
 * 
 */
public class AddressListFrame extends AbstractYFrame
{
	private static final long serialVersionUID = 1173030385751555763L;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(AddressListFrame.class);

	private static final String NAV_ADDRESS_EDIT = "addressEditPage";
	private static final String NAV_ADDRESS_LIST = "addressListPage";

	private YComponentBinding<ListAddressComponent> listAddressCmp = null;

	public AddressListFrame()
	{
		super();
		this.listAddressCmp = super.createComponentBinding(YComponent.LIST_ADDRESS.viewId);

		this.configureListAddressComponent(this.listAddressCmp.getValue());
	}

	/**
	 * @return {@link YComponentBinding} for {@link ListAddressComponent}
	 */
	public YComponentBinding<ListAddressComponent> getListAddressComponent()
	{
		return this.listAddressCmp;
	}

	/**
	 * External {@link YComponentEventListener} for {@link ShowAddressComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doEditAddress(final YComponentEvent<ShowAddressComponent> event)
	{
		//retrieve Address which shall be edited
		final ShowAddressComponent cmp = event.getComponent();
		final AddressModel address = cmp.getAddress();

		//retrieve EditAddressComponent from AddressEditFrame...
		final EditAddressComponent editCmp = getAddressEditFrame().getEditAddressComponent().getValue();

		//...and set address
		editCmp.setAddress(address);
		//...and set action to addresslist page
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_ADDRESS_LIST);
	}

	/**
	 * External {@link YComponentEventListener} for {@link ListAddressComponent}
	 * 
	 * @param event
	 *           {@link YComponentEvent}
	 */
	public void doCreateAddress(final YComponentEvent<ListAddressComponent> event)
	{
		final EditAddressComponent editCmp = getAddressEditFrame().getEditAddressComponent().getValue();
		editCmp.getSaveAddressEvent().getListener().setAction(NAV_ADDRESS_LIST);
		this.getListAddressComponent().getValue().setAddressList(null);
	}

	/**
	 * Configures a {@link ListAddressComponent} for this frame.
	 */
	private void configureListAddressComponent(final ListAddressComponent listAddressCmp)
	{
		final ShowAddressComponent showAddressCmp = listAddressCmp.getShowAddressComponentTemplate();
		final YComponentEventListener<ShowAddressComponent> showAdrListener = showAddressCmp.getEditAddressEvent().getListener();
		showAdrListener.setAction(NAV_ADDRESS_EDIT);
		showAdrListener.setActionListener(super.createExpressionString("doEditAddress"));

		final YComponentEventListener<ListAddressComponent> crAdrListener = listAddressCmp.getCreateAddressEvent().getListener();
		crAdrListener.setAction(NAV_ADDRESS_EDIT);
		crAdrListener.setActionListener(super.createExpressionString("doCreateAddress"));
	}

	/**
	 * @return {@link AddressEditFrame}
	 */
	private AddressEditFrame getAddressEditFrame()
	{
		final YConversationContext conversationCtx = YStorefoundation.getRequestContext().getPageContext().getConversationContext();
		final YPageContext nextPage = conversationCtx.getOrCreateNextPage();
		final AddressEditFrame result = nextPage.getOrCreateFrame(AddressEditFrame.class);
		return result;
	}

}
