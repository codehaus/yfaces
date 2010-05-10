package org.codehaus.yfaces;

import org.codehaus.yfaces.context.YApplicationContext;
import org.codehaus.yfaces.context.YConversationContext;
import org.codehaus.yfaces.context.YPageContext;
import org.codehaus.yfaces.context.YRequestContext;
import org.codehaus.yfaces.context.YSessionContext;

public class YFacesBean {

	public YRequestContext getRequestContext() {
		return YFaces.getRequestContext();
	}

	public YSessionContext getSessionContext() {
		return YFaces.getSessionContext();
	}

	public YApplicationContext getApplicationContext() {
		return YFaces.getApplicationContext();
	}

	public YConversationContext getCOnversationContext() {
		return YFaces.getSessionContext().getConversationContext();
	}

	public YPageContext getPageContext() {
		return YFaces.getRequestContext().getPageContext();
	}

}
