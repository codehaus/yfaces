/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.hybris.yfaces.component.html;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.log4j.Logger;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

import de.hybris.yfaces.component.DefaultYComponentInfo;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YComponentRegistry;
import de.hybris.yfaces.component.YComponentValidator;
import de.hybris.yfaces.component.YComponentValidator.YValidationAspekt;

/**
 * A custom {@link ComponentHandler} which prepares {@link HtmlYComponent}
 * 
 * @author Denny Strietzbaum
 */
public class HtmlYComponentHandler extends ComponentHandler {
	private static final Logger log = Logger.getLogger(HtmlYComponentHandler.class);
	private static final String BINDING_ATTRIBUTE = "binding";

	//private final TagAttribute attributes;

	// YComponentInfo gets newly created whenever facelet-file was changed
	private DefaultYComponentInfo cmpInfo = null;
	private boolean isValidateCmpInfo = false;

	public HtmlYComponentHandler(final ComponentConfig config) {
		super(config);

		final String tagPath = config.getTag().getLocation().getPath();

		this.cmpInfo = (DefaultYComponentInfo) YComponentRegistry.getInstance().getComponentByPath(
				tagPath);

		if (log.isDebugEnabled() && cmpInfo == null) {
			log.error("No " + YComponentInfo.class.getSimpleName() + " for " + tagPath + " found");
		}

		if (cmpInfo != null) {
			this.updateYComponentInfo(config.getTag());
			final YComponentValidator cmpValid = cmpInfo.createValidator();
			final Set<YValidationAspekt> errors = new HashSet<YValidationAspekt>(cmpValid
					.verifyComponent());
			errors.remove(YValidationAspekt.VIEW_ID_NOT_SPECIFIED);
			errors.remove(YValidationAspekt.SPEC_IS_MISSING);

			this.isValidateCmpInfo = !errors.isEmpty();

			if (log.isDebugEnabled() && !errors.isEmpty()) {
				log.debug("Component has validation errors" + errors);
			}
		}

	}

	private void updateYComponentInfo(final Tag tag) {

		log.debug("Refreshing " + YComponentInfo.class.getSimpleName() + " for "
				+ cmpInfo.getLocation() + "...");

		final String specClass = getAttributeValue(tag, YComponentInfo.SPEC_ATTRIBUTE);
		final String implClass = getAttributeValue(tag, YComponentInfo.IMPL_ATTRIBUTE);
		final String varName = getAttributeValue(tag, YComponentInfo.VAR_ATTRIBUTE);
		final String id = getAttributeValue(tag, YComponentInfo.ID_ATTRIBUTE);
		final String injectable = getAttributeValue(tag, YComponentInfo.INJECTABLE_ATTRIBUTE);

		if (log.isDebugEnabled()) {
			String updatedAttribs = "";
			if (isModified(cmpInfo.getSpecification(), specClass)) {
				updatedAttribs = updatedAttribs + YComponentInfo.SPEC_ATTRIBUTE + ",";
			}
			if (isModified(cmpInfo.getImplementation(), implClass)) {
				updatedAttribs = updatedAttribs + YComponentInfo.IMPL_ATTRIBUTE + ",";
			}
			if (isModified(cmpInfo.getVariableName(), varName)) {
				updatedAttribs = updatedAttribs + YComponentInfo.VAR_ATTRIBUTE + ",";
			}
			if (isModified(cmpInfo.getId(), id)) {
				updatedAttribs = updatedAttribs + YComponentInfo.ID_ATTRIBUTE + ",";
			}

			if (injectable != null) {
				final String[] props = injectable.trim().split("\\s*,\\s*");
				final Collection<String> push = new TreeSet<String>(Arrays.asList(props));
				if (!push.equals(cmpInfo.getPushProperties())) {
					updatedAttribs = updatedAttribs + YComponentInfo.INJECTABLE_ATTRIBUTE;
				}
			}

			// TODO: add 'injectable'

			log.debug("...updated attributes: "
					+ ((updatedAttribs.length() > 0) ? updatedAttribs : "[none]"));

		}

		cmpInfo.setSpecification(specClass);
		cmpInfo.setImplementation(implClass);
		cmpInfo.setVariableName(varName);
		cmpInfo.setId(id);
		cmpInfo.setPushProperties(injectable);
	}

	/**
	 * Returns the value of an {@link TagAttribute}.
	 * 
	 * @param tag
	 *          Tag
	 * @param name
	 *          Attribute name
	 * @return value
	 */
	private String getAttributeValue(final Tag tag, final String name) {

		final TagAttribute tagAttr = tag.getAttributes().get(name);
		final String result = tagAttr != null ? tagAttr.getValue() : null;
		return result;
	}

