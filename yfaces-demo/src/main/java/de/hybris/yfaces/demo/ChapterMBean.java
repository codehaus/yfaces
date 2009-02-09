package de.hybris.yfaces.demo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.taglib.YFacesTaglib;

/**
 * @author Denny.Strietzbaum
 * 
 */
public class ChapterMBean {

	private static final String SECTION_TITLE = "title";
	private static final String SECTION_USECASE = "usecase";
	private static final String SECTION_EXAMPLE = "example-";
	private static final String SECTION_LONGDESCRIPTION = "long";

	private static final Logger log = Logger.getLogger(ChapterMBean.class);

	private static Pattern chpIdPattern = Pattern.compile("(.*)/chp([0-9]+)(/.*)\\.xhtml");
	private static Pattern chpSectionPattern = Pattern.compile("\\{(.*?)\\}(.*?)(?=\\{|\\z)",
			Pattern.DOTALL);

	private static Pattern ycmpTagPattern = Pattern.compile("</?yf:(.*?)>", Pattern.DOTALL);

	private String chapterCtx = null;
	private String chapterRoot = null;
	private int chapter = 0;

	// chapter descriptor (index.txt)
	// private String indexFile = null;
	private String nextView = null;
	private String prevView = null;

	private String title = null;
	private String usecase = null;
	private String longDescription = null;
	private Map<String, String> examples = new HashMap<String, String>();
	private List participants = new ArrayList();

	public static class Participant {
		private String name = null;
		private String source = null;
		private String unformattedSource = null;
	}

	public ChapterMBean() {
		detectChapterRoot();
		this.prepareChapterNavigation();
		this.prepareChapterSections();
		this.detectParticipants();

	}

	public String getNextPage() {
		return this.nextView;
	}

	public String getPreviousPage() {
		return this.prevView;
	}

	/**
	 * @return title of this chapter
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @return small text which describes a typical use-case of this chapter
	 */
	public String getUseCase() {
		return this.usecase;
	}

	public String getLongDescription() {
		return this.longDescription;
	}

	/**
	 * @return map with all available examples of this chapter
	 */
	public Map<String, String> getExamples() {
		return this.examples;
	}

	public List getParticipants() {
		return this.participants;
	}

	/**
	 * Parses the chapter descriptor, generally an index.txt file, and prepares all chapter
	 * information like title, description etc.
	 */
	private void prepareChapterSections() {
		InputStream in = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream(chapterRoot + "/index.txt");
		StringWriter writer = new StringWriter();

		try {
			InputStreamReader reader = new InputStreamReader(in, "UTF-8");
			IOUtils.copy(reader, writer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String rawContent = writer.toString();
		Matcher m = chpSectionPattern.matcher(rawContent);

		while (m.find()) {
			String type = m.group(1).toLowerCase();
			String content = m.group(2).trim();

			if (type.equals(SECTION_TITLE)) {
				this.title = content;
			}

			if (type.equals(SECTION_USECASE)) {
				this.usecase = content;
			}

			if (type.equals(SECTION_LONGDESCRIPTION)) {
				this.longDescription = this.escapeYComponentTags(content);
			}

			if (type.startsWith(SECTION_EXAMPLE)) {
				String example = type.substring(SECTION_EXAMPLE.length());
				this.examples.put(example, escapeYComponentTags(content));
			}

		}
		log.debug(in);
	}

	/**
	 * Prepares the chapter navigation. This means build up links for previous and next chapter
	 */
	private void prepareChapterNavigation() {
		int index = this.chapterRoot.lastIndexOf("/chp");
		String blankChapter = this.chapterCtx + this.chapterRoot.substring(0, index);

		nextView = blankChapter + "/chp" + (this.chapter + 1) + "/index.jsf";
		if (this.chapter > 1) {
			prevView = blankChapter + "/chp" + (this.chapter - 1) + "/index.jsf";
		}
	}

	private void detectChapterRoot() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String id = fc.getViewRoot().getViewId();
		Matcher m = chpIdPattern.matcher(id);
		if (m.matches()) {
			this.chapter = Integer.parseInt(m.group(2));
			this.chapterCtx = fc.getExternalContext().getRequestContextPath();
			this.chapterRoot = m.group(1) + "/chp" + (chapter);
		} else {
			throw new IllegalStateException("Can't detect chapter root");
		}
	}

	private void detectParticipants() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

		// ...retrieve all available resources inside that path
		final Set<String> resources = ctx.getResourcePaths(this.chapterRoot);

		// iterate over resources...
		for (final String resource : resources) {

			// ...check whether name matches pattern for a YComponent view file
			if (YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {
				try {
					// fetch URL and register at component registry
					final URL url = ctx.getResource(resource);
					YComponentInfo info = YComponentRegistry.getInstance()
							.createYComponentInfo(url);
					if (info.getImplementationClassName() != null) {
						this.participants.add(info.getImplementationClassName());
					}
					if (info.getSpecificationClassName() != null) {
						this.participants.add(info.getSpecificationClassName());
					}

				} catch (final MalformedURLException e) {
					log.error(e.getMessage());
					log.error("Error while fetching URL for resource " + resource);
				}
			}
		}
	}

	/**
	 * Escapes YComponent tags from a given input string.Does no escaping for any other tags.
	 * 
	 * @param in
	 *            input string
	 * @return string with escaped ycompoment tags
	 */
	public String escapeYComponentTags(String in) {
		Matcher m = ycmpTagPattern.matcher(in);
		String result = in;
		if (m.find()) {
			m.reset();
			StringBuilder resultBuilder = null;

			int cursor = 0;
			while (m.find()) {
				if (resultBuilder == null) {
					resultBuilder = new StringBuilder();
				}
				String match = m.group(0);
				match = "<code>" + StringEscapeUtils.escapeHtml(match) + "</code>";

				resultBuilder.append(in.substring(cursor, m.start()));
				resultBuilder.append(match);
				cursor = m.end();
			}
			resultBuilder.append(in.substring(cursor));
			result = resultBuilder.toString();
		}
		return result;
	}

}
