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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.YFaces;
import org.codehaus.yfaces.YFacesConfig;
import org.codehaus.yfaces.YFacesELContext;
import org.codehaus.yfaces.YFacesException;
import org.codehaus.yfaces.component.YComponentConfig;
import org.codehaus.yfaces.component.YComponentHandler;
import org.codehaus.yfaces.component.YComponentHandlerImpl;
import org.codehaus.yfaces.component.YComponentValidator;
import org.codehaus.yfaces.component.YModel;
import org.codehaus.yfaces.component.YComponentValidator.YValidationAspekt;

/**
 * Each {@link YModel} must be enclosed by this {@link UIComponent}.<br/>
 * The enclosed {@link YModel} must be either passed or, when null, gets automatically instantiated
 * via the <code>default</code> Attribute.<br/>
 * An undefined amount of additional attributes can be defined.<br/>
 * Each of such attribute must be available as property (setter) at the {@link YModel} instance.<br/>
 * When such attributes are passed then the value gets injected into the {@link YModel}. <br/>
 * This component works within compile time and render time tags.<br/>
 * 
 * @author Denny Strietzbaum
 * 
 */
public class HtmlYComponent extends UIComponentBase implements NamingContainer {

	private static final Logger log = Logger.getLogger(HtmlYComponent.class);

	public final static String COMPONENT_TYPE = "hybris.YComponent";
	public final static String COMPONENT_FAMILY = "facelets";

	private static final String PARAM_YCMP_BINDING = "yfaces.HtmlYComponent.YComponentBinding";

	private static final String ID_DUPLICATECHECK_KEY = HtmlYComponent.class.getName() + "idSet";

	// properties are set by plain JSF 
	private String errorHandling = null;

	// properties are set by HtmlYComponentHandler ...
	// ... the YComponentInfo for this UIComponent
	private String viewLocation = null;
	private transient YComponentHandler cmpInfo = null;

	// other transient members
	private transient Exception error = null;
	private transient String logId = "[id]:";
	private transient String debugHtmlOut = null;

