package de.bodden.mopbox.generic;

import java.util.Set;

import de.bodden.mopbox.generic.indexing.StrategyCPlus;

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
	 */
	void processEvent(IEvent<L,K,V> e);
	
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
	 * Computes creation symbols for this monitor. Required by Algorithm {@link StrategyCPlus}.
	 * TODO what would be a safe default approximation here? 
	 */
	Set<ISymbol<L>> computeCreationSymbols();
	
	/**
	 * Returns the alphabet which monitors of this template are evaluated over.
	 */
	IAlphabet<L> getAlphabet();	
}
