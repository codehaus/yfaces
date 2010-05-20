package org.codehaus.yfaces.selenium.frame;

import org.codehaus.yfaces.YFaces;
import org.codehaus.yfaces.component.AbstractYComponentContainer;
import org.codehaus.yfaces.component.YComponentHandler;
import org.codehaus.yfaces.selenium.component.TestComponent;
import org.codehaus.yfaces.selenium.component.TestErrorHandlingCmp;

public class TestCmpContainer extends AbstractYComponentContainer {

	private TestErrorHandlingCmp testErrorHandlingCmp = null;
	private TestComponent testComponent1 = null;
	private TestComponent testComponent2 = null;
	private TestComponent testComponent3 = null;
	private TestComponent testComponent4 = null;

	public TestCmpContainer() {
		// create component via COmponentRegistry/COmponentInfo
		// this assures that created component type matches the configured one of component view
		final YComponentHandler cmpInfo = YFaces.getApplicationContext().getComponentHandlers()
				.getComponent("testComponent");
		this.testComponent1 = (TestComponent) cmpInfo.createModel();
		this.testComponent1.setValue("component1");

		// create component with constructor
		// this doesn't care about whether type matches component view
		this.testComponent2 = new TestComponent();
		this.testComponent2.setValue("component2");

		// create component implicitly by YFaces
		this.testComponent3 = null;

		this.testComponent4 = null;

	}

	public TestErrorHandlingCmp getTestErrorHandlingCmp() {
		return testErrorHandlingCmp;
	}

	public void setTestErrorHandlingCmp(final TestErrorHandlingCmp testErrorHandlingCmp) {
		this.testErrorHandlingCmp = testErrorHandlingCmp;
	}

	public TestComponent getTestComponent1() {
		return testComponent1;
	}

	public void setTestComponent1(final TestComponent testComponent1) {
		this.testComponent1 = testComponent1;
	}

	public TestComponent getTestComponent2() {
		return testComponent2;
	}

	public void setTestComponent2(final TestComponent testComponent2) {
		this.testComponent2 = testComponent2;
	}

	public TestComponent getTestComponent3() {
		return testComponent3;
	}

	public void setTestComponent3(final TestComponent testComponent3) {
		this.testComponent3 = testComponent3;
	}

	public TestComponent getTestComponent4() {
		return testComponent4;
	}

	public void setTestComponent4(final TestComponent testComponent4) {
		this.testComponent4 = testComponent4;
	}

}
