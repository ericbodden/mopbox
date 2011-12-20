package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
		
		State<NumberAndSymbol> initialCompoundState = stateFor(Collections.singleton(delegate.getInitialState()));

		for(State<L> s: delegate.getStates()) {
			Set<State<L>> statesSeen = new HashSet<State<L>>();
			Set<State<L>> frontier = new HashSet<State<L>>();
			frontier.add(s);		
			
			State<NumberAndSymbol> oldCompoundState = stateFor(frontier);

			int delta = 1;
			while(true) {
				Set<State<L>> nextFrontier = new HashSet<State<L>>();
				for (ISymbol<L,K> sym : alphabet) {
					Set<State<L>> symFrontier = new HashSet<State<L>>();
					for(State<L> curr : frontier) {
						State<L> succ = curr.successor(sym);
						if(succ!=null)
							symFrontier.add(succ);
					}
					State<NumberAndSymbol> newCompoundState = stateFor(symFrontier);
					oldCompoundState.addTransition(getSymbolByLabel(new NumberAndSymbol(delta,sym)), newCompoundState);
					nextFrontier.addAll(symFrontier);
				}
				statesSeen.addAll(nextFrontier);
				delta++;
				if(!frontier.equals(nextFrontier))
					frontier = nextFrontier;
				else
					break;
			}
		}
				
		return initialCompoundState;
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
		for(int number=1; number <= maxNumber(); number++) {
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