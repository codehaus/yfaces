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
	 * Constructor.
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

		// add html markup which opens this selection
		doc.targetLine.append(this.sourceSelection.getOpeningTag());

		// when no end pattern is available the range of start pattern match is taken instead
		if (sourceSelection.getEndPattern() == null) {

			// add content to target, remove content from source, finish selection
			doc.targetLine.append(startMatch);
			doc.remainingLine = doc.remainingLine.substring(startMatch.length());
			doc.selectionStack.pop();
		} else {

			// try to find the end of this selection ...
			Matcher m = sourceSelection.getEndPattern().matcher(doc.remainingLine);

			// when end is available...
			if (m.find()) {

				// extract and process content
				int index = m.end();
				String match = doc.remainingLine.substring(0, index);
				match = StringEscapeUtils.escapeHtml(match);

				// add content to target, remove content from source, finish selection
				doc.targetLine.append(match);
				doc.remainingLine = doc.remainingLine.substring(index);
				doc.selectionStack.pop();

			} else {
				// when no selection end is available...
				// and multiline is enabled
				if (sourceSelection.isMultilineMode()) {

					// extract and process content
					String match = StringEscapeUtils.escapeHtml(doc.remainingLine);
					// add content to target, remove content from source, don't finish selection
					doc.targetLine.append(match);
					doc.remainingLine = null;
				} else {
					// exception when no multiline is allowed
					throw new IllegalStateException("Can't find end of current element at line "
							+ doc.lineCount + ":" + doc.sourceLine);
				}
			}
		}
		// add html markup which closes this selection
		doc.targetLine.append(this.sourceSelection.getClosingTag());
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.sourceSelection.getName() + "]";
	}

}
