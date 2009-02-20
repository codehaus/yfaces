package de.hybris.yfaces.demo.chapter2;

import de.hybris.yfaces.component.YComponentBinding;

public class Chapter2Bean {

	private YComponentBinding<DemoCmp> cmp1 = null;
	private YComponentBinding<DemoCmp> cmp2 = new YComponentBinding<DemoCmp>();
	private DemoCmp cmp3 = null;

	public Chapter2Bean() {
		// cmp2 = super.c this.component = new YComponentBinding<Demo2Cmp>();
	}

	public YComponentBinding<DemoCmp> getCmp1() {
		return cmp1;
	}

	public void setCmp1(YComponentBinding<DemoCmp> component) {
		this.cmp1 = component;
	}

	public YComponentBinding<DemoCmp> getCmp2() {
		return cmp2;
	}

	public DemoCmp getCmp3() {
		return cmp3;
	}

	public void setCmp3(DemoCmp cmp3) {
		this.cmp3 = cmp3;
	}

}