	/**
	 * Constructor.
	 */
	public HtmlYComponent() {
		super();
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getErrorHandling() {
		return errorHandling;
	}

	public void setErrorHandling(final String errorHandling) {
		this.errorHandling = errorHandling.toLowerCase();
	}

	/**
	 * @return value of 'varName' attribute
	 */
	private String getVarName() {
		return (String) super.getAttributes().get(YComponentConfig.VAR_ATTRIBUTE);
	}

	/**
	 * Set {@link YModel} instance as value of variable whose name was passed as 'var'
	 * 
	 * @param component
	 *          {@link YModel}
	 */
	private void setVarValue(final Object component) {
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
	}

	private void refreshVarValue() {
		final Object component = getComponentModel();
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
	}

	//	/**
	//	 * Sets the {@link ValueExpression} which binds a YComponent.
	//	 * 
	//	 * @param binding
	//	 */
	protected void setComponentModelBinding(final ValueExpression binding) {
		super.setValueExpression(PARAM_YCMP_BINDING, binding);
	}

	private Object getYModelFromBinding() {

		// 'binding' value expression which binds a YComponent instance
		final ValueExpression binding = getValueExpression(PARAM_YCMP_BINDING);
		Object value = null;
		if (binding != null) {
			final ELContext eCtx = getFacesContext().getELContext();
			((YFacesELContext) eCtx.getContext(YFacesELContext.class)).setCmp(getYComponent());

			// get value of that binding
			value = binding.getValue(eCtx);
		}
		return value;

	}

	/**
	 * Sets the {@link YModel}.<br/>
	 * Setting means injecting the value into the writable binding.<br/>
	 * 
	 * @param cmp
	 *          {@link YModel}
	 */
	private void setYModelToBinding(final Object model) {
		final ValueExpression ve = getValueExpression(PARAM_YCMP_BINDING);

		if (model == null) {
			throw new NullPointerException();
		}

		if (ve != null) {
			final ELContext elCtx = FacesContext.getCurrentInstance().getELContext();
			//			if (!ve.isReadOnly(elCtx)) {
			ve.setValue(elCtx, model);
			//			} else {
			//				log.warn("Can't update model instance; " + ve.toString() + " is readOnly");
			//			}

		}
	}

	/**
	 * Returns the final {@link YModel} which is used for rendering.<br/>
	 * This is the merge result of the passed {@link YModel} and passed model attributes.<br/>
	 * 
	 * @return {@link YModel}
	 */
	public Object getComponentModel() {
		// it's possible that one HtmlYComponent instance is used with multiple YComponent instances
		// example for that are loops like dataList, table etc..
		// in that case UIComponents and YComponent instances are similar
		// therefore the YComponent instance is mapped to the UIComponent instance via it's clientid
		final String key = super.getClientId(getFacesContext());
		final Map<String, Object> map = getStateMap();
		final Object result = map.get(key + "_COMPONENT");
		return result;
	}

	/**
	 * Internal.<br/>
	 * Sets the final {@link YModel} which shall be used.<br/>
	 * Normally this is done after all passed {@link YModel} Attributes are injected.<br/>
	 * 
	 * @param model
	 *          {@link YModel} to set.
	 */
	private void setComponentModel(final Object model) {
		final String clientId = super.getClientId(getFacesContext());
		final Map<String, Object> map = getStateMap();
		map.put(clientId + "_COMPONENT", model);
	}

	/**
	 * Internal.<br/>
	 * The state map is used for save/restore used YComponent.<br/>
	 * Actually this is the UITrees Attribute map.<br/>
	 * 
	 * @return {@link Map}
	 */
	private Map<String, Object> getStateMap() {
		return super.getAttributes();
	}

	// /**
	// * Returns a ValueBinding for a given binding identifier when it is
	// writable.
	// * This is the case when it is backed by a bean who has an appropriate
	// setter.
	// * <br/>
	// * It is never the case with "detached" ValueBindings
	// * (e.g. creatable via Application#createValueBinding(...)) who aren't
	// bound to any bean
	// * although such bindings can be writable too.<br/>
	// *
	// * @param binding binding id
	// * @return {@link ValueBinding}
	// */
	// private boolean isValueBindingWriteable(ValueExpression vb)
	// {
	// boolean result = true;
	//		
	// //check for readonly alone doesn't guarantee that this binding is backed
	// by a bean
	// //check for type filters out any binding which isn't bound to a bean
	// ELContext ctx = getFacesContext().getELContext();
	// if (vb == null || vb.isReadOnly(ctx) || vb.getType(ctx) == null)
	// result = false;
	//
	// //NOTE (personal experiences):
	// //type is detected by requesting an instance (not by reflection)
	// //readOnly works only at current classlevel, superclasses are ignored
	//
	// //Another Note:
	// //a cool approach would be to do this automatically within
	// deserialization of the component
	// //however, currently there's no way to find out the original source
	// ValueBinding expression
	// //which is necessary therefore (Facelets; VarMapper)
	//		
	// return result;
	// }

	@Override
	public Object saveState(final FacesContext context) {
		int i = 0;
		final Object values[] = new Object[4];
		values[i++] = super.saveState(context);
		values[i++] = this.viewLocation;

		return values;
	}

	@Override
	public void restoreState(final FacesContext context, final Object state) {
		// some tomahawk components needs a special behavior here
		// eg. dataTable component with preserveSort/preserveModel = true pushes
		// the deserialized object into the ValueBinding; therefore we must
		// assure
		// that the ValueBindings are available before any child gets restored

		// Fact: Seems that only some tomahawk components are using this phase
		// for
		// restoring valuebindings.
		// others like the core components or facelets (ui:repeat) are doing
		// this
		// in second phase
		// However, there will be always problems when mixing these both
		// approaches
		// e.g. ui:repeat within a dataTable won't work after a post request

		int i = 0;
		final Object values[] = (Object[]) state;

		super.restoreState(context, values[i++]);
		this.viewLocation = (String) values[i++];

		// can't restore bindings here as the ViewRoot isn't completed yet
		// which
		// means no renderer can be found (for some components who need them)
		// this.restoreValueBindings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#processDecodes(javax.faces.context .FacesContext)
	 */
	@Override
	public void processDecodes(final FacesContext context) {
		final Object restoredModel = getComponentModel();

		// it's possible that a previously unrenderable component can be renderable after a
		// faces request, in that case a YComponent instance isn't available yet
		if (restoredModel != null) {

			// set 'var' before any child processing starts
			// this is necessary to assure that 'rendered' Attributes work properly
			this.setVarValue(restoredModel);

			// only has an affect when component is backed by a writable
			// ValueBinding
			// XXX 1.2 simulate old behavior that non-framed components aren't
			// available

			if (restoredModel instanceof YModel
					&& ((YModel) restoredModel).getComponentContainer() != null) {
				// TODO: add a check which recognizes, whether parent has changed
				// and only if instance is not same inject the model
				this.setYModelToBinding(restoredModel);
			}

			// process childs
			if (isRendered()) {
				super.processDecodes(context);
			}
		}
	}

	@Override
	public void processUpdates(final FacesContext arg0) {
		// set 'var' value again
		// necessary when using this component within a loop
		final Object restoredModel = getComponentModel();
		if (restoredModel != null) {
			this.setVarValue(restoredModel);
			super.processUpdates(arg0);
		}
	}

	@Override
	public void processValidators(final FacesContext arg0) {
		// set 'var' value again
		// necessary when using this component within a loop
		final Object restored = getComponentModel();
		if (restored != null) {
			this.setVarValue(restored);
			super.processValidators(arg0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.faces.component.UIComponentBase#encodeBegin(javax.faces.context .FacesContext)
	 */
	@Override
	public void encodeBegin(final FacesContext context) throws IOException {
		this.logId = getId() + ": ";

		// FIXME: isRendered is never evaluated here; super call can be removed
		super.encodeBegin(context);

		// get YComponentInfo for current processed component
		final YComponentHandler cmpInfo = this.getYComponent();

		// YComponentInfo must be validated when HtmlYComponentHandler detected a change in Facelet file
		//boolean isValidated = !this.isValidateYComponentInfo();
		if (!cmpInfo.isValidated()) {
			final YComponentValidator cmpValid = cmpInfo.createValidator();
			try {
				this.validateComponentInfo(cmpValid);
				((YComponentHandlerImpl) cmpInfo).setValidated(true);
			} catch (final YFacesException e) {
				log.error("Validation error", e);
				this.error = e;
			}
		}

		// if component is valid ...
		Object cmp = null;
		if (cmpInfo.isValidated()) {
			try {
				// ... get or create appropriate YComponent instance
				cmp = this.getOrCreateComponentModel(cmpInfo);

			} catch (final YFacesException e) {
				log.error("Error creating " + YModel.class.getSimpleName() + " ("
						+ cmpInfo.getConfiguration().getModelImplementation() + ")", e);
				this.error = e;
			}
		}

		// if a component is available...
		if (cmp != null) {

			// ... generate html debug output when enabled
			if (YFacesConfig.ENABLE_HTML_DEBUG.getBoolean()) {
				this.generateHtmlDebug("Start ");
			}

			// ... inject viewToModel attributes into component
			try {
				this.pushAttributesIntoModel(cmp, cmpInfo);
			} catch (final Exception e) {
				log.error(logId + "Error injecting component attributes", e);
				this.error = e;
			}

			// invoke phase: YComponent#validate
			if (this.error == null) {
				try {

					//cmpInfo.getModelProcessor().validateModel(cmp);
					if (cmp instanceof YModel) {
						((YModel) cmp).validate();
					}

				} catch (final Exception e) {
					log.error(logId + "Error while validating component", e);
					this.error = e;
				}
			}

			this.verifyRenderTimeID();

			// set YComponent
			this.setComponentModel(cmp);

			// set var value
			this.setVarValue(cmp);

			// //give YComponent instance a uid
			// ((AbstractYComponent)cmp).setId(super.getClientId(context));
		}
	}

	@Override
	public void encodeChildren(final FacesContext context) throws IOException {

		// an error is expected here, otherwise throw an exception
		if (this.error == null) {
			throw new YFacesException("Illegal " + HtmlYComponent.class.getSimpleName()
					+ " state; error handling finds no error");
		}
		try {
			// work with requested errorHandling or take default configuration
			String _errorHandling = this.errorHandling;
			if (_errorHandling == null || _errorHandling.trim().length() == 0) {
				_errorHandling = cmpInfo.getErrorHandling();
			}

			// only produce error message output when requested
			if (!"none".equals(_errorHandling)) {

				final ResponseWriter writer = FacesContext.getCurrentInstance().getResponseWriter();
				writer.startElement("div", this);
				writer.writeAttribute("style", "color: red;font-weight:bold", null);

				if ("info".equals(_errorHandling)) {
					writer.writeText("An error occured", null);
				} else {
					final String id = this.getYComponent().getViewId();
					writer.writeText("Error rendering '" + id + "'", null);
					writer.startElement("div", this);
					writer.writeAttribute("style", "color:red;font-style:italic;font-weight:normal", null);

					final String msg = "debug".equals(_errorHandling) ? error.getClass().getSimpleName()
							: error.getMessage();

					writer.writeText("(" + msg + ")", null);
					writer.endElement("div");
				}

				writer.endElement("div");
				// ((AbstractYComponent) getYComponent()).setErrorMessage(null);
			}
		} catch (final Exception e) {
			log.error("Error while generating HTML debug comment: " + e.getMessage());
		}

		// TODO Auto-generated method stub
		// super.encodeChildren(context);
	}

	@Override
	public boolean getRendersChildren() {

		final boolean result = (this.error != null) ? true : super.getRendersChildren();
		return result;
	}

	/**
	 * Returns a {@link YComponentHandler} which provides YFaces specific meta-information for this
	 * component.
	 * 
	 * @return {@link YComponentHandler}
	 */
	protected YComponentHandler getYComponent() {
		if (cmpInfo == null) {
			cmpInfo = YFaces.getApplicationContext().getComponentHandlers().getComponentByPath(
					this.viewLocation);
		}
		return cmpInfo;
	}

	protected void setComponent(final YComponentHandler yCmp) {
		this.cmpInfo = yCmp;
		this.viewLocation = yCmp.getViewLocation();
	}

	private void validateComponentInfo(final YComponentValidator cmpValid) {

		if (log.isDebugEnabled()) {
			log.debug("Validating component " + getYComponent().getViewLocation());
		}

		final Set<YValidationAspekt> errors = new HashSet<YValidationAspekt>(cmpValid
				.validateComponent());
		errors.remove(YValidationAspekt.VIEW_ID_NOT_SPECIFIED);
		errors.remove(YValidationAspekt.SPEC_IS_MISSING);

		final String errorString = YValidationAspekt.getFormattedErrorMessage(errors, getYComponent(),
				null);
		if (errorString != null) {
			throw new YFacesException(errorString);
		}

	}

	/**
	 * Gets or creates the {@link YModel} instance for this UiComponent.
	 * <p/>
	 * Evaluates the result of {@link ValueExpression} 'binding'.<br/>
	 * If 'binding' is not set, a new {@link YModel} instance is always created.<br/>
	 * If 'binding' is set, but returns null, a new {@link YModel} gets created and 'binding' gets
	 * set.<br/>
	 * If 'binding' returns a value, that value is taken, but validated whether it matches
	 * {@link YComponentHandler} criteria.
	 * 
	 * @return {@link YModel}
	 */
	private Object getOrCreateComponentModel(final YComponentHandler cmpInfo) {
		Object result = null;

		final Object value = this.getYModelFromBinding();

		// when no binding is available or 'binding' returned 'null' ...
		if (value == null) {

			// ...create a default Model 
			result = cmpInfo.createModel();

			// ...and update ValueBinding (if any)
			this.setYModelToBinding(result);

			// if a Model instance is available...
		} else {

			// ...validate passed model 
			final YComponentValidator cmpValid = cmpInfo.createValidator();
			final Set<YValidationAspekt> errors = cmpValid.validateImplementationClass(value.getClass());
			//...and stop processing this component in case of any errors
			if (!errors.isEmpty()) {
				throw new YFacesException(YValidationAspekt.getFormattedErrorMessage(errors, cmpInfo, value
						.getClass()));
			}

			result = value;

			if (log.isDebugEnabled()) {
				log.debug(logId + "found valid Component (" + result.getClass().getSimpleName() + ")");
			}
		}
		return result;

	}

	/**
	 * Verifies the component ID for duplicates. This may happen when the component is embedded into
	 * some compile time tags like &lt;c:forEach&gt;.
	 */
	private void verifyRenderTimeID() {
		// when component is transient no check must be performed
		if (!super.isTransient()) {

			final Map map = getFacesContext().getExternalContext().getRequestMap();

			// create new ID when used within a)compile time tags or b)within
			// same naming container
			Set<String> set = (Set) map.get(ID_DUPLICATECHECK_KEY);
			if (set == null) {
				map.put(ID_DUPLICATECHECK_KEY, set = new HashSet<String>());
			}
			final String id = super.getClientId(getFacesContext());
			final boolean isDupplicate = !set.add(id);

			if (isDupplicate) {
				final FacesContext fc = getFacesContext();
				final String key = super.getClientId(fc) + "_COUNT";
				Integer count = (Integer) map.get(key);
				int result = 1;
				if (count != null) {
					result = count.intValue() + 1;
				}
				count = new Integer(result);
				map.put(key, count);

				final String newId = super.getId() + "_" + result;
				log.warn("Change duplicate id from " + getId() + " to " + newId);
				super.setId(newId);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.faces.component.UIComponentBase#encodeEnd(javax.faces.context. FacesContext)
	 */
	@Override
	public void encodeEnd(final FacesContext context) throws IOException {
		// Nullpointer and isRendered checks
		super.encodeEnd(context);

		// release variables for model and controller
		this.setVarValue(null);

		// release possible ID attribute
		final ValueExpression vb = getFacesContext().getApplication().getExpressionFactory()
				.createValueExpression(getFacesContext().getELContext(), "#{id}", Object.class);
		vb.setValue(getFacesContext().getELContext(), null);

		if (YFacesConfig.ENABLE_HTML_DEBUG.getBoolean()) {
			this.generateHtmlDebug("End ");
		}

	}

	@Override
	public void broadcast(final FacesEvent arg0) throws AbortProcessingException {
		if (arg0 instanceof HtmlYComponentFacesEvent) {
			((HtmlYComponent) arg0.getComponent()).refreshVarValue();
		}
		super.broadcast(arg0);
	}

	@Override
	public void queueEvent(final FacesEvent event) {
		// Problem:
		// after a phase has finished, event listeners gets notified (broadcast)
		// But when the same YComponent is used multiple times at one page
		// (loop or simple multiple uses) the var-value is always that one of
		// the
		// last processed YComponent
		// The result is that always the listeners of these YComponent gets
		// invoked

		// Solution:
		// to assure correct YComponent processing (or better correct listener
		// processing)
		// a custom event is added before the original event;
		// the YComponent broadcast handles this custom event and refreshes the
		// var-value
		super.queueEvent(new HtmlYComponentFacesEvent(this, event.getPhaseId()));
		super.queueEvent(event);

		// Note(1): another solution
		// an additional FacesListener /for the action uicomponent will work
		// too;
		// however since addFacesListener() is protected it is not (without a
		// hack) directly
		// callable at the uicomponent

		// Note(2): another (and more elegant solution) would be an own
		// ValueBinding implementation
		// for YComponent; this would also remove the need of var-refreshment as
		// first statement of
		// every process###() method
	}

	/**
	 * Iterates over all writable properties of the passed {@link YModel}. If a properties name
	 * matches an attributes name of this component, the value of the attribute gets injected into the
	 * passed {@link YModel} instance.
	 * 
	 * @param cmp
	 *          {@link YModel}
	 */
	private void pushAttributesIntoModel(final Object cmp, final YComponentHandler cmpInfo) {

		final Collection<String> attributes = cmpInfo.getPushProperties();

		// now go through all attributes which shall be injected
		if (attributes != null) {
			for (final String attribute : attributes) {

				// attribute value may either be a ValueExpression or a Literal
				final ValueExpression vb = getValueExpression(attribute);
				final Object value = (vb != null) ? vb.getValue(getFacesContext().getELContext())
						: getAttributes().get(attribute);

				// when a value can be found
				if (value != null) {
					cmpInfo.setModelProperty(cmp, attribute, value);
				}
			}
		}
	}

	/**
	 * Creates HTML debug output. Forwards the content directly into the ResponseWriter.
	 * 
	 * @param cmp
	 *          {@link YModel}
	 * @param prefix
	 *          general prefix
	 */
	private void generateHtmlDebug(final String prefix) {
		if (this.debugHtmlOut == null) {
			final YComponentHandler yInfo = getYComponent();
			debugHtmlOut = yInfo.getViewId() + ":" + yInfo.getViewLocation();
		}
		try {
			FacesContext.getCurrentInstance().getResponseWriter().writeComment(
					"[YFACES] " + prefix + debugHtmlOut);
		} catch (final Exception e) {
			log.error("Error while generating HTML debug comment: " + e.getMessage());
		}

	}

}
