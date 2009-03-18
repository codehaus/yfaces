/*
 * Copyright 2008 the original author or authors.
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

import java.io.Serializable;
import java.util.Map;

/**
 * The base of all components.<br/>
 * 
 * @author Denny.Strietzbaum
 */
public interface YComponent extends /* Externalizable */Serializable {
	/**
	 * The id of this component. This is the same ID which is used within the components renderer
	 * file.
	 * 
	 * @return component id
	 */
	public String getId();

	// public String getStyleClass();
	//	
	// public void setStyleClass(String styleClass);

	/**
	 * Returns a custom for general usage with the scope of this component.
	 * 
	 * @return Map of Attributes
	 */
	public Map<String, Object> getAttributes();

	/**
	 * PostInitialization<br/>
	 * This is the last step of component initialization.<br/>
	 * - step1) Constructor (for all non-expensive members)<br/>
	 * - step2) attribute injection from the renderer (xhtml)<br/>
	 * - step3) this one <br/>
	 * PostInitialization must assure that each member of this component has a valid initialized
	 * state or, when not, give it a nice default one.<br/>
	 * <br/>
	 * Use this phase for expensive members.<br/>
	 * (database queries etc.)
	 */
	public void postInitialize();

	/**
	 * Updates this component.<br/>
	 * 
	 * @param propertyChangeLog
	 */
	public void update();

	/**
	 * Returns the parent {@link YFrame} of this component.<br/>
	 * May return 'null' when this component isn't bound.
	 * 
	 * @return {@link YFrame}
	 */
	public YFrame getFrame();

}
