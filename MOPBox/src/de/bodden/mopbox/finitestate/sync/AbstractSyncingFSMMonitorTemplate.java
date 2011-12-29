package de.bodden.mopbox.finitestate.sync;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableMultiset.Builder;
import com.google.common.collect.Multiset;

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
public abstract class AbstractSyncingFSMMonitorTemplate<L, K, V, A extends AbstractSyncingFSMMonitorTemplate<L,K,V,A>.SymbolMultisetAbstraction>
	extends OpenFSMMonitorTemplate<AbstractSyncingFSMMonitorTemplate<L,K,V,A>.AbstractionAndSymbol, K, V>{
	
	protected final OpenFSMMonitorTemplate<L, K, V> delegate;
	
	protected final Map<Set<State<L>>,State<AbstractionAndSymbol>> stateSetToCompoundState = new HashMap<Set<State<L>>, State<AbstractionAndSymbol>>();
	
	protected final int MAX;

	public AbstractSyncingFSMMonitorTemplate(OpenFSMMonitorTemplate<L, K, V> delegate, int max) {
		this.delegate = delegate;
		this.MAX = max;
		initialize();
	}

	@Override
	protected State<AbstractionAndSymbol> setupStatesAndTransitions() {
		IAlphabet<L, K> alphabet = delegate.getAlphabet();
		
		Set<Set<State<L>>> worklist = new HashSet<Set<State<L>>>();
		worklist.add(Collections.singleton(delegate.getInitialState()));
		
		Set<State<AbstractionAndSymbol>> statesVisited = new HashSet<State<AbstractionAndSymbol>>();
		
		while(!worklist.isEmpty()){
			//pop some element
			Iterator<Set<State<L>>> iter = worklist.iterator();
			Set<State<L>> currentStates = iter.next();
			iter.remove();
			
			State<AbstractionAndSymbol> compoundState = stateFor(currentStates);
			statesVisited.add(compoundState);

			Set<Multiset<ISymbol<L, K>>> worklistSyms = new HashSet<Multiset<ISymbol<L, K>>>();
			final ImmutableMultiset<ISymbol<L, K>> EMPTY = ImmutableMultiset.<ISymbol<L,K>>of();
			worklistSyms.add(EMPTY); //add empty multiset
			
			Map<A,Set<State<L>>> abstractionToStates = new HashMap<A, Set<State<L>>>();
			abstractionToStates.put(abstraction(EMPTY), currentStates);

			while(!worklistSyms.isEmpty()) {
				//pop entry off symbols worklist
				Iterator<Multiset<ISymbol<L, K>>> symsIter = worklistSyms.iterator();
				Multiset<ISymbol<L, K>> syms = symsIter.next();
				symsIter.remove();

				A abstraction = abstraction(syms);
				Set<State<L>> frontier = abstractionToStates.get(abstraction);
				
				for (ISymbol<L,K> sym : alphabet) {
					Set<State<L>> symSuccs = new HashSet<State<L>>();
					for(State<L> curr : frontier) {
						State<L> succ = curr.successor(sym);
						if(succ!=null)
							symSuccs.add(succ);
					}
					if(!symSuccs.isEmpty()) {
						State<AbstractionAndSymbol> newCompoundState = stateFor(symSuccs);
						ISymbol<AbstractionAndSymbol, K> compoundSymbol = getSymbolByLabel(new AbstractionAndSymbol(abstraction, sym));
						if(compoundState.successor(compoundSymbol)==null) 
							compoundState.addTransition(compoundSymbol, newCompoundState);
						else
							assert compoundState.successor(compoundSymbol)==newCompoundState;
						if(!statesVisited.contains(newCompoundState)) {
							worklist.add(symSuccs);
						}
					}
					if(syms.size()<MAX) {
						ImmutableMultiset<ISymbol<L, K>> newSyms = union(syms, sym);
						worklistSyms.add(newSyms);
						A newAbstraction = abstraction(newSyms);
						Set<State<L>> old = abstractionToStates.get(newAbstraction);
						if(old==null) {
							old = new HashSet<State<L>>();
							abstractionToStates.put(newAbstraction, old);
						}
						old.addAll(symSuccs);
					}
				}
			} 
		}
				
		return stateFor(Collections.singleton(delegate.getInitialState()));
	}

	private ImmutableMultiset<ISymbol<L, K>> union(
			Multiset<ISymbol<L, K>> syms, ISymbol<L, K> sym) {
		Builder<ISymbol<L,K>> builder = ImmutableMultiset.<ISymbol<L,K>>builder();
		builder.addAll(syms);
		builder.add(sym);
		ImmutableMultiset<ISymbol<L, K>> newMultiSet = builder.build();
		return newMultiSet;
	}

	protected abstract A abstraction(Multiset<ISymbol<L, K>> symbols);

	private State<AbstractionAndSymbol> stateFor(Set<State<L>> set) {
		State<AbstractionAndSymbol> compoundState = stateSetToCompoundState.get(set);
		if(compoundState==null) {
			//FIXME still not quite what we want...
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
	protected IIndexingStrategy<AbstractionAndSymbol, K, V> createIndexingStrategy() {
		//TODO can we somehow choose the strategy based on the one used for the delegate?
		return new StrategyB<DefaultFSMMonitor<AbstractionAndSymbol>, AbstractionAndSymbol, K, V>(this); 
	}

	@Override
	protected void fillAlphabet(IAlphabet<AbstractionAndSymbol, K> alphabet) {
		Set<Multiset<ISymbol<L, K>>> worklistSyms = new HashSet<Multiset<ISymbol<L, K>>>();
		final ImmutableMultiset<ISymbol<L, K>> EMPTY = ImmutableMultiset.<ISymbol<L,K>>of();
		worklistSyms.add(EMPTY); //add empty multiset
		
		while(!worklistSyms.isEmpty()) {
			//pop entry off symbols worklist
			Iterator<Multiset<ISymbol<L, K>>> symsIter = worklistSyms.iterator();
			Multiset<ISymbol<L, K>> syms = symsIter.next();
			symsIter.remove();

			A abstraction = abstraction(syms);

			for (ISymbol<L,K> sym : delegate.getAlphabet()) {				
				alphabet.makeNewSymbol(
						new AbstractionAndSymbol(abstraction, sym),
						sym.getVariables() //FIXME What should be the variables of a compound symbol?
				);
				if(syms.size()<MAX) {
					ImmutableMultiset<ISymbol<L, K>> newSyms = union(syms, sym);
					worklistSyms.add(newSyms);
				}
			}
		}
	}
	
	public void processEvent(AbstractionAndSymbol label, IVariableBinding<K,V> binding) {
		//TODO must have a catch-all mechanism if the number is larger than maxNumber() 
		super.processEvent(label, binding);
	}
	
	public class AbstractionAndSymbol {
		private final A abstraction;
		private final ISymbol<L, K> symbol;
		public AbstractionAndSymbol(A abstraction, ISymbol<L, K> symbol) {
			this.abstraction = abstraction;
			this.symbol = symbol;
		}
		public A getAbstraction() {
			return abstraction;
		}
		public ISymbol<L, K> getSymbol() {
			return symbol;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((abstraction == null) ? 0 : abstraction.hashCode());
			result = prime * result
					+ ((symbol == null) ? 0 : symbol.hashCode());
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
			AbstractionAndSymbol other = (AbstractionAndSymbol) obj;
			if (abstraction == null) {
				if (other.abstraction != null)
					return false;
			} else if (!abstraction.equals(other.abstraction))
				return false;
			if (symbol == null) {
				if (other.symbol != null)
					return false;
			} else if (!symbol.equals(other.symbol))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "<"+abstraction+";"+symbol+">";
		}
	}

	public abstract class SymbolMultisetAbstraction {		
		public abstract int hashCode();

		public abstract boolean equals(Object obj);		
	}
	
}