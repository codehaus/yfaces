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
	 * Adds {@link YComponent} to passed {@link YModel}
	 * 
	 * @param cmp
	 */
	void setYComponent(T model);

	/**
	 * Adds {@link YFrame} to passed {@link YModel}
	 * 
	 * @param model
	 * @param frame
	 */
	void setFrame(T model, YFrame frame);

	void validateModel(T model);

	void setProperty(T model, String property, Object value);

}
