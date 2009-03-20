package de.hybris.yfaces.application;

public interface RequestCycle {

	public void startPageRequest(String viewid);

	public void finishPageRequest(String viewid);

}
