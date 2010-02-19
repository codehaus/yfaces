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

package de.hybris.yfaces.component;

import java.net.URL;
import java.util.Collection;

import com.sun.facelets.tag.Tag;

import de.hybris.yfaces.component.html.HtmlYComponentHandler;

/**
 * Holds {@link YComponent} specific meta information.
 * 
 * @author Denny Strietzbaum
 */
public interface YComponentInfo {

	static final String VAR_ATTRIBUTE = "var";
	static final String ID_ATTRIBUTE = "id";
	static final String SPEC_ATTRIBUTE = "spec";
	static final String IMPL_ATTRIBUTE = "impl";
	static final String INJECTABLE_ATTRIBUTE = "injectable";

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
	String getSpecification();

	/**
	 * Returns the default model implementation for this component. This value is set in component
	 * view as Tag attribute. Mustn't be empty
	 * 
	 * @return class name
	 */
	String getImplementation();

	/**
	 * Returns the name of the variable under which the component model is made available in view.
	 * This value is set in component view as Tag attribute.
	 * 
	 * @return variable name
	 */
	String getVariableName();

	/**
	 * Returns component properties which are allowed to be "pushed" from view into current processed
	 * component instance. This value is set in component view as Tag attribute.
	 * 
	 * @return
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
	URL getURL();

	/**
	 * Returns the location for the component view. This URI is relative to webapplication root and
	 * points to same target like {@link #getURL()}. This value gets detected and set automatically.
	 * <p/>
	 * Location is used, to have a mapping between {@link YComponentInfo} and Facelets managed view
	 * files. This location is identical to that one of {@link Tag#getLocation()} which is made
	 * available in {@link HtmlYComponentHandler}.
	 * 
	 * @return
	 */
	String getLocation();

	/**
	 * Returns the namespace for this component.
	 * 
	 * @return
	 */
	String getNamespace();

	/**
	 * Creates a {@link YComponentValidator}.
	 * 
	 * @return {@link YComponentValidator}
	 */
	YComponentValidator createValidator();

}