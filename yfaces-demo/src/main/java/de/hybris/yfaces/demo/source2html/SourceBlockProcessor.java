package de.hybris.yfaces.demo.source2html;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class SourceBlockProcessor {

	private SourceBlock sourceBlock = null;

	protected Pattern startPattern = null;
	protected Pattern endPattern = null;

	public SourceBlockProcessor(SourceBlock node) {
		this.sourceBlock = node;
		if (node.getStart() != null) {
			this.startPattern = Pattern.compile(node.getStart());
		}
		if (node.getEnd() != null) {
			this.endPattern = Pattern.compile(node.getEnd());
		}
	}

	public Pattern getEndPattern() {
		return this.endPattern;
	}

	public Pattern getStartPattern() {
		return this.startPattern;
	}

	public void process(SourceDocument doc, String matchElement) {

		// open a span tag with passed style
		this.openSpanTag(doc);

		if (this.endPattern == null) {
			// update remaining content
			doc.remainingLine = doc.remainingLine.substring(matchElement.length());
			doc.targetLine.append(matchElement);
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
				doc.targetLine.append(match);

				// update remaining content
				doc.remainingLine = doc.remainingLine.substring(index);

				doc.foModeStack.pop();

			} else {
				if (sourceBlock.isMultilineMode()) {
					String match = StringEscapeUtils.escapeHtml(doc.remainingLine);
					doc.targetLine.append(match);
					doc.remainingLine = null;
				} else {
					// exception when no literal end is found
					throw new IllegalStateException("Can't find end of current element at line "
							+ doc.lineCount + ":" + doc.sourceLine);
				}
			}
		}
		doc.targetLine.append("</span>");
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[" + this.sourceBlock.getName() + "]";
	}

	protected void openSpanTag(SourceDocument doc) {
		if (this.sourceBlock.getStyleClass() != null) {
			doc.targetLine.append("<span class=\"").append(this.sourceBlock.getStyleClass())
					.append("\">");
		}
	}

	protected void closeSpanTag(SourceDocument doc) {
		if (this.sourceBlock.getStyleClass() != null) {
			doc.targetLine.append("</span>");
		}
	}

}
