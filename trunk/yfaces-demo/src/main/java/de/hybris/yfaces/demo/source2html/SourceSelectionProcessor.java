package de.hybris.yfaces.demo.source2html;

import java.util.regex.Matcher;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Processes a {@link SourceSelection} according their selection properties.
 * 
 * @author Denny.Strietzbaum
 */
public class SourceSelectionProcessor {

	protected SourceSelection sourceSelection = null;

	/**
	 * Constructor. The passed {@link SourceSelection} specifies the part of source which shall be
	 * processed by this processor instance
	 * 
	 * @param selection
	 *            {@link SourceSelection} which this processor belongs to
	 */
	protected SourceSelectionProcessor(SourceSelection selection) {
		this.sourceSelection = selection;
	}

	/**
	 * Processes the source document with current selection properties. The first char of source
	 * documents remaining line must be the first char of this processors {@link SourceSelection}
	 * During processing reduces the source documents remaining content and increases source
	 * documents target content
	 * 
	 * @param doc
	 *            the {@link SourceDocument}; used to retrieve the output writer
	 * @param startMatch
	 *            the start patterns matching value
	 */
	public void process(SourceDocument doc, String startMatch) {

		// when no end pattern is available the range of start pattern match is taken instead
		if (sourceSelection.getEndPattern() == null) {
			this.processContent(doc, startMatch);
			doc.selectionStack.pop();
		} else {

			// try to find the end of this selection ...
			Matcher m = sourceSelection.getEndPattern().matcher(doc.remainingLine);

			// when an end match is available...
			if (m.find()) {
				// process content until end index of current match
				String content = doc.remainingLine.substring(0, m.end());
				this.processContent(doc, content);
				doc.selectionStack.pop();

			} else {
				// when no selection end is available...
				// and multiline is enabled
				if (sourceSelection.isMultilineMode()) {
					// process content until end of line
					this.processContent(doc, doc.remainingLine);
				} else {
					// or throw exception when no multiline is allowed
					throw new IllegalStateException("Can't find end of current element at line "
							+ doc.lineCount + ":" + doc.sourceLine);
				}
			}
		}
	}

	/**
	 * Prints passed content into documents output channel. Does all necessary html escaping and
	 * updates the documents input channel (remove processed content)
	 * 
	 * @param doc
	 *            {@link SourceDocument}
	 * @param content
	 */
	protected void processContent(SourceDocument doc, String content) {
		// escape content for valid html markup
		String escaped = StringEscapeUtils.escapeHtml(content);

		// open appropriate html tag, add content to output, close tag
		doc.targetLine.append(sourceSelection.getOpeningTag());
		doc.targetLine.append(escaped);
		doc.targetLine.append(sourceSelection.getClosingTag());

		// remove processed content from input
		doc.remainingLine = doc.remainingLine.substring(content.length());

	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.sourceSelection.getName() + "]";
	}

}
