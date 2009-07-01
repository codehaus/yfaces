package de.hybris.yfaces.demo;

public class ChapterParticipiant implements Comparable<ChapterParticipiant> {

	public static final String TYPE_CMPVIEW = "cmpView";
	public static final String TYPE_CMPSPEC = "cmpSpec";
	public static final String TYPE_CMPIMPL = "cmpImpl";
	public static final String TYPE_FRAMEVIEW = "frameView";
	public static final String TYPE_FRAMECLASS = "frameClass";
	public static final String TYPE_FACESCFG = "facesConfig";
	public static final String TYPE_BEANCLASS = "beanClass";

	public String name = null;
	public String source = null;
	public String sourceType = null;
	public String facesType = null;
	public String description = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFacesType() {
		return facesType;
	}

	public void setFacesType(String facesType) {
		this.facesType = facesType;
	}

	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public String getSourceType() {
		return sourceType;
	}

	@Override
	public boolean equals(Object obj) {
		return this.source.equals(((ChapterParticipiant) obj).source);
	}

	@Override
	public int hashCode() {
		return this.source.hashCode();
	}

	public int compareTo(ChapterParticipiant o) {
		return this.source.compareTo(o.source);
	}
}
