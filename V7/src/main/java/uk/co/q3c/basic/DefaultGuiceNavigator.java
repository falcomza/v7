package uk.co.q3c.basic;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import uk.co.q3c.basic.view.ErrorView;
import uk.co.q3c.basic.view.GuiceView;
import uk.co.q3c.basic.view.GuiceViewDisplay;
import uk.co.q3c.basic.view.GuiceViewProvider;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

public class DefaultGuiceNavigator implements GuiceNavigator {

	private final GuiceNavigationStateManager stateManager;
	private final GuiceViewDisplay display;
	private GuiceView currentView = null;
	private final List<GuiceViewChangeListener> listeners = new LinkedList<GuiceViewChangeListener>();
	private final GuiceViewProvider viewProvider;
	private final Provider<ErrorView> errorViewPro;
	private final URIDecoder uriDecoder;

	@Inject
	protected DefaultGuiceNavigator(GuiceNavigationStateManager stateManager, GuiceViewDisplay viewDisplay,
			Provider<ErrorView> errorViewPro, GuiceViewProvider viewProvider, URIDecoder uriDecoder) {
		super();
		this.stateManager = stateManager;
		this.display = viewDisplay;
		this.errorViewPro = errorViewPro;
		this.viewProvider = viewProvider;
		this.uriDecoder = uriDecoder;
	}

	/**
	 * Navigates to a view and initialize the view with given parameters.
	 * <p>
	 * The view string consists of a view name optionally followed by a slash and a parameters part that is passed as-is
	 * to the view. ViewProviders are used to find and create the correct type of view.
	 * <p>
	 * If the view being deactivated indicates it wants a confirmation for the navigation operation, the user is asked
	 * for the confirmation.
	 * <p>
	 * Registered {@link ViewChangeListener}s are called upon successful view change.
	 * 
	 * @param navigationState
	 *            view name and parameters
	 * 
	 * @throws IllegalArgumentException
	 *             if <code>navigationState</code> does not map to a known view and no error view is registered
	 */
	@Override
	public void navigateTo(String navigationState) {

		GuiceView view = viewProvider.getView(navigationState);
		String viewName = viewProvider.getViewName(navigationState);
		if (view == null) {
			view = errorViewPro.get();
		}
		navigateTo(view, viewName, navigationState);
	}

	/**
	 * Internal method activating a view, setting its parameters and calling listeners.
	 * <p>
	 * This method also verifies that the user is allowed to perform the navigation operation.
	 * 
	 * @param view
	 *            view to activate
	 * @param viewName
	 *            (optional) name of the view or null not to change the navigation state
	 * @param parameters
	 *            parameters passed in the navigation state to the view
	 */
	protected void navigateTo(GuiceView view, String viewName, String navigationState) {
		GuiceViewChangeEvent event = new GuiceViewChangeEvent(this, currentView, view, viewName, navigationState);
		if (!fireBeforeViewChange(event)) {
			return;
		}

		if (display != null) {
			display.showView(view);
		}

		view.enter(event);
		currentView = view;

		fireAfterViewChange(event);
	}

	/**
	 * Fires an event before an imminent view change.
	 * <p>
	 * Listeners are called in registration order. If any listener returns <code>false</code>, the rest of the listeners
	 * are not called and the view change is blocked.
	 * <p>
	 * The view change listeners may also e.g. open a warning or question dialog and save the parameters to re-initiate
	 * the navigation operation upon user action.
	 * 
	 * @param event
	 *            view change event (not null, view change not yet performed)
	 * @return true if the view change should be allowed, false to silently block the navigation operation
	 */
	protected boolean fireBeforeViewChange(GuiceViewChangeEvent event) {
		for (GuiceViewChangeListener l : listeners) {
			if (!l.beforeViewChange(event)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the current navigation state reported by this Navigator's {@link NavigationStateManager}.
	 * 
	 * @return The navigation state.
	 */
	@Override
	public String getState() {
		return getStateManager().getState();
	}

	/**
	 * Fires an event after the current view has changed.
	 * <p>
	 * Listeners are called in registration order.
	 * 
	 * @param event
	 *            view change event (not null)
	 */
	protected void fireAfterViewChange(GuiceViewChangeEvent event) {
		for (GuiceViewChangeListener l : listeners) {
			l.afterViewChange(event);
		}
	}

	/**
	 * Listen to changes of the active view.
	 * <p>
	 * Registered listeners are invoked in registration order before (
	 * {@link ViewChangeListener#beforeViewChange(ViewChangeEvent) beforeViewChange()}) and after (
	 * {@link ViewChangeListener#afterViewChange(ViewChangeEvent) afterViewChange()}) a view change occurs.
	 * 
	 * @param listener
	 *            Listener to invoke during a view change.
	 */
	public void addViewChangeListener(GuiceViewChangeListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a view change listener.
	 * 
	 * @param listener
	 *            Listener to remove.
	 */
	public void removeViewChangeListener(GuiceViewChangeListener listener) {
		listeners.remove(listener);
	}

	public GuiceNavigationStateManager getStateManager() {
		return stateManager;
	}
}
