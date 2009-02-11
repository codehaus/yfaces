package de.hybris.yfaces.demo.source2html;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class CompositeSourceElement extends DefaultSourceElement {

	private DefaultSourceElement[] elements = null;

	private Pattern subElementsPattern = null;

	public CompositeSourceElement(List<DefaultSourceElement> subElements) {
		String startPattern = getCompositePatternString(subElements);
		super.startPattern = Pattern.compile(startPattern);
		this.subElementsPattern = this.startPattern;
		super.styleClass = null;
		this.elements = subElements.toArray(new DefaultSourceElement[0]);
	}

	public CompositeSourceElement(String startPattern, String endPattern,
			List<DefaultSourceElement> subElements, String styleClass) {
		super(startPattern, endPattern, styleClass);

		String subPattern = this.getCompositePatternString(subElements) + "|(" + endPattern + ")";
		this.subElementsPattern = Pattern.compile(subPattern);
		this.elements = subElements.toArray(new DefaultSourceElement[0]);
		super.styleClass = styleClass;
	}

	public CompositeSourceElement(SourceNode sourceNode, List<DefaultSourceElement> subElements) {
		super(sourceNode);
		String subPattern = this.getCompositePatternString(subElements) + "|(" + endPattern + ")";
		this.subElementsPattern = Pattern.compile(subPattern);
		this.elements = subElements.toArray(new DefaultSourceElement[0]);
	}

	private String getCompositePatternString(List<DefaultSourceElement> elements) {
		StringBuilder sb = new StringBuilder();
		for (DefaultSourceElement element : elements) {
			sb.append("|(" + element.getStartPattern().pattern() + ")");
		}
		sb.deleteCharAt(0);
		return sb.toString();
	}

	/**
	 * Processes remaining content until nothing is left. Each processing step reduces the amount of
	 * remaining content and increases the amount of target content.
	 */
	@Override
	public void process(SourceDocument doc, String match) {

		// open a span tag with passed style
		// doc.targetLine.append("<span class=\"").append(this.styleClass).append("\">");
		super.openSpanTag(doc);

		// matcher for special java occurrences
		Matcher m = subElementsPattern.matcher(doc.remainingLine);

		// when content needs special formatting...
		if (m.find()) {
			// update target content (with current format until matched position)
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine.substring(0, m.start()));
			doc.targetLine.append(out);

			// doc.targetLine.append("</span>");
			super.closeSpanTag(doc);

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

			if (matchIndex <= this.elements.length) {
				// get the enum value for that matching group
				DefaultSourceElement matchingElement = this.elements[matchIndex - 1];
				doc.foModeStack.push(matchingElement);
				matchingElement.process(doc, matchValue);
			} else {
				// super.process(doc, matchValue);
				doc.foModeStack.pop();
				// doc.targetLine.append("</span>");
				super.closeSpanTag(doc);
			}

		} else {
			// content needs no formatting; just add it to target and clear remaining content
			String out = StringEscapeUtils.escapeHtml(doc.remainingLine);
			doc.targetLine.append(out);
			doc.remainingLine = null;
			// doc.targetLine.append("</span>");
			super.closeSpanTag(doc);
		}
	}

}
