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
import org.codehaus.yfaces.YFaces;
import org.codehaus.yfaces.component.YComponentConfig;
import org.codehaus.yfaces.component.YComponentConfigImpl;
import org.codehaus.yfaces.component.YComponentHandler;
import org.codehaus.yfaces.component.YComponentHandlerImpl;
import org.codehaus.yfaces.component.YComponentHandlerRegistry;

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

	private static final String COMPONENT_MODEL_ATTRIBUTE = "model";

	// YComponentHandler is bound to this Facelet-ComponentHandler lifecycle
	// Whenever a xhtml file gets changed (whenever a new ComponentHandler gets created) the appropriate
	// YComponentHandler gets refreshed
	// The mapping ComponentHandler to YComponentHandler is done by the location (tagpath) of the xhtml file.
	private YComponentHandlerImpl cmpHandler = null;

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

		// get the location of the tag file (xhtml file)
		final String tagPath = config.getTag().getLocation().getPath();

		// get the YComponentHandler which is responsible for that tag file 
		final YComponentHandlerRegistry cmpHandlers = YFaces.getApplicationContext()
				.getComponentHandlers();
		this.cmpHandler = (YComponentHandlerImpl) cmpHandlers.getComponentByPath(tagPath);

		// there is no reason why a ComponentHandler shouldn't be available
		// TODO: throw an IllegalStateException?
		if (cmpHandler == null) {
			log.error("No " + YComponentHandler.class.getSimpleName() + " for " + tagPath + " found");
		}

		// a ComponentInfo is available for the 'location' (view file location) of this handler
		if (cmpHandler != null) {

			// refresh YComponentHandler
			this.refreshYComponentHandler();

			// and validate lazily (when this component gets rendered first time after refresh) 
			cmpHandler.setValidated(false);
		}

	}

	private void refreshYComponentHandler() {

		log.debug("Refreshing " + YComponentHandler.class.getSimpleName() + " for "
				+ cmpHandler.getViewLocation() + "...");

		// retrieve some YComponent specific attributes from xhtml view file
		final String specClass = getAttributeValue(YComponentConfig.MODEL_SPEC_ATTRIBUTE);
		final String implClass = getAttributeValue(YComponentConfig.MODEL_IMPL_ATTRIBUTE);
		final String varName = getAttributeValue(YComponentConfig.VAR_ATTRIBUTE);
		final String id = getAttributeValue(YComponentConfig.ID_ATTRIBUTE);
		final String injectable = getAttributeValue(YComponentConfig.PASS_TO_MODEL_ATTRIBUTE);
		final String errorHandling = getAttributeValue(YComponentConfig.ERROR_ATTRIBUTE);

		// gets the ComponentHandler's underlying ComponentConfig
		final YComponentConfigImpl cmpCfg = (YComponentConfigImpl) cmpHandler.getConfiguration();

		if (log.isDebugEnabled()) {
			String updatedAttribs = "";
			if (isModified(cmpCfg.getModelSpecification(), specClass)) {
				updatedAttribs = updatedAttribs + YComponentConfig.MODEL_SPEC_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getModelImplementation(), implClass)) {
				updatedAttribs = updatedAttribs + YComponentConfig.MODEL_IMPL_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getVariableName(), varName)) {
				updatedAttribs = updatedAttribs + YComponentConfig.VAR_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getId(), id)) {
				updatedAttribs = updatedAttribs + YComponentConfig.ID_ATTRIBUTE + ",";
			}
			if (isModified(cmpCfg.getPushProperties(), injectable)) {
				updatedAttribs = updatedAttribs + YComponentConfig.PASS_TO_MODEL_ATTRIBUTE + ",";
			}

			log.debug("...updated attributes: "
					+ ((updatedAttribs.length() > 0) ? updatedAttribs : "[none]"));

		}

		// ... update ComponentConfig properties
		cmpCfg.setModelSpecification(specClass);
		cmpCfg.setModelImplementation(implClass);
		cmpCfg.setVariableName(varName);
		cmpCfg.setId(id);
		cmpCfg.setErrorHandling(errorHandling);
		cmpCfg.setPushProperties(injectable);

		// and refresh YComponentHandler based on currently updated ComponentConfig
		cmpHandler.initialize();
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

		// check whether tag-caller defines an own 'id' attribute
		// ctx.getAttribute behaves like ctx.getVariableMapper().resolveVariable(..)
		//		final String _id = (String) ctx.getAttribute("id");
		//		if (_id == null) {

		// check whether xhtml tag file has an 'id' attribute for that YComponent
		final TagAttribute attrib = getAttribute("id");

		// if not take view id as component id
		if (attrib == null) {
			final String id = this.cmpHandler.getViewId();
			cmp.setId(id);
			log.debug("Setting component id: " + cmpHandler.getViewId());
		}
		//		} else {
		//			cmp.setId(_id);
		//		}

		// pass ValueExpression for component model into HtmlYComponent
		final ValueExpression _binding = ctx.getVariableMapper().resolveVariable(
				COMPONENT_MODEL_ATTRIBUTE);
		htmlYCmp.setComponentModelBinding(_binding);

		// publish ValueExpression for push-properties into HtmlYComponent
		// values of that expressions are injected/pushed into YComponent
		final Collection<String> passToModelProps = this.cmpHandler.getPushProperties();
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
		ctx.getVariableMapper().setVariable(COMPONENT_MODEL_ATTRIBUTE, null);

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
		((HtmlYComponent) cmp).setComponent(cmpHandler);
	}

	/**
	 * Debug helper; returns the value of an {@link TagAttribute} or null when attribute isn't set.
	 */
	private String getAttributeValue(final String attributeName) {

		final TagAttribute tagAttr = getTag().getAttributes().get(attributeName);
		final String result = tagAttr != null ? tagAttr.getValue() : null;
		return result;
	}

	private boolean isModified(final String oldValue, final String currentValue) {
		final boolean changed = (oldValue == null) ? (currentValue != null) : !oldValue
				.equals(currentValue);
		return changed;
	}

	private Tag getTag() {
		return super.tag;
	}

}
