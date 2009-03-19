package de.hybris.yfaces.application;

import org.springframework.context.ApplicationContext;

public class YApplicationContext {

	private static ApplicationContext appCtx = null;

	static void setApplicationContext(ApplicationContext ctx) {
		appCtx = ctx;
	}

	public static ApplicationContext getApplicationContext() {
		return appCtx;
	}

}
