package de.hybris.yfaces.demo;

import de.str.prettysource.InputNode;

public enum XhtmlInputDef {
	/** General comment block */
	COMMENTBLOCK("<!--", "-->", true),

	YF_TAG("</?yf:", ">", true),
	YF_TAG_ATTRIB("\\w+:?\\w+(?=\\s*=)", null, false),
	YF_LITERAL("\"", "[^\\\\]\"", false),

	TAG("<(?!!)", ">", true),

	TAG_ATTRIB("\\w+:?\\w+(?=\\s*=)", null, false),

	/** Entities (named, decimal, hexadecimal) */
	ENTITY("&(?:\\w+|#[0-9]+|#x[a-fA-F0-9]+);", null, false),

	/** String literal */
	LITERAL("\"", "[^\\\\]\"", false);

	private InputNode node = null;

	private XhtmlInputDef(String start, String end, boolean isMultiLine) {
		this.node = new InputNode(this.name(), start, end);
		this.node.setMultilineMode(isMultiLine);
	}

	private InputNode createNode() {
		return new InputNode(this.node);
	}

	/**
	 * Creates tree of {@link InputNode} instances which is used for java source content.
	 * 
	 * @param rootNodeId
	 *            id of root node
	 * @return tree of {@link InputNode} instances
	 */
	public static InputNode createParserTree(String rootNodeId) {

		InputNode result = new InputNode(rootNodeId);

		InputNode tagProc = TAG.createNode();
		tagProc.getChildNodes().add(LITERAL.createNode());
		tagProc.getChildNodes().add(TAG_ATTRIB.createNode());

		InputNode ytagProc = YF_TAG.createNode();
		ytagProc.getChildNodes().add(YF_LITERAL.createNode());
		ytagProc.getChildNodes().add(YF_TAG_ATTRIB.createNode());

		result.getChildNodes().add(COMMENTBLOCK.createNode());
		result.getChildNodes().add(ytagProc);
		result.getChildNodes().add(tagProc);
		result.getChildNodes().add(LITERAL.createNode());
		result.getChildNodes().add(ENTITY.createNode());

		return result;

	}
}
