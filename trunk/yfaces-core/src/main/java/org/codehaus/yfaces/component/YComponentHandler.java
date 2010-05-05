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

package org.codehaus.yfaces.component;

import java.net.URL;
import java.util.Collection;

import org.codehaus.yfaces.component.html.HtmlYComponentHandler;

import com.sun.facelets.tag.Tag;

/**
 * Runtime information about view and model for every YFaces managed component. Additionally
 * {@link ModelProcessor} and {@link YComponentValidator}
 * 
 * @author Denny Strietzbaum
 */
public interface YComponentHandler {

	/**
	 * Returns the view 'id' which is unique within the same namespace. ID is taken from
	 * {@link YComponentConfig#getId()} or when not available automatically generated.
	 * 
	 * @return id view id
	 */
	String getViewId();

	/**
	 * Returns an URL for the component view. This value is detected and set automatically.
	 * 
	 * @return URL of component view
	 */
	URL getViewURL();

	/**
	 * Returns the location for the component view. This URI is relative to webapplication root and
	 * points to same target like {@link #getViewURL()}. This value gets detected and set
	 * automatically.
	 * <p/>
	 * Location is used, to have a mapping between {@link YComponentHandler} and Facelets managed view
	 * files. This location is identical to that one of {@link Tag#getLocation()} which is made
	 * available in {@link HtmlYComponentHandler}.
	 * 
	 * @return
	 */
	String getViewLocation();

	/**
	 * Runtime value based on {@link YComponentConfig#getVariableName()}
	 * 
	 * @return variable name
	 */
	String getVariableName();

	/**
	 * Runtime value based on {@link YComponentConfig#getErrorHandling()}
	 */
	String getErrorHandling();

	/**
	 * Runtime value based on {@link YComponentConfig#getPushProperties()}
	 * 
	 * @return Collection of properties which are passed to model
	 */
	Collection<String> getPushProperties();

	/**
	 * Returns the name of this component. This value is created based on the components URL/filename
	 * 
	 * @return name of component
	 */
	String getName();

	/**
	 * Returns the namespace for this component.
	 * 
	 * @return
	 */
	String getNamespace();

	/**
	 * Runtime value based on {@link YComponentConfig#getModelSpecification()}
	 * 
	 * @return model specification class
	 */
	public Class getModelSpecification();

	/**
	 * Runtime value based on {@link YComponentConfig#getModelImplementation()}
	 * 
	 * @return model implementation class
	 */
	public Class<?> getModelImplementation();

	/**
	 * Creates and returns a new component instance which fulfills all configuration settings.
	 * 
	 * @return
	 */
	Object createModel();

	void setModelProperty(Object model, String property, Object value);

	/**
	 * Creates a {@link YComponentValidator}.
	 * 
	 * @return {@link YComponentValidator}
	 */
	YComponentValidator createValidator();

	/**
	 * Returns a plain, unprocessed component configuration which is used to build this
	 * {@link YComponentHandler}.
	 * 
	 * @return {@link YComponentConfig}
	 */
	YComponentConfig getConfiguration();

	/**
	 * Returns true when component settings where validated successfully at least one times.
	 * Successfully means {@link #createModel()} will create and return an instance which matches all
	 * configuration settings.
	 * 
	 * @return true when validated
	 */
	boolean isValidated();

}