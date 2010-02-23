package org.codehaus.yfaces.component;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.YFrameRegistry.YFrameContext;

public class YModelProcessor extends PojoModelProcessor<YModel> {

	private static final Logger log = Logger.getLogger(YModelProcessor.class);

	public YModelProcessor(final YComponentImpl cmpInfo) {
		super(cmpInfo);
	}

	@Override
	public YModel createModel() {
		return super.createModel();
	}

	@Override
	public void validateModel(final YModel model) {
		model.validate();
	}

	@Override
	public void setYComponent(final YModel model) {
		final AbstractYModel amodel = (AbstractYModel) model;
		amodel.setYComponent(cmpInfo);
	}

	@Override
	public void setYFrame(final YModel model, final YFrame frame, final String frameProperty) {
		final AbstractYModel amodel = (AbstractYModel) model;
		if (amodel.getFrameBinding() == null && frame != null) {
			final YFrameContext frameCtx = YFrameRegistry.getInstance().getFrameContext(frame.getClass());
			amodel.setFrame("#{" + frameCtx.getBeanId() + "}");

			((AbstractYFrame) frame).addYModel(frameProperty, model);

			if (log.isDebugEnabled()) {
				final String bind = frameCtx.getBeanId() + "." + frameProperty;
				log.debug("Resolved framebinding for model " + model.getClass().getSimpleName() + ": #{"
						+ bind + "}");
			}
		}
	}

}
