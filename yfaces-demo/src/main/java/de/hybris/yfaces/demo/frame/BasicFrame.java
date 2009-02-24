package de.hybris.yfaces.demo.frame;

import de.hybris.yfaces.component.AbstractYFrame;

public class BasicFrame extends AbstractYFrame {

	private String time = null;

	public BasicFrame() {
		this.time = String.valueOf(System.currentTimeMillis());

	}

	public String getCreationTime() {
		return this.time;
	}
}
