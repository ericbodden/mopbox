package de.bodden.mopbox.finitestate;

import java.util.HashSet;
import java.util.Set;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;


public abstract class OpenFSMMonitorTemplate<L, K, V> extends AbstractFSMMonitorTemplate<L, K, V> {

	protected Set<State<L>> stateCache;
	
	public State<L> getInitialState() {
		return super.initialState();
	}
	
	public Set<State<L>> getStates() {
		if(stateCache==null) {
			stateCache = new HashSet<State<L>>();
			addStateAndSuccsToCache(getInitialState());			
		}
		return stateCache;
	}

	private void addStateAndSuccsToCache(State<L> s) {
		IAlphabet<L, K> alphabet = getAlphabet();
		if(stateCache.add(s)) {
			for(ISymbol<L, K> sym: alphabet) {
				State<L> succ = s.successor(sym);
				if(succ!=null)
					addStateAndSuccsToCache(succ);
			}
		}
	}
	
	
	
}
