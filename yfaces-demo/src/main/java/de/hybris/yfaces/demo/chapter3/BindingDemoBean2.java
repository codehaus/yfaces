package de.hybris.yfaces.demo.chapter3;

import de.hybris.yfaces.component.YComponentBinding;

public class BindingDemoBean2 {

	private YComponentBinding<DemoCmp> cmp1 = null;
	private YComponentBinding<DemoCmp> cmp2 = null;

	public BindingDemoBean2() {
		this.cmp1 = new YComponentBinding<DemoCmp>();
	}

	/**
	 * Returns the binding for component1. As there's no setter available the binding must be
	 * available and is not allowed to be null.
	 * 
	 * @return {@link YComponentBinding}
	 */
	public YComponentBinding<DemoCmp> getComponent1() {
		return this.cmp1;
	}

	/**
	 * Returns the binding for component2. As there's a setter available 'null' is allowed as return
	 * value.
	 * 
	 * @return {@link YComponentBinding}
	 */
	public YComponentBinding<DemoCmp> getComponent2() {
		return this.cmp2;
	}

	/**
	 * Sets the binding for component2.
	 * 
	 * @param cmp2
	 *            binding for component2
	 */
	public void setComponent2(YComponentBinding<DemoCmp> cmp2) {
		this.cmp2 = cmp2;
	}

}
