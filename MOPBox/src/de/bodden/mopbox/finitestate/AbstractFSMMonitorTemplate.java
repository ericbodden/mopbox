package de.bodden.mopbox.finitestate;

import java.util.HashSet;
import java.util.Set;

import de.bodden.mopbox.generic.AbstractMonitorTemplate;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;

/**
 * This is an abstract super class for {@link IMonitorTemplate}s of finite-state runtime monitors.
 * It provides methods to create states, and to compute statistics over the finite-state machine.
 *
 * @param <L,K> The type of labels used at transitions.
 * @param <K> The type of keys used in {@link IVariableBinding}s.
 * @param <V> The type of values used in {@link IVariableBinding}s.
 */
public abstract class AbstractFSMMonitorTemplate<L,K,V> extends AbstractMonitorTemplate<DefaultFSMMonitor<L>,L,K,V> {

	private int nextStateNum = 0;
	private State<L> initialState;	

	protected State<L> makeState(boolean isFinal) {
		return new State<L>(getAlphabet(),isFinal,Integer.toString(nextStateNum++));
	}
	
	/**
	 * In a FSM, a creation symbol is a symbol that from the initial state
	 * either (a) leads to a non-initial state or (b) leads to no state at all,
	 * i.e., prevents a match.  
	 */
	public Set<ISymbol<L,K>> computeCreationSymbols() {
		Set<ISymbol<L,K>> creationSyms = new HashSet<ISymbol<L,K>>();
		for(ISymbol<L,K> s: getAlphabet()) {
			State<L> target = initialState().targetFor(s);
			if(target==null || !target.equals(initialState())){
				creationSyms.add(s);
			}
		}
		return creationSyms;
	}
	
	/**
	 * @see de.bodden.mopbox.generic.IMonitorTemplate#createMonitorPrototype()
	 */
	@Override
	public DefaultFSMMonitor<L> createMonitorPrototype() {
		return new DefaultFSMMonitor<L>(initialState());
	}

	/**
	 * Creates and returns this template's initial state.
	 * The state is wired to all other states with the given
	 * transitions.
	 */
	protected State<L> initialState() {
		if(initialState==null) {
			nextStateNum= 0;
			initialState = setupStatesAndTransitions();
		}
		return initialState;
	}
	
	/**
	 * Subclasses implement this method to create all states and
	 * wire them with transitions. The method then returns the
	 * unique initial state. For example:
	 * <code>
	 * 	protected State<String> doCreateAndWireInitialState() {
			State<String> initial = makeState(false);
			State<String> closed = makeState(false);
			State<String> error = makeState(true);
			
			initial.addTransition(getSymbolByLabel("close"), closed);
			closed.addTransition(getSymbolByLabel("close"),closed);
			closed.addTransition(getSymbolByLabel("write"), error);
			return initial;
		}
	 * </code>
	 * 
	 */
	protected abstract State<L> setupStatesAndTransitions();
	
}
