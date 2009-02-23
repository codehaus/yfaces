package de.hybris.yfaces.demo;

public class ChapterParticipiant implements Comparable<ChapterParticipiant> {

	public String name = null;
	public String source = null;
	public String type = null;

	public String getName() {
		return name;
	}

	public String getSource() {
		return source;
	}

	public String getType() {
		return type;
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
