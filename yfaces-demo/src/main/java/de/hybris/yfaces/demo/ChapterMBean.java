package de.hybris.yfaces.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * @author Denny.Strietzbaum
 */
public class ChapterMBean {

	private static final Logger log = Logger.getLogger(ChapterMBean.class);

	private static final String CHAPTERS_KEY = "CHAPTERS";
	private static final String CHAPTERS_MODIFIED_KEY = "CHAPTERS2";

	private static final String CHAPTERS_ROOT = "/demo/";
	private static final String CHAPTERS_DESCRIPTOR = CHAPTERS_ROOT + "chapters.txt";

	private Chapter currentChapter = null;

	public ChapterMBean() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		Map<String, Chapter> chapters = this.getAllChaptersAsMap();
		this.currentChapter = chapters.get(viewId);
	}

	public Chapter getCurrentChapter() {
		return this.currentChapter;
	}

	public List<Chapter> getAllChapters() {
		return new ArrayList(getAllChaptersAsMap().values());
	}

	/**
	 * Returns all available chapters as a Mapping chapterid(viewid) -> {@link Chapter} Does all
	 * necessary initialization and parsings when chapter descriptor wasn't parsed yet or has
	 * changed.<br/>
	 * Initialization of Chapter map is not thread-safe (is just ignored).
	 * 
	 * @return Map of {@link Chapter}
	 */
	private Map<String, Chapter> getAllChaptersAsMap() {
		FacesContext fc = FacesContext.getCurrentInstance();
		Map appMap = fc.getExternalContext().getApplicationMap();

		// try to fetch already initialized chapters
		Map<String, Chapter> result = (Map) appMap.get(CHAPTERS_KEY);
		URL url = null;
		try {
			// check whether chapter descriptor has changed
			url = fc.getExternalContext().getResource(CHAPTERS_DESCRIPTOR);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}

		// true when chapter descriptor has changed or wasn't parsed yet
		boolean isChanged = this.isChanged(url);
		if (isChanged) {
			log.info("Reading " + url + "(" + (isChanged ? "has changed" : "") + ")");

			// create the result map
			result = new LinkedHashMap<String, Chapter>();
			appMap.put(CHAPTERS_KEY, result);

			// parse the chapter descriptor file
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

				// create and add a chapter instance for each input line
				Chapter prevChapter = null;
				while (reader.ready()) {
					String line = null;
					try {
						line = reader.readLine().trim();
						if (!line.startsWith("#")) {
							String[] values = line.split(":");
							Chapter chapter = new Chapter(CHAPTERS_ROOT + values[1]);
							chapter.setTitle(values[0] + " - " + chapter.getTitle());
							result.put(chapter.getViewId(), chapter);

							if (prevChapter != null) {
								prevChapter.setNextChapter(chapter);
								chapter.setPrevChapter(prevChapter);
							}
							prevChapter = chapter;
						}
					} catch (IllegalArgumentException e) {
						log.error("Skipping:" + line);
					}
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	/**
	 * Checks whether chapter descriptor has changed or wasn't evaluated yet
	 * 
	 * @param url
	 *            chapter descriptor
	 * @return true when chapter descriptor has changed or wasn't evaluated yet
	 */
	private boolean isChanged(URL url) {

		Long actualVersion = null;

		try {
			actualVersion = Long.valueOf(url.openConnection().getLastModified());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Map map = FacesContext.getCurrentInstance().getExternalContext().getApplicationMap();
		Long chapterVersion = (Long) map.get(CHAPTERS_MODIFIED_KEY);

		boolean changed = !actualVersion.equals(chapterVersion);
		if (changed) {
			map.put(CHAPTERS_MODIFIED_KEY, actualVersion);
		}
		return changed;
	}

}
