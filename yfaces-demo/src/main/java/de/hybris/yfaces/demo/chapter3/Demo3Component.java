package de.hybris.yfaces.demo.chapter3;

import de.hybris.yfaces.component.AbstractYComponent;

public class Demo3Component extends AbstractYComponent {

	private String message = null;
	private int count = 0;

	public Demo3Component() {
		this.message = "Default message (constructor)";
	}

	@Override
	public void postInitialize() {
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
