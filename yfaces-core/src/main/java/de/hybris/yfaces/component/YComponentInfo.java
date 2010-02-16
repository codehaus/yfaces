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

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import com.sun.facelets.tag.Tag;

import de.hybris.yfaces.component.html.HtmlYComponent;
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
	 * Returns the specification for this component.
	 * <p/>
	 * This is optional and if available always an interface-class literal.
	 * 
	 * @return name of interface
	 */
	String getSpecification();

	/**
	 * Returns the implementation for this component.
	 * <p/>
	 * This is mandatory and is the name of class which either is the implementation of the components
	 * specification or, if no specification is available, just any kind of implementation.
	 * 
	 * @return class name
	 */
	String getImplementation();

	/**
	 * Returns the id of this component.
	 * 
	 * @return id
	 */
	String getId();

	/**
	 * Returns the name of the variable under which the component implementation instance is available
	 * in the view layer.
	 * 
	 * @return variable name
	 */
	String getVariableName();

	/**
	 * Returns component properties which are allowed to be "pushed" from view into current processed
	 * component instance.
	 * 
	 * @return
	 */
	Collection<String> getPushProperties();

	/**
	 * Returns the name of this component. Generally the same as the id.
	 * 
	 * @return
	 */
	String getComponentName();

	URL getURL();

	/**
	 * Returns the location for this component. This is an URI-fragment which is the relative path
	 * based on webapplication root and which points to the Facelet xhtml resource represented by this
	 * {@link YComponentInfo}.
	 * <p/>
	 * This location is identical to that one of {@link Tag#getLocation()} which is made available in
	 * {@link HtmlYComponentHandler}. This is used to provide {@link HtmlYComponent} directly with the
	 * {@link YComponentInfo} it currently works.
	 * 
	 * @return
	 */
	String getLocation();

	String getNamespace();

	/**
	 * Creates an instance of this component. Does no additional validity check, expects that an
	 * implementation class is specified which is instantiable.
	 * 
	 * @return
	 */
	YComponent createComponent();

	/**
	 * Creates a {@link YComponentValidator}.
	 * 
	 * @return {@link YComponentValidator}
	 */
	YComponentValidator createValidator();

	Map<String, Method> getAllProperties();

	//void pushProperty(YComponent cmp, String propName, Object propValue);

}