package de.hybris.yfaces.demo.basic2;

import de.hybris.yfaces.component.AbstractYComponent;

public class ShowProductComponent extends AbstractYComponent {

	private String imageUrl = null;
	private String description = null;
	private String name = null;
	private String code = null;

	public void postInitialize() {
		if (code != null) {
			// here's the place where your services may come into action and load
			// a product by 'code' from your persistence layer
			// For demonstration only a dummy procedures is implemented here

			if (code.equals("FRUIT-1")) {
				this.name = "Name for Product with code:" + code;
				this.description = "Description for product with code: " + code;
				this.imageUrl = "/images/CEF_1112.jpg";
			} else {
				this.name = "???";
				this.description = "???";
			}
		}
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String id) {
		this.code = id;
	}

}
