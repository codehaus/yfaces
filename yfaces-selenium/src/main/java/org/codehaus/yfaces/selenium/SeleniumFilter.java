package org.codehaus.yfaces.selenium;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.yfaces.YFacesConfig;


/**
 * Selenium filter which prepares and provides various stuff for various test operations.
 * 
 * @author Denny.Strietzbaum
 * 
 */
public class SeleniumFilter implements Filter {

	private static final String SESSIONKEY_COUNTER = "REQUEST_COUNTER";
	private static final String QPARAM_CLEARCOUNTER = "clearCounter";

	public void init(final FilterConfig arg0) throws ServletException {

	}

	public void destroy() {

	}

	public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain arg2)
			throws IOException, ServletException {

		this.setCounter((HttpServletRequest) req);

		// change config setting 
		final String cfgKey = (String) req.getParameterMap().get("cfgkey");
		Object oldCfgValue = null;
		if (cfgKey != null) {
			oldCfgValue = YFacesConfig.getValue(cfgKey);
			final String newCfgValue = (String) req.getParameterMap().get("cfgValue");
			YFacesConfig.setValue(cfgKey, newCfgValue);
		}

		arg2.doFilter(req, resp);

		// restore config setting 
		if (cfgKey != null) {
			YFacesConfig.setValue(cfgKey, oldCfgValue);
		}

	}

	private void setCounter(final HttpServletRequest req) {
		Integer count = (Integer) req.getSession().getAttribute(SESSIONKEY_COUNTER);
		final boolean reset = req.getParameter(QPARAM_CLEARCOUNTER) != null;
		if (count == null || reset) {
			count = Integer.valueOf(0);
		}
		req.getSession().setAttribute(SESSIONKEY_COUNTER, Integer.valueOf(count.intValue() + 1));
	}

}
