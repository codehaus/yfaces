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

	/**
	 * Adds {@link YFrame} to passed {@link YComponent}
	 * 
	 * @param model
	 * @param frame
	 */
	void setYFrame(T model, YFrame frame, String frameProperty);

	void setYComponent(T model);

	void validateModel(T model);

	void setProperty(T model, String property, Object value);

}
