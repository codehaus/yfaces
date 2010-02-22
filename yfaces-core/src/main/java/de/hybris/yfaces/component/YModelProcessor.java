package de.hybris.yfaces.component;

import org.apache.log4j.Logger;

public class YModelProcessor extends PojoModelProcessor<YComponent> {

	private static final Logger log = Logger.getLogger(YModelProcessor.class);

	public YModelProcessor(final YComponentInfoImpl cmpInfo) {
		super(cmpInfo);
	}

	@Override
	public YComponent createModel() {
		return super.createModel();
	}

	@Override
	public void initializeModel(final YComponent cmp) {
		((AbstractYComponent) cmp).setYComponentInfo(cmpInfo);
	}

	@Override
	public void validateModel(final YComponent model) {
		model.validate();
	}

}
