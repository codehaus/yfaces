package de.hybris.yfaces.demo.chapter3x;

import de.hybris.yfaces.component.AbstractYComponent;

public class DemoCmp extends AbstractYComponent {

	private String message = null;
	private int count = 0;

	public DemoCmp() {
		this.message = "Default message (constructor)";
	}

	@Override
	public void validate() {
		if (this.message == null || this.message.trim().length() == 0) {
			this.message = "A message changed by postinitialize (was empty)";
		}
		count++;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCount() {
		return this.count;
	}

	public String getHashCode() {
		return String.valueOf(hashCode());
	}

}
