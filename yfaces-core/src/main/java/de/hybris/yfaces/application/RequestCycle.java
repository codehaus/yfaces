package de.hybris.yfaces.application;

public interface RequestCycle {

	public void startPageRequest(String viewid);

	public void finishPageRequest(String viewid);

	public boolean isPostback();

	public boolean isFlashback();

	public void setFlashbackEnabled();

	public boolean isFlashbackEnabled();

}
