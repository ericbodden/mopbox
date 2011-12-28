package de.bodden.mopbox.finitestate.sync;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.indexing.simple.StrategyB;

/**
 * Idea: can use this technique to buffer event and then process them in bursts.
 */
public abstract class AbstractSyncingFSMMonitorTemplate<L, K, V, G extends AbstractSyncingFSMMonitorTemplate<L,K,V,G>.ObservationGapInfo>
	extends OpenFSMMonitorTemplate<G, K, V>{
	
	protected final OpenFSMMonitorTemplate<L, K, V> delegate;
	
	protected final Map<Set<State<L>>,State<G>> stateSetToCompoundState = new HashMap<Set<State<L>>, State<G>>();

	public AbstractSyncingFSMMonitorTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		this.delegate = delegate;
		initialize();
	}

	@Override
	protected State<G> setupStatesAndTransitions() {
		IAlphabet<L, K> alphabet = delegate.getAlphabet();
		
		Set<Set<State<L>>> worklist = new HashSet<Set<State<L>>>();
		worklist.add(Collections.singleton(delegate.getInitialState()));
		
		Set<State<G>> statesVisited = new HashSet<State<G>>();
		
		while(!worklist.isEmpty()){
			//pop some element
			Iterator<Set<State<L>>> iter = worklist.iterator();
			Set<State<L>> currentStates = iter.next();
			iter.remove();
			
			State<G> compoundState = stateFor(currentStates);
			statesVisited.add(compoundState);

			Set<State<L>> nextFrontier = new HashSet<State<L>>(currentStates);
			Set<State<L>> frontier;
			
			int delta = 0;
			do {
				frontier = nextFrontier;
				if(!frontier.isEmpty()) {
					for (ISymbol<L,K> sym : alphabet) {
						Set<State<L>> symSuccs = new HashSet<State<L>>();
						for(State<L> curr : frontier) {
							State<L> succ = curr.successor(sym);
							if(succ!=null)
								symSuccs.add(succ);
						}
						if(!symSuccs.isEmpty()) {
							State<G> newCompoundState = stateFor(symSuccs);
							compoundState.addTransition(getSymbolByLabel(makeTransition(delta,sym)), newCompoundState);
							if(!statesVisited.contains(newCompoundState)) {
								worklist.add(symSuccs);
							}
						}
					}
				}
				
				nextFrontier = nextFrontier(frontier);
				
				delta++;
			//in the following conditional, the 2nd branch is to make sure that we create
			//transitions for all symbols in the alphabet; alternatively we could do a
			//dynamic transition matching
			} while(!frontier.equals(nextFrontier) || delta<maxNumber());
		}
				
		return stateFor(Collections.singleton(delegate.getInitialState()));
	}

	protected abstract Set<State<L>> nextFrontier(Set<State<L>> frontier);
	
	protected abstract G makeTransition(int delta, ISymbol<L, K> sym);

	private State<G> stateFor(Set<State<L>> set) {
		State<G> compoundState = stateSetToCompoundState.get(set);
		if(compoundState==null) {
			boolean isFinal = true;
			for (State<L> state : set) {
				if(!state.isFinal() && !state.equals(delegate.getInitialState())) {
					isFinal = false;
					break;
				}
			}			
			compoundState = makeState(isFinal);
			System.err.println(set + " --> " + compoundState);
			stateSetToCompoundState.put(set, compoundState);
		}
		return compoundState;
	}

	@Override
	protected void fillVariables(Set<K> variables) {
		variables.addAll(delegate.getVariables());
	}

	@Override
	protected IIndexingStrategy<G, K, V> createIndexingStrategy() {
		//TODO can we somehow choose the strategy based on the one used for the delegate?
		return new StrategyB<DefaultFSMMonitor<G>, G, K, V>(this); 
	}

	@Override
	protected void fillAlphabet(IAlphabet<G, K> alphabet) {
		Set<ISymbol<L, K>> asSet = delegate.getAlphabet().asSet();
		for(int delta=0; delta <= maxNumber(); delta++) {
			for (ISymbol<L, K> sym : asSet) {
				alphabet.makeNewSymbol(makeTransition(delta, sym), sym.getVariables());
			}
		}
	}
	
	public void processEvent(G label, IVariableBinding<K,V> binding) {
		//TODO must have a catch-all mechanism if the number is larger than maxNumber() 
		super.processEvent(label, binding);
	}
	
	private int maxNumber() {
		return delegate.getStates().size();
	}

	public abstract class ObservationGapInfo {
		public abstract ISymbol<L,K> getCurrentSymbol();
		
		public abstract int hashCode();

		public abstract boolean equals(Object obj);		
	}
	
}