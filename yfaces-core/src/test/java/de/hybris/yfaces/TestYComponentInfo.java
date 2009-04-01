/**
 * 
 */
package de.hybris.yfaces;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.hybris.yfaces.component.YComponentFactory;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YComponentRegistry;
import de.hybris.yfaces.component.YComponentInfo.ERROR_STATE;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class TestYComponentInfo extends TestCase {

	private static final Logger log = Logger.getLogger(TestYComponentInfo.class);

	private static final String TEST_COMPONENT = YTestComponent.class.getName();
	private static final String TEST_COMPONENT_IMPL = YTestComponentImpl.class.getName();

	private class AddSingleYComponentTest {
		private String componentFile = null;
		private YComponentRegistry registry = null;
		private YComponentFactory cmpFac = new YComponentFactory();

		private boolean expectedToAdd = false;
		private Set<YComponentInfo.ERROR_STATE> expectedErrors = Collections.emptySet();
		private String expSpecClassName = null;
		private String expImplClassName = null;
		private String expId = null;
		private String expVar = null;
		private Collection<String> expInjectableAttributes = Collections.emptySet();

		private YComponentInfo cmpInfo = null;
		private boolean wasAdded = false;

		public AddSingleYComponentTest(YComponentRegistry registry, String file, boolean mustBeAdded) {
			this.componentFile = file;
			this.expectedToAdd = mustBeAdded;
			this.registry = registry;
		}

		public void setExpectedErrors(YComponentInfo.ERROR_STATE... errors) {
			this.expectedErrors = new HashSet<ERROR_STATE>(Arrays.asList(errors));
		}

		public void run() {
			URL url = null;
			ClassLoader loader = TestYComponentInfo.class.getClassLoader();
			url = loader.getResource("testYComponentInfo/" + componentFile);

			log.info("Now processing: " + componentFile + " ...");
			log.info("Expected errors: " + this.expectedErrors);

			//registry.processURL(url, this);
			this.cmpInfo = cmpFac.createComponentInfo(url, null);
			this.wasAdded = registry.addComponent(cmpInfo);

			if (this.wasAdded) {
				log.info("...added");
				assertTrue("Component was added but must be skipped:", expectedToAdd);
			} else {
				log.info("...skipped");
				assertFalse("Component was skipped but must be added:", expectedToAdd);
			}

			if (this.cmpInfo == null) {
				assertNull(this.expectedErrors);
			} else {
				assertEquals(this.expSpecClassName, cmpInfo.getSpecificationClassName());
				assertEquals(this.expImplClassName, cmpInfo.getImplementationClassName());
				assertEquals(this.expId, cmpInfo.getId());
				assertEquals(this.expVar, cmpInfo.getVarName());
				assertEquals(this.expInjectableAttributes, cmpInfo.getInjectableProperties());
				assertEquals(this.cmpInfo.verifyComponent(), this.expectedErrors);
			}
		}

		//		public void addedYComponent(final YComponentInfo cmpInfo) {
		//			this.wasAdded = true;
		//			this.cmpInfo = cmpInfo;
		//		}
		//
		//		public void skippedYComponent(final URL url, final YComponentInfo cmpInfo) {
		//			this.wasAdded = false;
		//			this.cmpInfo = cmpInfo;
		//		}
	}

	/**
	 * Tests parsing (whitespaces, etc.)
	 */
	public void testComponentInfoParser() {

		// some predefined attribute values
		String spec = "ystorefoundationpackage.yfaces.component.misc.AdBannerComponent";
		String impl = "ystorefoundationpackage.yfaces.component.misc.DefaultAdBannerComponent";
		String var = "adBannerCmpVar";
		String id = "id1";
		Set<String> properties = new HashSet<String>(Arrays.asList("prop1", "prop2", "prop3",
				"prop4"));

		// test various whitespaces (space, tab etc)
		String[] cmps1 = new String[] {
				"<yf:component id=\"id1\" impl=\"" + impl + "\" spec=\"" + spec + "\" var=\"" + var
						+ "\">",
				"<yf:component   id =\"id1\"  impl=\"" + impl + "\"   spec=	\"" + spec
						+ "\"		var=\"" + var + "\" >",
				"<yf:component	id	=	\"id1\"	impl	=	\"" + impl + "\"	spec=\"" + spec + "\"	var	=\""
						+ var + "\" >",
				"<yf:component	id	= \" id1\"	impl=	\"		" + impl + "\"	spec=\"" + spec + "\" var=\""
						+ var + "	\" >", };

		YComponentFactory cmpFac = new YComponentFactory();
		for (String s : cmps1) {
			// System.out.println(count++ + ": " + s);
			YComponentInfo cmpInfo = cmpFac.createComponentInfo(s);
			assertEquals(spec, cmpInfo.getSpecificationClassName());
			assertEquals(impl, cmpInfo.getImplementationClassName());
			assertEquals(var, cmpInfo.getVarName());
			assertEquals(id, cmpInfo.getId());
			assertEquals(0, cmpInfo.getInjectableProperties().size());
		}

		// test 'injectable' properties (boths styles)
		String[] cmps2 = new String[] {
				"<yf:component spec=\"" + impl + "\" injectable=\"prop1,prop2,prop3,prop4\">",
				"<yf:component default=\"" + impl
						+ "\" injectable=\"	prop1 ,prop2	,prop3,	prop4\">",
				"<yf:component spec=\""
						+ impl
						+ "\" prop1=\"#{prop1}\" prop2=\"#{prop2}\" prop3=\"#{prop3}\" prop4=\"#{prop4}\">",
				"<yf:component spec=\""
						+ impl
						+ "\" prop1=\"#{prop1}\" prop2=\"#{prop1}\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component spec=\"" + impl
						+ "\" injectable=\"prop1,prop2\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component spec=\""
						+ impl
						+ "\" injectable=\"prop1,prop2,prop3\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component spec=\"" + impl
						+ "\" injectable=\"prop1,prop2,prop3\" prop4 =	\" #{prop4}	\">", };
		for (String s : cmps2) {
			// System.out.println(count++ + ": " + s);
			YComponentInfo cmpInfo = cmpFac.createComponentInfo(s);
			Collection<String> props = cmpInfo.getInjectableProperties();
			assertEquals(4, props.size());
			assertTrue("Got properties " + props.toString(), props.containsAll(properties));
		}
	}

	/**
	 * Tests various validation errors which can occur. YComponent definition is given as simple
	 * String.
	 */
	public void testYComponentInfoValidation() {

		log.info("---------------------------------");
		log.info("Testing YComponentInfoValidation");
		log.info("---------------------------------");

		String spec = TEST_COMPONENT;
		String impl = TEST_COMPONENT_IMPL;
		String var = "adBannerCmpVar";
		YComponentInfo cmpInfo = null;
		YComponentFactory cmpFac = new YComponentFactory();

		// test various errors
		int count = -1;
		while (++count >= 0) {
			String cmp = null;
			Collection<ERROR_STATE> expected = null;
			switch (count) {
			case 0:
				cmp = "<yf:component spec=\"" + impl + "\" definition=\"" + spec + "\" var=\""
						+ var + "\">";
				expected = Arrays.asList(ERROR_STATE.VIEW_ID_NOT_SPECIFIED);
				break;
			case 1:
				cmp = "<yf:component spec=\"" + impl + "\" >";
				expected = Arrays.asList(ERROR_STATE.VIEW_ID_NOT_SPECIFIED,
						ERROR_STATE.VIEW_VAR_NOT_SPECIFIED, ERROR_STATE.SPEC_IS_MISSING);
				break;
			case 2:
				cmp = "<yf:component id=\"id\" spec=\"java.util.List\" var=\"var\">";
				expected = Arrays.asList(ERROR_STATE.SPEC_IS_MISSING,
						ERROR_STATE.IMPL_IS_INTERFACE, ERROR_STATE.IMPL_IS_NO_YCMP);
				break;
			case 3:
				cmp = "<yf:component id=\"id\" spec=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(ERROR_STATE.SPEC_IS_MISSING, ERROR_STATE.IMPL_IS_NO_YCMP);
				break;
			case 4:
				cmp = "<yf:component id=\"id\" spec=\"java.util.List\" impl=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(ERROR_STATE.SPEC_IS_NO_YCMP, ERROR_STATE.IMPL_IS_NO_YCMP);
				break;
			case 5:
				cmp = "<yf:component id=\"id\" spec=\"java.util.ArrayList\" impl=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(ERROR_STATE.SPEC_IS_NO_INTERFACE,
						ERROR_STATE.SPEC_IS_NO_YCMP, ERROR_STATE.IMPL_IS_NO_YCMP);
				break;
			case 6:
				cmp = "<yf:component id=\"id\" spec=\"java.util.Listxxx\" impl=\"java.util.ArrayListxxx\" var=\"var\">";
				expected = Arrays.asList(ERROR_STATE.SPEC_NOT_LOADABLE,
						ERROR_STATE.IMPL_NOT_LOADABLE);
				break;

			default:
				count = -5;
			}

			if (count >= 0) {
				log.info("Asserting: " + cmp);
				log.info("Expecting: " + expected);
				cmpInfo = cmpFac.createComponentInfo(cmp);
				Set<ERROR_STATE> errors = cmpInfo.verifyComponent();
				assertEquals(new HashSet<ERROR_STATE>(expected), errors);
			}
		}
	}

	/**
	 * Tests {@link YComponentRegistry} and {@link YComponentInfo} validation. Additional does
	 * enhanced text parsing as components are provided as external resources.
	 */
	public void testYComponentRegistry() {

		log.info("---------------------------------");
		log.info("Testing YComponentRegistry");
		log.info("---------------------------------");

		List<AddSingleYComponentTest> tests = new ArrayList<AddSingleYComponentTest>();
		AddSingleYComponentTest test = null;
		final YComponentRegistry reg = new YComponentRegistry();

		// no ycomponent at all; plain facelet tag
		test = new AddSingleYComponentTest(reg, "plainFaceletTag.xhtml", false);
		test.expectedErrors = null;
		tests.add(test);

		test = new AddSingleYComponentTest(reg, "validComponent1Tag.xhtml", true);
		test.setExpectedErrors(ERROR_STATE.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent1";
		test.expVar = "validComponent1Var";
		tests.add(test);

		test = new AddSingleYComponentTest(reg, "validComponent2Tag.xhtml", true);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expSpecClassName = TEST_COMPONENT;
		test.expId = "validComponent2";
		test.expVar = "validComponent2Var";
		tests.add(test);

		test = new AddSingleYComponentTest(reg, "validComponent3Tag.xhtml", true);
		test.setExpectedErrors(ERROR_STATE.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent3";
		test.expVar = "validComponent3Var";
		test.expInjectableAttributes = new HashSet<String>(Arrays.asList("value", "primitiveInt",
				"wrappedInt", "primitiveBoolean", "wrappedBoolean", "enumValue", "stringValue"));
		tests.add(test);

		test = new AddSingleYComponentTest(reg, "validComponent4Tag.xhtml", true);
		test.setExpectedErrors(ERROR_STATE.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent4";
		test.expVar = "validComponent4Var";
		test.expInjectableAttributes = new HashSet<String>(Arrays.asList("primitiveInt",
				"wrappedInt", "primitiveBoolean", "wrappedBoolean", "enumValue", "stringValue"));
		tests.add(test);

		for (AddSingleYComponentTest _test : tests) {
			_test.run();
		}
	}
}
