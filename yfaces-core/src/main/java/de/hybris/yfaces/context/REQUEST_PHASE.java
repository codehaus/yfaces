package de.hybris.yfaces.context;

public enum REQUEST_PHASE {
	/**
	 * Gets activated when RESTORE_VIEW is finished
	 * 
	 * @author Denny Strietzbaum
	 */
	START_REQUEST,

	/**
	 * Gets never activated for a general GET request but under one of two circumstances:
	 * <ul>
	 * <li>with start of RENDER_RESPONSE and when request method is POST</li>
	 * <li>with ending if RESTORE_VIEW _and_ request method is GET _and_ flashback is enabled</li>
	 * </ul>
	 */
	FORWARD_REQUEST,

	/**
	 * Gets activated when RENDER_RESPONSE is finished
	 */
	END_REQUEST
};
