/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hybris.yfaces.myfaces;

import javax.faces.context.FacesContext;

import de.hybris.yfaces.application.YFacesContext;
import de.hybris.yfaces.application.YFacesErrorHandler;

/**
 * This class is meant to be the myfaces error handler which must be declared as
 * deployment parameter in web.xml.<br/>
 * <code>
 * &lt;context-param&gt;<br/>
 * 		&lt;param-name&gt;org.apache.myfaces.ERROR_HANDLER&lt;/param-name&gt;<br/>
 * 		&lt;param-value&gt;de.hybris.yfaces.myfaces.MyFacesErrorHandler&lt;/param-value&gt;<br/>
 * 	&lt;/context-param&gt;<br/>
 * </code> After doing this {@link YFacesErrorHandler} gets invoked whenever an
 * exception gets thrown.
 * <p/>
 * Learn more about the error handling of myfaces here:
 * http://wiki.apache.org/myfaces/Handling_Server_Errors
 * 
 * @author Denny.Strietzbaum
 */
public class MyFacesErrorHandler {
	public void handleException(final FacesContext fc, final Exception ex) {
		YFacesContext.getCurrentContext().getErrorHandler().handleException(fc, ex);
	}

}
