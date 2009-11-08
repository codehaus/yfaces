package de.hybris.yfaces.component.html;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;

import de.hybris.yfaces.YFacesException;

/**
 * {@link HtmlYComponent} uses this {@link FacesEvent} just as a kind of marker. It is used to
 * refresh the 'var' value binding within INVOKE_APPLICATION Phase.
 * <p>
 * How it works:<br/>
 * When a {@link FacesEvent} is thrown, it gets queued. When the {@link FacesEvent} gets processed,
 * it gets broadcasted. So when a child {@link UIComponent} of {@link HtmlYComponent} throws a event
 * {@link HtmlYComponent} first gets notified with {@link HtmlYComponent#queueEvent(FacesEvent)}.
 * This method adds an additional {@link HtmlYComponentFacesEvent} before that source event. During
 * INVOKE_APPLICATION Phase {@link HtmlYComponent#broadcast(FacesEvent)} is invoked. Whenever the
 * broadcasted event is of type {@link HtmlYComponentFacesEvent} some render time properties
 * (var-value) are refreshed.
 * 
 * @see HtmlYComponent#queueEvent(FacesEvent)
 * @see HtmlYComponent#broadcast(FacesEvent)
 * 
 * @author Denny Strietzbaum
 */

public class HtmlYComponentFacesEvent extends FacesEvent {

	private static final long serialVersionUID = 1L;

	public HtmlYComponentFacesEvent(final HtmlYComponent source, final PhaseId phaseId) {
		super(source);
		super.setPhaseId(phaseId);
	}

	@Override
	public boolean isAppropriateListener(final FacesListener faceslistener) {
		throw new YFacesException("", new UnsupportedOperationException());
	}

	@Override
	public void processListener(final FacesListener faceslistener) {
		throw new YFacesException("", new UnsupportedOperationException());
	}
}
