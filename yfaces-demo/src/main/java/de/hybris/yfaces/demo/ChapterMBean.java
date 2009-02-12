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

/**
 * @author Denny.Strietzbaum
 */
public class ChapterMBean {

	private static final String SECTION_TITLE = "title";
	private static final String SECTION_USECASE = "usecase";
	private static final String SECTION_EXAMPLE = "example-";
	private static final String SECTION_LONGDESCRIPTION = "long";

	private static final Logger log = Logger.getLogger(ChapterMBean.class);

	private static Pattern chpIdPattern = Pattern.compile("(.*)/chp([0-9]+)(/.*)\\.xhtml");
	private static Pattern chpSectionPattern = Pattern.compile("^\\{(.*?)\\}(.*?)(?=^\\{|\\z)",
			Pattern.DOTALL + Pattern.MULTILINE);

	private static Pattern ycmpTagPattern = Pattern.compile("</?yf:(.*?)>", Pattern.DOTALL);

	private static Pattern ycmpBindingAttribute = Pattern
			.compile("<yf:(?:.*)binding=\"#\\{(.*)\\.(?:.*)>");

	private String chapterCtx = null;
	private String chapterRoot = null;
	private int chapter = 0;

	private String nextView = null;
	private String prevView = null;

	private String title = null;
	private String usecase = null;
	private String longDescription = null;
	private Map<String, String> examples = new HashMap<String, String>();
	private List<Participant> participants = new ArrayList<Participant>();

	/**
	 * A Participant defines a resource (java source, xhtml file) which is part of this chapter.
	 */
	public static class Participant implements Comparable<Participant> {
		private String name = null;
		private String source = null;
		private String type = null;

		public String getName() {
			return name;
		}

		public String getSource() {
			return source;
		}

		public String getType() {
			return type;
		}

		@Override
		public boolean equals(Object obj) {
			return this.source.equals(((Participant) obj).source);
		}

		@Override
		public int hashCode() {
			return this.source.hashCode();
		}

		public int compareTo(Participant o) {
			return this.source.compareTo(o.source);
		}

	}

	public ChapterMBean() {
		initChapterRoot();
		this.initChapterNavigation();
		this.initChapterSections();
		this.initParticipants();

	}

	public String getNextPage() {
		return this.nextView;
	}

	public String getPreviousPage() {
		return this.prevView;
	}

	/**
	 * The title of this chapter. Chapter tag: {title}
	 * 
	 * @return title of this chapter
	 */
	public String getTitle() {
		return this.title;
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

	public List<Participant> getParticipants() {
		return this.participants;
	}

	/**
	 * Parses the chapter descriptor, generally an index.txt file, and prepares all chapter
	 * information like title, description etc.
	 */
	private void initChapterSections() {
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
	 * Some basic initializations for this chapter.
	 */
	private void initChapterRoot() {
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

	/**
	 * initializes this chapter with next/previous navigation options.
	 */
	private void initChapterNavigation() {
		int index = this.chapterRoot.lastIndexOf("/chp");
		String blankChapter = this.chapterCtx + this.chapterRoot.substring(0, index);

		nextView = blankChapter + "/chp" + (this.chapter + 1) + "/index.jsf";
		if (this.chapter > 1) {
			prevView = blankChapter + "/chp" + (this.chapter - 1) + "/index.jsf";
		}
	}

	/**
	 * Initializes this chapter with all detected {@link Participant}
	 */
	private void initParticipants() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

		Set<Participant> set = new LinkedHashSet<Participant>();

		// ...retrieve all available resources inside that path
		final Set<String> resources = ctx.getResourcePaths(this.chapterRoot);

		// iterate over resources...
		for (final String resource : resources) {

			if (resource.endsWith(".xhtml")) {
				Participant p = this.createParticipiantFromXhtml(resource);
				set.add(p);
			}

			// ... from a component extract interface and implementation class
			if (YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {

				// fetch URL and register at component registry
				URL url = this.getResource(resource);
				YComponentInfo info = YComponentRegistry.getInstance().createYComponentInfo(url);
				if (info.getImplementationClassName() != null) {
					Participant p = this.createParticipiantFromClass(info
							.getImplementationClassName());
					set.add(p);
				}
				if (info.getSpecificationClassName() != null) {
					Participant p = this.createParticipiantFromClass(info
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
						Participant p = this.createParticipiantFromClass(frameClass);
						set.add(p);
					}
				}
			}
		}
		this.participants = new ArrayList<Participant>(set);
		Collections.sort(this.participants);
	}

	/**
	 * Creates a {@link Participant} and assumes the passed resource string represents a class.
	 * 
	 * @param resource
	 *            java class as String
	 * @return {@link Participant}
	 */
	private Participant createParticipiantFromClass(String resource) {
		Participant p = new Participant();
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
	private Participant createParticipiantFromXhtml(String resource) {
		Participant p = new Participant();
		p.source = resource;
		p.type = null;
		p.name = resource.substring(resource.lastIndexOf("/") + 1);
		return p;
	}

	/**
	 * Escapes each YComponent tags (&ltyf:.../&gt;) from passed content so that they can be used
	 * with html markup. Any other tags (generally html ones) stay untouched.
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
				match = "<code>" + StringEscapeUtils.escapeHtml(match) + "</code>";

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
