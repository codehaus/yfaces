package de.hybris.yfaces.application;

import java.util.Map;

import javax.faces.context.FacesContext;

public class RequestCycleImpl implements RequestCycle {

	private static final String IS_FLASHBACK = RequestCycle.class.getName() + "_isFlashback";

	public enum REQUEST_PHASE {
		START_REQUEST, FORWARD_REQUEST, END_REQUEST
	};

	private REQUEST_PHASE currentPhase = REQUEST_PHASE.END_REQUEST;

	public boolean isFlashback() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestMap().containsKey(
				IS_FLASHBACK);
	}

	public boolean isFlashbackEnabled() {
		return isFlashback();
	}

	public void setFlashbackEnabled() {
		FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(IS_FLASHBACK,
				Boolean.TRUE);
	}

	public boolean isPostback() {
		// JSF postback: true only when a JSF form was submitted
		// (when requestmap contains a javax.faces.ViewState parameter)
		return FacesContext.getCurrentInstance().getRenderKit().getResponseStateManager()
				.isPostback(FacesContext.getCurrentInstance());
	}

	public void startPageRequest(String viewid) {
		// TODO Auto-generated method stub

	}

	public void finishPageRequest(String viewid) {
		// TODO Auto-generated method stub

	}

	/**
	 * Returns a URI starting with a slash and relative to the webapps context root for the
	 * requested view.
	 * 
	 * @param viewId
	 *            view to generate the URL for
	 * @return String
	 */
	private String getViewURL(final String viewId, final boolean addCurrentQueryParams) {
		final FacesContext fc = FacesContext.getCurrentInstance();

		// request view url but without context path
		String result2 = fc.getApplication().getViewHandler().getActionURL(fc, viewId);
		result2 = result2.substring(fc.getExternalContext().getRequestContextPath().length());

		// optional append a query parameter string
		// HttpServletRequest#getQueryString isn't used here as it is not
		// available in an portlet
		// environment and, more important, may return an incorrect string when
		// urlrewriting is used.
		if (addCurrentQueryParams) {
			final Map<String, String[]> values = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterValuesMap();
			if (!values.isEmpty()) {
				String params = "?";
				for (final Map.Entry<String, String[]> entry : values.entrySet()) {
					for (final String value : entry.getValue()) {
						params = params + entry.getKey() + "=" + value + ";";
					}
				}
				result2 = result2 + params.substring(0, params.length() - 1);
			}
		}

		return result2;
	}

}
