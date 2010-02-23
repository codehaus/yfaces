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
package org.codehaus.yfaces.component.html;

import java.io.IOException;
import java.util.Collection;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.YComponent;
import org.codehaus.yfaces.component.YComponentConfiguration;
import org.codehaus.yfaces.component.YComponentConfigurationImpl;
import org.codehaus.yfaces.component.YComponentImpl;
import org.codehaus.yfaces.component.YComponentRegistry;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

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
	private YComponentImpl cmpInfo = null;

	/**
	 * Constructor.
	 * <p/>
	 * Watched behavior:<br/>
	 * - gets newly constructed, whenever Facelet file was modified
	 * 
	 * @param config
	 *          {@link ComponentConfig}
	 */
	public HtmlYComponentHandler(final ComponentConfig config) {
		super(config);

		final String tagPath = config.getTag().getLocation().getPath();

		this.cmpInfo = (YComponentImpl) YComponentRegistry.getInstance().getComponentByPath(tagPath);

		// should be considered, to make this an exception
		if (log.isDebugEnabled() && cmpInfo == null) {
			log.error("No " + YComponent.class.getSimpleName() + " for " + tagPath + " found");
		}

		// a ComponentInfo is available for the 'location' (view file location) of this handler
		if (cmpInfo != null) {

			// refresh some attributes
			this.updateYComponentInfo(config.getTag());

			// force validation in HtmlYComponent
			cmpInfo.setValid(false);
		}

	}

	private void updateYComponentInfo(final Tag tag) {

		log.debug("Refreshing " + YComponent.class.getSimpleName() + " for "
				+ cmpInfo.getViewLocation() + "...");

		final String specClass = getAttributeValue(tag, YComponentConfiguration.MODEL_SPEC_ATTRIBUTE);
		final String implClass = getAttributeValue(tag, YComponentConfiguration.MODEL_IMPL_ATTRIBUTE);
		final String varName = getAttributeValue(tag, YComponentConfiguration.VAR_ATTRIBUTE);
		final String id = getAttributeValue(tag, YComponentConfiguration.ID_ATTRIBUTE);
		final String injectable = getAttributeValue(tag,
				YComponentConfiguration.PASS_TO_MODEL_ATTRIBUTE);
		final String errorHandling = getAttributeValue(tag, YComponentConfiguration.ERROR_ATTRIBUTE);

		final YComponentConfigurationImpl cmpCfg = (YComponentConfigurationImpl) cmpInfo
				.getConfiguration();

		if (log.isDebugEnabled()) {
			String updatedAttribs = "";
			if (isModified(cmpCfg.getModelSpecification(), specClass)) {
				updatedAttribs = updatedAttribs + YComponentConfiguration.MODEL_SPEC_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getModelImplementation(), implClass)) {
				updatedAttribs = updatedAttribs + YComponentConfiguration.MODEL_IMPL_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getVariableName(), varName)) {
				updatedAttribs = updatedAttribs + YComponentConfiguration.VAR_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getId(), id)) {
				updatedAttribs = updatedAttribs + YComponentConfiguration.ID_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getPushProperties(), injectable)) {
				updatedAttribs = updatedAttribs + YComponentConfiguration.PASS_TO_MODEL_ATTRIBUTE + ",";
			}

			log.debug("...updated attributes: "
					+ ((updatedAttribs.length() > 0) ? updatedAttribs : "[none]"));

		}

		cmpCfg.setModelSpecification(specClass);
		cmpCfg.setModelImplementation(implClass);
		cmpCfg.setVariableName(varName);
		cmpCfg.setId(id);
		cmpCfg.setErrorHandling(errorHandling);
		cmpCfg.setPushProperties(injectable);

		cmpInfo.initialize();
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
	// GET: first hook-point
	// - gets called to create UIComponent
	@Override
	protected UIComponent createComponent(final FaceletContext ctx) {
		final UIComponent result = super.createComponent(ctx);
		return result;
	}

	// watched behavior:
	// GET: second hook-point
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
	// GET: third hook-point
	@Override
	protected void onComponentCreated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		super.onComponentCreated(ctx, cmp, uicomponent1);

		final HtmlYComponent htmlYCmp = (HtmlYComponent) cmp;

		//		final String _id = (String) ctx.getAttribute("id");
		//		if (_id == null) {
		final TagAttribute attrib = getAttribute("id");
		if (attrib == null) {
			final String id = this.cmpInfo.getId();
			cmp.setId(id);
			log.debug("Setting component id: " + cmpInfo.getId());
		}
		//		} else {
		//			cmp.setId(_id);
		//		}

		// publish ValueExpression for 'binding' into HtmlYComponent
		// value of that expression is the YComponent instance
		final ValueExpression _binding = ctx.getVariableMapper().resolveVariable(BINDING_ATTRIBUTE);
		htmlYCmp.setYComponentBinding(_binding);

		// publish ValueExpression for push-properties into HtmlYComponent
		// values of that expressions are injected/pushed into YComponent
		final Collection<String> passToModelProps = this.cmpInfo.getPushProperties();
		if (passToModelProps != null) {
			// iterate over each supported push-property...
			for (final String passToModelProp : passToModelProps) {
				// ... and try find a ValueExpression for this one
				final ValueExpression valueExpr = ctx.getVariableMapper().resolveVariable(passToModelProp);
				// ... if a ValueExpression is available (property is set in facelet tag)
				if (valueExpr != null) {
					// ... publish that expression to HtmlYComponent
					htmlYCmp.setValueExpression(passToModelProp, valueExpr);
					if (log.isDebugEnabled()) {
						log.debug("Publish ValueExpression " + passToModelProp + "='"
								+ valueExpr.getExpressionString() + "' to " + htmlYCmp.getId() + " ("
								+ htmlYCmp.hashCode() + ")");
					}
				}
			}
		}
	}

	// watched behavior:
	// GET: fourth hook-point
	// POST: first hook-point (but never before RENDER_RESPONSE)
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
	// GET: fifth hook-point
	// POST: second hook-point (but never before RENDER_RESPONSE)
	@Override
	protected void onComponentPopulated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		super.onComponentPopulated(ctx, cmp, uicomponent1);
		((HtmlYComponent) cmp).setComponentInfo(cmpInfo);
		((HtmlYComponent) cmp).setViewLocation(cmpInfo.getViewLocation());
	}

}
