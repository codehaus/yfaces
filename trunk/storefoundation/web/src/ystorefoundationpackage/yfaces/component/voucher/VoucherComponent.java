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
package ystorefoundationpackage.yfaces.component.voucher;

import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentEventHandler;

import javax.faces.model.DataModel;


/**
 * The user can redeem or release the vouchers with this component.
 */
public interface VoucherComponent extends YComponent
{

	public static final String ATTRIB_SELECT_VOUCHER = "selectedVoucher";

	/**
	 * Returns the voucher code
	 * 
	 * @return the voucher code
	 */
	public String getVoucherCode();

	/**
	 * Sets the voucher code
	 * 
	 * @param voucherCode
	 *           the voucher code to be set
	 */
	public void setVoucherCode(String voucherCode);

	/**
	 * Returns all redeemed vouchers for the current cart
	 * 
	 * @return all redeemed vouchers
	 */
	public DataModel getAppliedVouchers();

	/**
	 * Returns the event handler for the redeem voucher event
	 * 
	 * @return the event handler for the redeem voucher event
	 */
	public YComponentEventHandler<VoucherComponent> getRedeemVoucherEvent();

	/**
	 * Returns the event handler for the release voucher event
	 * 
	 * @return the event handler for the release voucher event
	 */
	public YComponentEventHandler<VoucherComponent> getReleaseVoucherEvent();

}