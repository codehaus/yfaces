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
public class Java2Html {

	private static Pattern pAll = null;

	private enum TYPE {
		JAVADOC("/\\*\\*", "\\*/"),
		COMMENT_BLOCK("/\\*", "\\*/" ),
		COMMENT_LINE("//", null),
		LITERAL("\"", "[^\\\\]\""),
		KEYWORDS("(?<=[\\s\\.\\(\\{)};=]|^)(?:public|static|final|private|new|void|this|return|class|package|enum|import|if|else|try|catch|throw|switch|case|break|default|for|do|while|int|long|boolean|double|float|null)(?=[\\s\\.\\(\\{)};]|$)", null);
		
		private String startPatternString = null;
		private String endPatternString = null;
		private Pattern endPattern = null;

		private TYPE(String start, String end) {
			this.startPatternString = start;
			this.endPatternString = end;
			if (endPatternString != null) {
				this.endPattern = Pattern.compile(endPatternString);
			}
		}

		private static String getFullPattern() {
			StringBuilder sb = new StringBuilder();
			for (TYPE type : TYPE.values()) {
				sb.append("|(" + type.startPatternString + ")");
			}
			sb.deleteCharAt(0);
			return sb.toString();
		}
	}

	static {
		pAll = Pattern.compile(TYPE.getFullPattern());

	}

	private String currentComentBlockStyle = null;

	private int lineCount = 0;
	private String currentLine = null;
	private StringBuilder sBuilder = null;

	/**
	 * Formats a single java source file and prints the result into a targetr file.
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

		//create directory structure when not already available
		File directory = new File(target.getParent());
		if (!directory.exists()) {
			target.mkdirs();
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
				+ "{ color:rgb(63,127,95) } span.commentBlockJavadoc {color:rgb(63,95,191)} "
				+ "span.commentLine {color:rgb(63,127,95)} "
				+ "span.lineNumber {color:rgb(120,120,120)}" + "span.literal {color:rgb(42,0,255)}"
				+ "span.keyword {color:rgb(127,0,85); font-weight:bold} </style>");
		printer.println("</head>");
		printer.println("<body><pre><code>");

		try {
			while (reader.ready()) {
				this.sBuilder = new StringBuilder();
				this.currentLine = reader.readLine();
				lineCount++;
				this.processLine(currentLine);
				String processedLine = this.sBuilder.toString();
				printer.println(processedLine);
			}
			printer.println("</code></pre></body></html>");
			printer.flush();
			printer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processed a whole line from java source.
	 * 
	 * @param line
	 *            java source code line
	 * @return html formatted line
	 */
	private void processLine(String line) {

		this.sBuilder.append("<span class=\"lineNumber\">").append(lineCount).append(": </span>");
		if (this.currentComentBlockStyle != null) {
			this.processStringAsPartOfBlockComment(line, currentComentBlockStyle);
		} else {
			this.processString(line);
		}
	}

	//general processing 
	private void processString(String source) {
		Matcher m = pAll.matcher(source);

		if (m.find()) {

			//group zero returns the match (one of the other groups linked via OR)
			String matchValue = m.group(0);

			int matchIndex = 1;
			for (matchIndex = 1; matchIndex <= m.groupCount(); matchIndex++) {
				if (m.group(matchIndex) != null) {
					break;
				}
			}

			sBuilder.append(source.substring(0, m.start()));
			String nextString = source.substring(m.start());

			TYPE t = TYPE.values()[matchIndex - 1];

			switch (t) {
			case JAVADOC:
				this.currentComentBlockStyle = "commentBlockJavadoc";
				this.processStringAsPartOfBlockComment(nextString, currentComentBlockStyle);
				break;
			case COMMENT_BLOCK:
				this.currentComentBlockStyle = "commentBlock";
				this.processStringAsPartOfBlockComment(nextString, currentComentBlockStyle);
				break;
			case COMMENT_LINE:
				this.processStringAsLineComment(nextString, "commentLine");
				break;
			case LITERAL:
				this.processStringAsLiteral(nextString, "literal");
				break;
			case KEYWORDS:
				this.processStringAsKeyword(nextString, matchValue, "keyword");
				break;
			}
		} else {
			sBuilder.append(source);
		}
	}

