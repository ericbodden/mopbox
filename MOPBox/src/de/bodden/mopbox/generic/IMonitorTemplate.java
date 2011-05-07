package de.bodden.mopbox.generic;


/**
 * An {@link IMonitorTemplate} represents a template for runtime monitors, i.e.,
 * usually for finite-state machines.
 * 
 * @param <M> The type of monitors this template will create.
 * @param <L> The type of labels which the transitions of the monitors are labeled with.
 *            (often {@link String})
 * @param <K> The type of keys used in variable bindings. (often {@link String})            
 * @param <V> The type of values used in variable bindings. (often {@link Object}, but may be more precise)            
 */
public interface IMonitorTemplate<M extends IMonitor<M,L>,L,K,V> {
	
	/**
	 * Update the internal state of all related monitors by sending the parameter event
	 * to the respective monitors.
	 * 
	 * @param label The label of the event.
	 * @param binding The variable binding at this event.
	 */
	void processEvent(L label, IVariableBinding<K,V> binding);
	
	/**
	 * This is a call-back method. Implementers of this interface can implement this method
	 * to be called back whenever a match is detected. The Method receives the matching binding
	 * as an argument. If multiple matches exist at the same event, this method will be called
	 * multiple times, one for each match, in an undefined order.
	 * @param binding The binding for which a match was found.
	 */
	void matchCompleted(IVariableBinding<K,V> binding);

	/**
	 * Returns a concrete instance of a runtime monitor for this template.
	 */
	M createMonitorPrototype();
	
	/**
	 * Returns the alphabet which monitors of this template are evaluated over.
	 */
	IAlphabet<L,K> getAlphabet();	
}
