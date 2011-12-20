package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;

/**
 * Idea: can use this technique to buffer event and then process them in bursts.
 */
public abstract class NumberSyncFSMMonitorTemplate<L, K, V> extends OpenFSMMonitorTemplate<NumberSyncFSMMonitorTemplate<L,K,V>.NumberAndSymbol, K, V>{
	
	protected final OpenFSMMonitorTemplate<L, K, V> delegate;
	
	protected final Map<Set<State<L>>,State<NumberAndSymbol>> stateSetToCompoundState = new HashMap<Set<State<L>>, State<NumberAndSymbol>>();

	public NumberSyncFSMMonitorTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		this.delegate = delegate;
		initialize();
	}

	@Override
	protected State<NumberAndSymbol> setupStatesAndTransitions() {
		IAlphabet<L, K> alphabet = delegate.getAlphabet();
		
		Set<Set<State<L>>> worklist = new HashSet<Set<State<L>>>();
		worklist.add(Collections.singleton(delegate.getInitialState()));
		
		Set<State<NumberAndSymbol>> statesVisited = new HashSet<State<NumberAndSymbol>>();
		
		while(!worklist.isEmpty()){
			//pop some element
			Iterator<Set<State<L>>> iter = worklist.iterator();
			Set<State<L>> currentStates = iter.next();
			iter.remove();
			
			State<NumberAndSymbol> compoundState = stateFor(currentStates);
			statesVisited.add(compoundState);

			Set<State<L>> lastFrontier = new HashSet<State<L>>(currentStates);
			
			for(int delta=0; delta <= maxNumber(); delta++) {
				Set<State<L>> newFrontier = new HashSet<State<L>>();
				for(State<L> curr : lastFrontier) {
					for (ISymbol<L,K> sym : alphabet) {
						State<L> succ = curr.successor(sym);
						if(succ!=null)
							newFrontier.add(succ);
					}
				}
				if(!newFrontier.isEmpty()) {
					for (ISymbol<L,K> sym : alphabet) {
						Set<State<L>> symSuccs = new HashSet<State<L>>();
						for(State<L> curr : newFrontier) {
							State<L> succ = curr.successor(sym);
							if(succ!=null)
								symSuccs.add(succ);
						}
						if(!symSuccs.isEmpty()) {
							State<NumberAndSymbol> newCompoundState = stateFor(symSuccs);
							compoundState.addTransition(getSymbolByLabel(new NumberAndSymbol(delta,sym)), newCompoundState);
							if(!statesVisited.contains(newCompoundState)) {
								worklist.add(symSuccs);
							}
						}
					}
				}
				lastFrontier = newFrontier;
			}
		}
				
		return stateFor(Collections.singleton(delegate.getInitialState()));
	}

	private State<NumberAndSymbol> stateFor(Set<State<L>> set) {
		State<NumberAndSymbol> compoundState = stateSetToCompoundState.get(set);
		if(compoundState==null) {
			boolean isFinal = true;
			for (State<L> state : set) {
				if(!state.isFinal()) {
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
	protected IIndexingStrategy<NumberAndSymbol, K, V> createIndexingStrategy() {
		return new IIndexingStrategy<NumberSyncFSMMonitorTemplate<L,K,V>.NumberAndSymbol, K, V>() {
			@Override
			public void processEvent(ISymbol<NumberAndSymbol, K> symbol, IVariableBinding<K, V> bind) {
				delegate.getIndexingStrategy().processEvent(symbol.getLabel().getSymbol(), bind);
			}
		};
	}

	@Override
	protected void fillAlphabet(IAlphabet<NumberAndSymbol, K> alphabet) {
		Set<ISymbol<L, K>> asSet = delegate.getAlphabet().asSet();
		for(int number=0; number <= maxNumber(); number++) {
			for (ISymbol<L, K> sym : asSet) {
				alphabet.makeNewSymbol(new NumberAndSymbol(number,sym), sym.getVariables());
			}
		}
	}
	
	private int maxNumber() {
		return delegate.getStates().size();
	}

	public class NumberAndSymbol {

		private final int number;
		private final ISymbol<L, K> sym;

		protected NumberAndSymbol(int number, ISymbol<L, K> sym) {
			super();
			this.number = number;
			this.sym = sym;
		}
		
		public int getNumber() {
			return number;
		}
		
		public ISymbol<L, K> getSymbol() {
			return sym;
		}
		
		@Override
		public String toString() {
			return "("+number+":"+sym+")";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + number;
			result = prime * result + ((sym == null) ? 0 : sym.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			NumberAndSymbol other = (NumberAndSymbol) obj;
			if (number != other.number)
				return false;
			if (sym == null) {
				if (other.sym != null)
					return false;
			} else if (!sym.equals(other.sym))
				return false;
			return true;
		}
		
	}
	
	public static void main(String[] args) {
		FailSafeIterMonitorTemplate fsi = new FailSafeIterMonitorTemplate();
		NumberSyncFSMMonitorTemplate<String,Var,Object> sync = new NumberSyncFSMMonitorTemplate<String,Var,Object>(fsi) {
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				// TODO Auto-generated method stub
				
			}
		};
		sync.getInitialState();
		System.out.println(fsi);
		System.out.println(sync);
	}
}