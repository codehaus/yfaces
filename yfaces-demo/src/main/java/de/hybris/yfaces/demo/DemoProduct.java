package de.hybris.yfaces.demo;

import java.net.URL;

public class DemoProduct {

	private String id = null;
	private String name = null;
	private String description = null;
	private int onStockCount = 0;
	private URL thumbnail = null;
	private URL picture = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getOnStockCount() {
		return onStockCount;
	}

	public void setOnStockCount(int onStockCount) {
		this.onStockCount = onStockCount;
	}

	public URL getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(URL thumbnail) {
		this.thumbnail = thumbnail;
	}

	public URL getPicture() {
		return picture;
	}

	public void setPicture(URL picture) {
		this.picture = picture;
	}

}
