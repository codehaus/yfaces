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

import java.io.Serializable;
import java.util.Map;

import javax.faces.event.PhaseId;

/**
 * The base of all components.
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

	/**
	 * Returns a custom for general usage with the scope of this component.
	 * 
	 * @return Map of Attributes
	 */
	public Map<String, Object> getAttributes();

	/**
	 * Validates this component. Validation always is processed after this components attributes are
	 * injected.
	 * <p>
	 * This all happens during execution of {@link PhaseId#RENDER_RESPONSE}
	 */
	public void validate();

	/**
	 * Refreshes this component.A refresh is processed when this component was already created and
	 * needs to be displayed again (POST or GET (flashback enabled) to same page). A refresh always
	 * is performed before attribute injection.
	 * <p>
	 * In case of a POST request this happens after {@link PhaseId#INVOKE_APPLICATION}.<br/>
	 * in case of a GET with enabled flashback this happens after {@link PhaseId#RESTORE_VIEW}.
	 */
	public void refresh();

	/**
	 * Returns the parent {@link YFrame} of this component.<br/>
	 * May return 'null' when this component isn't bound.
	 * 
	 * @return {@link YFrame}
	 */
	public YFrame getFrame();

}
