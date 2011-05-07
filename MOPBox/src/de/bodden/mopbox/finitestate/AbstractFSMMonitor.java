package de.bodden.mopbox.finitestate;

import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.ISymbol;

/**
 * Implementation of the {@link IMonitor} interface that is based in a finite-state machine.
 * Invocations of the {@link #processEvent(ISymbol)} method cause a simple state transition.
 * 
 * @param <M>
 */
public abstract class AbstractFSMMonitor<M extends AbstractFSMMonitor<M,L>,L> implements IMonitor<M,L>, Cloneable {

	private State<L> currentState;
	
	protected AbstractFSMMonitor(State<L> initialState) {
		this.currentState = initialState;
	}
	
	/**
	 * @see IMonitor#processEvent(ISymbol)
	 */
	@Override
	public boolean processEvent(ISymbol<L,?> s) {
		if(currentState!=null)
			currentState = currentState.successor(s);
		if(currentState!=null && currentState.isFinal()) {			
			return true;
		} 
		return false;
	}

	/**
	 * @see IMonitor#copy()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public M copy() {
		try {
			M clone = (M) clone();
			if(currentState!=null)
				clone.currentState = currentState.copy();
			return clone;
		} catch (CloneNotSupportedException e) {
			//cannot be thrown
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String toString() {
		if(currentState==null) return "<no state>";
		return currentState.toString();
	}

}
