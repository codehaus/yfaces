/**
 * 
 */
package org.codehaus.yfaces;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.YComponentHandlerImpl;
import org.codehaus.yfaces.component.YComponentHandlerRegistry;
import org.codehaus.yfaces.component.YComponentHandler;
import org.codehaus.yfaces.component.YComponentHandlerFactory;
import org.codehaus.yfaces.component.YComponentValidator.YValidationAspekt;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class TestYComponentInfo /* extends TestCase */{

	private static final Logger log = Logger.getLogger(TestYComponentInfo.class);

	private static final String TEST_COMPONENT = YTestComponent.class.getName();
	private static final String TEST_COMPONENT_IMPL = YTestComponentImpl.class.getName();

	private static final String HEAD = "<ui:composition xmlns:yf=\"http://yfaces.codehaus.org/taglib\">\n\n";

	private class AddSingleYComponentTest {
		private String componentFile = null;
		private YComponentHandlerRegistry registry = null;
		private final YComponentHandlerFactory cmpFac = new YComponentHandlerFactory();

		private boolean expectedToAdd = false;
		private Set<YValidationAspekt> expectedErrors = Collections.emptySet();
		private String expSpecClassName = null;
		private String expImplClassName = null;
		private String expId = null;
		private String expVar = null;
		private Collection<String> expInjectableAttributes = Collections.emptySet();

		private YComponentHandler cmpInfo = null;
		private boolean wasAdded = false;

		public AddSingleYComponentTest(final YComponentHandlerRegistry registry, final String file,
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
			this.cmpInfo = cmpFac.createHandler(url, "some.namespace");
			this.wasAdded = registry.addComponent(cmpInfo);

			if (this.wasAdded) {
				log.info("...added");
				Assert.assertTrue("Component was added but must be skipped:", expectedToAdd);
			} else {
				log.info("...skipped");
				Assert.assertFalse("Component was skipped but must be added:", expectedToAdd);
			}

			if (this.cmpInfo == null) {
				Assert.assertNull(this.expectedErrors);
			} else {
				Assert.assertEquals(this.expSpecClassName, cmpInfo.getConfiguration()
						.getModelSpecification());
				Assert.assertEquals(this.expImplClassName, cmpInfo.getConfiguration()
						.getModelImplementation());
				Assert.assertEquals(this.expId, cmpInfo.getViewId());
				Assert.assertEquals(this.expVar, cmpInfo.getVariableName());
				Assert.assertEquals(this.expInjectableAttributes, cmpInfo.getPushProperties());
				Assert
						.assertEquals(this.cmpInfo.createValidator().validateComponent(), this.expectedErrors);
			}
		}

	}

	/**
	 * Tests parsing (whitespaces, etc.)
	 */
	@Test
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
				"<yf:component id=\"id1\" model=\"" + impl + "\" modelspec=\"" + spec + "\" var=\"" + var
						+ "\">",
				"<yf:component   id =\"id1\"  model=\"" + impl + "\"   modelspec=	\"" + spec + "\"		var=\""
						+ var + "\" >",
				"<yf:component	id	=	\"id1\"	model	=	\"" + impl + "\"	modelspec=\"" + spec + "\"	var	=\""
						+ var + "\" >",
				"<yf:component	id	= \" id1\"	model=	\"		" + impl + "\"	modelspec=\"" + spec + "\" var=\""
						+ var + "	\" >", };

		final YComponentHandlerFactory cmpFac = new YComponentHandlerFactory();
		for (final String s : cmps1) {
			// System.out.println(count++ + ": " + s);
			final YComponentHandler cmpInfo = cmpFac.createHandler(HEAD + s);
			Assert.assertEquals(spec, cmpInfo.getConfiguration().getModelSpecification());
			Assert.assertEquals(impl, cmpInfo.getConfiguration().getModelImplementation());
			Assert.assertEquals(var, cmpInfo.getVariableName());
			Assert.assertEquals(id, cmpInfo.getViewId());
			Assert.assertEquals(0, cmpInfo.getPushProperties().size());
		}

		// test 'injectable' properties 
		final String[] cmps2 = new String[] {
				"<yf:component model=\"" + impl + "\" passToModel=\"prop1,prop2,prop3,prop4\">",
				"<yf:component model=\"" + impl + "\" passToModel=\"	prop1 ,prop2	,prop3,	prop4\">", };
		for (final String s : cmps2) {
			final YComponentHandler cmpInfo = cmpFac.createHandler(HEAD + s);
			final Collection<String> props = cmpInfo.getPushProperties();
			Assert.assertEquals(4, props.size());
			Assert.assertTrue("Got properties " + props.toString(), props.containsAll(properties));
		}
	}

	/**
	 * Tests various validation errors which can occur. YComponent definition is given as simple
	 * String.
	 */
	@Test
	public void testYComponentInfoValidation() {

		log.info("---------------------------------");
		log.info("Testing YComponentInfoValidation");
		log.info("---------------------------------");

		final String spec = TEST_COMPONENT;
		final String impl = TEST_COMPONENT_IMPL;
		final String var = "adBannerCmpVar";
		YComponentHandler cmpInfo = null;
		final YComponentHandlerFactory cmpFac = new YComponentHandlerFactory();

		// test various errors
		int count = -1;
		while (++count >= 0) {
			String cmp = null;
			Collection<YValidationAspekt> expected = null;
			switch (count) {
			case 0:
				cmp = "<yf:component modelspec=\"" + spec + "\" model=\"" + impl + "\"  var=\"" + var
						+ "\">";
				expected = Arrays.asList(YValidationAspekt.VIEW_ID_NOT_SPECIFIED);
				break;
			case 1:
				cmp = "<yf:component model=\"" + impl + "\" >";
				expected = Arrays.asList(YValidationAspekt.VIEW_ID_NOT_SPECIFIED,
						YValidationAspekt.VIEW_VAR_NOT_SPECIFIED, YValidationAspekt.SPEC_IS_MISSING);
				break;
			case 2:
				cmp = "<yf:component id=\"id2\" model=\"java.util.List\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_MISSING,
						YValidationAspekt.IMPL_IS_INTERFACE);
				break;
			case 3:
				cmp = "<yf:component id=\"id3\" model=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_MISSING);
				break;
			case 4:
				cmp = "<yf:component id=\"id4\" modelspec=\"java.util.List\" model=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList();
				break;
			case 5:
				cmp = "<yf:component id=\"id5\" modelspec=\"java.util.ArrayList\" model=\"java.util.ArrayList\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_IS_NO_INTERFACE);
				break;
			case 6:
				cmp = "<yf:component id=\"id6\" modelspec=\"java.util.Listxxx\" model=\"java.util.ArrayListxxx\" var=\"var\">";
				expected = Arrays.asList(YValidationAspekt.SPEC_NOT_LOADABLE,
						YValidationAspekt.IMPL_NOT_LOADABLE);
				break;

			default:
				count = -5;
			}

			if (count >= 0) {
				log.info("Asserting: " + cmp);
				log.info("Expecting: " + expected);
				cmpInfo = cmpFac.createHandler(HEAD + cmp);
				final Set<YValidationAspekt> errors = cmpInfo.createValidator().validateComponent();
				Assert.assertEquals(new HashSet<YValidationAspekt>(expected), errors);
			}
		}
	}

	/**
	 * Tests {@link YComponentHandlerRegistry} and {@link YComponentHandlerImpl} validation. Additional does
	 * enhanced text parsing as components are provided as external resources.
	 */
	@Test
	public void testYComponentRegistry() {

		log.info("---------------------------------");
		log.info("Testing YComponentRegistry");
		log.info("---------------------------------");

		AddSingleYComponentTest test = null;
		final YComponentHandlerRegistry reg = new YComponentHandlerRegistry();

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
		final TestYComponentInfo test = new TestYComponentInfo();
		test.testYComponentInfoValidation();
	}
}
