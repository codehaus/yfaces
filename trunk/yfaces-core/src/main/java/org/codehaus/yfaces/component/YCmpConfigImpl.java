package org.codehaus.yfaces.component;

public class YCmpConfigImpl implements YComponentConfig {

	private YComponentHandlerImpl cmpImpl = null;
	private String id = null;
	private String variableName = null;
	private String errorHandling = null;
	private String pushProperties = null;

	protected YCmpConfigImpl(final YComponentHandlerImpl cmpImpl) {
		this.cmpImpl = cmpImpl;
	}

	private String modelSpecClassName = null;
	private String modelImplClassName = null;

	/**
	 * Returns the classname of the interface/ specification class.
	 * 
	 * @return classname
	 */
	public String getModelSpecification() {
		return this.modelSpecClassName;
	}

	/**
	 * Sets the classname of the interface/specification class.<br/>
	 * Nullifies an already (optional) created instance of the implementation class.<br/>
	 * Does no verification at all.<br/>
	 * 
	 * @param className
	 *          classname
	 */
	public void setModelSpecification(String className) {
		if (className != null && (className = className.trim()).length() == 0) {
			className = null;
		}

		if (className == null || !className.equals(this.modelSpecClassName)) {
			this.modelSpecClassName = className;
			cmpImpl.setModelSpecification(null);
		}
	}

	/**
	 * Returns the classname of the implementation class.
	 * 
	 * @return classname
	 */
	public String getModelImplementation() {
		return this.modelImplClassName;
	}

	/**
	 * Sets the classname of the implementation class.
	 * 
	 * @param className
	 */
	public void setModelImplementation(String className) {
		if (className != null && (className = className.trim()).length() == 0) {
			className = null;
		}

		if (className == null || !className.equals(this.modelSpecClassName)) {
			this.modelImplClassName = className;
			cmpImpl.setModelImplementation(null);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(final String variableName) {
		this.variableName = variableName;
	}

	public String getErrorHandling() {
		return errorHandling;
	}

	public void setErrorHandling(final String errorHandling) {
		this.errorHandling = errorHandling;
	}

	public String getPushProperties() {
		return pushProperties;
	}

	public void setPushProperties(final String pushProperties) {
		this.pushProperties = pushProperties;
	}

}
