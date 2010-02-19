/**
 * 
 */
package de.hybris.yfaces;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import de.hybris.yfaces.component.DefaultYComponentInfo;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YComponentInfoFactory;
import de.hybris.yfaces.component.YComponentRegistry;
import de.hybris.yfaces.component.YComponentValidator.YValidationAspekt;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class TestYComponentInfo extends TestCase {

	private static final Logger log = Logger.getLogger(TestYComponentInfo.class);

	private static final String TEST_COMPONENT = YTestComponent.class.getName();
	private static final String TEST_COMPONENT_IMPL = YTestComponentImpl.class.getName();

	private static final String HEAD = "<ui:composition xmlns:yf=\"http://yfaces.codehaus.org/taglib\">\n\n";

	private class AddSingleYComponentTest {
		private String componentFile = null;
		private YComponentRegistry registry = null;
		private final YComponentInfoFactory cmpFac = new YComponentInfoFactory();

		private boolean expectedToAdd = false;
		private Set<YValidationAspekt> expectedErrors = Collections.emptySet();
		private String expSpecClassName = null;
		private String expImplClassName = null;
		private String expId = null;
		private String expVar = null;
		private Collection<String> expInjectableAttributes = Collections.emptySet();

		private YComponentInfo cmpInfo = null;
		private boolean wasAdded = false;

		public AddSingleYComponentTest(final YComponentRegistry registry, final String file,
				final boolean mustBeAdded) {
			this.componentFile = file;
			this.expectedToAdd = mustBeAdded;
			this.registry = registry;
		}

		public void setExpectedErrors(final YValidationAspekt... errors) {
			this.expectedErrors = new HashSet<YValidationAspekt>(Arrays.asList(errors));
		}

		public void run() {
			URL url = null;
			final ClassLoader loader = TestYComponentInfo.class.getClassLoader();
			url = loader.getResource("testYComponentInfo/" + componentFile);

			log.info("Now processing: " + componentFile + " ...");
			log.info("Expected errors: " + this.expectedErrors);

			//registry.processURL(url, this);
			this.cmpInfo = cmpFac.createComponentInfo(url, "some.namespace");
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
				assertEquals(this.expSpecClassName, cmpInfo.getSpecification());
				assertEquals(this.expImplClassName, cmpInfo.getImplementation());
				assertEquals(this.expId, cmpInfo.getId());
				assertEquals(this.expVar, cmpInfo.getVariableName());
				assertEquals(this.expInjectableAttributes, cmpInfo.getPushProperties());
				assertEquals(this.cmpInfo.createValidator().verifyComponent(), this.expectedErrors);
			}
		}

	}

	/**
	 * Tests parsing (whitespaces, etc.)
	 */
	public void testComponentInfoParser() {

		// some predefined attribute values
		final String spec = "ystorefoundationpackage.yfaces.component.misc.AdBannerComponent";
		final String impl = "ystorefoundationpackage.yfaces.component.misc.DefaultAdBannerComponent";
		final String var = "adBannerCmpVar";
		final String id = "id1";
		final Set<String> properties = new HashSet<String>(Arrays.asList("prop1", "prop2", "prop3",
				"prop4"));

		// test various whitespaces (space, tab etc)
		final String[] cmps1 = new String[] {
				"<yf:component id=\"id1\" impl=\"" + impl + "\" spec=\"" + spec + "\" var=\"" + var + "\">",
				"<yf:component   id =\"id1\"  impl=\"" + impl + "\"   spec=	\"" + spec + "\"		var=\"" + var
						+ "\" >",
				"<yf:component	id	=	\"id1\"	impl	=	\"" + impl + "\"	spec=\"" + spec + "\"	var	=\"" + var
						+ "\" >",
				"<yf:component	id	= \" id1\"	impl=	\"		" + impl + "\"	spec=\"" + spec + "\" var=\"" + var
						+ "	\" >", };

		final YComponentInfoFactory cmpFac = new YComponentInfoFactory();
		for (final String s : cmps1) {
			// System.out.println(count++ + ": " + s);
			final DefaultYComponentInfo cmpInfo = cmpFac.createComponentInfo(HEAD + s);
			assertEquals(spec, cmpInfo.getSpecification());
			assertEquals(impl, cmpInfo.getImplementation());
			assertEquals(var, cmpInfo.getVariableName());
			assertEquals(id, cmpInfo.getId());
			assertEquals(0, cmpInfo.getPushProperties().size());
		}

		// test 'injectable' properties (boths styles)
		final String[] cmps2 = new String[] {
				"<yf:component impl=\"" + impl + "\" injectable=\"prop1,prop2,prop3,prop4\">",
				"<yf:component impl=\"" + impl + "\" injectable=\"	prop1 ,prop2	,prop3,	prop4\">",
				"<yf:component impl=\"" + impl
						+ "\" prop1=\"#{prop1}\" prop2=\"#{prop2}\" prop3=\"#{prop3}\" prop4=\"#{prop4}\">",
				"<yf:component impl=\"" + impl
						+ "\" prop1=\"#{prop1}\" prop2=\"#{prop1}\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component impl=\"" + impl
						+ "\" injectable=\"prop1,prop2\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component impl=\"" + impl
						+ "\" injectable=\"prop1,prop2,prop3\" prop3=\"#{prop1}\" prop4=\"#{prop1}\">",
				"<yf:component impl=\"" + impl
						+ "\" injectable=\"prop1,prop2,prop3\" prop4 =	\" #{prop4}	\">", };
		for (final String s : cmps2) {
			// System.out.println(count++ + ": " + s);
			final DefaultYComponentInfo cmpInfo = cmpFac.createComponentInfo(HEAD + s);
			final Collection<String> props = cmpInfo.getPushProperties();
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

		final String spec = TEST_COMPONENT;
		final String impl = TEST_COMPONENT_IMPL;
		final String var = "adBannerCmpVar";
		DefaultYComponentInfo cmpInfo = null;
		final YComponentInfoFactory cmpFac = new YComponentInfoFactory();

		// test various errors
		int count = -1;
		while (++count >= 0) {
			String cmp = null;
			Collection<YValidationAspekt> expected = null;
			switch (count) {
			case 0:
				cmp = "<yf:component impl=\"" + impl + "\"  spec=\"" + spec + "\" var=\"" + var + "\">";
				expected = Arrays.asList(YValidationAspekt.VIEW_ID_NOT_SPECIFIED);
				break;
			case 1:
				cmp = "<yf:component impl=\"" + impl + "\" >";
				expected = Arrays.asList(YValidationAspekt.VIEW_ID_NOT_SPECIFIED,
						YValidationAspekt.VIEW_VAR_NOT_SPECIFIED, YValidationAspekt.SPEC_IS_MISSING);
				break;
			case 2:
				cmp = "<yf:component id=\"id\" impl=\"java.util.List\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_MISSING,
						YValidationAspekt.IMPL_IS_INTERFACE, YValidationAspekt.IMPL_IS_NO_YCMP);
				break;
			case 3:
				cmp = "<yf:component id=\"id\" impl=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_MISSING,
						YValidationAspekt.IMPL_IS_NO_YCMP);
				break;
			case 4:
				cmp = "<yf:component id=\"id\" spec=\"java.util.List\" impl=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_NO_YCMP,
						YValidationAspekt.IMPL_IS_NO_YCMP);
				break;
			case 5:
				cmp = "<yf:component id=\"id\" spec=\"java.util.ArrayList\" impl=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_NO_INTERFACE,
						YValidationAspekt.SPEC_IS_NO_YCMP, YValidationAspekt.IMPL_IS_NO_YCMP);
				break;
			case 6:
				cmp = "<yf:component id=\"id\" spec=\"java.util.Listxxx\" impl=\"java.util.ArrayListxxx\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_NOT_LOADABLE,
						YValidationAspekt.IMPL_NOT_LOADABLE);
				break;

			default:
				count = -5;
			}

			if (count >= 0) {
				log.info("Asserting: " + cmp);
				log.info("Expecting: " + expected);
				cmpInfo = cmpFac.createComponentInfo(HEAD + cmp);
				final Set<YValidationAspekt> errors = cmpInfo.createValidator().verifyComponent();
				assertEquals(new HashSet<YValidationAspekt>(expected), errors);
			}
		}
	}

	/**
	 * Tests {@link YComponentRegistry} and {@link DefaultYComponentInfo} validation. Additional does
	 * enhanced text parsing as components are provided as external resources.
	 */
	public void testYComponentRegistry() {

		log.info("---------------------------------");
		log.info("Testing YComponentRegistry");
		log.info("---------------------------------");

		AddSingleYComponentTest test = null;
		final YComponentRegistry reg = new YComponentRegistry();

		// no ycomponent at all; plain facelet tag
		test = new AddSingleYComponentTest(reg, "plainFaceletTag.xhtml", false);
		test.expectedErrors = null;
		test.run();

		test = new AddSingleYComponentTest(reg, "validComponent1Tag.xhtml", true);
		test.setExpectedErrors(YValidationAspekt.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent1";
		test.expVar = "validComponent1Var";
		test.run();

		test = new AddSingleYComponentTest(reg, "validComponent2Tag.xhtml", true);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expSpecClassName = TEST_COMPONENT;
		test.expId = "validComponent2";
		test.expVar = "validComponent2Var";
		test.run();

		test = new AddSingleYComponentTest(reg, "validComponent3Tag.xhtml", true);
		test.setExpectedErrors(YValidationAspekt.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent3";
		test.expVar = "validComponent3Var";
		test.expInjectableAttributes = new HashSet<String>(Arrays.asList("value", "primitiveInt",
				"wrappedInt", "primitiveBoolean", "wrappedBoolean", "enumValue", "stringValue"));
		test.run();

		test = new AddSingleYComponentTest(reg, "validComponent4Tag.xhtml", true);
		test.setExpectedErrors(YValidationAspekt.SPEC_IS_MISSING);
		test.expImplClassName = TEST_COMPONENT_IMPL;
		test.expId = "validComponent4";
		test.expVar = "validComponent4Var";
		test.expInjectableAttributes = new HashSet<String>(Arrays.asList("primitiveInt", "wrappedInt",
				"primitiveBoolean", "wrappedBoolean", "enumValue", "stringValue"));
		test.run();
	}

	public static void main(final String argc[]) {
		final TestYComponentInfo info = new TestYComponentInfo();
		info.testYComponentRegistry();
	}
}
