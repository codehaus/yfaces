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


import javax.faces.model.DataModel;

import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YEventHandler;


/**
 * The user can redeem or release the vouchers with this component.
 */
public interface VoucherComponent extends YModel
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
	public YEventHandler<VoucherComponent> getRedeemVoucherEvent();

	/**
	 * Returns the event handler for the release voucher event
	 * 
	 * @return the event handler for the release voucher event
	 */
	public YEventHandler<VoucherComponent> getReleaseVoucherEvent();

}
