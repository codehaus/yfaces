package org.codehaus.yfaces;

import javax.servlet.ServletRequest;

import org.codehaus.yfaces.context.YRequestContext;

/**
 * @author Denny Strietzbaum
 */
public interface YRequestContextBuilder {

	YRequestContext buildYRequestContext(ServletRequest request);

}
