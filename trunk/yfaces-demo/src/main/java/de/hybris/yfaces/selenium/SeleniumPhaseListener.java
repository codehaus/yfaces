package de.hybris.yfaces.selenium;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.log4j.Logger;

public class SeleniumPhaseListener implements PhaseListener {

	private static final Logger log = Logger.getLogger(SeleniumPhaseListener.class);

	public void afterPhase(PhaseEvent arg0) {
		Map map = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		String sessionParam = (String) map.get("session");
		if (sessionParam != null) {
			this.setIntoSession(sessionParam);
		}
	}

	public void beforePhase(PhaseEvent arg0) {
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

	private void setIntoSession(String textField) {
		String[] param = textField.split("=|$");
		Map map = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

		String key = param[0];
		String value = param.length > 1 ? param[1] : null;

		log.info("Put into session:" + key + "->" + value);
		map.put(key, value);

	}

}
