package de.hybris.yfaces.demo.source2html;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class CompositeSourceBlockProcessor extends SourceSelectionProcessor {

	private SourceSelectionProcessor[] processors = null;
	private Pattern compositePattern = null;

	protected CompositeSourceBlockProcessor(SourceSelection sourceNode,
			List<SourceSelectionProcessor> subElements) {
		super(sourceNode);
		String subPattern = this.getCompositePatternString(subElements);
		if (sourceSelection.getEndPattern() != null) {
			subPattern = subPattern + "|(" + sourceSelection.getEndPattern().pattern() + ")";
		}

		this.compositePattern = Pattern.compile(subPattern);
		this.processors = subElements.toArray(new SourceSelectionProcessor[0]);
	}

	private String getCompositePatternString(List<SourceSelectionProcessor> elements) {
		StringBuilder sb = new StringBuilder();
		for (SourceSelectionProcessor element : elements) {
			sb.append("|(" + element.sourceSelection.getStartPattern().pattern() + ")");
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}

	/**
	 * Processes remaining content until nothing is left. Each processing step reduces the amount of
	 * remaining content and increases the amount of target content.
	 */
	@Override
	public void process(SourceDocument doc, String startMatch) {

		// open a span tag with passed style
		doc.targetLine.append(sourceSelection.getOpeningTag());

		// matcher for special java occurrences
		Matcher m = compositePattern.matcher(doc.remainingLine);

		// when content needs special formatting...
		if (m.find()) {
			// update target content (with current format until matched position)
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine.substring(0, m.start()));
			doc.targetLine.append(out);

			doc.targetLine.append(sourceSelection.getClosingTag());

			// update remaining content
			doc.remainingLine = doc.remainingLine.substring(m.start());

			// the match itself (one of the optional ones)
			String matchValue = m.group(0);

			// find the group which is responsible for the match
			int matchIndex = 1;
			for (matchIndex = 1; matchIndex <= m.groupCount(); matchIndex++) {
				if (m.group(matchIndex) != null) {
					break;
				}
			}

			if (matchIndex <= this.processors.length) {
				// get the enum value for that matching group
				SourceSelectionProcessor matchingElement = this.processors[matchIndex - 1];
				doc.selectionStack.push(matchingElement);
				matchingElement.process(doc, matchValue);
			} else {
				doc.selectionStack.pop();
				doc.targetLine.append(sourceSelection.getClosingTag());
			}

		} else {
			// content needs no formatting; just add it to target and clear remaining content
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine);
			doc.targetLine.append(out);
			doc.remainingLine = null;
			doc.targetLine.append(sourceSelection.getClosingTag());
		}
	}

}
