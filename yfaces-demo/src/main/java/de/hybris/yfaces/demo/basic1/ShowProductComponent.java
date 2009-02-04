package de.hybris.yfaces.demo.basic1;

import de.hybris.yfaces.component.AbstractYComponent;

public class ShowProductComponent extends AbstractYComponent {

	private String imageUrl = null;
	private String description = null;
	private String name = "[no name available]";
	private String code = null;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String id) {
		this.code = id;
	}

}
