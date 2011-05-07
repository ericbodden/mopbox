package de.bodden.mopbox.generic;


/**
 * An {@link IMonitor} is a concrete monitor instance, representing the
 * internal state of an {@link IMonitorTemplate} for one single concrete
 * variable binding. In case of a finite-state based monitor, an {@link IMonitor}
 * may hold a single integer state variable, but in general, more complex 
 * implementations may be provided.
 *
 * @param <M> The concrete monitor type.
 * @param <L> The type of labels that this monitor uses at transitions.
 */
public interface IMonitor<M extends IMonitor<M,L>,L> {
	
	/**
	 * Updates the monitors internal state by reading an event labeled with the given symbol.
	 * This method is not usually called by clients but rather by an {@link IIndexingStrategy}.
	 * Users instead should call {@link IMonitorTemplate#processEvent(IEvent)}.
	 * 
	 * @return <code>true</code> if a final state was reached
	 */
	boolean processEvent(ISymbol<L,?> iSymbol);
	
	/**
	 * Creates a deep copy of this monitor.
	 */	
	M copy();
	
}
