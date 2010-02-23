package org.codehaus.yfaces.component;


public interface YComponentConfiguration {

	static final String VAR_ATTRIBUTE = "var";
	static final String ID_ATTRIBUTE = "id";
	static final String MODEL_SPEC_ATTRIBUTE = "modelspec";
	static final String MODEL_IMPL_ATTRIBUTE = "model";
	static final String ERROR_ATTRIBUTE = "errorHandling";
	static final String PASS_TO_MODEL_ATTRIBUTE = "passToModel";

	/**
	 * Returns the 'id' which is unique within this components namespace. This value is set in
	 * component view as Tag attribute. When empty an ID generated automatically.
	 * 
	 * @return id id of this component
	 */
	String getId();

	/**
	 * Return the model specification of this component. This value is set in component view as Tag
	 * attribute. Can be empty.
	 * 
	 * @return name of interface
	 */
	String getModelSpecification();

	/**
	 * Returns the default model implementation for this component. This value is set in component
	 * view as Tag attribute. Mustn't be empty
	 * 
	 * @return class name
	 */
	String getModelImplementation();

	/**
	 * Returns the name of the variable under which the component model is made available in view.
	 * This value is set in component view as Tag attribute.
	 * 
	 * @return variable name
	 */
	String getVariableName();

	String getErrorHandling();

	/**
	 * Returns component properties which are allowed to be "pushed" from view into current processed
	 * component instance. This value is set in component view as Tag attribute.
	 * 
	 * @return
	 */
	String getPushProperties();

}
