package de.hybris.yfaces;

import javax.servlet.ServletRequest;

import de.hybris.yfaces.context.YRequestContext;

/**
 * @author Denny Strietzbaum
 */
public interface YRequestContextBuilder {

	static final String YREQUEST_CONTEXT_BUILDER = "ycontext-builder";

	YRequestContext buildYRequestContext(ServletRequest request);

}
