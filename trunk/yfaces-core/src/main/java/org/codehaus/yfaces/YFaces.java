package org.codehaus.yfaces;

import org.codehaus.yfaces.context.YApplicationContext;
import org.codehaus.yfaces.context.YRequestContext;
import org.codehaus.yfaces.context.YSessionContext;

public class YFaces {

	private static ThreadLocal<YRequestContext> localCtx = new ThreadLocal<YRequestContext>();

	public static YRequestContext getRequestContext() {
		return localCtx.get();
	}

	public static YSessionContext getSessionContext() {
		return getRequestContext().getSessionContext();
	}

	public static YApplicationContext getApplicationContext() {
		return getRequestContext().getSessionContext().getApplicationContext();
	}

	static void setRequestContext(final YRequestContext ctx) {
		localCtx.set(ctx);
	}

	static void removeRequestContext() {
		localCtx.remove();
	}

	//	// for now a singleton, should be integrated into the yfaces-framework as part of application
	//	private static YComponentHandlerRegistry singleton = new YComponentHandlerRegistry();
	//
	//	public static YComponentHandlerRegistry getYComponentRegistry() {
	//		return singleton;
	//	}

}
