package org.codehaus.yfaces;

import javax.servlet.ServletRequest;

import org.codehaus.yfaces.context.YRequestContext;


/**
 * @author Denny Strietzbaum
 */
public interface YRequestContextBuilder {

	static final String YREQUEST_CONTEXT_BUILDER = "ycontext-builder";

	YRequestContext buildYRequestContext(ServletRequest request);

}
