package de.hybris.yfaces.demo.source2html;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A virtual selection over the source code. Virtual because range of selection is given implicitly
 * via a regular expression. Formatting options for this selection are set as css styles. Each
 * selection can have child definitions which are only processed when this parent selection is
 * processed. The range of a selection is at least given by a start pattern. When an end pattern is
 * given, the selection reaches from start index of start {@link Matcher} until start index of end
 * {@link Matcher} (useful for selecting blocks of code). When no ending pattern is given selection
 * reaches from start index of start {@link Matcher} until end index of start {@link Matcher}
 * (useful for selecting single keywords) <br/>
 * <b>Neither start nor end patterns mustn't contain any capturing group (non-capturing is
 * allowed)</b>
 * 
 * @author Denny.Strietzbaum
 */
public class SourceSelection {

	private String styleClass = null;
	private String styleValues = null;

	private String openingTag = "";
	private String closingTag = "";

	private boolean isMultilineMode = false;
	private String name = null;

	private List<SourceSelection> childSelections = null;

	private Pattern startPattern = null;
	private Pattern endPattern = null;

	/**
	 * Constructor. Creates a virtual selection over a range of chars (source code). range of
	 * selection is defined by a start and end point which both are given as regular expression.
	 * 
	 * @param start
	 *            starting point as regular expression
	 * @param end
	 *            ending point as regular expression
	 */
	public SourceSelection(String start, String end) {
		this.childSelections = new ArrayList<SourceSelection>();
		if (start != null) {
			this.startPattern = this.createPattern(start);
		}
		if (end != null) {
			this.endPattern = this.createPattern(end);
		}
	}

	/**
	 * Add a child selection to this parent selection. A child selection gets only processed when
	 * current processing takes place inside that parent selection.
	 * 
	 * @param subnode
	 *            child selection
	 */
	public void addChild(SourceSelection subnode) {
		this.childSelections.add(subnode);
	}

	/**
	 * Returns a list of all child selections
	 * 
	 * @return List of {@link SourceSelection}
	 */
	protected List<SourceSelection> getAllChilds() {
		return this.childSelections;
	}

	/**
	 * Returns the style values for this selection.
	 * 
	 * @return style values
	 */
	public String getStyleValues() {
		return styleValues;
	}

	/**
	 * Sets the style values for this selection. This is a semicolon separated list of styles. (no
	 * style class, no opening and closing braces)
	 * 
	 * @param styleValues
	 *            style values
	 */
	public void setStyleValues(String styleValues) {
		this.styleValues = styleValues;
	}

	/**
	 * Returns the virtual start of this selection. A selection starts, when the regular expression
	 * returned by this method matches the currently processed source code.
	 * 
	 * @return regular expression which indicates the start of this selection
	 */
	public Pattern getStartPattern() {
		return this.startPattern;
	}

	/**
	 * Returns the virtual end of this selection. A selection ends when the regular expression
	 * returned by this method matches the currently processed source code.
	 * 
	 * @return regular expression which indicates the end of this selection
	 */
	public Pattern getEndPattern() {
		return this.endPattern;
	}

	/**
	 * Returns the style class for this selection.
	 * 
	 * @return style class as string
	 */
	public String getStyleClass() {
		return styleClass;
	}

	/**
	 * Sets the style class for this selection. Whenever a non-empty style class is set this
	 * selection is supposed to return an opened/closed &lt;span&gt; tag with this style class
	 * associated whenever {@link #getOpeningTag()} and {@link #getClosingTag()} is called.
	 * 
	 * @param styleClass
	 *            style class of this selection
	 */
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;

		if (styleClass != null && styleClass.trim().length() > 0) {
			this.openingTag = "<span class=\"" + styleClass + "\">";
			this.closingTag = "</span>";
		} else {
			this.openingTag = "";
			this.closingTag = "";
		}
	}

	/**
	 * Returns whether this selection supports spanning over multiple lines.
	 * 
	 * @return true when selection is allowed to span over multiple lines
	 */
	public boolean isMultilineMode() {
		return isMultilineMode;
	}

	/**
	 * Set to true when this selection is allowed to span over multiple lines of source code.
	 * 
	 * @param isMultilineMode
	 *            true when selection may span over multiple lines
	 */
	public void setMultilineMode(boolean isMultilineMode) {
		this.isMultilineMode = isMultilineMode;
	}

	/**
	 * Returns the name of this selection.
	 * 
	 * @return name of this selection as String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this selection. This is just for information and doesn't have any effect on
	 * the selection nor the rendered output.
	 * 
	 * @param name
	 *            name of this selection
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the opening tag of this selection (html markup)
	 * 
	 * @return opening html tag as String
	 */
	public String getOpeningTag() {
		return this.openingTag;
	}

	/**
	 * Returns the closing tag for this selection (html markup)
	 * 
	 * @return clsoing html tag as String
	 */
	public String getClosingTag() {
		return this.closingTag;
	}

	/**
	 * Creates a {@link Pattern} for the passed argument and validates whether it contains no
	 * capturing groups
	 * 
	 * @param regex
	 *            regular expression
	 * @return {@link Pattern}
	 */
	private Pattern createPattern(String regex) {
		Pattern result = null;

		// first compile the requested pattern
		try {
			result = Pattern.compile(regex);
		} catch (Exception e) {
			throw new IllegalArgumentException(regex + " is not a valid Pattern", e);
		}

		// second check whether it contains no capturing groups
		// therefore add a 'wildcard' group which always matches and ask the Matcher for
		// the number of groups
		String verifyPattern = "(.*)|" + regex;
		Pattern checkPattern = Pattern.compile(verifyPattern);
		int groups = checkPattern.matcher("dummy").groupCount();
		if (groups > 1) {
			throw new IllegalArgumentException(regex + " contains " + (groups - 1)
					+ " capturing group(s) (only non-capturing is allowed)");
		}

		return result;
	}

}
