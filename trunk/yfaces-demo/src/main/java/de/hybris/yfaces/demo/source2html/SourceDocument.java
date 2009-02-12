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

/**
 * Represents a source which shall be transformed into html markup. Transformation is processed line
 * based. One line is taken from source (e.g. a {@link Reader}), processed according available
 * {@link SourceSelection} and written into a target (e.g. a {@link Writer}.
 * 
 * @author Denny.Strietzbaum
 */
public class SourceDocument {

	public static final String STYLECLASS_LINENUMBER = "jh_line";

	protected int lineCount = 0;
	protected String sourceLine = null;
	protected String remainingLine = null;
	protected StringBuilder targetLine = null;

	// stack with current active selection (TODO: make private?)
	protected Stack<SourceSelectionProcessor> selectionStack = null;

	private String style = null;

	// sourceselection which selects the whole document
	private SourceSelection sourceSelection = new SourceSelection(null, null);

	/**
	 * Returns the root selection for this source document. This one has the whole document selected
	 * but child selections are added via {@link SourceSelection#addChild(SourceSelection)}
	 * 
	 * @return {@link SourceSelection} which selects the whole document
	 */
	public SourceSelection getSourceSelection() {
		return this.sourceSelection;
	}

	public void compileConfiguration() {

		// create processors for all selections
		SourceSelectionProcessor root = createProcessors(this.sourceSelection);

		// create inline styles for all selections
		this.style = createInlineStyles(this.sourceSelection);

		// initialize selection stack
		this.selectionStack = new Stack<SourceSelectionProcessor>();
		this.selectionStack.push(root);
	}

	/**
	 * Creates the hierarchy of all {@link SourceSelectionProcessor} for this document. The passed
	 * sourceselection ought to be the root selection which selects the whole document. For this
	 * selection as well as each child selection an appropriate {@link SourceSelectionProcessor} is
	 * created.
	 * 
	 * @param sourceSelection
	 *            root selection which selects the full document
	 * @return {@link SourceSelectionProcessor}
	 */
	private SourceSelectionProcessor createProcessors(SourceSelection sourceSelection) {

		SourceSelectionProcessor result = null;

		// no subnodes; take a default sourceelement
		if (sourceSelection.getAllChilds().isEmpty()) {
			result = new SourceSelectionProcessor(sourceSelection);
		} else {
			List<SourceSelectionProcessor> subElements = new ArrayList<SourceSelectionProcessor>();
			for (SourceSelection subnode : sourceSelection.getAllChilds()) {
				SourceSelectionProcessor se = createProcessors(subnode);
				subElements.add(se);
			}
			result = new ParentSourceSelectionProcessor(sourceSelection, subElements);
		}
		return result;
	}

	/**
	 * Collects all styles from passed selection and all child selections. Creates and returns an
	 * inline style representation which is meant to be placed into the &lt;head&gt; body.
	 * 
	 * @param selection
	 *            the selection to start with collecting
	 * @return inline style sheet representation
	 */
	private String createInlineStyles(SourceSelection selection) {
		String result = "";
		Map<String, String> styles = new LinkedHashMap<String, String>();
		this.collectStyles(selection, styles);
		for (Map.Entry<String, String> entry : styles.entrySet()) {
			result = result + " span." + entry.getKey() + " {" + entry.getValue() + "} ";
		}
		result = "<style type=\"text/css\"> " + result + " span." + STYLECLASS_LINENUMBER
				+ " {color:rgb(120,120,120)} </style";
		return result;
	}

	/**
	 * Internal. Recursive collection of styles and their classes.
	 * 
	 * @param selection
	 * @param styleMap
	 */
	private void collectStyles(SourceSelection selection, Map<String, String> styleMap) {
		if (selection.getStyleClass() != null) {
			styleMap.put(selection.getStyleClass(), selection.getStyleValues());
		}
		for (SourceSelection subnode : selection.getAllChilds()) {
			this.collectStyles(subnode, styleMap);
		}
	}

	/**
	 * Processes content from one file and writes html markup into another file.
	 * 
	 * @param source
	 *            file which provides the source
	 * @param target
	 *            file which is used for generated html markup
	 */
	public void format(File source, File target) {

		if (!source.exists()) {
			throw new IllegalArgumentException("Source '" + source + " doesn't exist");
		}

		if (!source.isFile()) {
			throw new IllegalArgumentException("Source '" + source + " is not a file");
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
	 * Processes content from one Reader and writes html markup into a Writer.
	 * 
	 * @param source
	 *            reader which provides the source
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
				String result = this.processLine(line);
				printer.println(result);
			}
			long end = System.currentTimeMillis();
			printer.println("</code></pre>");
			printer.println();
			printer.println("<i style=\"font-size:8pt\">Generated in " + (end - start)
					+ "ms with PrettySourceCode (PSC)");
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
	 * Sets style information for this document.By default inline styles are generated which can be
	 * overwritten.For inline styles use <code>&lt;style type="text/css"&gt;...&lt;style&gt;</code>
	 * for external styles use
	 * <code>&ltlink rel="stylesheet" type="text/css" href="path/to/styles.css"&gt;</code>
	 * 
	 * @param style
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Returns the stylesheet definition for this documents generated html markup.
	 * 
	 * @return styles as String
	 */
	public String getStyle() {
		return this.style;
	}

	/**
	 * Processes a whole line of source. Iterates over the line content and chooses an appropriate
	 * {@link SourceSelectionProcessor} for the content
	 * 
	 * @return html formatted line
	 */
	private String processLine(String sourceLine) {
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
