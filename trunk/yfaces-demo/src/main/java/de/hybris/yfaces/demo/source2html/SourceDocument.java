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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SourceDocument {

	public static final String STYLECLASS_LINENUMBER = "jh_line";

	protected int lineCount = 0;
	protected String sourceLine = null;
	protected String remainingLine = null;
	protected StringBuilder targetLine = null;

	// stack with current active selection
	protected Stack<SourceSelectionProcessor> selectionStack = null;

	protected String style = "";

	private SourceSelection sourceRoot = new SourceSelection(null, null);

	/**
	 * Returns the root selection for this source document. This one has the whole document selected
	 * but child selections are added via {@link SourceSelection#addChild(SourceSelection)}
	 * 
	 * @return {@link SourceSelection} which selects the whole document
	 */
	public SourceSelection getSourceSelection() {
		return this.sourceRoot;
	}

	public void compileConfiguration() {

		// build pattern tree
		SourceSelectionProcessor root = getSourceElementForSourceNode(this.sourceRoot);

		// collect styles
		Map<String, String> styles = new LinkedHashMap<String, String>();
		this.collectAllStyles(this.sourceRoot, styles);
		for (Map.Entry<String, String> entry : styles.entrySet()) {
			this.style = this.style + " span." + entry.getKey() + " {" + entry.getValue() + "} ";
		}
		this.style = "<style type=\"text/css\"> " + this.style + " </style";

		this.selectionStack = new Stack<SourceSelectionProcessor>();
		this.selectionStack.push(root);
	}

	private SourceSelectionProcessor getSourceElementForSourceNode(SourceSelection node) {

		SourceSelectionProcessor result = null;

		// no subnodes; take a default sourceelement
		if (node.getAllChilds().isEmpty()) {
			result = new SourceSelectionProcessor(node);
		} else {
			List<SourceSelectionProcessor> subElements = new ArrayList<SourceSelectionProcessor>();
			for (SourceSelection subnode : node.getAllChilds()) {
				SourceSelectionProcessor se = getSourceElementForSourceNode(subnode);
				subElements.add(se);
			}
			result = new ParentSourceSelectionProcessor(node, subElements);
		}
		return result;
	}

	private void collectAllStyles(SourceSelection node, Map<String, String> styles) {
		if (node.getStyleClass() != null) {
			styles.put(node.getStyleClass(), node.getStyleValues());
		}
		for (SourceSelection subnode : node.getAllChilds()) {
			this.collectAllStyles(subnode, styles);
		}
	}

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

		String fileExtension = source.getName().substring(source.getName().lastIndexOf(".") + 1);
		String _target = source.getName().replace(fileExtension, "html");
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

		this.lineCount = 0;
		printer.println("<html>");
		printer.println("<head>");
		if (this.style != null) {
			printer.println(style);
		}
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
			printer.println("<i style=\"font-size:8pt\">Generated in " + (end - start)
					+ "ms with Source2Html");
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
		// System.out.println(this.sourceLine);
		while (this.remainingLine != null && this.remainingLine.length() > 0) {
			SourceSelectionProcessor foMode = this.selectionStack.peek();
			foMode.process(this, null);
		}
		return this.targetLine.toString();
	}
}
