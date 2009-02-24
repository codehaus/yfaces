package de.hybris.yfaces.selenium;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Selenium filter which prepares and provides various stuff for various test operations.
 * 
 * @author Denny.Strietzbaum
 * 
 */
public class SeleniumFilter implements Filter {

	private static final String SESSIONKEY_COUNTER = "REQUEST_COUNTER";
	private static final String QPARAM_CLEARCOUNTER = "clearCounter";

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {

		this.setCounter((HttpServletRequest) arg0);
		arg2.doFilter(arg0, arg1);
	}

	public void init(FilterConfig arg0) throws ServletException {

	}

	private void setCounter(HttpServletRequest req) {
		Integer count = (Integer) req.getSession().getAttribute(SESSIONKEY_COUNTER);
		boolean reset = req.getParameter(QPARAM_CLEARCOUNTER) != null;
		if (count == null || reset) {
			count = Integer.valueOf(0);
		}
		req.getSession().setAttribute(SESSIONKEY_COUNTER, count + 1);
	}

}
