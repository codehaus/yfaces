package org.codehaus.yfaces.component;

/**
 * A Processor for each operation which is done on a component model.
 * 
 * @author Denny Strietzbaum
 * 
 * @param <T>
 *          type of model
 */
public interface ModelProcessor<T> {

	/**
	 * Creates a new instance of the model.
	 * 
	 * @return
	 */
	T createModel();

	void initializeModel(T cmp);

	void validateModel(T model);

	void setProperty(T cmp, String property, Object value);

}
