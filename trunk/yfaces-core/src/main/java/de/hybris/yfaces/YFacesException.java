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

package de.hybris.yfaces;

import de.hybris.yfaces.component.YComponent;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class YFacesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public YFacesException(final YComponent cmp) {
		super(createMessage(cmp, ""));
	}

	public YFacesException(final Throwable cause) {
		super(cause);
	}

	public YFacesException(final String msg) {
		super(msg);
	}

	public YFacesException(final String msg, final Throwable cause) {
		super(msg, cause);
	}

	public YFacesException(final YComponent cmp, final String message) {
		super(createMessage(cmp, message));
	}

	public YFacesException(final YComponent cmp, final String message, final Throwable cause) {
		super(createMessage(cmp, message), cause);
	}

	private static String createMessage(final YComponent cmp, final String subMessage) {
		return subMessage + " (" + cmp.getId() + ")";
	}

}
