/*
 * Copyright 2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hybris.yfaces.demo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Generates formatted html markup based on java source code.
 * 
 * @author Denny.Strietzbaum
 */

// TODO: configurable tabsize
public class Java2Html {

	private static final String STYLECLASS_LINENUMBER = "jh_line";
	private static final String STYLECLASS_JAVADOCR = "jh_jdoc";
	private static final String STYLECLASS_COMMENTBLOCK = "jh_cblk";
	private static final String STYLECLASS_COMMENTLINE = "jh_cline";
	private static final String STYLECLASS_KEYWORD = "jh_keyw";
	private static final String STYLECLASS_LITERAL = "jh_lit";

	private static Pattern nextMatchPattern = null;

	private static final String NOTHING_REMAINS = "";

	private enum COMMENTBLOCK {
		NORMAL_BLOCK,
		JAVADOC_BLOCK
	}

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum MATCH_TYPE {
		/** Javadoc comment block */
		JAVADOC("/\\*\\*", "\\*/"),

		/** General comment block */
		COMMENT_BLOCK("/\\*", "\\*/"),

		/** Comment line */
		COMMENT_LINE("//", null),

		/** String literal */
		LITERAL("\"", "[^\\\\]\""),

		/** Reserved java keywords */
		KEYWORDS("(?<=[\\s\\.\\(\\{)};=]|^)(?:public|static|final|private|new|void|this|return|"
				+ "class|package|enum|import|if|else|try|catch|finally|throw|switch|case|break|"
				+ "default|for|do|while|int|long|boolean|double|float|null)(?=[\\s\\.\\(\\{)};]|$)", null);

		private String startPatternString = null;
		private String endPatternString = null;
		private Pattern endPattern = null;

		private MATCH_TYPE(String start, String end) {
			this.startPatternString = start;
			this.endPatternString = end;
			if (endPatternString != null) {
				this.endPattern = Pattern.compile(endPatternString);
			}
		}

		private static String getAllAsPatternString() {
			StringBuilder sb = new StringBuilder();
			for (MATCH_TYPE type : MATCH_TYPE.values()) {
				sb.append("|(" + type.startPatternString + ")");
			}
			sb.deleteCharAt(0);
			return sb.toString();
		}
	}

	static {
		nextMatchPattern = Pattern.compile(MATCH_TYPE.getAllAsPatternString());
	}

	private COMMENTBLOCK commentMode = null;

	private int lineCount = 0;
	private String sourceLine = null;
	private String remainingLine = null;
	private StringBuilder targetLine = null;

