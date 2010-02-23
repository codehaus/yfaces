package org.codehaus.yfaces.component;

import org.apache.log4j.Logger;

public class YModelProcessor extends PojoModelProcessor<YModel> {

	private static final Logger log = Logger.getLogger(YModelProcessor.class);

	public YModelProcessor(final YComponentInfoImpl cmpInfo) {
		super(cmpInfo);
	}

	@Override
	public YModel createModel() {
		return super.createModel();
	}

	@Override
	public void initializeModel(final YModel cmp) {
		((AbstractYModel) cmp).setYComponentInfo(cmpInfo);
	}

	@Override
	public void validateModel(final YModel model) {
		model.validate();
	}

}
