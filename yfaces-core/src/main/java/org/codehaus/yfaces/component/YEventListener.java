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

import java.io.Serializable;

/**
 * A Listener which must be registered at and gets called from an {@link YEventHandler}.
 * 
 * @author Denny Strietzbaum
 */
public interface YEventListener<T extends YModel> extends Serializable {
	/**
	 * Process action logic.
	 * 
	 * @return navigation outcome
	 */
	public String action();

	/**
	 * Process actionListener logic.
	 * 
	 * @param event
	 *          {@link YEvent}
	 */
	public void actionListener(YEvent<T> event);

	/**
	 * Process valueChangeListener logic.
	 * 
	 * @param event
	 *          {@link YEvent}
	 */
	public void valueChangeListener(YEvent<T> event);

	/**
	 * Sets a binding for an action.<br/>
	 * A binding overrules {@link #action()}<br/>
	 * 
	 * @param binding
	 *          a valid expression string
	 */
	public void setAction(String binding);

	/**
	 * Sets a binding for an actionListener<br/>
	 * This binding overrules {@link #actionListener(YEvent)}
	 * 
	 * @param binding
	 *          a valid expression string
	 */
	public void setActionListener(String binding);

	/**
	 * Sets a binding for a valueChangeListener.<br/>
	 * This binding overrules {@link #valueChangeListener(YEvent)}
	 * 
	 * @param binding
	 *          valid expression string
	 */
	public void setValueChangeListener(String binding);

}
