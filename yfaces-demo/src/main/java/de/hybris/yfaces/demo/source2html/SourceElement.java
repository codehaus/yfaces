package de.hybris.yfaces.demo.source2html;

import java.util.regex.Pattern;

public interface SourceElement {

	public String getName();

	public String getStyleClass();

	public Pattern getStartPattern();

	public Pattern getEndPattern();

	// true when fully formatted
	public void process(SourceDocument doc, String matchElement);
}
