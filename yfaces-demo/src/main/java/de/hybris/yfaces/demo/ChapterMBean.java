package de.hybris.yfaces.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

/**
 * @author Denny.Strietzbaum
 */
public class ChapterMBean {

	private static final Logger log = Logger.getLogger(ChapterMBean.class);

	private static Pattern chpIdPattern = Pattern.compile("(.*)/chp([0-9]+)(/.*)\\.xhtml");

	private String nextView = null;
	private String prevView = null;

	private Chapter currentChapter = null;

	public ChapterMBean() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		boolean resetChapters = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap().get("clear") != null;
		Map<String, Chapter> chapters = this.getAllChapters(resetChapters);
		this.currentChapter = chapters.get(viewId);
	}

	public String getNextPage() {
		return this.nextView;
	}

	public String getPreviousPage() {
		return this.prevView;
	}

	public Chapter getCurrentChapter() {
		return this.currentChapter;
	}

	// not threadsafe, but thats ok
	private Map<String, Chapter> getAllChapters(boolean reset) {
		FacesContext fc = FacesContext.getCurrentInstance();

		Map map = fc.getExternalContext().getApplicationMap();
		Map<String, Chapter> result = (Map) map.get("CHAPTERS");
		if (result == null || reset) {
			result = new HashMap<String, Chapter>();
			map.put("CHAPTERS", result);

			// parse the chapter descriptor file
			try {
				URL url = fc.getExternalContext().getResource("/demo/chapters.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

				Chapter prevChapter = null;
				while (reader.ready()) {
					String line = reader.readLine();
					String[] values = line.split(":");
					Chapter chapter = new Chapter("/demo/" + values[1]);
					chapter.setTitle(values[0] + " - " + chapter.getTitle());
					result.put(chapter.getViewId(), chapter);

					if (prevChapter != null) {
						prevChapter.setNextChapter(chapter);
						chapter.setPrevChapter(prevChapter);
					}
					prevChapter = chapter;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