	private boolean isModified(final String oldValue, final String currentValue) {
		final boolean changed = (oldValue == null) ? (currentValue != null) : !oldValue
				.equals(currentValue);
		return changed;

	}

	@Override
	protected String getId(final FaceletContext ctx) {
		return super.getId(ctx);
	}

	//
	// METHODS ARE NOT CALLED AFTER A POST (exception: Facelet file was modified)
	//

	// watched behavior:
	// - GET: first hook-point
	// - gets called to create UIComponent
	@Override
	protected UIComponent createComponent(final FaceletContext ctx) {
		final UIComponent result = super.createComponent(ctx);
		return result;
	}

	// watched behavior:
	// - GET: second hook-point
	// - applies all MetaRules which were created in createMetaRuleset
	@Override
	protected void setAttributes(final FaceletContext ctx, final Object instance) {
		super.setAttributes(ctx, instance);
	}

	// watched behavior:
	// - gets invoked only one times between setAttribute and onComponentCreated  
	// - defines some general rules which have to be executed (commands?)
	@Override
	protected MetaRuleset createMetaRuleset(final Class type) {
		final MetaRuleset result = super.createMetaRuleset(type);
		return result;
	}

	// watched behavior:
	// - GET: third hook-point
	@Override
	protected void onComponentCreated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		super.onComponentCreated(ctx, cmp, uicomponent1);

		final HtmlYComponent htmlYCmp = (HtmlYComponent) cmp;

		// YFACES-46: prototype code; dynamic ID calculation
		//		final TagAttribute attrib = getAttribute("id");
		//		if (attrib == null) {
		//			String id = this.cmpInfo.getComponentName();
		//			final ValueExpression idValueExpr = ctx.getVariableMapper().resolveVariable("id");
		//			if (idValueExpr != null) {
		//				id = (String) idValueExpr.getValue(FacesContext.getCurrentInstance().getELContext());
		//			}
		//			cmp.setId(id);
		//			log.debug("Setting component id: " + cmpInfo.getComponentName());
		//		}

		// publish ValueExpression for 'binding' into HtmlYComponent
		// value of that expression is the YComponent instance
		final ValueExpression _binding = ctx.getVariableMapper().resolveVariable(BINDING_ATTRIBUTE);
		htmlYCmp.setYComponentBinding(_binding);

		// publish ValueExpression for push-properties into HtmlYComponent
		// values of that expressions are injected/pushed into YComponent
		final Collection<String> injectable = this.cmpInfo.getPushProperties();
		if (injectable != null) {
			// iterate over each supported push-property...
			for (final String property : injectable) {
				// ... and try find a ValueExpression for this one
				final ValueExpression valueExpr = ctx.getVariableMapper().resolveVariable(property);
				// ... if a ValueExpression is available (property is set in facelet tag)
				if (valueExpr != null) {
					// ... publish that expression to HtmlYComponent
					htmlYCmp.setValueExpression(property, valueExpr);
					if (log.isDebugEnabled()) {
						log.debug("Publish ValueExpression " + property + "='"
								+ valueExpr.getExpressionString() + "' to " + htmlYCmp.getId() + " ("
								+ htmlYCmp.hashCode() + ")");
					}
				}
			}
		}
	}

	// watched behavior:
	// - GET: fourth hook-point
	// - POST: first hook-point
	@Override
	protected void applyNextHandler(final FaceletContext ctx, final UIComponent cmp)
			throws IOException, FacesException, ELException {
		// release binding for nested views:
		// Facelet attributes are scoped to the facelet view and any included child
		// This means 'bindig' attribute is shared through all nested HtmlYComponentHandlers/HtmlYComponents
		// This is no problem, when nested elements have it's own binding (binding gets updated than)
		// but it is a problem when they don't because then the parent binding will be taken
		ctx.getVariableMapper().setVariable(BINDING_ATTRIBUTE, null);

		// XXX: whats with the other variables? pushProperties? id?
		// why not reset the variables within onCOmponentCreated

		super.applyNextHandler(ctx, cmp);
	}

	// watched behavior:
	// - GET: fifth hook-point
	// - POST: second hook-point
	@Override
	protected void onComponentPopulated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		super.onComponentPopulated(ctx, cmp, uicomponent1);
		((HtmlYComponent) cmp).setValidateYComponentInfo(this.isValidateCmpInfo);
		((HtmlYComponent) cmp).setComponentInfo(cmpInfo);
	}

}
