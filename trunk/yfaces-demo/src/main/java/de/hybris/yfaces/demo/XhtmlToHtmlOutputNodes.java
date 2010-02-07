package de.hybris.yfaces.demo;

import de.str.prettysource.output.HtmlOutputNode;
import de.str.prettysource.output.HtmlOutputNodeFactory;

public enum XhtmlToHtmlOutputNodes implements HtmlOutputNodeFactory {

	COMMENTBLOCK("xhtml_ctblk", "color:rgb(63,127,95)"),
	LITERAL("xhtml_literal", "color:rgb(42,0,255)"),
	TAG("xhtml_tag", "color:rgb(63,127,95)"),
	TAG_ATTRIB("xhtml_attrib", "color:rgb(127,0,85)"),
	YF_LITERAL("xhtml_yfliteral", "color:rgb(42,0,255); font-weight:normal; font-style:italic"),
	YF_TAG("xhtml_yftag", "color:rgb(63,127,95); font-weight:bold; font-style:italic"),
	YF_TAG_ATTRIB("xhtml_yfattrib", "color:rgb(127,0,85); font-weight:bold; font-style:italic");

	private final String styleClass;
	private final String stylevalue;

	private XhtmlToHtmlOutputNodes(String styleClass, String styleValue) {
		this.styleClass = styleClass;
		this.stylevalue = styleValue;
	}

	public HtmlOutputNode createNode() {
		return new HtmlOutputNode(getStyleClass());
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public String getStyleValue() {
		return this.stylevalue;
	}

	public String getNodeId() {
		return this.name();
	}

}
