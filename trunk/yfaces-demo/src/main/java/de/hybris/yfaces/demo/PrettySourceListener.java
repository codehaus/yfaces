package de.hybris.yfaces.demo;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletResponse;

import de.str.prettysource.format.HtmlFormatFactory;
import de.str.prettysource.output.HtmlOutputFormat;

/**
 * {@link PhaseListener} which reads various non html based source files and formats it on-the-fly
 * into pretty html markup. Currently supported are sources of type java and xhtml.
 * 
 * @author Denny.Strietzbaum
 */
public class PrettySourceListener implements PhaseListener {

	private static final String P_SOURCE = "source";
	private static final String P_TYPE = "type";

	private static final String TYPE_JAVA = "java";
	private static final String TYPE_XML = "xml";

	private HtmlOutputFormat javaSource = null;
	private HtmlOutputFormat xhtmlSource = null;

	private PrettySourceLoader resourceLoader = null;

	public PrettySourceListener() {
		this.javaSource = HtmlFormatFactory.javaToHtml();
		this.xhtmlSource = Xhtml2HtmlFormatFactory.getXhtmlToHtmlFormat();
		this.resourceLoader = new LocalPrettySourceLoader();
	}

	public void afterPhase(PhaseEvent arg0) {
		Map<?, ?> map = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();

		boolean prettyResponse = map.get(P_SOURCE) != null;
		if (prettyResponse) {
			this.prettyResponse();
		}
	}

	public void beforePhase(PhaseEvent arg0) {

	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private void prettyResponse() {
		Map<?, ?> map = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();

		String resource = (String) map.get(P_SOURCE);
		String type = (String) map.get(P_TYPE);
		if (type == null || type.trim().length() == 0) {
			if (resource.startsWith("de.hybris.yfaces")) {
				type = TYPE_JAVA;
			} else {
				type = resource.substring(resource.lastIndexOf(".") + 1);
			}
		}

		HtmlOutputFormat sourceFormat = null;
		if (TYPE_JAVA.equals(type)) {
			resource = "/../java/" + resource.replaceAll("\\.", "/") + ".java";
			sourceFormat = this.javaSource;
		}

		if ("xhtml".equals(type) || TYPE_XML.equals(type)) {
			sourceFormat = this.xhtmlSource;
		}
		Writer writer = getResponseWriter();
		Reader reader = getSourceReader(resource);
		sourceFormat.format(reader, writer);

		FacesContext.getCurrentInstance().responseComplete();

	}

	/**
	 * Creates a Writer used for writing into the response.
	 * 
	 * @return {@link Writer}
	 */
	private Writer getResponseWriter() {
		Writer result = null;
		try {
			result = ((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
					.getResponse()).getWriter();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Creates a Reader which points to the requested Resource.
	 * 
	 * @param resource
	 *            resource to read from
	 * @return Reader
	 */
	private Reader getSourceReader(String resource) {
		Reader result = null;
		try {
			URL sourceUrl = this.resourceLoader.getResourceURL(resource);
			result = new InputStreamReader(sourceUrl.openStream());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
