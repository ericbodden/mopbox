package de.bodden.rvlib.finitestate;

import de.bodden.rvlib.generic.IMonitor;
import de.bodden.rvlib.generic.ISymbol;

public abstract class AbstractFSMMonitor<M extends AbstractFSMMonitor<M,L>,L> implements IMonitor<M,L>, Cloneable {

	private State<L> currentState;
	
	protected AbstractFSMMonitor(State<L> initialState) {
		this.currentState = initialState;
	}
	
	@Override
	public boolean processEvent(ISymbol<L> s) {
		if(currentState!=null)
			currentState = currentState.successor(s);
		if(currentState!=null && currentState.isFinal()) {			
			return true;
		} 
		return false;
	}

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
