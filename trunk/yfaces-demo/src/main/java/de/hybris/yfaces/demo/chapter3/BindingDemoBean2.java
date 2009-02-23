package de.hybris.yfaces.demo.chapter3;

public class BindingDemoBean2 {

	private DemoCmp component = null;

	/**
	 * Sets the component.
	 * 
	 * @param cmp3
	 *            component to set
	 */
	public void setComponent(DemoCmp cmp3) {
		this.component = cmp3;
	}

	/**
	 * Returns the component. A component instance was either set programmatically or initially
	 * created and set by JSF.
	 * 
	 * @return {@link DemoCmp}
	 */
	public DemoCmp getComponent() {
		return this.component;
	}

}
