package se.findout.tempo.client.model;

/**
 * Corresponds to the same java beans object, but compatible with GWT.
 */
public interface PropertyChangeListener {

    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *          and the property that has changed.
     */

    void propertyChange(PropertyChangeEvent evt);

}
