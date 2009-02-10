package de.hybris.yfaces.demo.source2html;

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
import java.util.Stack;

public class SourceDocument {

	public static final String NOTHING_REMAINS = null;
	public static final String NOTHING_REMAINS_BUT_KEPP_MODE = "";

	public static final String STYLECLASS_LINENUMBER = "jh_line";

	protected int lineCount = 0;
	protected String sourceLine = null;
	protected String remainingLine = null;
	protected StringBuilder targetLine = null;

	protected Stack<SourceElement> foModeStack = null;

	protected String style = null;

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
		if (this.style != null) {
			printer.println(style);
		}
		// printer.println("<style type=\"text/css\"> span." + STYLECLASS_COMMENTBLOCK
		// + " { color:rgb(63,127,95) } span." + STYLECLASS_JAVADOC
		// + " {color:rgb(63,95,191)} " + "span." + STYLECLASS_COMMENTLINE
		// + " {color:rgb(63,127,95)} " + "span." + STYLECLASS_LINENUMBER
		// + " {color:rgb(120,120,120)}" + "span." + STYLECLASS_LITERAL
		// + " {color:rgb(42,0,255)}" + "span." + STYLECLASS_KEYWORD
		// + " {color:rgb(127,0,85); font-weight:bold} </style>");
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
		boolean keepMode = false;
		System.out.println(this.sourceLine);
		while (this.remainingLine != null && this.remainingLine.length() > 0) {
			SourceElement foMode = this.foModeStack.peek();
			foMode.process(this, null);
		}
		return this.targetLine.toString();
	}
}
