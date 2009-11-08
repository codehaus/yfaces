package de.hybris.yfaces;

import de.hybris.yfaces.context.YRequestContext;

public class YFaces {

	private static ThreadLocal<YRequestContext> localCtx = new ThreadLocal<YRequestContext>();

	public static YRequestContext getRequestContext() {
		return localCtx.get();
		//		return (YRequestContext) YApplicationContext.getApplicationContext().getBean(
		//				YRequestContext.class.getSimpleName());
	}

	static void setRequestContext(YRequestContext ctx) {
		localCtx.set(ctx);
	}

	static void removeRequestContext() {
		localCtx.remove();
	}
}
