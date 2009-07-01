package de.hybris.yfaces.demo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class ChapterText {

	private static Pattern ycmpTagPattern = Pattern.compile("</?(?:yf|chp):(.*?)>", Pattern.DOTALL);

	private static Pattern placeholderPattern = Pattern.compile("_([^\\n]*?)_", Pattern.DOTALL);

	private String content = null;
	private StringBuilder builder = null;
	private int cursor = 0;
	private String formattedHtml = null;

	private Chapter chapter = null;

	public ChapterText(Chapter chapter, String rawContent) {
		this.chapter = chapter;
		this.content = rawContent;
		this.builder = new StringBuilder();
		content = this.escapeYComponentTags(rawContent);
		this.processContent();
	}

	public void processContent() {
		Matcher m = placeholderPattern.matcher(content);
		while (m.find()) {
			int start = m.start();
			int end = m.end();

			String head = content.substring(cursor, start);
			head = head.replaceAll("\\r\\n", " <br/>");
			builder.append(head);

			String match = content.substring(start + 1, end - 1);

			if (!match.startsWith("$")) {
				this.processAsBoldText(match);
			} else {
				this.processAsParticipantLink(match.substring(1));
			}

			cursor = end;
		}
		String tail = content.substring(cursor, content.length());
		builder.append(tail.replaceAll("\\r\\n", "<br/>"));

		this.formattedHtml = builder.toString();
	}

	private void processAsBoldText(String content) {
		builder.append("<span style=\"font-family:monospace; font-weight:bold; color:#555555\">"
				+ content + "</span>");
	}

	private void processAsParticipantLink(String content) {
		ChapterParticipiant part = chapter.getParticipantsMap().get(content);
		builder.append("<a href=\"?source=" + part.getSource() + "\" target=\"" + part.getName()
				+ "\" " + "onclick=\"javascript:window.open('#','" + part.getName()
				+ "','height=600,width=800,scrollbars=yes,resizeable=yes')\">");
		builder.append("<span style=\"yfPrettySourceLink\">[" + part.getName() + "]</span>");
		builder.append("</a>");
	}

	/**
	 * Escapes each YComponent tags (&ltyf:.../&gt;) from passed content so that they can be used
	 * with html markup. Oher tags (generally html ones) are staying untouched.
	 * 
	 * @param content
	 *            input string
	 * @return string with escaped ycompoment tags
	 */
	public String escapeYComponentTags(String content) {
		Matcher m = ycmpTagPattern.matcher(content);
		String result = content;
		if (m.find()) {
			m.reset();
			StringBuilder resultBuilder = null;

			int cursor = 0;
			while (m.find()) {
				if (resultBuilder == null) {
					resultBuilder = new StringBuilder();
				}
				String match = m.group(0);
				match = "<span class='yfExample'><code>" + StringEscapeUtils.escapeHtml(match)
						+ "</code></span>";

				resultBuilder.append(content.substring(cursor, m.start()));
				resultBuilder.append(match);
				cursor = m.end();
			}
			resultBuilder.append(content.substring(cursor));
			result = resultBuilder.toString();
		}
		return result;
	}

	public String getAsHtml() {
		return this.formattedHtml;
	}
}
