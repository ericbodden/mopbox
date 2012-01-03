package de.bodden.mopbox.finitestate;

import java.util.LinkedHashSet;
import java.util.Set;

import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;


/**
 * A FSM monitor template that exposes the set of states through a convenience accessor method.  
 *
 * @param <L> The type of labels used at transitions.
 * @param <K> The type of keys used in {@link IVariableBinding}s.
 * @param <V> The type of values used in {@link IVariableBinding}s.
 */
public abstract class OpenFSMMonitorTemplate<L, K, V> extends AbstractFSMMonitorTemplate<L, K, V> {

	protected Set<State<L>> stateCache;
	
	public State<L> getInitialState() {
		return super.initialState();
	}
	
	public Set<State<L>> getStates() {
		if(stateCache==null) {
			stateCache = new LinkedHashSet<State<L>>();
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(State<L> s: getStates()) {
			sb.append("state ");
			sb.append(s);
			sb.append(":\n");
			for(ISymbol<L, K> sym: getAlphabet()) {
				State<L> succ = s.successor(sym);
				if(succ!=null) {
					sb.append("- ");
					sb.append(sym);
					sb.append("-> ");
					sb.append("state ");
					sb.append(succ);
					sb.append("\n");
				}
			}
			sb.append("\n");
		}		
		return sb.toString();
	}

	
}
