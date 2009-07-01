package de.hybris.yfaces.demo;

import java.io.File;
import java.net.URL;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

public class LocalPrettySourceLoader implements PrettySourceLoader {

	private String root = null;

	public LocalPrettySourceLoader() {
		// nop
	}

	public LocalPrettySourceLoader(String root) {
		this.root = root;
	}

	public URL getResourceURL(String resource) {
		URL result = null;
		try {
			result = new File(getRoot() + resource).toURL();
		} catch (Exception e) {

		}

		return result;
	}

	private String getRoot() {
		if (this.root == null) {
			ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance()
					.getExternalContext().getContext();
			this.root = ctx.getRealPath("/");
		}
		return this.root;
	}

}
