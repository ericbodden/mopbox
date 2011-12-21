package de.bodden.mopbox.finitestate.sync;

import helpers.FailSafeIterMonitorTemplate;
import helpers.FailSafeIterMonitorTemplate.Var;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;
import de.bodden.mopbox.generic.indexing.simple.StrategyB;

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
							State<NumberAndSymbol> newCompoundState = stateFor(symSuccs);
							compoundState.addTransition(getSymbolByLabel(new NumberAndSymbol(delta,sym)), newCompoundState);
							if(!statesVisited.contains(newCompoundState)) {
								worklist.add(symSuccs);
							}
						}
					}
				}
				
				nextFrontier = new HashSet<State<L>>();
				for(State<L> curr : frontier) {
					for (ISymbol<L,K> sym : alphabet) {
						State<L> succ = curr.successor(sym);
						if(succ!=null)
							nextFrontier.add(succ);
					}
				}
				
				delta++;
			//in the following conditional, the 2nd branch is to make sure that we create
			//transitions for all symbols in the alphabet; alternatively we could do a
			//dynamic transition matching
			} while(!frontier.equals(nextFrontier) || delta<maxNumber());
		}
				
		return stateFor(Collections.singleton(delegate.getInitialState()));
	}

	private State<NumberAndSymbol> stateFor(Set<State<L>> set) {
		State<NumberAndSymbol> compoundState = stateSetToCompoundState.get(set);
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
	protected IIndexingStrategy<NumberAndSymbol, K, V> createIndexingStrategy() {
		//TODO can we somehow choose the strategy based on the one used for the delegate?
		return new StrategyB<DefaultFSMMonitor<NumberAndSymbol>, NumberAndSymbol, K, V>(this); 
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
	
	public void processEvent(NumberAndSymbol label, IVariableBinding<K,V> binding) {
		//TODO must have a catch-all mechanism if the number is larger than maxNumber() 
		super.processEvent(label, binding);
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
	
	protected static String trace = "";

	public static void main(String[] args) {
		FailSafeIterMonitorTemplate fsi = new FailSafeIterMonitorTemplate();
		FailSafeIterMonitorTemplate fsi2 = new FailSafeIterMonitorTemplate();
		NumberSyncFSMMonitorTemplate<String,Var,Object> sync = new NumberSyncFSMMonitorTemplate<String,Var,Object>(fsi2) {
			public void matchCompleted(IVariableBinding<Var, Object> binding) {
				trace += binding.toString();				
			}
		};
		
		System.out.println(fsi);
		System.out.println(sync);

		Collection<Object> c = new ArrayList<Object>();
		Iterator<Object> i = c.iterator();
		
		final int MAX = 100000;
		
		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_c = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_c.put(Var.C, c);

		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_i = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_i.put(Var.I, i);

		IVariableBinding<FailSafeIterMonitorTemplate.Var, Object> b_ci = new VariableBinding<FailSafeIterMonitorTemplate.Var, Object>();
		b_ci.put(Var.C, c);
		b_ci.put(Var.I, i);
		
		
		fsi.processEvent("create", b_ci);
		//sync.processEvent(sync.new NumberAndSymbol(0, fsi.getSymbolByLabel("create")), b_ci);
		fsi.processEvent("update", b_c);
		sync.processEvent(sync.new NumberAndSymbol(1, fsi.getSymbolByLabel("update")), b_c);					
		fsi.processEvent("iter", b_i);
		sync.processEvent(sync.new NumberAndSymbol(0, fsi.getSymbolByLabel("iter")), b_i);					

		System.out.println("1: "+fsi.getTrace());
		System.out.println("2: "+trace);
	
		
		System.exit(0);
		
		//RANDOMIZED TEST

		Random random = new Random();
		
		int missed = 0;
		for(int n=0; n<MAX; n++) {
			double rand = Math.random();
			boolean miss = random.nextBoolean();
			if(missed>3) miss = false; // TODO how to bound in the general case?
			if(rand<1/3.0) {
				fsi.processEvent("create", b_ci);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new NumberAndSymbol(missed, fsi2.getSymbolByLabel("create")), b_ci);
				}
			} else if(rand>=1/3.0 && rand<2/3.0) {
				fsi.processEvent("iter", b_i);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new NumberAndSymbol(missed, fsi2.getSymbolByLabel("iter")), b_i);					
				}
			} else {
				fsi.processEvent("update", b_c);
				if(miss) {
					missed++;
				} else {
					sync.processEvent(sync.new NumberAndSymbol(missed, fsi2.getSymbolByLabel("update")), b_c);					
				}
			}
			if(!miss) missed = 0;
		}
		System.out.println(fsi.getTrace());
		System.out.println(fsi2.getTrace());
	}
}