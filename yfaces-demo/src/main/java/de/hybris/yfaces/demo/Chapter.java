package de.hybris.yfaces.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import de.hybris.yfaces.YComponentInfo;
import de.hybris.yfaces.YComponentRegistry;
import de.hybris.yfaces.taglib.YFacesTaglib;

public class Chapter {

	private static final Logger log = Logger.getLogger(Chapter.class);

	private static final String SECTION_TITLE = "title";
	private static final String SECTION_USECASE = "usecase";
	private static final String SECTION_EXAMPLE = "example-";
	private static final String SECTION_LONGDESCRIPTION = "long";

	private static Pattern chpSectionPattern = Pattern.compile("^\\{(.*?)\\}(.*?)(?=^\\{|\\z)",
			Pattern.DOTALL + Pattern.MULTILINE);

	private static Pattern ycmpTagPattern = Pattern.compile("</?(?:yf)|(?:chp):(.*?)>",
			Pattern.DOTALL);

	private static Pattern ycmpBindingAttribute = Pattern
			.compile("<(?:yf|chp):(?:.*)binding=\"#\\{(.*)\\.(?:.*)>");

	private String title = null;
	private String usecase = null;
	private String longDescription = null;
	private Map<String, String> examples = new HashMap<String, String>();
	private List<ChapterParticipiant> participants = new ArrayList<ChapterParticipiant>();

	private Chapter prevChapter = null;
	private Chapter nextChapter = null;

	public Chapter getPrevChapter() {
		return prevChapter;
	}

	public void setPrevChapter(Chapter prevChapter) {
		this.prevChapter = prevChapter;
	}

	public Chapter getNextChapter() {
		return nextChapter;
	}

	public void setNextChapter(Chapter nextChapter) {
		this.nextChapter = nextChapter;
	}

	private String chapterViewIdRoot = null;
	private String chapterURL = null;

	public Chapter(String chapterViewIdRoot) throws MalformedURLException {
		FacesContext fc = FacesContext.getCurrentInstance();
		this.chapterViewIdRoot = chapterViewIdRoot;
		this.chapterURL = fc.getExternalContext().getRequestContextPath() + chapterViewIdRoot
				+ "/index.jsf";

		// process chapter descriptor
		URL chpDescrURL = fc.getExternalContext().getResource(chapterViewIdRoot + "/index.txt");
		this.initialize(chpDescrURL);

		// find participiants
		this.initParticipants();
	}

	/**
	 * The title of this chapter. Chapter tag: {title}
	 * 
	 * @return title of this chapter
	 */
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getViewId() {
		return this.chapterViewIdRoot + "/index.xhtml";
	}

	public String getViewURL() {
		return this.chapterURL;
	}

	/**
	 * The use-case of this chapter. Chapter tag: {usecase}
	 * 
	 * @return small text which describes a typical use-case of this chapter
	 */
	public String getUseCase() {
		return this.usecase;
	}

	/**
	 * The long description of this chapter. Chapter tag: {long}
	 * 
	 * @return
	 */
	public String getLongDescription() {
		return this.longDescription;
	}

	/**
	 * @return map with all available examples of this chapter
	 */
	public Map<String, String> getExamples() {
		return this.examples;
	}

	public List<ChapterParticipiant> getParticipants() {
		return this.participants;
	}

	/**
	 * Parses the chapter descriptor, generally an index.txt file, and prepares all chapter
	 * information like title, description etc.
	 */
	private void initialize(URL url) {
		StringWriter writer = new StringWriter();

		try {
			InputStream in = url.openStream();
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
	}

	/**
	 * Initializes this chapter with all detected {@link Participant}
	 */
	private void initParticipants() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

		Set<ChapterParticipiant> set = new LinkedHashSet<ChapterParticipiant>();

		// ...retrieve all available resources inside that path
		final Set<String> resources = ctx.getResourcePaths(this.chapterViewIdRoot);

		// iterate over resources...
		for (final String resource : resources) {

			// handle general xml/xhtml
			if (resource.endsWith(".xhtml") || resource.endsWith(".xml")) {
				ChapterParticipiant p = this.createParticipiantFromXhtml(resource);
				set.add(p);
			}

			// handle ycomponent view file (find interface and implementation class)
			if (YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {

				// fetch URL and register at component registry
				URL url = this.getResource(resource);
				YComponentInfo info = YComponentRegistry.getInstance().createYComponentInfo(url);
				if (info.getImplementationClassName() != null) {
					ChapterParticipiant p = this.createParticipiantFromClass(info
							.getImplementationClassName());
					set.add(p);
				}
				if (info.getSpecificationClassName() != null) {
					ChapterParticipiant p = this.createParticipiantFromClass(info
							.getSpecificationClassName());
					set.add(p);

				}
			} else {
				if (resource.endsWith(".xhtml")) {
					String content = getResourceAsString(resource);
					Matcher m = ycmpBindingAttribute.matcher(content);
					while (m.find()) {
						String frame = m.group(1);
						ELContext ectx = FacesContext.getCurrentInstance().getELContext();
						String frameClass = ectx.getELResolver().getValue(ectx, null, frame)
								.getClass().getName();
						ChapterParticipiant p = this.createParticipiantFromClass(frameClass);
						set.add(p);
					}
				}
			}
		}
		this.participants = new ArrayList<ChapterParticipiant>(set);
		Collections.sort(this.participants);
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

	/**
	 * Creates a {@link Participant} and assumes the passed resource string represents a class.
	 * 
	 * @param resource
	 *            java class as String
	 * @return {@link Participant}
	 */
	private ChapterParticipiant createParticipiantFromClass(String resource) {
		ChapterParticipiant p = new ChapterParticipiant();
		p.source = resource;
		p.type = null;
		p.name = resource.substring(resource.lastIndexOf(".") + 1) + ".java";
		return p;
	}

	/**
	 * Creates a {@link Participant} and assumes passed resource String represents a xhtml file.
	 * 
	 * @param resource
	 *            xhtl file location as string
	 * @return {@link Participant}
	 */
	private ChapterParticipiant createParticipiantFromXhtml(String resource) {
		ChapterParticipiant p = new ChapterParticipiant();
		p.source = resource;
		p.type = null;
		p.name = resource.substring(resource.lastIndexOf("/") + 1);
		return p;
	}

	/**
	 * Creates a URL based on the passed resource
	 * 
	 * @param resource
	 *            URL as string
	 * @return URL
	 */
	private URL getResource(String resource) {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		URL result = null;
		try {
			result = ctx.getResource(resource);
		} catch (MalformedURLException e) {
			log.error(e.getMessage());
			log.error("Error while fetching URL for resource " + resource);
		}
		return result;
	}

	/**
	 * Creates a URL based on the passed resource and returns the content.
	 * 
	 * @param resource
	 *            url as string
	 * @return url content
	 */
	private String getResourceAsString(String resource) {
		String result = "";
		try {
			URL url = getResource(resource);
			StringWriter writer = new StringWriter();
			Reader reader = new InputStreamReader(url.openStream());
			IOUtils.copy(reader, writer);
			result = writer.toString();
		} catch (IOException e) {

		}
		return result;
	}

}
