package de.hybris.yfaces.demo.chapter3x;

import de.hybris.yfaces.component.AbstractYFrame;
import de.hybris.yfaces.component.YComponentBinding;

public class Demo4Frame extends AbstractYFrame {

	private YComponentBinding<DemoCmp> cmp1 = null;
	private YComponentBinding<DemoCmp> cmp2 = null;
	private YComponentBinding<DemoCmp> cmp3 = null;
	private YComponentBinding<DemoCmp> cmp4 = null;

	public Demo4Frame() {

		//the same as no binding at all
		this.cmp1 = null;

		//empty binding
		//an instance gets injected when component is rendered
		//injected instance is returned next time 
		this.cmp2 = createComponentBinding();

		//initialized binding 
		//with concrete instance
		DemoCmp demoCmp = new DemoCmp();
		demoCmp.setMessage("Component was created by Frame");
		this.cmp3 = createComponentBinding(demoCmp);

		//initialize binding 
		//indirectly with component id
		this.cmp4 = createComponentBinding("frameDemoCmp");
	}

	public YComponentBinding<DemoCmp> getComponent1() {
		return this.cmp1;
	}

	public YComponentBinding<DemoCmp> getComponent2() {
		return this.cmp2;
	}

	public YComponentBinding<DemoCmp> getComponent3() {
		return this.cmp3;
	}

}
