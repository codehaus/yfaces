package de.hybris.yfaces.demo;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import de.str.prettysource.html.JavaHtmlFormat;

/**
 * {@link PhaseListener} which reads various non html based source files and formats it on-the-fly
 * into pretty html markup. Currently supported are sources of type java and xhtml.
 * 
 * @author Denny.Strietzbaum
 */
public class PrettySourceListener implements PhaseListener {

	private static final String P_SOURCE = "source";
	private static final String P_TYPE = "type";

	private JavaHtmlFormat javaSource = null;
	private Xhtml2HtmlFormat xhtmlSource = null;

	public PrettySourceListener() {
		this.javaSource = new JavaHtmlFormat();
		this.xhtmlSource = new Xhtml2HtmlFormat();
	}

	public void afterPhase(PhaseEvent arg0) {
		Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String source = (String) map.get(P_SOURCE);
		if (source != null) {
			String type = (String) map.get(P_TYPE);
			if (type == null || type.trim().length() == 0) {
				type = detectType(source);
			}

			if ("java".equals(type)) {
				this.writeAsJavaSource(source);
			}

			if ("xhtml".equals(type) || "xml".equals(type)) {
				this.writeAsXmlSource(source);
			}
			FacesContext.getCurrentInstance().responseComplete();
		}
	}

	public void beforePhase(PhaseEvent arg0) {

	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private String detectType(String source) {
		String result = null;
		if (source.startsWith("de.hybris.yfaces")) {
			result = "java";
		} else {
			result = source.substring(source.lastIndexOf(".") + 1);
		}
		return result;
	}

	private void writeAsXmlSource(String source) {
		Reader reader = getReader(source);
		PrintWriter writer = new PrintWriter(getWriter());
		this.xhtmlSource.format(reader, writer);
	}

	private void writeAsJavaSource(String source) {

		// get URL for unformatted java source code
		String sourcePath = "/src/main/java/" + source.replaceAll("\\.", "/") + ".java";
		Reader reader = getReader(sourcePath);
		Writer writer = getWriter();
		this.javaSource.format(reader, writer);
	}

	private Writer getWriter() {
		Writer result = null;
		try {
			result = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse()).getWriter();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Reader getReader(String resource) {
		Reader result = null;
		try {
			URL sourceUrl = FacesContext.getCurrentInstance().getExternalContext().getResource(
					resource);
			result = new InputStreamReader(sourceUrl.openStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}