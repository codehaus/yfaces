package de.hybris.yfaces.demo.chapter2;

import de.hybris.yfaces.component.YComponentBinding;

public class Chapter2Bean {

	private YComponentBinding<Demo2Cmp> cmp1 = null;
	private YComponentBinding<Demo2Cmp> cmp2 = new YComponentBinding<Demo2Cmp>();
	private Demo2Cmp cmp3 = null;

	public Chapter2Bean() {
		// cmp2 = super.c this.component = new YComponentBinding<Demo2Cmp>();
	}

	public YComponentBinding<Demo2Cmp> getCmp1() {
		return cmp1;
	}

	public void setCmp1(YComponentBinding<Demo2Cmp> component) {
		this.cmp1 = component;
	}

	public YComponentBinding<Demo2Cmp> getCmp2() {
		return cmp2;
	}

	public Demo2Cmp getCmp3() {
		return cmp3;
	}

	public void setCmp3(Demo2Cmp cmp3) {
		this.cmp3 = cmp3;
	}

}
