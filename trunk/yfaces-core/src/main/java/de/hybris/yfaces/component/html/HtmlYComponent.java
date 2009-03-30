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
package de.hybris.yfaces.component.html;

import java.io.IOException;
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
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesConfig;
import de.hybris.yfaces.YFacesELContext;
import de.hybris.yfaces.YFacesException;
import de.hybris.yfaces.component.AbstractYComponent;
import de.hybris.yfaces.component.YComponent;
import de.hybris.yfaces.component.YComponentBinding;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YComponentRegistry;
import de.hybris.yfaces.component.YComponentInfo.ERROR_STATE;

/**
 * Each {@link YComponent} must be enclosed by this {@link UIComponent}.<br/>
 * The enclosed {@link YComponent} must be either passed or, when null, gets automatically
 * instantiated via the <code>default</code> Attribute.<br/>
 * An undefined amount of additional attributes can be defined.<br/>
 * Each of such attribute must be available as property (setter) at the {@link YComponent} instance.<br/>
 * When such attributes are passed then the value gets injected into the {@link YComponent}. <br/>
 * This component works within compile time and render time tags.<br/>
 * 
 * @author Denny.Strietzbaum
 * 
 */
public class HtmlYComponent extends UIComponentBase implements NamingContainer {

	private static final Logger log = Logger.getLogger(HtmlYComponent.class);

	public final static String COMPONENT_TYPE = "hybris.YComponent";
	public final static String COMPONENT_FAMILY = "facelets";

	private static final String PARAM_YCMP_BINDING = "yfaces.HtmlYComponent.YComponentBinding";
	private static final String PARAM_VAR = "var";
	private static final String PARAM_INJECTABLE = "injectable";

	private static final String ID_DUPLICATECHECK_KEY = HtmlYComponent.class.getName() + "idSet";

	private String implClassName = null;
	private String specClassName = null;

	private String[] injectableProperties = null;

	// transient members
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
	 * Sets the default Component.<br/>
	 * An instance is created and used when no component was given. <br/>
	 * 
	 * @param defaultClass
	 *            name of class
	 */
	public void setImpl(final String defaultClass) {
		this.implClassName = defaultClass;
	}

	/**
	 * Sets the definition (interface) for this Component. This is an optional but recommended
	 * property.<br./>
	 * 
	 * @param interfaceClass
	 *            interface
	 */
	public void setSpec(final String interfaceClass) {
		this.specClassName = interfaceClass;
	}

	/**
	 * Returns the name of the variable.<br/>
	 * 
	 * @return String
	 */
	private String getVarName() {
		return (String) super.getAttributes().get(PARAM_VAR);
	}

	/**
	 * Returns the {@link ValueExpression} which binds a YComponent. Returns 'null' when no binding
	 * is available.
	 * 
	 * @return {@link ValueExpression}
	 */
	private ValueExpression getYComponentBinding() {
		final ValueExpression vb = getValueExpression(PARAM_YCMP_BINDING);
		return vb;
	}

	/**
	 * Sets the {@link ValueExpression} which binds a YComponent.
	 * 
	 * @param binding
	 */
	protected void setYComponentBinding(final ValueExpression binding) {
		super.setValueExpression(PARAM_YCMP_BINDING, binding);
	}

	/**
	 * Sets the {@link YComponent}.<br/>
	 * Setting means injecting the value into the writable binding.<br/>
	 * 
	 * @param cmp
	 *            {@link YComponent}
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
				((YComponentBinding) value).setValue(cmp);
			}

			if (value == null) {
				if (YComponent.class.isAssignableFrom(vb.getType(elCtx))) {
					vb.setValue(elCtx, cmp);
				} else {
					YComponentBinding binding = new YComponentBinding<YComponent>(this.getId());
					vb.setValue(elCtx, binding);
					binding.setValue(cmp);
				}
			}

			yCtx.setResolveYComponentBinding(true);
		}

		// if (this.isValueBindingWriteable(vb))
		// {
		// vb.setValue(getFacesContext().getELContext(), cmp);
		// }
	}

	/**
	 * Returns the class for the default {@link YComponent}.<br/>
	 * An instance is created when no value was passed.<br/>
	 * 
	 * @return class of default component.
	 */
	public String getImpl() {
		return this.implClassName;
	}

	/**
	 * Returns the class of the interface which must be assignable to the passed {@link YComponent}.
	 * 
	 * @return class of interface
	 */
	public String getSpec() {
		return this.specClassName;
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
	 *            {@link YComponent} to set.
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
		// return
		// Storefoundation.getInstance().getNavigationContext().getCurrentView().getAttributes();
	}

