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

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.log4j.Logger;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YComponentEventHandlerImpl implements YComponentEventHandler {
	private static final Logger log = Logger.getLogger(YComponentEventHandlerImpl.class);

	// list of listeners
	private List<YComponentEventListener> listeners = null;

	// enable/disable handler
	private boolean enabled = true;

	// name (localization key)
	private String name = null;

	private transient FacesEvent eventSource = null;

	public YComponentEventHandlerImpl() {
		this(new DefaultYComponentEventListener<YComponent>());
	}

	protected YComponentEventHandlerImpl(final YComponentEventListener listener) {
		this.listeners = new LinkedList<YComponentEventListener>();
		listeners.add(listener);
	}

	public void addCustomListener(final YComponentEventListener listener) {
		this.listeners.add(listener);
	}

	public YComponentEventListener getListener() {
		return this.listeners.get(0);
	}

	public void setListener(final YComponentEventListener listener) {
		this.listeners.set(0, listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storefoundation.yfaces.YComponentEventHandler#action()
	 */
	public String action() {
		if (this.eventSource == null) {
			log
					.warn("Firing action without a FacesEvent; (missing ActionListener or ValueChangeListener?)");
		}

		String result = null;
		for (final YComponentEventListener listener : this.listeners) {
			try {
				log.debug("Notify listener (action) " + listener.getClass().getSimpleName());
				result = ((AbstractYComponentEventListener) listener).fireAction();
			} catch (final RuntimeException e) {
				final String msg = "Exception while processing Action (" + "Event: "
						+ listener.getClass().getSimpleName() + ")";

				log.error(msg, e);
				throw e;
			}

		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * storefoundation.yfaces.YComponentEventHandler#actionListener(javax.faces
	 * .event.ActionEvent)
	 */
	public void actionListener(final ActionEvent event) {
		this.eventSource = event;
		for (final YComponentEventListener listener : this.listeners) {
			final YComponentEvent _event = new YComponentEventImpl(this.eventSource);

			// separate exception handling is included since jsf 1.2 (myfaces)
			// catches any exception and wraps it into a
			// AbortProcessingException
			// (see MethodExpressionActionListener#processAction(...)).
			// UIViewRoot#processApplication(...) catches this and interrupts
			// any further processing
			//
			// The result is that each exception gets swallowed without any
			// notification
			try {
				log
						.debug("Notify listener (actionListener) "
								+ listener.getClass().getSimpleName());
				((AbstractYComponentEventListener) listener).fireActionListener(_event);
			} catch (final RuntimeException e) {
				final String msg = "Exception while processing ActionListener ("
						+ YComponent.class.getSimpleName() + ": "
						+ _event.getActionComponent().getId() + "; Event: "
						+ listener.getClass().getSimpleName() + ")";

				log.error(msg, e);
				throw e;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * storefoundation.yfaces.YComponentEventHandler#valueChangeListener(javax
	 * .faces.event.ValueChangeEvent)
	 */
	public void valueChangeListener(final ValueChangeEvent event) {
		this.eventSource = event;

		for (final YComponentEventListener listener : this.listeners) {
			final YComponentEvent _event = new YComponentEventImpl(this.eventSource);

			// see #actionListener(...) for the reason of this exception
			// handling
			try {
				log.debug("Notify listener (valueChangeListener) "
						+ listener.getClass().getSimpleName());
				((AbstractYComponentEventListener) listener).fireValueChangeListener(_event);
			} catch (final RuntimeException e) {
				final String msg = "Exception while processing ValueChangeListener ("
						+ YComponent.class.getSimpleName() + ": "
						+ _event.getActionComponent().getId() + "; Event: "
						+ listener.getClass().getSimpleName() + ")";

				log.error(msg, e);
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.faces.components.JsfEventContainer#isEnabled()
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ystorefoundationpackage.faces.components.JsfEventContainer#setEnabled
	 * (boolean)
	 */
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see storefoundation.yfaces.YComponentEventHandler#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * storefoundation.yfaces.YComponentEventHandler#setName(java.lang.CharSequence
	 * )
	 */
	public void setName(final CharSequence key) {
		this.name = key.toString();
	}

}
