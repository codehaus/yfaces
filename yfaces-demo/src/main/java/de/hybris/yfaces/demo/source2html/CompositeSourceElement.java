package de.hybris.yfaces.demo.source2html;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class CompositeSourceElement extends DefaultSourceElement {

	private SourceElement[] elements = null;

	private Pattern subElementsPattern = null;

	public CompositeSourceElement(List<SourceElement> subElements, String styleClass) {
		this(null, null, subElements, styleClass);
	}

	public CompositeSourceElement(String startPattern, String endPattern, List<SourceElement> subElements,
			String styleClass) {
		super(startPattern, endPattern, styleClass);
		StringBuilder sb = new StringBuilder();
		for (SourceElement element : subElements) {
			sb.append("|(" + element.getStartPattern().pattern() + ")");
		}

		if (endPattern != null) {
			// add this ending pattern
			sb.append("|(" + endPattern + ")");
		}
		sb.deleteCharAt(0);

		this.subElementsPattern = Pattern.compile(sb.toString());
		this.elements = subElements.toArray(new SourceElement[0]);
	}

	/**
	 * Processes remaining content until nothing is left. Each processing step reduces the amount of
	 * remaining content and increases the amount of target content.
	 */
	@Override
	public void process(SourceDocument doc, String match) {

		// matcher for special java occurrences
		Matcher m = subElementsPattern.matcher(doc.remainingLine);

		// when content needs special formatting...
		if (m.find()) {

			// update target content (with current format until matched position)
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine.substring(0, m.start()));
			doc.targetLine.append(out);

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

			// get the enum value for that matching group
			SourceElement matchingElement = this.elements[matchIndex - 1];

			if (matchingElement == this) {
				super.process(doc, matchValue);
			} else {
				doc.foModeStack.push(matchingElement);
				matchingElement.process(doc, matchValue);
			}

		} else {
			// content needs no formatting; just add it to target and clear remaining content
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine);
			doc.targetLine.append(out);
			doc.remainingLine = null;
		}
	}

}