	/**
	 * Set {@link YComponent} instance as value of variable whose name was passed as 'var'
	 * 
	 * @param component
	 *            {@link YComponent}
	 */
	private void setVarValue(final YComponent component) {
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
	}

	private void refreshVarValue() {
		final YComponent component = getYComponent();
		getFacesContext().getExternalContext().getRequestMap().put(getVarName(), component);
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

		// we can't restore bindings here as the ViewRoot isn't completed yet
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

		// retrieve some meta information
		final YComponentInfo cmpInfo = this.getYComponentInfo();

		final YComponent cmp = this.getOrCreateYComponent(cmpInfo);

		// generate some html debug output when enabled
		if (YFacesConfig.ENABLE_HTML_DEBUG.getBoolean()) {
			this.generateHtmlDebug(cmp, "Start ");
		}

		// inject attributes
		this.injectAttributes(cmpInfo, cmp);

		// invoke components postinitialize()
		cmp.postInitialize();

		this.verifyRenderTimeID();

		// set YComponent
		this.setYComponent(cmp);

		// set var value
		this.setVarValue(cmp);

		// //give YComponent instance a uid
		// ((AbstractYComponent)cmp).setId(super.getClientId(context));
	}

	/**
	 * Returns a {@link YComponentInfo} which matches the {@link YComponent} bound to this
	 * {@link UIComponent} instance.
	 * 
	 * @return {@link YComponentInfo}
	 */
	private YComponentInfo getYComponentInfo() {
		// validation
		final YComponentInfo cmpInfo = new YComponentInfo(getId(), getVarName(), this
				.getSpec(), this.getImpl());
		final Set<ERROR_STATE> errors = new HashSet<ERROR_STATE>(cmpInfo.verifyComponent());
		errors.remove(ERROR_STATE.VIEW_ID_NOT_SPECIFIED);
		errors.remove(ERROR_STATE.SPEC_IS_MISSING);

		final String errorString = ERROR_STATE.getFormattedErrorMessage(errors, cmpInfo, null);
		if (errorString != null) {
			throw new YFacesException(errorString);
		}
		return cmpInfo;
	}

	private YComponent getOrCreateYComponent(final YComponentInfo cmpInfo) {
		YComponent cmp = null;

		// retrieve passed value (ycomponent instance)
		final ValueExpression binding = getYComponentBinding();
		final Object _cmp = binding != null ? binding.getValue(getFacesContext().getELContext())
				: null;

		// when no value was passed...
		if (_cmp == null) {
			// create default implementation
			cmp = cmpInfo.createDefaultComponent();

			// only has an affect when component is backed by a writable
			// ValueBinding
			this.setValue(cmp);
		} else {
			final Set<ERROR_STATE> errors = cmpInfo
					.assertCustomImplementationClass(_cmp.getClass());
			if (!errors.isEmpty()) {
				throw new YFacesException(ERROR_STATE.getFormattedErrorMessage(errors, cmpInfo,
						_cmp.getClass()));
			}

			cmp = (YComponent) _cmp;

			// When created via a Frame a YComponent has per default no ID
			// in that case use UIComponent id (the unchanged one before
			// duplicate check)
			if (cmp.getId() == null) {
				((AbstractYComponent) cmp).setId(getId());
			}

			log.debug(logId + "found valid Component (" + cmp.getClass().getSimpleName() + ")");
		}

		return cmp;
	}

