/*
 * Copyright 2008 the original author or authors.
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
package de.hybris.yfaces.integration;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.application.Application;

import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YFrame;
import de.hybris.yfaces.context.YRequestContext;
import de.hybris.yfaces.context.YRequestContextImpl;
import de.hybris.yfaces.util.myfaces.YFacesApplicationFactory;
import de.hybris.yfaces.util.myfaces.YFacesApplicationFactory.YFacesApplication;

/**
 * A wrapper around a {@link ELResolver}.<br/>
 * This wrapper is used instead of the regular JSF one.<br/>
 * <br/>
 * Task of this resolver is a postprocessing of the resolved value.<br/>
 * This is needed when resolving {@link YComponentBinding} instances automatically to a
 * {@link YComponentBinding#getValue()} or {@link YComponentBinding#setValue(YComponent)}. <br/>
 * <br/>
 * 
 * @see YFacesApplicationFactory
 * @see YFacesApplication
 * 
 * @author Denny.Strietzbaum
 * 
 */
public class YFacesELResolver extends ELResolver {
	private ELResolver resolver = null;

	/**
	 * Constructor.<br/>
	 * The passed resolver should be the {@link CompositeELResolver} created by the JSF Framework
	 * for the {@link Application} instance. <br/>
	 * 
	 * @param resolver
	 */
	public YFacesELResolver(final ELResolver resolver) {
		this.resolver = resolver;
	}

	/**
	 * Returns the original {@link ELResolver}.<br/>
	 * Generally this is the {@link CompositeELResolver} created by JSF for the {@link Application}
	 * instance. <br/>
	 * 
	 * @return the source {@link ELResolver}
	 */
	public ELResolver getSourceResolver() {
		return this.resolver;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Object getValue(final ELContext context, Object base, final Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		// when base is a YComponentBinding (Components within components;
		// component templates) then resolve to its value
		if (base instanceof YComponentBinding) {
			base = ((YComponentBinding) base).getValue();
		}

		// delegate to original resolver
		Object result = this.resolver.getValue(context, base, property);

		// ... when value is a Frame: notify current YPage
		if (result instanceof YFrame) {
			//			((YConversationContextImpl) YRequestContext.getCurrentContext()
			//					.getConversationContext()).handleFrameRequest((YFrame) result);
			this.handleFrameRequest((YFrame) result);
		}

		// ... when value is a YCOmponentBinding and resolving is enabled,
		// resolve value to YCOmponentBindings value
		if (getYContext(context).isResolveYComponentBinding()
				&& result instanceof YComponentBinding) {
			result = ((YComponentBinding) result).getValue();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#setValue(javax.el.ELContext, java.lang.Object, java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public void setValue(final ELContext context, final Object base, final Object property,
			Object value) throws NullPointerException, PropertyNotFoundException,
			PropertyNotWritableException, ELException {

		Class type = this.resolver.getType(context, base, property);

		//special handling in case of YComponentBinding
		if (YComponentBinding.class.equals(type)) {
			boolean resolveBinding = getYContext(context).isResolveYComponentBinding();
			if (resolveBinding) {
				YComponentBinding binding = (YComponentBinding) this.resolver.getValue(context,
						base, property);
				binding.setValue((YComponent) value);
			} else {
				this.resolver.setValue(context, base, property, value);
			}
		} else {
			this.resolver.setValue(context, base, property, value);
		}

		// 

		//		if (YComponentBinding.class.equals(this.resolver.getType(context, base, property))) {
		//			final YComponentBinding binding = (YComponentBinding) this.resolver.getValue(context,
		//					base, property);
		//			binding.setValue((YComponent) value);
		//		} else {
		//			this.resolver.setValue(context, base, property, value);
		//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#isReadOnly(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isReadOnly(final ELContext context, final Object base, final Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		final boolean result = false;
		// if (!this.resolver.getType(context, base,
		// property).equals(YComponentBinding.class))
		// {
		// result = this.resolver.isReadOnly(context, base, property);
		// }
		// else
		// {
		// context.setPropertyResolved(true);
		// }
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#getType(javax.el.ELContext, java.lang.Object, java.lang.Object)
	 */
	@Override
	public Class<?> getType(final ELContext context, final Object base, final Object property)
			throws NullPointerException, PropertyNotFoundException, ELException {
		// delegate to original resolver
		Class result = this.resolver.getType(context, base, property);

		// when enabled, resolve YComponentBinding to YComponent
		if (getYContext(context).isResolveYComponentBinding()
				&& YComponentBinding.class.equals(result)) {
			result = YComponent.class;
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#getCommonPropertyType(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Class<?> getCommonPropertyType(final ELContext context, final Object base) {
		return this.resolver.getCommonPropertyType(context, base);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.el.ELResolver#getFeatureDescriptors(javax.el.ELContext, java.lang.Object)
	 */
	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(final ELContext context,
			final Object base) {
		return this.resolver.getFeatureDescriptors(context, base);
	}

	/**
	 * Returns the current {@link YFacesELContext}
	 * 
	 * @param context
	 *            {@link ELContext}
	 * @return {@link YFacesELContext}
	 */
	private YFacesELContext getYContext(final ELContext context) {
		return (YFacesELContext) context.getContext(YFacesELContext.class);
	}

	/**
	 * Internal. Notifies the context that a Frame is right now requested.
	 * 
	 * @param frame
	 * 
	 * @see YFacesELResolver
	 */
	private void handleFrameRequest(final YFrame frame) {
		// frames are getting added when:
		// a) method is get
		// b) method is post and START_REQUEST phase has finished
		// e.g. nothing is done when the Frame was requested from within an
		// action/actionlistener
		boolean isPostback = YRequestContext.getCurrentContext().isPostback();
		boolean isStartRequest = ((YRequestContextImpl) YRequestContext.getCurrentContext())
				.getRequestPhase()
				.equals(
						de.hybris.yfaces.context.YRequestContextImpl.REQUEST_PHASE.START_REQUEST);

		//		final boolean doNothing = YRequestContext.getCurrentContext().isPostback()
		//				&& (this.currentPhase.equals(REQUEST_PHASE.START_REQUEST));
		//		if (!doNothing) {
		//			this.getCurrentPage().addFrame(frame);
		//		}

		boolean addFrameToCurrentPage = !(isPostback && isStartRequest);

		if (addFrameToCurrentPage) {
			YRequestContext.getCurrentContext().getPageContext().addFrame(frame);
		}
	}

}