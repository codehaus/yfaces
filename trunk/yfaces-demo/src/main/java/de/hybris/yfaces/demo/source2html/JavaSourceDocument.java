package de.hybris.yfaces.demo.source2html;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import de.hybris.yfaces.demo.util.Java2Html;

public class JavaSourceDocument extends SourceDocument {

	// various style classes for generated html
	private static final String STYLECLASS_JAVADOC = "jh_jdoc";
	private static final String STYLECLASS_COMMENTBLOCK = "jh_cblk";
	private static final String STYLECLASS_COMMENTLINE = "jh_cline";
	private static final String STYLECLASS_KEYWORD = "jh_keyw";
	private static final String STYLECLASS_LITERAL = "jh_lit";

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum MATCH_TYPE {
		/** Javadoc comment block */
		JAVADOC("/\\*\\*", "\\*/", STYLECLASS_JAVADOC),

		/** General comment block */
		COMMENT_BLOCK("/\\*", "\\*/", STYLECLASS_COMMENTBLOCK),

		/** Comment line */
		COMMENT_LINE("//", "$", STYLECLASS_COMMENTLINE),

		/** String literal */
		LITERAL("\"", "[^\\\\]\"", STYLECLASS_LITERAL),

		/** Reserved java keywords */
		KEYWORDS("(?<=[\\s\\.\\(\\{)};=]|^)(?:public|static|final|private|new|void|this|return|"
				+ "class|package|enum|import|if|else|try|catch|finally|throw|switch|case|break|"
				+ "default|for|do|while|int|long|boolean|double|float|null)(?=[\\s\\.\\(\\{)};]|$)", null, STYLECLASS_KEYWORD);

		private String startPatternString = null;
		private String endPatternString = null;
		private String styleClass = null;

		private MATCH_TYPE(String start, String end, String styleClass) {
			this.startPatternString = start;
			this.endPatternString = end;
			this.styleClass = styleClass;
		}
	}

	public JavaSourceDocument() {

		List<SourceElement> elements = new ArrayList<SourceElement>();
		for (MATCH_TYPE t : MATCH_TYPE.values()) {
			SourceElement e = new DefaultSourceElement(t.startPatternString, t.endPatternString,
					t.styleClass);
			elements.add(e);
		}
		CompositeSourceElement ce = new CompositeSourceElement(elements, "");
		super.foModeStack = new Stack<SourceElement>();
		foModeStack.push(ce);

		super.style = "<style type=\"text/css\"> span." + STYLECLASS_COMMENTBLOCK
				+ " { color:rgb(63,127,95) } span." + STYLECLASS_JAVADOC
				+ " {color:rgb(63,95,191)} " + "span." + STYLECLASS_COMMENTLINE
				+ " {color:rgb(63,127,95)} " + "span." + STYLECLASS_LINENUMBER
				+ " {color:rgb(120,120,120)}" + "span." + STYLECLASS_LITERAL
				+ " {color:rgb(42,0,255)}" + "span." + STYLECLASS_KEYWORD
				+ " {color:rgb(127,0,85); font-weight:bold} </style>";
	}

	//
	//
	// Testing area
	//
	private static final String base = "D:/projects/yfaces/yfaces-demo/src/main/java";
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

		// for (int i = 0; i <= 10; i++) {
		long t1 = System.currentTimeMillis();
		JavaSourceDocument test = new JavaSourceDocument();
		test.format(source, target);
		long t2 = System.currentTimeMillis();
		System.out.println("Took: " + (t2 - t1) + "ms");
		// }
	}
}
