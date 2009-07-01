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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import de.hybris.yfaces.YFacesTaglib;
import de.hybris.yfaces.component.YComponentFactory;
import de.hybris.yfaces.component.YComponentInfo;
import de.hybris.yfaces.component.YFrame;

public class Chapter {

	private static final Logger log = Logger.getLogger(Chapter.class);

	private static final String CHAPTER_DESCRIPTOR = "index.txt";

	private static final String SECTION_TITLE = "title";
	private static final String SECTION_USECASE = "usecase";
	private static final String SECTION_EXAMPLE = "example-";
	private static final String SECTION_LONGDESCRIPTION = "long";

	private static Pattern chpSectionPattern = Pattern.compile("^\\{(.*?)\\}(.*?)(?=^\\{|\\z)",
			Pattern.DOTALL + Pattern.MULTILINE);

	private static Pattern ycmpBindingAttribute = Pattern
			.compile("<(?:yf|chp):(?:.*)binding=\"#\\{(.*)\\.(?:.*)>");

	private String title = null;
	private String usecase = null;
	private String longDescription = null;
	private Map<String, String> examples = new HashMap<String, String>();
	private List<ChapterParticipiant> participants = new ArrayList<ChapterParticipiant>();
	Map<String, ChapterParticipiant> participantsMap = new LinkedHashMap<String, ChapterParticipiant>();

	private Chapter prevChapter = null;
	private Chapter nextChapter = null;
	private String chapterViewIdRoot = null;
	private String chapterURL = null;

	/**
	 * Constructs a new Chapter based on the passed view root.
	 * 
	 * @param chapterViewIdRoot
	 *            view root e.g. /demo/chapter5
	 * @throws IllegalArgumentException
	 *             in case this chapter can't be created
	 */
	public Chapter(String chapterViewIdRoot) {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
		this.chapterViewIdRoot = chapterViewIdRoot;
		this.chapterURL = ctx.getRequestContextPath() + chapterViewIdRoot + "/index.jsf";

		// process chapter descriptor
		URL chpDescrURL = null;
		try {
			chpDescrURL = ctx.getResource(chapterViewIdRoot + "/" + CHAPTER_DESCRIPTOR);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Can't create resource " + chapterURL, e);
		}

		if (chpDescrURL == null) {
			throw new IllegalArgumentException("Can't create resource " + chapterURL);
		}

		// find participiants
		this.initParticipants();

		this.initialize(chpDescrURL);
	}

	/**
	 * Returns the previous {@link Chapter}
	 * 
	 * @return {@link Chapter}
	 */
	public Chapter getPrevChapter() {
		return prevChapter;
	}

	/**
	 * Sets the previous {@link Chapter}
	 * 
	 * @param prevChapter
	 *            {@link Chapter}
	 */
	public void setPrevChapter(Chapter prevChapter) {
		this.prevChapter = prevChapter;
	}

	/**
	 * Returns the next {@link Chapter}
	 * 
	 * @return {@link Chapter}
	 */
	public Chapter getNextChapter() {
		return nextChapter;
	}

	/**
	 * Sets the next {@link Chapter}
	 * 
	 * @param nextChapter
	 *            {@link Chapter}
	 */
	public void setNextChapter(Chapter nextChapter) {
		this.nextChapter = nextChapter;
	}

	/**
	 * Returns the title of this chapter.
	 * 
	 * @return title of this chapter
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the title of this chapter.
	 * 
	 * @param title
	 */
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

	/**
	 * Returns a list of {@link ChapterParticipiant} for this chapter.
	 * 
	 * @return List of {@link ChapterParticipiant}
	 */
	public List<ChapterParticipiant> getParticipants() {
		return this.participants;
	}

	public Map<String, ChapterParticipiant> getParticipantsMap() {
		return this.participantsMap;
	}

	/**
	 * Parses the chapter descriptor, generally an index.txt file, and sets some chapter properties
	 * like title, description, participants
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
				ChapterText txt = new ChapterText(this, content);
				this.usecase = txt.getAsHtml();
			}

			// if (type.equals(SECTION_LONGDESCRIPTION)) {
			// ChapterText txt = new ChapterText(this, content);
			// this.longDescription = txt.getAsHtml();
			// }

			if (type.startsWith(SECTION_EXAMPLE)) {
				String id = type.substring(SECTION_EXAMPLE.length());
				ChapterText txt = new ChapterText(this, content);
				this.examples.put(id, txt.getAsHtml());

			}
		}
	}

	/**
	 * Initializes this chapter with all detected {@link Participant}
	 */
	private void initParticipants() {
		ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();

		// ...retrieve all available resources inside that path
		final Set<String> resources = ctx.getResourcePaths(this.chapterViewIdRoot);

		// iterate over resources...
		for (final String resource : resources) {

			// handle general xml/xhtml
			if (resource.endsWith(".xml")) {
				ChapterParticipiant p = this.createParticipiantFromXhtml(resource);
				p.setFacesType(ChapterParticipiant.TYPE_FACESCFG);
				participantsMap.put(p.getFacesType(), p);
			}

			if (resource.endsWith(".xhtml")) {
				ChapterParticipiant p = this.createParticipiantFromXhtml(resource);

				// handle ycomponent view file (find interface and implementation class)
				if (YFacesTaglib.COMPONENT_RESOURCE_PATTERN.matcher(resource).matches()) {

					p.setFacesType(ChapterParticipiant.TYPE_CMPVIEW);
					participantsMap.put(p.getFacesType(), p);

					// fetch URL and register at component registry
					URL url = this.getResource(resource);
					YComponentFactory cmpFac = new YComponentFactory();
					YComponentInfo info = cmpFac.createComponentInfo(url, null);
					if (info.getImplementationClassName() != null) {
						p = this.createParticipiantFromClass(info.getImplementationClassName());
						p.setFacesType(ChapterParticipiant.TYPE_CMPIMPL);
						participantsMap.put(p.getFacesType(), p);
					}
					if (info.getSpecificationClassName() != null) {
						p = this.createParticipiantFromClass(info.getSpecificationClassName());
						p.setFacesType(ChapterParticipiant.TYPE_CMPSPEC);
						participantsMap.put(p.getFacesType(), p);
					}
				} else {
					p.setFacesType(ChapterParticipiant.TYPE_FRAMEVIEW);
					participantsMap.put(p.getFacesType(), p);

					String content = getResourceAsString(resource);
					Matcher m = ycmpBindingAttribute.matcher(content);
					while (m.find()) {
						String frame = m.group(1);
						ELContext ectx = FacesContext.getCurrentInstance().getELContext();
						Class bindClass = ectx.getELResolver().getValue(ectx, null, frame)
								.getClass();
						p = this.createParticipiantFromClass(bindClass.getName());
						if (bindClass.isAssignableFrom(YFrame.class)) {
							p.setFacesType(ChapterParticipiant.TYPE_FRAMECLASS);
						} else {
							p.setFacesType(ChapterParticipiant.TYPE_BEANCLASS);
						}
						participantsMap.put(p.getFacesType(), p);
					}
				}
			}
		}
		this.participants = new ArrayList<ChapterParticipiant>(participantsMap.values());
		Collections.sort(this.participants);
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
		p.sourceType = null;
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
		p.sourceType = null;
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
