package de.hybris.yfaces;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import de.hybris.yfaces.context.YApplicationContext;
import de.hybris.yfaces.context.YRequestContext;
import de.hybris.yfaces.context.YRequestContextImpl;
import de.hybris.yfaces.context.YSessionContext;
import de.hybris.yfaces.context.YSessionContextImpl;
import de.hybris.yfaces.util.YFacesErrorHandler;

public class DefaultYRequestContextBuilder implements YRequestContextBuilder {

	private static final Logger log = Logger.getLogger(DefaultYRequestContextBuilder.class);

	private static final String YFACES_PROPERTIES_PARAM_NAME = "yfaces-properties";
	private static final String YFACES_PROPERTIES_PARAM_VALUE = "/WEB-INF/yfaces.properties";

	private static final String YFACES_KEY_SUFFIX = "yfaces.";
	private static final String YFACES_REQCTXCLASS_KEY = YFACES_KEY_SUFFIX + "requestcontext.class";
	private static final String YFACES_REQCTXCLASS_VALUE = YRequestContextImpl.class.getName();
	private static final String YFACES_SESSCTXCLASS_KEY = YFACES_KEY_SUFFIX + "sessioncontext.class";
	private static final String YFACES_SESSCTXCLASS_VALUE = YSessionContextImpl.class.getName();
	private static final String YFACES_APPCTXCLASS_KEY = YFACES_KEY_SUFFIX
			+ "applicationcontext.class";
	private static final String YFACES_APPCTXCLASS_VALUE = YApplicationContext.class.getName();

	private Class<YRequestContext> reqCtxClass = null;
	private Class<YSessionContext> sessCtxClass = null;
	private Class<YApplicationContext> appCtxClass = null;

	public YRequestContext buildYRequestContext(final ServletRequest request) {

		YRequestContext result;

		result = getYRequestContext((HttpServletRequest) request);

		return result;
	}

	private void initialize(final HttpServletRequest req) {

		// lookup configuration file or take default one
		String cfgFile = req.getSession().getServletContext().getInitParameter(
				YFACES_PROPERTIES_PARAM_NAME);
		cfgFile = cfgFile != null ? cfgFile.trim() : YFACES_PROPERTIES_PARAM_VALUE;

		// read configuration
		final Properties propCfg = new Properties();
		final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(
				cfgFile);

		if (in != null) {
			try {
				propCfg.load(in);
			} catch (final IOException e) {
				throw new YFacesException(e);
			}
		} else {
			if (cfgFile != YFACES_PROPERTIES_PARAM_VALUE) {
				throw new YFacesException("Can't load configuration " + propCfg);
			} else {
				log
						.debug("No configuration found (" + YFACES_PROPERTIES_PARAM_VALUE
								+ "); taking defaults");
			}
		}

		// evaluate configuration
		this.reqCtxClass = (Class) this.loadClass(propCfg, YFACES_REQCTXCLASS_KEY,
				YFACES_REQCTXCLASS_VALUE);
		log.debug("Using " + this.reqCtxClass.getName() + " for "
				+ YRequestContext.class.getSimpleName());

		this.sessCtxClass = (Class) this.loadClass(propCfg, YFACES_SESSCTXCLASS_KEY,
				YFACES_SESSCTXCLASS_VALUE);
		log.debug("Using " + this.sessCtxClass.getName() + " for "
				+ YSessionContext.class.getSimpleName());

		this.appCtxClass = (Class) this.loadClass(propCfg, YFACES_APPCTXCLASS_KEY,
				YFACES_APPCTXCLASS_VALUE);
		log.debug("Using " + this.appCtxClass.getName() + " for "
				+ YApplicationContext.class.getSimpleName());

	}

	private YRequestContext getYRequestContext(final HttpServletRequest ctx) {

		// read yfaces configuration one times
		if (this.reqCtxClass == null) {
			this.initialize(ctx);
		}

		final YSessionContext session = getYSessionContext(ctx.getSession());

		final YRequestContextImpl result = (YRequestContextImpl) getInstance(this.reqCtxClass);
		result.setErrorHandler(new YFacesErrorHandler());
		result.setSessionContext(session);

		return result;
	}

	private YSessionContext getYSessionContext(final HttpSession ctx) {

		YSessionContext result = (YSessionContext) ctx.getAttribute(YSessionContext.class.getName());

		if (result == null) {
			final YApplicationContext appCtx = getYApplicationContext(ctx.getServletContext());

			result = getInstance(this.sessCtxClass);
			((YSessionContextImpl) result).setYApplicationContext(appCtx);

			ctx.setAttribute(YSessionContext.class.getName(), result);
		}

		return result;
	}

	private YApplicationContext getYApplicationContext(final ServletContext ctx) {

		YApplicationContext result = (YApplicationContext) ctx.getAttribute(YApplicationContext.class
				.getName());

		if (result == null) {
			result = getInstance(this.appCtxClass);
			ctx.setAttribute(YApplicationContext.class.getName(), result);
		}

		return result;
	}

	private <T> T getInstance(final Class<T> clazz) {
		T result;
		try {
			result = clazz.newInstance();
		} catch (final Exception e) {
			throw new YFacesException("Error instantiating class " + clazz, e);
		}
		return result;
	}

	private Class<?> loadClass(final Properties cfg, final String keyName, final String defValue) {
		final String className = cfg.contains(keyName) ? ((String) cfg.get(keyName)).trim() : defValue;
		final Class<?> result = loadClass(className);
		return result;
	}

	private Class<?> loadClass(final String className) {
		Class<?> result;
		try {
			result = Thread.currentThread().getContextClassLoader().loadClass(className.trim());
		} catch (final Exception e) {
			throw new YFacesException("Error loading class " + className, e);
		}
		return result;
	}

}
