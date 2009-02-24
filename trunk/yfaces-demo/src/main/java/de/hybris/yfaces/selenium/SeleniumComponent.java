package de.hybris.yfaces.selenium;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import de.hybris.yfaces.component.AbstractYComponent;

public class SeleniumComponent extends AbstractYComponent {

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
