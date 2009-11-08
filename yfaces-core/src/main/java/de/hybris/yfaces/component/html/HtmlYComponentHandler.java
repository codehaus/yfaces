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

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.apache.log4j.Logger;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

import de.hybris.yfaces.component.YComponent;

/**
 * This {@link ComponentHandler} works with an {@link YComponent}. It injects the value of a
 * possible "binding" attribute as well as values of attributes which are configured as being
 * injectable.
 * 
 * @author Denny Strietzbaum
 */
public class HtmlYComponentHandler extends ComponentHandler {
	private static final Logger log = Logger.getLogger(HtmlYComponentHandler.class);
	private static final String VAR_BINDING = "binding";

	//private final TagAttribute attributes;

	public HtmlYComponentHandler(final ComponentConfig config) {
		super(config);
		//this.attributes = super.getAttribute("value");
	}

	@Override
	protected MetaRuleset createMetaRuleset(final Class type) {
		// called once after instanciation
		return super.createMetaRuleset(type);
	}

	@Override
	protected void applyNextHandler(final FaceletContext ctx, final UIComponent cmp)
			throws IOException, FacesException, ELException {
		// release binding
		ctx.getVariableMapper().setVariable(VAR_BINDING, null);

		super.applyNextHandler(ctx, cmp);
	}

	@Override
	protected void onComponentPopulated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		// TODO Auto-generated method stub
		super.onComponentPopulated(ctx, cmp, uicomponent1);
	}

	@Override
	protected String getId(final FaceletContext ctx) {
		// TODO Auto-generated method stub
		return super.getId(ctx);
	}

	//
	// METHODS ARE NOT CALLED AFTER A POST
	//
	@Override
	protected UIComponent createComponent(final FaceletContext ctx) {
		// TODO Auto-generated method stub
		return super.createComponent(ctx);
	}

	@Override
	protected void setAttributes(final FaceletContext ctx, final Object instance) {
		// here the metarules from createMetaRuleset are affected
		super.setAttributes(ctx, instance);
	}

	// private static final Pattern pattern = Pattern.compile("(\\w+),?");

	@Override
	protected void onComponentCreated(final FaceletContext ctx, final UIComponent cmp,
			final UIComponent uicomponent1) {
		// called after setAttributes()
		super.onComponentCreated(ctx, cmp, uicomponent1);

		final HtmlYComponent ycmp = (HtmlYComponent) cmp;

		// inject ValueExpression "binding" into HtmlYComponent
		// "binding" gets released within applyNextHandler (as values are
		// inherited in child facelets)
		final ValueExpression _binding = ctx.getVariableMapper().resolveVariable(VAR_BINDING);
		ycmp.setYComponentBinding(_binding);

		// inject ValueExpressions given as comma separated List with Attribute
		// "injectable"
		final String[] injectable = ycmp.getInjectableProperties();
		if (injectable != null) {
			for (final String property : injectable) {
				final ValueExpression binding = ctx.getVariableMapper().resolveVariable(property);
				if (binding != null) {
					ycmp.setValueExpression(property, binding);
					if (log.isDebugEnabled()) {
						log.debug("Pass property " + property + "(" + binding.getExpressionString() + ") to "
								+ ycmp.getId() + " (" + ycmp.hashCode() + ")");
					}
				}
			}
		}
	}
}
