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
	public void setYComponent(final YModel model) {
		((AbstractYModel) model).setYComponent(cmpInfo);
	}

	@Override
	public void validateModel(final YModel model) {
		model.validate();
	}

	@Override
	public void setFrame(final YModel model, final YFrame frame) {
		final YFrameContext frameCtx = YFrameRegistry.getInstance().getFrameContext(frame.getClass());
		((AbstractYModel) model).setFrame("#{" + frameCtx.getBeanId() + "}");
	}
}