	/**
	 * Verifies the component ID for duplicates. This may happen when the component is embedded into
	 * some compile time tags like &lt;c:forEach&gt;.
	 */
	private void verifyRenderTimeID() {
		// when component is transient no check must be performed
		if (!super.isTransient()) {
			// create new ID when used within a)compile time tags or b)within
			// same naming container
			Set set = (Set) getFacesContext().getExternalContext().getRequestMap().get(
					ID_DUPLICATECHECK_KEY);
			if (set == null) {
				getFacesContext().getExternalContext().getRequestMap().put(ID_DUPLICATECHECK_KEY,
						set = new HashSet<String>());
			}
			final String id = super.getClientId(getFacesContext());
			final boolean isDupplicate = !set.add(id);

			if (isDupplicate) {
				final FacesContext fc = getFacesContext();
				final Map<Object, Object> map = (Map) fc.getExternalContext().getRequestMap();
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
		if (arg0 instanceof YComponentActionEvent) {
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
		super.queueEvent(new YComponentActionEvent(this, event.getPhaseId()));
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
	 * matches an attributes name of this component, the value of the attribute gets injected into
	 * the passed {@link YComponent} instance.
	 * 
	 * @param cmp
	 *            {@link YComponent}
	 */
	private void injectAttributes(final YComponentInfo cmpInfo, final YComponent cmp) {
		final Map<String, Method> attributeToMethodMap = cmpInfo.getAllComponentProperties();

		// attributes can be given as comma separated list ("injectable"
		// attribute)
		// e.g. <yf:component ... injectable="myProperty1,myProperty2"
		// in that case name of attribute must match the components setter name
		String[] attributes = this.getInjectableProperties();

		// attributes can be given as separate mapping
		// e.g. <yf:component ... myProperty1 =
		// "#{passedProperty}  myProperty2 = "#{otherProperty}">
		if (attributes == null) {
			attributes = attributeToMethodMap.keySet().toArray(new String[] {});
		}

		// now go through all attributes which shall be injected
		for (final String attribute : attributes) {
			// attribute value may either be a ValueExpression or a Literal
			final ValueExpression vb = getValueExpression(attribute);
			Object value = (vb != null) ? vb.getValue(getFacesContext().getELContext())
					: getAttributes().get(attribute);

			// when a value can be found
			if (value != null) {
				try {
					// JSF 1.1
					// attrValue = getConvertedAttributeValue(attrValue,
					// entry.getValue());

					// JSF 1.2: do type coercion (e.g. String->Integer)
					final Method method = attributeToMethodMap.get(attribute);
					value = FacesContext.getCurrentInstance().getApplication()
							.getExpressionFactory().coerceToType(value,
									method.getParameterTypes()[0]);

					// and finally inject value
					method.invoke(cmp, value);
				} catch (final Exception e) {
					throw new YFacesException(logId + ": can't set attribute " + attribute
							+ " (argument mismatch?)", e);
				}

				// some nice debug output for bughunting
				if (log.isDebugEnabled()) {
					final String _value = (value != null) ? value.toString() : "null";
					String suffix = "";
					if (value instanceof Collection) {
						suffix = "(count:" + ((Collection) value).size() + ")";
					}

					log.debug(logId
							+ "injected Attribute "
							+ attribute
							+ " ("
							+ (_value.length() < 30 ? _value : _value.substring(0, 29)
									.concat("...")) + ")" + suffix);
				}
			}
		}
	}

	// //JSF 1.1
	// private Object getConvertedAttributeValue(Object value, Method
	// injectMethod)
	// {
	// Object result = value;
	//		
	// //when value is a String
	// if (value instanceof String)
	// {
	// Class<?> paramType = injectMethod.getParameterTypes()[0];
	//
	// //...but should be 'null' (when String is explicitly set empty)
	// if (!paramType.equals(String.class) && ((String)result).length() == 0)
	// return null;
	//			
	// //...or must be handled as a primitive
	// if (paramType.isPrimitive())
	// paramType = ClassUtils.primitiveToWrapper(paramType);
	//			
	// //...or as a primitive wrapper class (Number, Boolean)
	// if (Number.class.isAssignableFrom(paramType) ||
	// Boolean.class.isAssignableFrom(paramType))
	// {
	// try
	// {
	// Constructor<?> c = paramType.getConstructor(String.class);
	// //create wrapper instance
	// result = c.newInstance(result);
	// }
	// catch (Exception e)
	// {
	// throw new YFacesException("Error converting attribute", e);
	// }
	// }
	// }
	//		
	// return result;
	// }

	/**
	 * Creates HTML debug output. Forwards the content directly into the ResponseWriter.
	 * 
	 * @param cmp
	 *            {@link YComponent}
	 * @param prefix
	 *            general prefix
	 */
	private void generateHtmlDebug(final YComponent cmp, final String prefix) {
		if (this.debugHtmlOut == null) {
			final YComponentInfo yInfo = YComponentRegistry.getInstance().getComponent(cmp.getId());
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

	/**
	 * This component brings an own event which refreshes the value for the 'var' ValueBinding.<br/>
	 * This event must be added before any other event.
	 * 
	 * @see HtmlYComponent#queueEvent(FacesEvent)
	 * @see HtmlYComponent#broadcast(FacesEvent)
	 */
	private static class YComponentActionEvent extends FacesEvent {
		public YComponentActionEvent(final HtmlYComponent source, final PhaseId phaseId) {
			super(source);
			super.setPhaseId(phaseId);
		}

		@Override
		public boolean isAppropriateListener(final FacesListener faceslistener) {
			throw new YFacesException("", new UnsupportedOperationException());
		}

		@Override
		public void processListener(final FacesListener faceslistener) {
			throw new YFacesException("", new UnsupportedOperationException());
		}
	}

	protected String[] getInjectableProperties() {
		if (this.injectableProperties == null && getAttributes().get(PARAM_INJECTABLE) != null) {
			final String s = (String) getAttributes().get(PARAM_INJECTABLE);
			this.injectableProperties = s.trim().split("\\s*,\\s*");
		}
		return this.injectableProperties;
	}

}
