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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

import de.hybris.yfaces.YFacesConfig;
import de.hybris.yfaces.YFacesELContext;
import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.component.DefaultYComponentInfo;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YComponentValidator;
import de.hybris.yfaces.component.YComponentValidator.YValidationAspekt;

/**
 * Each {@link YComponent} must be enclosed by this {@link UIComponent}.<br/>
 * The enclosed {@link YComponent} must be either passed or, when null, gets automatically
 * instantiated via the <code>default</code> Attribute.<br/>
 * An undefined amount of additional attributes can be defined.<br/>
 * Each of such attribute must be available as property (setter) at the {@link YComponent} instance.<br/>
 * When such attributes are passed then the value gets injected into the {@link YComponent}. <br/>
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
	private String implClassName = null;
	private String specClassName = null;
	private String[] injectableProperties = null;
	private String errorHandling = null;

	// properties are set by HtmlYComponentHandler ...
	// ... the YComponentInfo for this UIComponent
	private transient DefaultYComponentInfo cmpInfo = null;
	// ... whether YComponentInfo needs validation (e.g. when view-file was modified)
	private transient boolean isValidateCmpInfo = false;

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

	/**
	 * @return value of 'impl' attribute
	 */
	public String getImpl() {
		return this.implClassName;
	}

	/**
	 * Sets value of 'impl' attribute.
	 * 
	 * @param value
	 *          value to set
	 */
	public void setImpl(final String value) {
		this.implClassName = value;
	}

	/**
	 * @return value of 'spec' attribute
	 */
	public String getSpec() {
		return this.specClassName;
	}

	/**
	 * Sets value of 'spec' attribute.
	 * 
	 * @param value
	 *          value to set
	 */
	public void setSpec(final String interfaceClass) {
		this.specClassName = interfaceClass;
	}

	@Override
	public void setId(final String id) {
		super.setId(id);
	}

	@Override
	public String getId() {
		return super.getId();
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
		return (String) super.getAttributes().get(YComponentInfo.VAR_ATTRIBUTE);
	}

	/**
	 * Set {@link YComponent} instance as value of variable whose name was passed as 'var'
	 * 
	 * @param component
	 *          {@link YComponent}
	 */
	private void setVarValue(final YComponent component) {
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
	}

	private void refreshVarValue() {
		final YComponent component = getYComponent();
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
	}

	//	/**
	//	 * Returns the {@link ValueExpression} which binds a YComponent. Returns 'null' when no binding is
	//	 * available.
	//	 * 
	//	 * @return {@link ValueExpression}
	//	 */
	//	private ValueExpression getYComponentBinding() {
	//		final ValueExpression vb = getValueExpression(PARAM_YCMP_BINDING);
	//		return vb;
	//	}

	//	/**
	//	 * Sets the {@link ValueExpression} which binds a YComponent.
	//	 * 
	//	 * @param binding
	//	 */
	protected void setYComponentBinding(final ValueExpression binding) {
		super.setValueExpression(PARAM_YCMP_BINDING, binding);
	}

	/**
	 * Sets the {@link YComponent}.<br/>
	 * Setting means injecting the value into the writable binding.<br/>
	 * 
	 * @param cmp
	 *          {@link YComponent}
	 */
	private void setValue(final YComponent cmp) {
		final ValueExpression vb = getValueExpression(PARAM_YCMP_BINDING);

		if (vb != null) {
			final ELContext elCtx = FacesContext.getCurrentInstance().getELContext();
			final YFacesELContext yCtx = (YFacesELContext) FacesContext.getCurrentInstance()
					.getELContext().getContext(YFacesELContext.class);

			yCtx.setResolveYComponentBinding(false);
			final Object value = vb.getValue(elCtx);
			if (value instanceof YComponentBinding) {
				((YComponentBinding<YComponent>) value).setValue(cmp);
			}

			if (value == null) {
				if (YComponent.class.isAssignableFrom(vb.getType(elCtx))) {
					vb.setValue(elCtx, cmp);
				} else {
					final YComponentInfo cmpInfo = cmp.getComponentInfo();
					final YComponentBinding<YComponent> binding = new YComponentBinding<YComponent>(cmpInfo);
					vb.setValue(elCtx, binding);
					binding.setValue(cmp);
				}
			}

			yCtx.setResolveYComponentBinding(true);
		}
	}

	/**
	 * Returns the final {@link YComponent} which is used for rendering.<br/>
	 * This is the merge result of the passed {@link YComponent} and passed model attributes.<br/>
	 * 
	 * @return {@link YComponent}
	 */
	public YComponent getYComponent() {
		// it's possible that one HtmlYComponent instance is used with multiple YComponent instances
		// example for that are loops like dataList, table etc..
		// in that case UIComponents and YComponent instances are similar
		// therefore the YComponent instance is mapped to the UIComponent instance via it's clientid
		final String key = super.getClientId(getFacesContext());
		final Map<String, Object> map = getStateMap();
		final YComponent result = (YComponent) map.get(key + "_COMPONENT");
		return result;
	}

	/**
	 * Internal.<br/>
	 * Sets the final {@link YComponent} which shall be used.<br/>
	 * Normally this is done after all passed {@link YComponent} Attributes are injected.<br/>
	 * 
	 * @param cmp
	 *          {@link YComponent} to set.
	 */
	private void setYComponent(final YComponent cmp) {
		final String clientId = super.getClientId(getFacesContext());
		final Map<String, Object> map = getStateMap();
		map.put(clientId + "_COMPONENT", cmp);
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
		values[i++] = this.specClassName;
		values[i++] = this.implClassName;
		values[i++] = this.injectableProperties;

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
		this.specClassName = (String) values[i++];
		this.implClassName = (String) values[i++];
		this.injectableProperties = (String[]) values[i++];

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
		final YComponent restored = getYComponent();

		// it's possible that a previously unrenderable component can be renderable after a
		// faces request, in that case a YComponent instance isn't available yet
		if (restored != null) {

			// set 'var' before any child processing starts
			// this is necessary to assure that 'rendered' Attributes work properly
			this.setVarValue(restored);

			// only has an affect when component is backed by a writable
			// ValueBinding
			// XXX 1.2 simulate old behavior that non-framed components aren't
			// available
			if (restored.getFrame() != null) {
				this.setValue(restored);
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
		final YComponent restored = getYComponent();
		if (restored != null) {
			this.setVarValue(restored);
			super.processUpdates(arg0);
		}
	}

	@Override
	public void processValidators(final FacesContext arg0) {
		// set 'var' value again
		// necessary when using this component within a loop
		final YComponent restored = getYComponent();
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
		final DefaultYComponentInfo cmpInfo = this.getYComponentInfo();

		// YComponentInfo must be validated when HtmlYComponentHandler detected a change in Facelet file
		boolean isValidated = !this.isValidateYComponentInfo();
		if (!isValidated) {
			final YComponentValidator cmpValid = cmpInfo.createValidator();
			try {
				this.validateComponentInfo(cmpValid);
				isValidated = true;
			} catch (final YFacesException e) {
				log.error("Validation error", e);
				this.error = e;
			}
		}

		// if component is valid ...
		YComponent cmp = null;
		if (isValidated) {
			try {
				// ... get or create appropriate YComponent instance
				cmp = this.getOrCreateYComponent(cmpInfo);

			} catch (final YFacesException e) {
				log.error("Error creating " + YComponent.class.getSimpleName() + " ("
						+ cmpInfo.getImplementation() + ")", e);
				this.error = e;
			}
		}

		// if a component is available...
		if (cmp != null) {

			// ... generate html debug output when enabled
			if (YFacesConfig.ENABLE_HTML_DEBUG.getBoolean()) {
				this.generateHtmlDebug(cmp, "Start ");
			}

			this.isValidateCmpInfo = false;

			// ... inject viewToModel attributes into component
			try {
				this.injectAttributes(cmp, cmpInfo);
			} catch (final Exception e) {
				log.error(logId + "Error injecting component attributes", e);
				this.error = e;
			}

			// invoke phase: YComponent#validate
			if (this.error == null) {
				try {
					cmp.validate();
				} catch (final Exception e) {
					log.error(logId + "Error while validating component", e);
					this.error = e;
				}
			}

			this.verifyRenderTimeID();

			// set YComponent
			this.setYComponent(cmp);

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
					final String id = this.getYComponentInfo().getId();
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
	 * Returns a {@link YComponentInfo} which provides YFaces specific meta-information for this
	 * component.
	 * 
	 * @return {@link YComponentInfo}
	 */
	protected DefaultYComponentInfo getYComponentInfo() {
		if (cmpInfo == null) {
			cmpInfo = new DefaultYComponentInfo(getId(), getVarName(), this.getSpec(), this.getImpl());
		}
		return cmpInfo;
	}

	protected void setComponentInfo(final DefaultYComponentInfo cmpInfo) {
		this.cmpInfo = cmpInfo;
	}

	protected boolean isValidateYComponentInfo() {
		return isValidateCmpInfo;
	}

	protected void setValidateYComponentInfo(final boolean isValidateCmpInfo) {
		this.isValidateCmpInfo = isValidateCmpInfo;
	}

	private void validateComponentInfo(final YComponentValidator cmpValid) {

		if (log.isDebugEnabled()) {
			log.debug("Validating component " + getYComponentInfo().getLocation());
		}

		final Set<YValidationAspekt> errors = new HashSet<YValidationAspekt>(cmpValid.verifyComponent());
		errors.remove(YValidationAspekt.VIEW_ID_NOT_SPECIFIED);
		errors.remove(YValidationAspekt.SPEC_IS_MISSING);

		final String errorString = YValidationAspekt.getFormattedErrorMessage(errors, cmpValid
				.getYComponentInfo(), null);
		if (errorString != null) {
			throw new YFacesException(errorString);
		}

	}

	/**
	 * Gets or creates the {@link YComponent} instance for this UiComponent.
	 * <p/>
	 * Evaluates the result of {@link ValueExpression} 'binding'.<br/>
	 * If 'binding' is not set, a new {@link YComponent} instance is always created.<br/>
	 * If 'binding' is set, but returns null, a new {@link YComponent} gets created and 'binding' gets
	 * set.<br/>
	 * If 'binding' returns a value, that value is taken, but validated whether it matches
	 * {@link YComponentInfo} criteria.
	 * 
	 * @return {@link YComponent}
	 */
	private YComponent getOrCreateYComponent(final YComponentInfo cmpInfo) {
		YComponent result = null;

		// 'binding' value expression which binds a YComponent instance
		final ValueExpression binding = getValueExpression(PARAM_YCMP_BINDING);
		final ELContext eCtx = getFacesContext().getELContext();
		// get value of that binding
		final Object value = (binding != null) ? binding.getValue(eCtx) : null;

		// when no binding is available or 'binding' returned 'null' ...
		if (value == null) {

			// ...create a default YComponent 
			result = ((DefaultYComponentInfo) cmpInfo).createComponent();

			// ...and update ValueBinding (if any)
			this.setValue(result);

			// if a YComponent instance is available...
		} else {
			// ...validate it
			final YComponentValidator cmpValid = cmpInfo.createValidator();
			final Set<YValidationAspekt> errors = cmpValid.assertCustomImplementationClass(value
					.getClass());
			//...and stop processing this component in case of any errors
			if (!errors.isEmpty()) {
				throw new YFacesException(YValidationAspekt.getFormattedErrorMessage(errors, cmpInfo, value
						.getClass()));
			}

			result = (YComponent) value;

			//			// When created via a Frame a YComponent has per default no ID
			//			// in that case use UIComponent id (the unchanged one before
			//			// duplicate check)
			//			if (result.getId() == null) {
			//				((AbstractYComponent) result).setId(getId());
			//			}

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
				int result = 0;
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
			this.generateHtmlDebug(getYComponent(), "End ");
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
	 * Iterates over all writable properties of the passed {@link YComponent}. If a properties name
	 * matches an attributes name of this component, the value of the attribute gets injected into the
	 * passed {@link YComponent} instance.
	 * 
	 * @param cmp
	 *          {@link YComponent}
	 */
	private void injectAttributes(final YComponent cmp, final DefaultYComponentInfo cmpInfo) {

		// attributes are given as comma separated list ("injectable"
		// attribute)
		// e.g. <yf:component ... injectable="myProperty1,myProperty2"
		//final String[] attributes = this.getInjectableProperties();
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
					this.pushProperty(cmp, attribute, value, cmpInfo);
				}
			}
		}
	}

	public void pushProperty(final YComponent cmp, final String property, Object value,
			final DefaultYComponentInfo cmpInfo) {
		final Method method = cmpInfo.getAllProperties().get(property);

		try {

			// JSF 1.2: do type coercion (e.g. String->Integer)
			value = FacesContext.getCurrentInstance().getApplication().getExpressionFactory()
					.coerceToType(value, method.getParameterTypes()[0]);

			// invoke setter
			method.invoke(cmp, value);

		} catch (final Exception e) {
			if (e instanceof IllegalArgumentException) {
				log.error(cmpInfo.getId() + " Error converting " + value.getClass().getName() + " to "
						+ method.getParameterTypes()[0].getName());
			} else {
				if (e instanceof InvocationTargetException) {
					log.error(cmpInfo.getId() + " Error while executing setter for attribute '" + property
							+ "'");
				}
			}
			final String error = cmpInfo.getId() + " Error setting attribute '" + property + "' at "
					+ cmp.getClass().getSimpleName() + "(" + method + ")";
			throw new YFacesException(error, e);
		}

		// some nice debug output for bughunting
		if (log.isDebugEnabled()) {
			final String _value = (value != null) ? value.toString() : "null";
			String suffix = "";
			if (value instanceof Collection<?>) {
				suffix = "(count:" + ((Collection<?>) value).size() + ")";
			}

			log.debug(cmpInfo.getId() + "injected Attribute " + property + " ("
					+ (_value.length() < 30 ? _value : _value.substring(0, 29).concat("...")) + ")" + suffix);
		}

	}

	/**
	 * Creates HTML debug output. Forwards the content directly into the ResponseWriter.
	 * 
	 * @param cmp
	 *          {@link YComponent}
	 * @param prefix
	 *          general prefix
	 */
	private void generateHtmlDebug(final YComponent cmp, final String prefix) {
		if (this.debugHtmlOut == null) {
			final YComponentInfo yInfo = cmp.getComponentInfo();
			debugHtmlOut = "???";
			if (yInfo != null) {
				final String _file = yInfo.getURL().toExternalForm();
				final String _frame = cmp.getFrame() != null ? cmp.getFrame().getId() : "unbound";
				debugHtmlOut = "Component: " + _file + " (Frame:" + _frame + ")";
			}
		}
		try {
			FacesContext.getCurrentInstance().getResponseWriter().writeComment(
					"[YFACES] " + prefix + debugHtmlOut);
		} catch (final Exception e) {
			log.error("Error while generating HTML debug comment: " + e.getMessage());
		}

	}

}
