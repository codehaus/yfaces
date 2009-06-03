package de.hybris.yfaces;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.el.ELContextListener;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.MethodBinding;
import javax.faces.el.PropertyResolver;
import javax.faces.el.ReferenceSyntaxException;
import javax.faces.el.ValueBinding;
import javax.faces.el.VariableResolver;
import javax.faces.event.ActionListener;
import javax.faces.validator.Validator;

/**
 * A custom {@link Application} implementation which wraps the default {@link Application} instance
 * which is returned by the underlying JSF implementation.
 * <p>
 * During creation the {@link YFacesELResolver} is constructed (based on the {@link ELResolver}
 * returned by the wrapped instance) and a custom {@link YFacesELContextListener} gets registered.
 * Except {@link Application#getELResolver()} each method is delegated to the wrapped Application
 * instance.
 * 
 * @author Denny.Strietzbaum
 */
public class YFacesApplication extends Application {

	private Application base = null;
	private ELResolver resolver = null;

	protected YFacesApplication(Application base) {
		this.base = base;
		this.resolver = new YFacesELResolver(base.getELResolver());
		this.addELContextListener(new YFacesELContextListener());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.myfaces.application.ApplicationImpl#getELResolver()
	 */
	@Override
	public ELResolver getELResolver() {
		return this.resolver;
	}

	/**
	 * @param s
	 * @param s1
	 * @see javax.faces.application.Application#addComponent(java.lang.String, java.lang.String)
	 */
	@Override
	public void addComponent(String s, String s1) {
		base.addComponent(s, s1);
	}

	/**
	 * @param class1
	 * @param s
	 * @see javax.faces.application.Application#addConverter(java.lang.Class, java.lang.String)
	 */
	@Override
	public void addConverter(Class class1, String s) {
		base.addConverter(class1, s);
	}

	/**
	 * @param s
	 * @param s1
	 * @see javax.faces.application.Application#addConverter(java.lang.String, java.lang.String)
	 */
	@Override
	public void addConverter(String s, String s1) {
		base.addConverter(s, s1);
	}

	/**
	 * @param listener
	 * @see javax.faces.application.Application#addELContextListener(javax.el.ELContextListener)
	 */
	@Override
	public void addELContextListener(ELContextListener listener) {
		base.addELContextListener(listener);
	}

	/**
	 * @param resolver
	 * @see javax.faces.application.Application#addELResolver(javax.el.ELResolver)
	 */
	@Override
	public void addELResolver(ELResolver resolver) {
		base.addELResolver(resolver);
	}

	/**
	 * @param s
	 * @param s1
	 * @see javax.faces.application.Application#addValidator(java.lang.String, java.lang.String)
	 */
	@Override
	public void addValidator(String s, String s1) {
		base.addValidator(s, s1);
	}

	/**
	 * @param s
	 * @return
	 * @throws FacesException
	 * @see javax.faces.application.Application#createComponent(java.lang.String)
	 */
	@Override
	public UIComponent createComponent(String s) throws FacesException {
		return base.createComponent(s);
	}

	/**
	 * @param valuebinding
	 * @param facescontext
	 * @param s
	 * @return
	 * @throws FacesException
	 * @deprecated
	 * @see javax.faces.application.Application#createComponent(javax.faces.el.ValueBinding,
	 *      javax.faces.context.FacesContext, java.lang.String)
	 */
	@Deprecated
	@Override
	public UIComponent createComponent(ValueBinding valuebinding, FacesContext facescontext,
			String s) throws FacesException {
		return base.createComponent(valuebinding, facescontext, s);
	}

	/**
	 * @param componentExpression
	 * @param facesContext
	 * @param componentType
	 * @return
	 * @throws FacesException
	 * @throws NullPointerException
	 * @see javax.faces.application.Application#createComponent(javax.el.ValueExpression,
	 *      javax.faces.context.FacesContext, java.lang.String)
	 */
	@Override
	public UIComponent createComponent(ValueExpression componentExpression,
			FacesContext facesContext, String componentType) throws FacesException,
			NullPointerException {
		return base.createComponent(componentExpression, facesContext, componentType);
	}

	/**
	 * @param class1
	 * @return
	 * @see javax.faces.application.Application#createConverter(java.lang.Class)
	 */
	@Override
	public Converter createConverter(Class class1) {
		return base.createConverter(class1);
	}

	/**
	 * @param s
	 * @return
	 * @see javax.faces.application.Application#createConverter(java.lang.String)
	 */
	@Override
	public Converter createConverter(String s) {
		return base.createConverter(s);
	}

	/**
	 * @param s
	 * @param aclass
	 * @return
	 * @throws ReferenceSyntaxException
	 * @deprecated
	 * @see javax.faces.application.Application#createMethodBinding(java.lang.String,
	 *      java.lang.Class[])
	 */
	@Deprecated
	@Override
	public MethodBinding createMethodBinding(String s, Class[] aclass)
			throws ReferenceSyntaxException {
		return base.createMethodBinding(s, aclass);
	}

	/**
	 * @param s
	 * @return
	 * @throws FacesException
	 * @see javax.faces.application.Application#createValidator(java.lang.String)
	 */
	@Override
	public Validator createValidator(String s) throws FacesException {
		return base.createValidator(s);
	}

	/**
	 * @param s
	 * @return
	 * @throws ReferenceSyntaxException
	 * @deprecated
	 * @see javax.faces.application.Application#createValueBinding(java.lang.String)
	 */
	@Deprecated
	@Override
	public ValueBinding createValueBinding(String s) throws ReferenceSyntaxException {
		return base.createValueBinding(s);
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return base.equals(obj);
	}

	/**
	 * @param context
	 * @param expression
	 * @param expectedType
	 * @return
	 * @throws ELException
	 * @see javax.faces.application.Application#evaluateExpressionGet(javax.faces.context.FacesContext,
	 *      java.lang.String, java.lang.Class)
	 */
	@Override
	public Object evaluateExpressionGet(FacesContext context, String expression, Class expectedType)
			throws ELException {
		return base.evaluateExpressionGet(context, expression, expectedType);
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getActionListener()
	 */
	@Override
	public ActionListener getActionListener() {
		return base.getActionListener();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getComponentTypes()
	 */
	@Override
	public Iterator<String> getComponentTypes() {
		return base.getComponentTypes();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getConverterIds()
	 */
	@Override
	public Iterator<String> getConverterIds() {
		return base.getConverterIds();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getConverterTypes()
	 */
	@Override
	public Iterator<Class> getConverterTypes() {
		return base.getConverterTypes();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getDefaultLocale()
	 */
	@Override
	public Locale getDefaultLocale() {
		return base.getDefaultLocale();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getDefaultRenderKitId()
	 */
	@Override
	public String getDefaultRenderKitId() {
		return base.getDefaultRenderKitId();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getELContextListeners()
	 */
	@Override
	public ELContextListener[] getELContextListeners() {
		return base.getELContextListeners();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getExpressionFactory()
	 */
	@Override
	public ExpressionFactory getExpressionFactory() {
		return base.getExpressionFactory();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getMessageBundle()
	 */
	@Override
	public String getMessageBundle() {
		return base.getMessageBundle();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getNavigationHandler()
	 */
	@Override
	public NavigationHandler getNavigationHandler() {
		return base.getNavigationHandler();
	}

	/**
	 * @return
	 * @deprecated
	 * @see javax.faces.application.Application#getPropertyResolver()
	 */
	@Deprecated
	@Override
	public PropertyResolver getPropertyResolver() {
		return base.getPropertyResolver();
	}

	/**
	 * @param ctx
	 * @param name
	 * @return
	 * @throws FacesException
	 * @throws NullPointerException
	 * @see javax.faces.application.Application#getResourceBundle(javax.faces.context.FacesContext,
	 *      java.lang.String)
	 */
	@Override
	public ResourceBundle getResourceBundle(FacesContext ctx, String name) throws FacesException,
			NullPointerException {
		return base.getResourceBundle(ctx, name);
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getStateManager()
	 */
	@Override
	public StateManager getStateManager() {
		return base.getStateManager();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getSupportedLocales()
	 */
	@Override
	public Iterator<Locale> getSupportedLocales() {
		return base.getSupportedLocales();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getValidatorIds()
	 */
	@Override
	public Iterator<String> getValidatorIds() {
		return base.getValidatorIds();
	}

	/**
	 * @return
	 * @deprecated
	 * @see javax.faces.application.Application#getVariableResolver()
	 */
	@Deprecated
	@Override
	public VariableResolver getVariableResolver() {
		return base.getVariableResolver();
	}

	/**
	 * @return
	 * @see javax.faces.application.Application#getViewHandler()
	 */
	@Override
	public ViewHandler getViewHandler() {
		return base.getViewHandler();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return base.hashCode();
	}

	/**
	 * @param listener
	 * @see javax.faces.application.Application#removeELContextListener(javax.el.ELContextListener)
	 */
	@Override
	public void removeELContextListener(ELContextListener listener) {
		base.removeELContextListener(listener);
	}

	/**
	 * @param actionlistener
	 * @see javax.faces.application.Application#setActionListener(javax.faces.event.ActionListener)
	 */
	@Override
	public void setActionListener(ActionListener actionlistener) {
		base.setActionListener(actionlistener);
	}

	/**
	 * @param locale
	 * @see javax.faces.application.Application#setDefaultLocale(java.util.Locale)
	 */
	@Override
	public void setDefaultLocale(Locale locale) {
		base.setDefaultLocale(locale);
	}

	/**
	 * @param s
	 * @see javax.faces.application.Application#setDefaultRenderKitId(java.lang.String)
	 */
	@Override
	public void setDefaultRenderKitId(String s) {
		base.setDefaultRenderKitId(s);
	}

	/**
	 * @param s
	 * @see javax.faces.application.Application#setMessageBundle(java.lang.String)
	 */
	@Override
	public void setMessageBundle(String s) {
		base.setMessageBundle(s);
	}

	/**
	 * @param navigationhandler
	 * @see javax.faces.application.Application#setNavigationHandler(javax.faces.application.NavigationHandler)
	 */
	@Override
	public void setNavigationHandler(NavigationHandler navigationhandler) {
		base.setNavigationHandler(navigationhandler);
	}

	/**
	 * @param propertyresolver
	 * @deprecated
	 * @see javax.faces.application.Application#setPropertyResolver(javax.faces.el.PropertyResolver)
	 */
	@Deprecated
	@Override
	public void setPropertyResolver(PropertyResolver propertyresolver) {
		base.setPropertyResolver(propertyresolver);
	}

	/**
	 * @param statemanager
	 * @see javax.faces.application.Application#setStateManager(javax.faces.application.StateManager)
	 */
	@Override
	public void setStateManager(StateManager statemanager) {
		base.setStateManager(statemanager);
	}

	/**
	 * @param arg0
	 * @see javax.faces.application.Application#setSupportedLocales(java.util.Collection)
	 */
	@Override
	public void setSupportedLocales(Collection<Locale> arg0) {
		base.setSupportedLocales(arg0);
	}

	/**
	 * @param variableresolver
	 * @deprecated
	 * @see javax.faces.application.Application#setVariableResolver(javax.faces.el.VariableResolver)
	 */
	@Deprecated
	@Override
	public void setVariableResolver(VariableResolver variableresolver) {
		base.setVariableResolver(variableresolver);
	}

	/**
	 * @param viewhandler
	 * @see javax.faces.application.Application#setViewHandler(javax.faces.application.ViewHandler)
	 */
	@Override
	public void setViewHandler(ViewHandler viewhandler) {
		base.setViewHandler(viewhandler);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return base.toString();
	}

}
