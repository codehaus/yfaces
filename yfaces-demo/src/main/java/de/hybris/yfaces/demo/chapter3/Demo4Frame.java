package de.hybris.yfaces.demo.chapter3;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

public class Demo4Frame extends AbstractYFrame {

	private YComponentBinding<Demo3Component> cmp1 = null;
	private YComponentBinding<Demo3Component> cmp2 = null;
	private YComponentBinding<Demo3Component> cmp3 = null;
	private YComponentBinding<Demo3Component> cmp4 = null;

	public Demo4Frame() {

		//the same as no binding at all
		this.cmp1 = null;

		//empty binding
		//an instance gets injected when component is rendered
		//injected instance is returned next time 
		this.cmp2 = createComponentBinding();

		//initialized binding 
		//with concrete instance
		Demo3Component demoCmp = new Demo3Component();
		demoCmp.setMessage("Component was created by Frame");
		this.cmp3 = createComponentBinding(demoCmp);

		//initialize binding 
		//indirectly with component id
		this.cmp4 = createComponentBinding("frameDemoCmp");
	}

	public YComponentBinding<Demo3Component> getComponent1() {
		return this.cmp1;
	}

	public YComponentBinding<Demo3Component> getComponent2() {
		return this.cmp2;
	}

	public YComponentBinding<Demo3Component> getComponent3() {
		return this.cmp3;
	}

}
