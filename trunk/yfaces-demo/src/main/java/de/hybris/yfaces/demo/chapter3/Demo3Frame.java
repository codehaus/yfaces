package de.hybris.yfaces.demo.chapter3;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

public class Demo3Frame extends AbstractYFrame {

	private YComponentBinding<Demo3Cmp> cmp1 = null;
	private YComponentBinding<Demo3Cmp> cmp2 = null;
	private YComponentBinding<Demo3Cmp> cmp3 = null;
	private YComponentBinding<Demo3Cmp> cmp4 = null;

	public Demo3Frame() {

		cmp1 = createComponentBinding();
		cmp2 = createComponentBinding();
		cmp3 = createComponentBinding();
		cmp4 = createComponentBinding();
	}

	public YComponentBinding<Demo3Cmp> getComponent1() {
		return this.cmp1;
	}

	public YComponentBinding<Demo3Cmp> getComponent2() {
		return this.cmp2;
	}

	public YComponentBinding<Demo3Cmp> getComponent3() {
		return this.cmp3;
	}

	public YComponentBinding<Demo3Cmp> getComponent4() {
		return this.cmp4;
	}

}
