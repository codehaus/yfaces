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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;
import org.codehaus.yfaces.component.DefaultYEventListener;
import org.codehaus.yfaces.component.YEvent;
import org.codehaus.yfaces.component.YEventHandler;

import ystorefoundationpackage.domain.SfSessionContext;
import ystorefoundationpackage.domain.YStorefoundation;
import ystorefoundationpackage.domain.impl.JaloBridge;
import ystorefoundationpackage.yfaces.frame.SummaryFrame;
import de.hybris.platform.jalo.order.price.JaloPriceFactoryException;
import de.hybris.platform.voucher.VoucherService;
import de.hybris.platform.voucher.model.VoucherModel;

/**
 * Implementation of the <code>VoucherComponent</code> interface.
 */
public class DefaultVoucherComponent extends AbstractYModel implements
		VoucherComponent {

	private static final long serialVersionUID = 1883457799742145232L;

	private String voucherCode = null;

	private YEventHandler<VoucherComponent> ehRedeem = null;
	private YEventHandler<VoucherComponent> ehRelease = null;

	// default constructor
	public DefaultVoucherComponent() {
		super();
		this.ehRedeem = super.createEventHandler(new RedeemVoucherAction());
		this.ehRelease = super.createEventHandler(new ReleaseVoucherAction());
	}

	public YEventHandler<VoucherComponent> getRedeemVoucherEvent() {
		return this.ehRedeem;
	}

	public YEventHandler<VoucherComponent> getReleaseVoucherEvent() {
		return this.ehRelease;
	}

	/**
	 * This event gets fired when the user tries to redeem a voucher.
	 */
	public static class RedeemVoucherAction extends
			DefaultYEventListener<VoucherComponent> {

		private static final long serialVersionUID = -6798481013719721264L;

		@Override
		public void actionListener(final YEvent<VoucherComponent> event) {
			final VoucherComponent cmp = event.getComponent();
			final SfSessionContext userSession = YStorefoundation
					.getRequestContext().getSessionContext();

			final String redeemVoucherCode = cmp.getVoucherCode();
			final String redeemResult = JaloBridge.getInstance().redeemVoucher(
					userSession.getCart(), redeemVoucherCode);

			final SummaryFrame ss = (SummaryFrame) cmp.getFrame();
			if (redeemResult != null) {
				ss.getVoucherComponent().setVoucherCode(redeemVoucherCode);
				userSession.getMessages().pushInfoMessage(redeemResult,
						redeemVoucherCode);
			} else {
				ss.getVoucherComponent().setVoucherCode(null);
				// userSession.getPropertyChangeLog().setPropertyChanged(SfUserSession.CART,
				// true);
				userSession.getPropertyHandler().setPropertyChanged(
						SfSessionContext.CART, true);
			}
		}
	}

	/**
	 * This event gets fired when the user tries to release a redeemed voucher.
	 */
	public static class ReleaseVoucherAction extends
			DefaultYEventListener<VoucherComponent> {

		private static final Logger log = Logger
				.getLogger(ReleaseVoucherAction.class);

		private static final long serialVersionUID = -3006112371875768981L;

		@Override
		public void actionListener(final YEvent<VoucherComponent> event) {
			final String voucherCode = (String) event.getFacesEvent()
					.getComponent().getAttributes().get(ATTRIB_SELECT_VOUCHER);
			final SfSessionContext userSession = YStorefoundation
					.getRequestContext().getSessionContext();
			try {
				YStorefoundation.getRequestContext().getPlatformServices()
						.getVoucherService().releaseVoucher(voucherCode,
								userSession.getCart());
			} catch (final JaloPriceFactoryException e) {
				log.error("Error when releasing voucher: " + e.getMessage());
				userSession.getMessages().pushInfoMessage("error");
			}

		}
	}

	public String getVoucherCode() {
		return this.voucherCode;
	}

	public void setVoucherCode(final String voucherCode) {
		this.voucherCode = voucherCode;
	}

	public DataModel getAppliedVouchers() {
		final VoucherService voucherService = YStorefoundation
				.getRequestContext().getPlatformServices().getVoucherService();
		final Collection<String> codes = voucherService
				.getAppliedVoucherCodes(YStorefoundation.getRequestContext()
						.getSessionContext().getCart());
		if (codes.isEmpty()) {
			return null;
		} else {
			final List<VoucherModel> vouchers = new ArrayList<VoucherModel>();
			for (final String code : codes) {
				final VoucherModel vm = voucherService.getVoucher(code);
				vm.setCode(code);
				vouchers.add(vm);
			}
			return new ArrayDataModel(vouchers.toArray());
		}
	}
}
