package de.hybris.yfaces.demo.source2html;

import java.io.File;

public class JavaSourceDocument extends SourceDocument {

	private static final String[] STYLE_JAVADOC = { "jv_jdoc", "color:rgb(63,95,191)" };
	private static final String[] STYLE_JAVADOCP = { "jv_jdocp",
			"color:rgb(127,159,191); font-weight:bold" };
	private static final String[] STYLE_COMMENTBLOCK = { "jv_cblk", "color:rgb(63,127,95)" };
	private static final String[] STYLE_COMMENTLINE = { "jv_cline", "color:rgb(63,127,95)" };
	private static final String[] STYLE_KEYWORD = { "jv_keyw",
			"color:rgb(127,0,85); font-weight:bold" };
	private static final String[] STYLE_LITERAL = { "jv_lit", "color:rgb(42,0,255)" };

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum SelectionType {
		/** Javadoc comment block */
		JAVADOC("/\\*\\*", "\\*/", true, STYLE_JAVADOC),

		JAVADOC_PARAM("@\\w+", "\\s", false, STYLE_JAVADOCP),

		/** General comment block */
		COMMENT_BLOCK("/\\*", "\\*/", true, STYLE_COMMENTBLOCK),

		/** Comment line */
		COMMENT_LINE("//", "$", false, STYLE_COMMENTLINE),

		/** String literal */
		LITERAL("\"", "[^\\\\]\"", false, STYLE_LITERAL),

		/** Reserved java keywords */
		KEYWORDS("(?<=[\\s\\.\\(\\{)};=]|^)(?:public|static|final|private|new|void|this|super|return|"
				+ "class|interface|extends|package|enum|import|if|else|try|catch|finally|throw|switch|case|break|"
				+ "default|for|do|while|int|long|boolean|double|float|null|true|false)(?=[\\s\\.\\(\\{)};,]|$)", null, true, STYLE_KEYWORD);

		private SourceSelection value = null;

		private SelectionType(String start, String end, boolean isMultiLine, String[] style) {
			this.value = new SourceSelection(start, end);
			value.setName(this.name());
			value.setStyleClass(style[0]);
			value.setStyleValues(style[1]);
			value.setMultilineMode(isMultiLine);
		}
	}

	public JavaSourceDocument() {

		SourceSelection all = super.getSourceSelection();
		all.addChild(SelectionType.JAVADOC.value);
		all.addChild(SelectionType.COMMENT_BLOCK.value);
		all.addChild(SelectionType.COMMENT_LINE.value);
		all.addChild(SelectionType.KEYWORDS.value);
		all.addChild(SelectionType.LITERAL.value);

		SourceSelection javadocBlock = SelectionType.JAVADOC.value;
		javadocBlock.addChild(SelectionType.JAVADOC_PARAM.value);

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
		String sourceName = base + "/" + JavaSourceDocument.class.getName().replaceAll("\\.", "/")
				+ ".java";

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
