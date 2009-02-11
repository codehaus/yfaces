package de.hybris.yfaces.demo.source2html;

import java.util.ArrayList;
import java.util.List;

public class SourceNode {

	private String start = null;
	private String end = null;
	private String styleClass = null;
	private String styleValues = null;

	private boolean isMultilineMode = false;
	private String name = null;

	private List<SourceNode> subNodes = null;

	protected SourceNode(String start, String end) {
		this.subNodes = new ArrayList<SourceNode>();
		this.start = start;
		this.end = end;
	}

	// public SourceNode addSubNode(String start, String end) {
	// SourceNode result = new SourceNode(start, end);
	// this.subNodes.add(result);
	// return result;
	// }

	public void addSubNode(SourceNode subnode) {
		this.subNodes.add(subnode);
	}

	protected List<SourceNode> getSubNodes() {
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
