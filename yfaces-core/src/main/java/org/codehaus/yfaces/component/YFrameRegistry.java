package org.codehaus.yfaces.component;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class YFrameRegistry {

	private static final Logger log = Logger.getLogger(YFrameRegistry.class);

	public static class YFrameContext {
		Class frameClass = null;
		boolean isResolved = false;
		String mbeanId = null;

		public YFrameContext(final Class clazz) {
			this.frameClass = clazz;
		}

		public void setBeanId(final String id) {
			this.mbeanId = id;
			this.isResolved = true;
			log.debug("Registering ManagedBean " + frameClass.getName() + " with id " + id);
		}

		public String getBeanId() {
			return this.mbeanId;
		}

		public boolean isResolved() {
			return isResolved;
		}
	}

	static Map<Class, YFrameContext> frameCtxMap = new HashMap<Class, YFrameContext>();
	static YFrameRegistry singleton = new YFrameRegistry();

	public static YFrameRegistry getInstance() {
		return singleton;
	}

	public YFrameContext getFrameContext(final Class frameClass) {
		YFrameContext result = frameCtxMap.get(frameClass);
		if (result == null) {
			result = new YFrameContext(frameClass);
			frameCtxMap.put(frameClass, result);
		}
		return result;
	}

}
