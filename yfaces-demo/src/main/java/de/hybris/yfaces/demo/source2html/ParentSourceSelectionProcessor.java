package de.hybris.yfaces.demo.source2html;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processes a {@link SourceSelection} according the selection properties. Additionally a collection
 * of child processors can be configured. Each of the child specifies a subselection within this
 * parent selection.
 * 
 * @author Denny.Strietzbaum
 */
public class ParentSourceSelectionProcessor extends SourceSelectionProcessor {

	private SourceSelectionProcessor[] processors = null;
	private Pattern compositePattern = null;

	/**
	 * Constructor. The passed {@link SourceSelection} specifies the part of source which shall be
	 * processed by this processor instance. Additionally a list of child processors are passed
	 * which all become active as long as this processor stays active (as long as the current
	 * selection is processed)
	 * 
	 * @param sourceSelection
	 *            {@link SourceSelection} which this processor belongs to
	 * @param childSelections
	 *            List of child processors
	 */
	protected ParentSourceSelectionProcessor(SourceSelection sourceSelection,
			List<SourceSelectionProcessor> childSelections) {
		super(sourceSelection);
		this.compositePattern = this.createCompositePattern(childSelections);
		this.processors = childSelections.toArray(new SourceSelectionProcessor[0]);
	}

	/**
	 * Processes remaining content until nothing is left. Each processing step reduces the amount of
	 * remaining content and increases the amount of target content.
	 */
	@Override
	public void process(SourceDocument doc, String startMatch) {

		// match beginning of a child selection or end of this parent selection
		Matcher m = compositePattern.matcher(doc.remainingLine);
		if (m.find()) {

			// print content between remaining content and matched position with current formatting
			if (m.start() > 0) {
				String content = doc.remainingLine.substring(0, m.start());
				this.processContent(doc, content);
			}

			// the match itself (one of the optional ones)
			String matchValue = m.group(0);

			// find the group which is responsible for the match
			int matchIndex = 1;
			for (matchIndex = 1; matchIndex <= m.groupCount(); matchIndex++) {
				if (m.group(matchIndex) != null) {
					break;
				}
			}

			// when current match belongs to a child selection ...
			if (matchIndex <= this.processors.length) {
				// ... make this selection active at document and start child processing
				SourceSelectionProcessor matchingElement = this.processors[matchIndex - 1];
				doc.selectionStack.push(matchingElement);
				matchingElement.process(doc, matchValue);
			} else {
				// ...otherwise current match was the end of this parent selection
				// process content of end match
				this.processContent(doc, doc.remainingLine.substring(0, m.end() - m.start()));
				doc.selectionStack.pop();
			}

		} else {
			this.processContent(doc, doc.remainingLine);
		}
	}

	/**
	 * Creates a Pattern which matches the beginning of each {@link SourceSelection} provided as
	 * List of {@link SourceSelectionProcessor} and additionally matches the ending of this
	 * processors {@link SourceSelection}.
	 * 
	 * @param elements
	 *            List of {@link SourceSelectionProcessor} whose {@link SourceSelection} beginning
	 *            pattern is used
	 * @return {@link Pattern}
	 */
	private Pattern createCompositePattern(List<SourceSelectionProcessor> elements) {
		StringBuilder sb = new StringBuilder();
		for (SourceSelectionProcessor element : elements) {
			sb.append("|(" + element.sourceSelection.getStartPattern().pattern() + ")");
		}
		sb.deleteCharAt(0);

		if (this.sourceSelection.getEndPattern() != null) {
			sb.append("|(" + this.sourceSelection.getEndPattern().pattern() + ")");
		}

		Pattern result = Pattern.compile(sb.toString());
		return result;
	}

}
