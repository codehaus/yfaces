package de.hybris.yfaces.demo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class ChapterMBean {

	private static final Logger log = Logger.getLogger(ChapterMBean.class);

	private static Pattern chpIdPattern = Pattern.compile("(.*)/chp([0-9]+)(/.*)\\.xhtml");
	private static Pattern chpSectionPattern = Pattern.compile("\\{(.*?)\\}(.*?)(?=\\{|\\z)",
			Pattern.DOTALL);

	public static Pattern YCOMPONENT = Pattern.compile("</?yf:(.*?)>", Pattern.DOTALL);

	private String indexFile = null;
	private String nextView = null;
	private String prevView = null;

	private String title = null;
	private String usecase = null;
	private String longDescription = null;
	private Map<String, String> examples = new HashMap<String, String>();

	public ChapterMBean() {
		this.prepareChapterNavigation();
		this.prepareChapterSections();

	}

	public String getNextPage() {
		return this.nextView;
	}

	public String getPreviousPage() {
		return this.prevView;
	}

	public String getTitle() {
		return this.title;
	}

	public String getUseCase() {
		return this.usecase;
	}

	public String getLongDescription() {
		return this.longDescription;
	}

	public Map<String, String> getExamples() {
		return this.examples;
	}

	private void prepareChapterSections() {
		InputStream in = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream(indexFile);
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

			if (type.equals("title")) {
				this.title = content;
			}

			if (type.equals("usecase")) {
				this.usecase = content;
			}

			if (type.equals("long")) {
				this.longDescription = this.escapeYComponentTags(content);
			}

			if (type.startsWith("example-")) {
				String example = type.substring("example-".length());
				this.examples.put(example, escapeYComponentTags(content));
			}

		}
		log.debug(in);
	}

	private void prepareChapterNavigation() {
		FacesContext fc = FacesContext.getCurrentInstance();
		String id = fc.getViewRoot().getViewId();
		Matcher m = chpIdPattern.matcher(id);
		if (m.matches()) {
			int chapterId = Integer.parseInt(m.group(2));
			String ctxPath = fc.getExternalContext().getRequestContextPath();
			nextView = ctxPath + m.group(1) + "/chp" + (chapterId + 1) + m.group(3) + ".jsf";

			if (chapterId > 1) {
				prevView = ctxPath + m.group(1) + "/chp" + (chapterId - 1) + m.group(3) + ".jsf";
			}

			this.indexFile = m.group(1) + "/chp" + (chapterId) + "/index.txt";
		}
	}

	public String escapeYComponentTags(String in) {
		Matcher m = YCOMPONENT.matcher(in);
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

	//	public static void main (String argc[]) {
	//		//ChapterMBean bean = new ChapterMBean();
	//		
	//		System.out.println(ChapterMBean.encodeYComponentOccurrences("Das is nur nen <yf:component attre='213'/> test <br/> Und noch was <yf:component> value </yf:component>"));
	//	}

}