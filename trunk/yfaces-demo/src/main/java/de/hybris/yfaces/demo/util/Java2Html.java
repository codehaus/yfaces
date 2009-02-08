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
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * general javadoc
 * 
 * @author Denny.Strietzbaum
 */

// TODO: documentation 
// TODO: configurable tabsize 
// TODO: keywords
// TODO: eclipse like color codes
// TODO: use stringbuilder
public class Java2Html {

	private static final String BLOCK_COMMENT_START_JAVADOC = "/**";
	private static final String BLOCK_COMMENT_START_GENERAL = "/*";

	private static final String LINE_COMMENT_START = "//";
	private static final String LITERAL_START = "\"";

	private static final Pattern blockCommentEndPattern = Pattern.compile("\\*/");
	private static final Pattern literalEndPattern = Pattern.compile("[^\\\\]\"");

	private static final Pattern generalPattern = Pattern.compile("(/\\*\\*)|(/\\*)|(//)|(\")");

	private String currentComentBlockStyle = null;

	private int lineCount = 0;

	private String currentLine = null;

	public void format(File source, File target) {
		if (!target.exists()) {
			target.mkdirs();
			try {
				target.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			Reader _source = new FileReader(source);
			Writer _target = new FileWriter(target);
			this.format(_source, _target);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void format(Reader source, Writer target) {
		BufferedReader reader = new BufferedReader(source);
		PrintWriter printer = new PrintWriter(new BufferedWriter(target));

		printer.println("<html>");
		printer.println("<head>");
		printer.println("<style type=\"text/css\"> span.commentBlock "
				+ "{ color:green } span.commentBlockJavadoc {color:blue} "
				+ "span.commentLine {color:green} " + "span.lineNumber {color:red}"
				+ "span.literal {color:blue} </style>");
		printer.println("</head>");
		printer.println("<body><pre><code>");

		try {
			while (reader.ready()) {
				this.currentLine = reader.readLine();
				lineCount++;
				String processedLine = this.processLine(currentLine);
				printer.println(processedLine);
			}
			printer.println("</code></pre></body></html>");
			printer.flush();
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String processLine(String line) {

		String s = null;
		if (this.currentComentBlockStyle != null) {
			s = this.processStringAsPartOfBlockComment(line, currentComentBlockStyle);
		} else {
			s = this.processString(line);
		}
		String result = "<span class=\"lineNumber\">" + lineCount + ":</span>" + s;
		return result;
	}

	//general processing 
	private String processString(String source) {
		Matcher m = generalPattern.matcher(source);

		String result = source;

		if (m.find()) {

			//group zero returns the match (one of the other groups linked via OR)
			String type = m.group(0);

			result = source.substring(0, m.start());
			String next = source.substring(m.start());

			if (type.equals(BLOCK_COMMENT_START_JAVADOC)) {
				this.currentComentBlockStyle = "commentBlockJavadoc";
				result = result
						+ this.processStringAsPartOfBlockComment(next, "commentBlockJavadoc");
			}

			if (type.equals(BLOCK_COMMENT_START_GENERAL)) {
				this.currentComentBlockStyle = "commentBlock";
				result = result + this.processStringAsPartOfBlockComment(next, "commentBlock");
			}

			if (type.equals(LINE_COMMENT_START)) {
				result = result + this.processStringAsLineComment(next, "commentLine");
			}

			if (type.equals(LITERAL_START)) {
				result = result + this.processStringAsLiteral(next, "literal");
			}
		}

		return result;
	}

	//String gets processed as block comment
	private String processStringAsPartOfBlockComment(String line, String styleClass) {
		String result = "<span class=\"" + styleClass + "\">";
		Matcher m = blockCommentEndPattern.matcher(line);

		if (m.find()) {
			this.currentComentBlockStyle = null;
			int index = m.end();
			String s = line.substring(0, index);
			s = StringEscapeUtils.escapeHtml(s);
			result = result + s + "</span>";

			s = line.substring(index);

			if (s.length() > 0) {
				result = result + this.processString(s);
			}

		} else {
			line = StringEscapeUtils.escapeHtml(line);
			result = result + line + "</span>";
		}

		return result;
	}

	//String gets processed as line comment
	private String processStringAsLineComment(String source, String styleClass) {
		source = StringEscapeUtils.escapeHtml(source);
		String result = "<span class=\"" + styleClass + "\">" + source + "</span>";
		return result;
	}

	private String processStringAsLiteral(String source, String styleClass) {

		String result = "<span class=\"" + styleClass + "\">";
		Matcher m = literalEndPattern.matcher(source);

		if (m.find()) {
			int index = m.end();
			String s = source.substring(0, index);
			s = StringEscapeUtils.escapeHtml(s);
			result = result + s + "</span>";

			s = source.substring(index);

			if (s.length() > 0) {
				result = result + this.processString(s);
			}
		} else {
			throw new RuntimeException("No literal ending found: " + lineCount + ":" + currentLine);
		}

		return result;
	}

	private static final String base = "D:/projects/yfaces/yfaces-core/src/main/java";
	private static String[] classes = new String[] { "de.hybris.yfaces.YComponentInfo", };

	/**
	 * Javadoc
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		String sourceName = base + "/" + classes[0].replaceAll("\\.", "/") + ".java";

		String s1 = "a normal literal";
		String s2 = "a literal with a quotate \" inside it ";
		String s3 = "a literal with " + "linebrerak";

		//File source = new File(sourceName);
		File source = new File("D:/projects/TestProject/src/main/java/doclet/Java2Html.java");
		File target = new File("d:/_temp/Test.html"); //comment after

		Java2Html test = new Java2Html();
		test.format(source, target);
	}

}
