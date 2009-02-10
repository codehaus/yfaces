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

import org.apache.commons.lang.StringEscapeUtils;

public class Xml2Html {

	// various style classes for generated html
	private static final String STYLECLASS_LINENUMBER = "jh_line";
	private static final String STYLECLASS_COMMENTBLOCK = "jh_cblk";
	private static final String STYLECLASS_LITERAL = "jh_lit";

	// pattern detects the occurrence of each special source fragment
	private static Pattern nextMatchPattern = Pattern.compile(MATCH_TYPE.getAllAsPatternString());

	private static final String NOTHING_REMAINS = "";

	/**
	 * Enumeration for all these occurrences which needs a special output treatment.
	 */
	private enum MATCH_TYPE {
		/** General comment block */
		COMMENT_BLOCK("<!--", "-->"),
		
//		TAG_BLOCK("<", ">"),
		
		/** String literal */
		LITERAL("\"", "[^\\\\]\""),

		;

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

		/**
		 * Builds a single pattern string from all available {@link MATCH_TYPE} patterns. Treats
		 * each {@link MATCH_TYPE} as extra group and connects them 'or'
		 * 
		 * @return A pattern string
		 */
		private static String getAllAsPatternString() {
			StringBuilder sb = new StringBuilder();
			for (MATCH_TYPE type : MATCH_TYPE.values()) {
				sb.append("|(" + type.startPatternString + ")");
			}
			sb.deleteCharAt(0);
			return sb.toString();
		}
	}

	private int lineCount = 0;
	private String sourceLine = null;
	private String remainingLine = null;
	private StringBuilder targetLine = null;

	private MATCH_TYPE lastMode = null;

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
		long start = System.currentTimeMillis();
		BufferedReader reader = new BufferedReader(source);
		PrintWriter printer = new PrintWriter(new BufferedWriter(target));

		printer.println("<html>");
		printer.println("<head>");
		printer.println("<style type=\"text/css\"> span." + STYLECLASS_COMMENTBLOCK
				+ " {color:rgb(63,127,95)} " + "span." + STYLECLASS_LINENUMBER
				+ " {color:rgb(120,120,120)}" + "span." + STYLECLASS_LITERAL
				+ " {color:rgb(42,0,255)} </style>");
		printer.println("</head>");
		printer.println("<body><pre><code>");

		try {
			while (reader.ready()) {
				String line = reader.readLine();

				// prevent an infinite loop; e.g. a StringReader is always ready
				if (line == null) {
					break;
				}
				String result = this.formatSingleLine(line);
				printer.println(result);
			}
			long end = System.currentTimeMillis();
			printer.println("</code></pre>");
			printer.println();
			printer.println("<i style=\"font-size:8pt\">Generated in " + (end - start) + "ms");
			printer.println("</body></html>");
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
	private String formatSingleLine(String sourceLine) {
		this.sourceLine = sourceLine;
		this.remainingLine = sourceLine;
		this.targetLine = new StringBuilder();
		this.lineCount++;
		this.targetLine.append("<span class=\"").append(STYLECLASS_LINENUMBER).append("\">")
				.append(lineCount).append(": </span>");
		
		//when blocks are processed previous mode is still active  
		if (this.lastMode != null) {
			this.processMatch(this.lastMode, null);
		}
		
		//process normal when no bloc is active or block is finished for that line
		this.processRemainingLine();
		
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
				String out = StringEscapeUtils.escapeHtml(this.remainingLine.substring(0, m.start()));
				targetLine.append(out);

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

				this.processMatch(matchType, matchValue);

			} else {
				// content needs no formatting; just add it to target and clear remaining content
				String out = StringEscapeUtils.escapeHtml(this.remainingLine);
				targetLine.append(out);
				this.remainingLine = NOTHING_REMAINS;
			}
		}
	}

	/**
	 * General block processing. Processing starts at first char and finishes when the block is
	 * closed. A Block is closed when the passed Pattern matches.
	 * 
	 * @param styleClass
	 *            styleclass to use
	 * @param endPattern
	 *            pattern which detects the end of the block
	 */
	private void processAsBlock(String styleClass, Pattern endPattern) {
		// update target content; open span tag
		targetLine.append("<span class=\"").append(styleClass).append("\">");

		// matcher to find the end of that comment block
		Matcher m = endPattern.matcher(this.remainingLine);

		// when comment block is closed ...
		if (m.find()) {
			// get comment and html escape
			int index = m.end();
			String comment = this.remainingLine.substring(0, index);
			comment = StringEscapeUtils.escapeHtml(comment);
			// update target content, close span tag
			targetLine.append(comment).append("</span>");
			// update remaining content
			this.remainingLine = this.remainingLine.substring(index);
			// reset block mode
			this.lastMode = null;
		} else {
			// comment block is not closed;
			// take full remaining content as comment
			String escaped = StringEscapeUtils.escapeHtml(this.remainingLine);
			targetLine.append(escaped).append("</span>");
			this.remainingLine = NOTHING_REMAINS;
		}
	}
	
	
	
	
	
	
	private void processMatch(MATCH_TYPE matchType, String matchValue) {
		switch (matchType) {
		case COMMENT_BLOCK:
			this.processAsCommentBlock();
			break;
//		case TAG_BLOCK:
//			this.processAsTagBlock();
//			break;
		case LITERAL:
			this.processAsLiteral();
			break;
		}
	}
	
	
	/**
	 * General comment block processing.
	 */
	private void processAsCommentBlock() {
		this.lastMode = MATCH_TYPE.COMMENT_BLOCK;
		this.processAsBlock(STYLECLASS_COMMENTBLOCK, MATCH_TYPE.COMMENT_BLOCK.endPattern);
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

	
	
	public static final Pattern TAG_ATTRIBUTE = Pattern.compile("\\w+(?=\\s*=)");
//
//	private void processAsTagBlock() {
//		
//		Matcher m = MATCH_TYPE.TAG_BLOCK.endPattern.matcher(this.remainingLine);
//
//		if (m.find()) {
//			this.processAsTagAttributes(this.remainingLine.substring(0, m.start())); 
//		} else {
//			this.processAsTagAttributes(this.remainingLine);
//		}
//		Matcher attributeMatcher = TAG_ATTRIBUTE.matcher(this.remainingLine);
//	}
//	
//	private void processAsTagAttributes(String values) {
//	}

	
	public static void main(String argc[]) {
		String[] test = new String[] { "sdafdsa  fasdf <br test=\"value\" test3  = \"value\"  />" };

		for (String s : test) {
			System.out.println(s);
			Matcher m = TAG_ATTRIBUTE.matcher(s);

			while (m.find()) {
				System.out.println("->" + s.substring(m.start(), m.end()));
			}
		}

	}

}
