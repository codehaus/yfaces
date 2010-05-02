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
public interface YComponentContext {

	/**
	 * Returns the view 'id' which is unique within the same namespace. ID is taken from
	 * {@link YComponentConfig} or, when not set, automatically generated.
	 * 
	 * @return id view id
	 */
	String getViewId();

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
	 * Location is used, to have a mapping between {@link YComponentContext} and Facelets managed view
	 * files. This location is identical to that one of {@link Tag#getLocation()} which is made
	 * available in {@link HtmlYComponentHandler}.
	 * 
	 * @return
	 */
	String getViewLocation();

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
	 * Creates a {@link YComponentValidator}.
	 * 
	 * @return {@link YComponentValidator}
	 */
	YComponentValidator createValidator();

	Object createComponent();

	void setProperty(Object model, String property, Object value);

	/**
	 * Returns the configuration which is used for this {@link YComponentContext}.
	 * 
	 * @return {@link YComponentConfig}
	 */
	YComponentConfig getConfiguration();

	boolean isValidated();

}