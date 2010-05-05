package org.codehaus.yfaces;

import org.codehaus.yfaces.component.YComponentHandlerRegistry;
import org.codehaus.yfaces.context.YRequestContext;

public class YFaces {

	private static ThreadLocal<YRequestContext> localCtx = new ThreadLocal<YRequestContext>();

	public static YRequestContext getRequestContext() {
		return localCtx.get();
	}

	static void setRequestContext(final YRequestContext ctx) {
		localCtx.set(ctx);
	}

	static void removeRequestContext() {
		localCtx.remove();
	}

	// for now a singleton, should be integrated intoe the yfaces-framework as part of application
	private static YComponentHandlerRegistry singleton = new YComponentHandlerRegistry();

	public static YComponentHandlerRegistry getYComponentRegistry() {
		return singleton;
	}

}
