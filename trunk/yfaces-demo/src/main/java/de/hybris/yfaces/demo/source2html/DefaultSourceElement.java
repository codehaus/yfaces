package de.hybris.yfaces.demo.source2html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class DefaultSourceElement implements SourceElement {

	private Pattern startPattern = null;
	private Pattern endPattern = null;
	protected String styleClass = null;
	private boolean isMultiLineModeEnabled = true;

	public DefaultSourceElement(String startPattern, String endPattern, String styleClass) {
		if (startPattern != null) {
			this.startPattern = Pattern.compile(startPattern);
		}
		if (endPattern != null) {
			this.endPattern = Pattern.compile(endPattern);
		}

		this.styleClass = styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public Pattern getEndPattern() {
		return this.endPattern;
	}

	public Pattern getStartPattern() {
		return this.startPattern;
	}

	public String getStyleClass() {
		return this.styleClass;
	}

	public void process(SourceDocument doc, String matchElement) {

		// open a span tag with passed style
		doc.targetLine.append("<span class=\"").append(this.styleClass).append("\">");

		if (this.endPattern == null) {
			// update remaining content
			doc.remainingLine = doc.remainingLine.substring(matchElement.length());
			doc.targetLine.append(matchElement);
			doc.targetLine.append("</span>");
			doc.foModeStack.pop();
		} else {

			// find end of literal...
			Matcher m = this.endPattern.matcher(doc.remainingLine);

			// literal end must be available
			if (m.find()) {
				int index = m.end();

				// get literal, html escaping, close span tag, add to target line
				String match = doc.remainingLine.substring(0, index);
				match = StringEscapeUtils.escapeHtml(match);
				doc.targetLine.append(match).append("</span>");

				// update remaining content
				doc.remainingLine = doc.remainingLine.substring(index);

				doc.foModeStack.pop();

			} else {
				if (isMultiLineModeEnabled) {
					String match = StringEscapeUtils.escapeHtml(doc.remainingLine);
					doc.targetLine.append(match).append("</span>");
					doc.remainingLine = null;
				} else {
					// exception when no literal end is found
					throw new IllegalStateException("Can't find end of current element at line "
							+ doc.lineCount + ":" + doc.sourceLine);
				}
			}
		}
	}

}
