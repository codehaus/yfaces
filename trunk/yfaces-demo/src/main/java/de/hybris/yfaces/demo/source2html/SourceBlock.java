package de.hybris.yfaces.demo.source2html;

import java.util.ArrayList;
import java.util.List;

public class SourceBlock {

	private String start = null;
	private String end = null;
	private String styleClass = null;
	private String styleValues = null;

	private boolean isMultilineMode = false;
	private String name = null;

	private List<SourceBlock> subNodes = null;

	protected SourceBlock(String start, String end) {
		this.subNodes = new ArrayList<SourceBlock>();
		this.start = start;
		this.end = end;
	}

	public void addSubNode(SourceBlock subnode) {
		this.subNodes.add(subnode);
	}

	protected List<SourceBlock> getSubNodes() {
		return this.subNodes;
	}

	public String getStyleValues() {
		return styleValues;
	}

	public void setStyleValues(String styleValues) {
		this.styleValues = styleValues;
	}

	public String getStart() {
		return start;
	}

	public String getEnd() {
		return end;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean isMultilineMode() {
		return isMultilineMode;
	}

	public void setMultilineMode(boolean isMultilineMode) {
		this.isMultilineMode = isMultilineMode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
