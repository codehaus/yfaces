package org.codehaus.yfaces.selenium.component;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;
import org.codehaus.yfaces.component.AbstractYModel;


public class SeleniumComponent extends AbstractYModel {

	private static final Logger log = Logger.getLogger(SeleniumComponent.class);

	private String textField = null;

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public void setIntoSessionAction() {
		String[] param = this.textField.split("=|$");
		Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		String key = param[0];
		String value = param.length > 1 ? param[1] : null;

		log.info("Put into session:" + key + "->" + value);
		map.put(key, value);
	}
}
