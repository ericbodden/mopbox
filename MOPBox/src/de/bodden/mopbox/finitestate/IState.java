package de.bodden.mopbox.finitestate;

import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;

/**
 * This interface represents a state in a finite-state machine.
 * A state can be final or not, and it can have outgoing transitions
 * to other states. 
 *
 * @param <S> The concrete state type.
 * @param <L> The type of labels used for transitions.
 */
public interface IState<S extends IState<S,L>,L> extends Cloneable {

	/**
	 * Adds an outgoing transition to this state. Transitions must be deterministic,
	 * i.e., there may be no two transitions with the same {@link ISymbol}.
	 * @param sym The {@link ISymbol} which the transition is labeled with.
	 * @param succ The transition's target state.
	 */
	public void addTransition(ISymbol<L,?> sym, S succ);

	/**
	 * Retrieves the successor state for the given symbol. 
	 */
	public IState<S,L> successor(ISymbol<L,?> sym);
	
	/**
	 * Returns <code>true</code> if this state is final. During evaluation,
	 * when an {@link IMonitor} reaches a final state, then the {@link IMonitorTemplate}
	 * of this monitor will call its {@link IMonitorTemplate#matchCompleted(de.bodden.mopbox.generic.IVariableBinding)}
	 * method with the binding that this monitor is associated with. 	 
	 */	
	public boolean isFinal();
	
	/**
	 * Returns a deep copy of this monitor.
	 */
	public S copy();

}