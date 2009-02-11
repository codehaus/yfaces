package de.hybris.yfaces.demo.source2html;

import java.io.File;

import de.hybris.yfaces.demo.util.Java2Html;

public class JavaSourceDocument extends SourceDocument {

	private static final String[] STYLE_JAVADOC = { "jv_jdoc", "color:rgb(63,95,191)" };
	private static final String[] STYLE_COMMENTBLOCK = { "jv_cblk", "color:rgb(63,127,95)" };
	private static final String[] STYLE_COMMENTLINE = { "jv_cline", "color:rgb(63,127,95)" };
	private static final String[] STYLE_KEYWORD = { "jv_keyw", "color:rgb(127,0,85)" };
	private static final String[] STYLE_LITERAL = { "jv_lit", "color:rgb(42,0,255)" };

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum MATCH_TYPE {
		/** Javadoc comment block */
		JAVADOC("/\\*\\*", "\\*/", true, STYLE_JAVADOC),

		/** General comment block */
		COMMENT_BLOCK("/\\*", "\\*/", true, STYLE_COMMENTBLOCK),

		/** Comment line */
		COMMENT_LINE("//", "$", false, STYLE_COMMENTLINE),

		/** String literal */
		LITERAL("\"", "[^\\\\]\"", false, STYLE_LITERAL),

		/** Reserved java keywords */
		KEYWORDS("(?<=[\\s\\.\\(\\{)};=]|^)(?:public|static|final|private|new|void|this|return|"
				+ "class|package|enum|import|if|else|try|catch|finally|throw|switch|case|break|"
				+ "default|for|do|while|int|long|boolean|double|float|null)(?=[\\s\\.\\(\\{)};]|$)", null, true, STYLE_KEYWORD);

		private String startPatternString = null;
		private String endPatternString = null;
		private String styleClass = null;

		private SourceBlock node = null;

		private MATCH_TYPE(String start, String end, boolean isMultiLine, String[] style) {
			this.startPatternString = start;
			this.endPatternString = end;
			this.styleClass = style[0];

			this.node = new SourceBlock(start, end);
			node.setName(this.name());
			node.setStyleClass(style[0]);
			node.setStyleValues(style[1]);
			node.setMultilineMode(isMultiLine);
		}
	}

	public JavaSourceDocument() {

		SourceBlock root = super.getSourcePattern();
		root.addSubNode(MATCH_TYPE.JAVADOC.node);
		root.addSubNode(MATCH_TYPE.COMMENT_BLOCK.node);
		root.addSubNode(MATCH_TYPE.COMMENT_LINE.node);
		root.addSubNode(MATCH_TYPE.KEYWORDS.node);
		root.addSubNode(MATCH_TYPE.LITERAL.node);

		super.compileConfiguration();
	}

	//
	//
	// Testing area
	//
	private static final String base = "D:/projects/yfaces-demo/src/main/java";
	private static final String targetDir = "C:/_test/";

	// private static final String base = "D:/projects/yfaces/yfaces-demo/src/main/java";
	// private static final String targetDir = "D:/_temp/";

	/**
	 * Javadoc
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		String sourceName = base + "/" + Java2Html.class.getName().replaceAll("\\.", "/") + ".java";

		String s1 = "a normal literal"; /* das is test */
		String s2 = "a literal with a quotate \" inside it ";
		String s3 = "a literal with a linebrerak because of a very very very very olong literal"
				+ " string sdfasdfasfdsf";
		String enumVar = "testtest";
		// System.out.println("Using Pattern:" + Java2Html.nextMatchPattern.pattern());

		File source = new File(sourceName);
		File target = new File(targetDir); // comment after

		JavaSourceDocument test = new JavaSourceDocument();
		for (int i = 0; i <= 10; i++) {
			long t1 = System.currentTimeMillis();
			test.format(source, target);
			long t2 = System.currentTimeMillis();
			System.out.println("Took: " + (t2 - t1) + "ms");
		}
	}
}
