package de.hybris.yfaces;

import de.hybris.yfaces.context.YApplicationContext;
import de.hybris.yfaces.context.YRequestContext;

public class YFaces {

	public static YRequestContext getCurrentContext() {
		return (YRequestContext) YApplicationContext.getApplicationContext().getBean(
				YRequestContext.class.getSimpleName());
	}
}
