package de.hybris.yfaces.demo;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.str.prettysource.SourceFormat;
import de.str.prettysource.SourceSelection;
import de.str.prettysource.html.HtmlFormat;
import de.str.prettysource.html.XmlHtmlFormat;

public class Xhtml2HtmlFormat extends HtmlFormat {
	private static final String[] STYLE_COMMENTBLOCK = { "xhtml_ctblk", "color:rgb(63,127,95)" };
	private static final String[] STYLE_LITERAL = { "xhtml_literal", "color:rgb(42,0,255)" };
	private static final String[] STYLE_TAG = { "xhtml_tag", "color:rgb(63,127,95)" };
	private static final String[] STYLE_TAGATTRIB = { "xhtml_attrib", "color:rgb(127,0,85)" };

	private static final String[] STYLE_YFLITERAL = { "xhtml_yfliteral",
			"color:rgb(42,0,255); font-weight:normal; font-style:italic" };
	private static final String[] STYLE_YFTAG = { "xhtml_yftag",
			"color:rgb(63,127,95); font-weight:bold; font-style:italic" };
	private static final String[] STYLE_YFTAGATTRIB = { "xhtml_yfattrib",
			"color:rgb(127,0,85); font-weight:bold; font-style:italic" };

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum TYPE {

		/** General comment block */
		COMMENTBLOCK("<!--", "-->", STYLE_COMMENTBLOCK),

		YF_TAG("</?yf:", ">", STYLE_YFTAG),
		YF_TAG_ATTRIB("\\w+:?\\w+(?=\\s*=)", null, STYLE_YFTAGATTRIB),
		YF_LITERAL("\"", "[^\\\\]\"", STYLE_YFLITERAL),

		TAG("<(?!!)", ">", STYLE_TAG),

		TAG_ATTRIB("\\w+:?\\w+(?=\\s*=)", null, STYLE_TAGATTRIB),

		/** String literal */
		LITERAL("\"", "[^\\\\]\"", STYLE_LITERAL);

		private SourceSelection node = null;

		private TYPE(String start, String end, String[] style) {
			this.node = new SourceSelection(start, end);
			node.setName(this.name());
			node.setStyleClass(style[0]);
			node.setStyleValues(style[1]);
		}
	}

	public Xhtml2HtmlFormat() {
		super(createSelections());
	}

	private static SourceSelection createSelections() {
		SourceSelection node = SourceFormat.createRootSelection();
		node.addChild(TYPE.COMMENTBLOCK.node);
		node.addChild(TYPE.YF_TAG.node);
		node.addChild(TYPE.TAG.node);
		node.addChild(TYPE.LITERAL.node);

		SourceSelection tagNode = TYPE.TAG.node;
		tagNode.addChild(TYPE.LITERAL.node);
		tagNode.addChild(TYPE.TAG_ATTRIB.node);

		SourceSelection yfTagNode = TYPE.YF_TAG.node;
		yfTagNode.addChild(TYPE.YF_LITERAL.node);
		yfTagNode.addChild(TYPE.YF_TAG_ATTRIB.node);

		return node;
	}

	//
	//
	// Testing area
	//
	/**
	 * Javadoc
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		// test();
		String sourceName = "";
		String targetName = "";

		if (argc.length > 1) {

		} else {
			String base = "D:/projects/yfaces-demo";
			sourceName = base + "/demo/test.xhtml";
			// sourceName = base + "/demo/template.xhtml";
			sourceName = base + "/demo/chp1/demo1Cmp.xhtml";
			targetName = "C:/_test/";
		}

		File sourceFile = new File(sourceName);
		File targetFile = new File(targetName);

		// for (int i = 0; i <= 10; i++) {
		long t1 = System.currentTimeMillis();
		SourceFormat source = new XmlHtmlFormat();
		source.format(sourceFile, targetFile);
		long t2 = System.currentTimeMillis();
		System.out.println("Took: " + (t2 - t1) + "ms");
		// }
	}

	public static void test() {
		Pattern p = Pattern.compile("(\")|(\\w+(?=\\s*=))|(>)");
		Matcher m = p.matcher("attribute=\"test\" ");
		if (m.find())
			System.out.println(m.group());
		System.exit(0);
	}
}