	//String gets processed as block comment
	private void processStringAsPartOfBlockComment(String line, String styleClass) {
		sBuilder.append("<span class=\"").append(styleClass).append("\">");
		Matcher m = TYPE.COMMENT_BLOCK.endPattern.matcher(line);

		if (m.find()) {
			this.currentComentBlockStyle = null;
			int index = m.end();
			String s = line.substring(0, index);
			s = StringEscapeUtils.escapeHtml(s);
			sBuilder.append(s).append("</span>");

			s = line.substring(index);

			if (s.length() > 0) {
				this.processString(s);
			}

		} else {
			line = StringEscapeUtils.escapeHtml(line);
			sBuilder.append(line).append("</span>");
		}
	}

	//String gets processed as line comment
	private void processStringAsLineComment(String source, String styleClass) {
		source = StringEscapeUtils.escapeHtml(source);
		sBuilder.append("<span class=\"").append(styleClass).append("\">").append(source).append(
				"</span>");
	}

	private void processStringAsLiteral(String source, String styleClass) {

		sBuilder.append("<span class=\"").append(styleClass).append("\">");
		Matcher m = TYPE.LITERAL.endPattern.matcher(source);

		if (m.find()) {
			int index = m.end();
			String s = source.substring(0, index);
			s = StringEscapeUtils.escapeHtml(s);
			sBuilder.append(s).append("</span>");

			s = source.substring(index);

			if (s.length() > 0) {
				this.processString(s);
			}
		} else {
			throw new RuntimeException("No literal ending found: " + lineCount + ":" + currentLine);
		}
	}

	private void processStringAsKeyword(String source, String keyword, String styleClass) {
		sBuilder.append("<span class=\"").append(styleClass).append("\">").append(keyword).append("</span>") ;
		source = source.substring(keyword.length());
		if (source.length() > 0) {
			this.processString(source);
		}

	}

	private static final String base = "D:/projects/yfaces-demo/src/main/java";

	/**
	 * Javadoc
	 * 
	 * @param argc
	 */
	public static void main(String[] argc) {
		String sourceName = base + "/" + Java2Html.class.getName().replaceAll("\\.", "/") + ".java";

		String s1 = "a normal literal";
		String s2 = "a literal with a quotate \" inside it ";
		String s3 = "a literal with a linebrerak because of a very very very very olong literal"
				+ " string sdfasdfasfdsf";
		String enumVar = "testtest";
		System.out.println("Using Pattern:" + Java2Html.pAll.pattern());

		//File source = new File(sourceName);
		File source = new File(sourceName);
		File target = new File("c:/_test/Test.html"); //comment after
		for (int i = 0; i <= 10; i++) {
			long t1 = System.currentTimeMillis();
			Java2Html test = new Java2Html();
			test.format(source, target);
			long t2 = System.currentTimeMillis();
			System.out.println("Took: " + (t2 - t1) + "ms");
		}
	}

	public static void main2(String argc[]) {
		String s = "Test.class ";

		Pattern p = Pattern
				.compile("(\")|((?<=[\\s\\(\\{\\.]|^)(?:public|static|final|private|void|class|package|enum|class|if|else|try|catch)[\\s])");
		Matcher m = p.matcher(s);
		
		Java2Html.class.getClass();
		
		if(0==0) {};
		
		;if (1==1) {Integer c =new Integer(2);};

		//Pattern endPattern = Pattern.compile("(?=[\\s])");
		//String s = "      public main";

		//Matcher m = endPattern.matcher(s);

		if (m.find()) {
			System.out.println(s.substring(m.start()));
		}
	}

}