	/**
	 * Generates html markup from java source code.
	 * 
	 * @param source
	 *            java source file
	 * @param target
	 *            html target file
	 */
	public void format(File source, File target) {

		if (!source.isFile()) {
			throw new IllegalArgumentException("Source is not a file");
		}

		// create directory structure when not already available
		target.mkdirs();

		String _target = source.getName().replace(".java", ".html");
		target = new File(target.getPath() + "/" + _target);

		try {
			Reader reader = new FileReader(source);
			Writer writer = new FileWriter(target);
			this.format(reader, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates html markup from java source code.
	 * 
	 * @param source
	 *            reader which provides java source code
	 * @param target
	 *            writer which is used for generated html markup
	 */
	public void format(Reader source, Writer target) {
		BufferedReader reader = new BufferedReader(source);
		PrintWriter printer = new PrintWriter(new BufferedWriter(target));

		printer.println("<html>");
		printer.println("<head>");
		printer.println("<style type=\"text/css\"> span." + STYLECLASS_COMMENTBLOCK
				+ " { color:rgb(63,127,95) } span." + STYLECLASS_JAVADOCR
				+ " {color:rgb(63,95,191)} " + "span." + STYLECLASS_COMMENTLINE
				+ " {color:rgb(63,127,95)} " + "span." + STYLECLASS_LINENUMBER
				+ " {color:rgb(120,120,120)}" + "span." + STYLECLASS_LITERAL
				+ " {color:rgb(42,0,255)}" + "span." + STYLECLASS_KEYWORD
				+ " {color:rgb(127,0,85); font-weight:bold} </style>");
		printer.println("</head>");
		printer.println("<body><pre><code>");

		try {
			while (reader.ready()) {
				String line = reader.readLine();

				// prevent an infinite loop; e.g. a StringReader is always ready
				if (line == null) {
					break;
				}
				String result = this.processSourceLine(line);
				printer.println(result);
			}
			printer.println("</code></pre></body></html>");
			printer.flush();
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generates html markup from java source code.
	 * 
	 * @param input
	 *            java source code (with or without linebreaks)
	 * @return generated html
	 */
	public String format(String input) {
		Reader source = new StringReader(input);
		Writer target = new StringWriter();
		this.format(source, target);
		return target.toString();
	}

	/**
	 * Processed a whole line of java source code. The passed string must't contain any line breaks.
	 * 
	 * @return html formatted line
	 */
	private String processSourceLine(String sourceLine) {
		this.sourceLine = sourceLine;
		this.remainingLine = sourceLine;
		this.targetLine = new StringBuilder();
		this.lineCount++;
		this.targetLine.append("<span class=\"lineNumber\">").append(lineCount).append(": </span>");
		if (this.commentMode != null) {
			this.processAsBlockComment();
		} else {
			this.processRemainingLine();
		}
		return this.targetLine.toString();
	}

	/**
	 * Processes remaining content until nothing is left. Each processing step reduces the amount of
	 * remaining content and increases the amount of target content.
	 */
	private void processRemainingLine() {

		// loop over remaining content
		while (this.remainingLine.length() > 0) {

			// matcher for special java occurrences
			Matcher m = nextMatchPattern.matcher(this.remainingLine);

			// when content needs special formatting...
			if (m.find()) {

				// update target content (neutral output until matched position)
				targetLine.append(this.remainingLine.substring(0, m.start()));

				// update remaining content
				this.remainingLine = this.remainingLine.substring(m.start());

				// the match itself (one of the optional ones)
				String matchValue = m.group(0);

				// find the group which is responsible for the match
				int matchIndex = 1;
				for (matchIndex = 1; matchIndex <= m.groupCount(); matchIndex++) {
					if (m.group(matchIndex) != null) {
						break;
					}
				}

				// get the enum value for that matching group
				MATCH_TYPE matchType = MATCH_TYPE.values()[matchIndex - 1];

				// and process further according the type
				switch (matchType) {
				case JAVADOC:
					this.commentMode = COMMENTBLOCK.JAVADOC_BLOCK;
					this.processAsBlockComment();
					break;
				case COMMENT_BLOCK:
					this.commentMode = COMMENTBLOCK.NORMAL_BLOCK;
					this.processAsBlockComment();
					break;
				case COMMENT_LINE:
					this.processAsLineComment();
					break;
				case LITERAL:
					this.processAsLiteral();
					break;
				case KEYWORDS:
					this.processAsKeyword(matchValue);
					break;
				}
			} else {
				// content needs no formatting; just add it to target and clear remaing content
				targetLine.append(this.remainingLine);
				this.remainingLine = NOTHING_REMAINS;
			}
		}
	}

	/**
	 * Block comment processing. Processing starts at first char and finishes when the comment block
	 * is closed (star followed by slash) or end of content is reached.
	 */
	private void processAsBlockComment() {
		// update target content; open span tag
		String styleClass = (this.commentMode == COMMENTBLOCK.NORMAL_BLOCK) ? STYLECLASS_COMMENTBLOCK
				: STYLECLASS_JAVADOCR;
		targetLine.append("<span class=\"").append(styleClass).append("\">");

		// matcher to find the end of that comment block
		Matcher m = MATCH_TYPE.COMMENT_BLOCK.endPattern.matcher(this.remainingLine);

		// when comment block is closed ...
		if (m.find()) {
			// get comment and html escape
			int index = m.end();
			String comment = this.remainingLine.substring(0, index);
			comment = StringEscapeUtils.escapeHtml(comment);
			// update target content, close span tag
			targetLine.append(comment).append("</span>");
			// update remaing content
			this.remainingLine = this.remainingLine.substring(index);
			// reset comment block mode by nullify comment style class
			this.commentMode = null;
		} else {
			// comment block is not closed;
			// take full remaining content as comment
			String escaped = StringEscapeUtils.escapeHtml(this.remainingLine);
			targetLine.append(escaped).append("</span>");
			this.remainingLine = NOTHING_REMAINS;
		}
	}

	/**
	 * Line comment processing. Processing starts at first char and finishes at last char. Remaining
	 * content gets marked as finished (nothing remains)
	 */
	private void processAsLineComment() {
		// html escape the comment
		String comment = StringEscapeUtils.escapeHtml(this.remainingLine);

		// update target content
		targetLine.append("<span class=\"").append(STYLECLASS_COMMENTLINE).append("\">").append(
				comment).append("</span>");

		// update remaining content
		this.remainingLine = NOTHING_REMAINS;
	}

	/**
	 * Literal processing. Processing starts at first char and finishes when an unescaped double
	 * quote is found. Remaining content gets updated by the content followed after current
	 * processed content.
	 */
	private void processAsLiteral() {
		// open a span tag with passed style
		targetLine.append("<span class=\"").append(STYLECLASS_LITERAL).append("\">");

		// find end of literal...
		Matcher m = MATCH_TYPE.LITERAL.endPattern.matcher(this.remainingLine);

		// literal end must be available
		if (m.find()) {
			int index = m.end();

			// get literal, html escaping, close span tag, add to target line
			String literal = remainingLine.substring(0, index);
			literal = StringEscapeUtils.escapeHtml(literal);
			targetLine.append(literal).append("</span>");

			// update remaining content
			this.remainingLine = remainingLine.substring(index);

		} else {
			// exception when no literal end is found
			throw new IllegalStateException("Can't find end of literal at line " + lineCount + ":"
					+ sourceLine);
		}
	}

	/**
	 * Keyword processing (e.g. public, static, final etc). Processing starts at first char and
	 * finishes right after the keyword. Remaining content gets updated by the content followed
	 * after current processed content.
	 * 
	 * @param keyword
	 *            the keyword itself
	 */
	private void processAsKeyword(String keyword) {
		// update target content
		targetLine.append("<span class=\"").append(STYLECLASS_KEYWORD).append("\">")
				.append(keyword).append("</span>");

		// update remaining content
		this.remainingLine = remainingLine.substring(keyword.length());
	}

	//
	//
	// Testing area
	//
	// private static final String base = "D:/projects/yfaces-demo/src/main/java";
	private static final String base = "D:/projects/yfaces/yfaces-demo/src/main/java";
	private static final String targetDir = "D:/_temp/";

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
		System.out.println("Using Pattern:" + Java2Html.nextMatchPattern.pattern());

		File source = new File(sourceName);
		File target = new File(targetDir); // comment after
		for (int i = 0; i <= 10; i++) {
			long t1 = System.currentTimeMillis();
			Java2Html test = new Java2Html();
			test.format(source, target);
			long t2 = System.currentTimeMillis();
			System.out.println("Took: " + (t2 - t1) + "ms");
		}
	}

	public static void main3(String[] argc) throws Exception {
		String sourceName = base + "/" + Java2Html.class.getName().replaceAll("\\.", "/") + ".java";
		File source = new File(sourceName);
		StringWriter w = new StringWriter();
		IOUtils.copy(new FileReader(source), w);

		String s = w.toString();

		System.out.println(new Java2Html().format(s));

	}

	public static void main2(String argc[]) {
		String s = "Test.class ";

		Pattern p = Pattern
				.compile("(\")|((?<=[\\s\\(\\{\\.]|^)(?:public|static|final|private|void|class|package|enum|class|if|else|try|catch)[\\s])");
		Matcher m = p.matcher(s);

		Java2Html.class.getClass();

		if (0 == 0) {
		}
		;

		;
		if (1 == 1) {
			Integer c = new Integer(2);
		}
		;

		// Pattern endPattern = Pattern.compile("(?=[\\s])");
		// String s = "      public main";

		// Matcher m = endPattern.matcher(s);

		if (m.find()) {
			System.out.println(s.substring(m.start()));
		}
	}

}
