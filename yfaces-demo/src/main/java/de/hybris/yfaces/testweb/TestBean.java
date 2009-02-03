package de.hybris.yfaces.testweb;

import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

public class TestBean {
	
	private static final Logger log = Logger.getLogger(TestBean.class);
	
	public TestBean() {
		
	}
	
	public void actionListener(ActionEvent e) {
		log.info("Action");
	}

}
