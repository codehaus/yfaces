package de.hybris.platform.core;

import org.springframework.context.ApplicationContext;

public class Registry {
	private static ApplicationContext ctx = null;

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	public static void setApplicationContext(ApplicationContext ctx) {
		Registry.ctx = ctx;
	}

}
