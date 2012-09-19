package de.bodden.mopbox.finitestate.sync;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IAlphabet;
import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.DynamicAlphabet;
import de.bodden.mopbox.generic.def.VariableBinding;
import de.bodden.mopbox.generic.indexing.simple.StrategyB;

/**
 * An abstract monitor template for a <i>syncing monitor</i>. Such a monitor may, for performance reasons
 * skip monitoring a certain number of events. Instead of dispatching those skipped events to the respective
 * monitor(s), the template instead gathers some summary information. At some point the template then
 * decides to start monitoring again, it <i>synchronizes</i>. In that case, the template transitions based
 * on the symbol of that monitored event and on the summary information computed for the gap that was skipped.
 *
 * The purpose of this template is to convert a monitor template for a regular FSM property into a template
 * that is capable of synchronizing. To implement the correct semantics, the alphabet, state set and transition
 * relation of the resulting automaton are expanded: transitions happen not just based on symbols but
 * based on a pair of symbol and the gap abstraction.  
 * 
 * The implementation of this class is independent of the particular abstraction used for modeling the
 * summary information. The abstraction must simply subclass {@link SymbolMultisetAbstraction}, implementing
 * equals and hashCode methods. The abstraction must, however, be independent of the order in which the skipped
 * events occur. Otherwise the algorithm used to create the transitions in question may be incorrect. 
 * 
 * @param <L> The type of labels used at transitions.
 * @param <K> The type of keys used in {@link IVariableBinding}s.
 * @param <V> The type of values used in {@link IVariableBinding}s.
 * @param <A> The type of abstraction used to model the summary information at monitoring gaps. The summary
 *            information must not contain any information about the order of skipped events, i.e.,
 *            abstraction(a b)=abstraction(b a) must hold for all events a,b.
 */
public abstract class AbstractSyncingFSMMonitorTemplate<L, K, V, A extends AbstractSyncingFSMMonitorTemplate<L,K,V,A>.SymbolMultisetAbstraction>
	extends OpenFSMMonitorTemplate<AbstractSyncingFSMMonitorTemplate<L,K,V,A>.AbstractionAndSymbol, K, V>{
	
	
	/**
	 * The empty multi-set of symbols.
	 */
	private final ImmutableMultiset<ISymbol<L, K>> EMPTY = ImmutableMultiset.<ISymbol<L,K>>of();

	
	/**
	 * The monitor template this syncing monitor template is based on.
	 */
	protected final OpenFSMMonitorTemplate<L, K, V> delegate;
	
	/**
	 * A mapping from states sets of the delegate to a compound state of this monitor template that represents
	 * the state set.
	 */
	protected final Map<Set<State<L>>,State<AbstractionAndSymbol>> stateSetToCompoundState = new HashMap<Set<State<L>>, State<AbstractionAndSymbol>>();
	
	/**
	 * A mapping used to record a transition relation over compound states, i.e., sets of states
	 * of the original automaton.
	 */
	protected final Map<Set<State<L>>,Map<ISymbol<AbstractionAndSymbol, K>,Set<State<L>>>> transitions =
		new HashMap<Set<State<L>>, Map<ISymbol<AbstractionAndSymbol,K>,Set<State<L>>>>();
	
	/**
	 * The multiset of skipped events.
	 */
	protected Multiset<ISymbol<L, K>> skippedSymbols = HashMultiset.create();
	
	/**
	 * The intersection of the variable bindings of skipped events.
	 */
	protected IVariableBinding<K, V> intersectionOfSkippedBindings = new VariableBinding<K, V>();
	
	/**
	 * The number of times we reenabled monitoring.
	 */
	protected long reenableTime;
	
	/**
	 * Boolean stating whether we monitored the last event.
	 */
	protected boolean didMonitorLastEvent = true;
	
	@SuppressWarnings("serial")
	protected final IVariableBinding<K, V> INCOMPATIBLE_BINDING = new VariableBinding<K, V>() {
		public boolean isCompatibleWith(IVariableBinding<K,V> other) { return false; };
	};
	
	/**
	 * We use this random source to produce coin flips that tell us whether to turn sampling
	 * on or off during any given sampling period. We use a deterministic seed for
	 * reproducibility. 
	 */
	protected final Random random = new Random(0L);
	
	/**
	 * The length of any sampling period.
	 */
	protected final int samplingPeriod;

	/**
	 * The length of any skip period.
	 */
	protected final int skipPeriod;

	/**
	 * The current phase of the sampling period, where 0 is the start of the period.
	 */
	protected int phase;
	
	/**
	 * This boolean tells us whether or not we will process events in the current period.
	 */
	protected boolean processEventsInCurrentPeriod;
	
	protected final Set<ISymbol<L, K>> criticalSymbols;
	
	/**
	 * @param delegate The monitor template this syncing monitor template is based on. The template will remain unmodified.
	 * @param max The maximal number of skipped events.
	 */
	public AbstractSyncingFSMMonitorTemplate(OpenFSMMonitorTemplate<L, K, V> delegate) {
		this.delegate = delegate;
		initialize();
		this.samplingPeriod = samplingPeriod();
		this.skipPeriod = (int) (1.0d/samplingRate() - 1) * samplingPeriod; 
		this.criticalSymbols = criticalSymbols();
	}

	/**
	 * This methods implements the algorithm at the core of this monitor template. The algorithm creates
	 * transitions of the form (symbol,abstraction) where different abstractions of gaps during monitoring
	 * are possible. The algorithm uses two worklists, one for state sets of the delegate (which will become
	 * states in this automaton), and one for multisets of skipped symbols. For each reachable state set
	 * the algorithm computes all possible successor state sets under an expanded transition relation. This
	 * transition relation takes into account the abstractions of all possible multisets of skipped events
	 * up to {@link AbstractSyncingFSMMonitorTemplate#MAX}. 
	 */
	@Override
	protected State<AbstractionAndSymbol> setupStatesAndTransitions() {
		IAlphabet<L, K> alphabet = delegate.getAlphabet();
		
		Set<Set<State<L>>> worklist = new HashSet<Set<State<L>>>();
		worklist.add(Collections.singleton(delegate.getInitialState()));
		
		Set<Set<State<L>>> statesVisited = new HashSet<Set<State<L>>>();		
				
		while(!worklist.isEmpty()){
			//pop some element
			Iterator<Set<State<L>>> iter = worklist.iterator();
			Set<State<L>> currentStates = iter.next();
			iter.remove();
			
			//have visited current set of states; to terminate, don't visit again
			statesVisited.add(currentStates);

			//create a work list for abstractions of multisets of skipped symbols;
			//starting with the abstraction of the empty multiset
			Set<A> worklistSyms = new HashSet<A>();
			worklistSyms.add(abstraction(EMPTY)); //add empty multiset
			
			//this maps an abstraction of a gap info to all the states reachable through this gap
			//info (and any symbol) 
			Map<A,Set<State<L>>> abstractionToStates = new HashMap<A, Set<State<L>>>();
			A emptyAbstraction = abstraction(EMPTY);
			abstractionToStates.put(emptyAbstraction, currentStates);
			
			while(!worklistSyms.isEmpty()) {
				//pop entry off symbols worklist
				Iterator<A> symsIter = worklistSyms.iterator();
				A abstraction = symsIter.next();
				symsIter.remove();

				//compute abstraction for symbols and the set of states reachable by the abstraction
				Set<State<L>> frontier = abstractionToStates.get(abstraction);
				
				//this set is used to register all newly computed successor state sets
				//it is important that this be an identity hash set because the contents of the element sets can change
				//during the course of the remaining algorithm
				Set<Set<State<L>>> newStateSets = Collections.newSetFromMap(new IdentityHashMap<Set<State<L>>, Boolean>());
				
				for (ISymbol<L,K> sym : alphabet) {
					//compute successors of the current state set under sym
					Set<State<L>> symSuccs = new HashSet<State<L>>();
					for(State<L> curr : frontier) {
						State<L> succ = curr.successor(sym);
						if(succ!=null)
							symSuccs.add(succ);
					}
					if(!symSuccs.isEmpty()) {						
						boolean subsumed = transitionForSymbolAlreadyExists(currentStates,sym,symSuccs);
						
						
						if(!subsumed) {
							//create label for new transition: (abstraction,sym)
							ISymbol<AbstractionAndSymbol, K> compoundSymbol = getSymbolByLabel(new AbstractionAndSymbol(abstraction, sym));
							//register possible target states under that transition
							Set<State<L>> newTargets = addTargetStatesToTransition(currentStates, compoundSymbol, symSuccs);
							//register the new state set so that we can later-on add it to the worklist
							newStateSets.add(newTargets);
							
							A newAbstraction = abstraction.add(sym);
							worklistSyms.add(newAbstraction);
							Set<State<L>> old = abstractionToStates.get(newAbstraction);
							if(old==null) {
								old = new HashSet<State<L>>();
								abstractionToStates.put(newAbstraction, old);
							} 
							old.addAll(symSuccs);
							//for abstracted events we do not know whether they happened on the same object
							//or not; hence we could also have stayed in the same set of states
							old.addAll(frontier);
						}
					}
				}
				
				//push all newly discovered state sets not yet processed onto the worklist
				for (Set<State<L>> states : newStateSets) {
					if(!statesVisited.contains(states)) {
						worklist.add(states);
					}
				}
			} 
			
		}
				
		createTransitions();
		
		return stateFor(Collections.singleton(delegate.getInitialState()));
	}
	
	protected IAlphabet<AbstractSyncingFSMMonitorTemplate<L,K,V,A>.AbstractionAndSymbol,K> createAlphabet() {
		super.createAlphabet();
		return new DynamicAlphabet<AbstractSyncingFSMMonitorTemplate<L,K,V,A>.AbstractionAndSymbol, K>();
	}
	
	private boolean transitionForSymbolAlreadyExists(Set<State<L>> currentStates, ISymbol<L, K> symbol, Set<State<L>> symSuccs) {
		Map<ISymbol<AbstractionAndSymbol, K>, Set<State<L>>> symbolToTargets = transitions.get(currentStates);
		if(symbolToTargets==null) return false;
		for(Map.Entry<ISymbol<AbstractionAndSymbol, K>, Set<State<L>>> symbolAndTargets: symbolToTargets.entrySet()) {
			ISymbol<AbstractionAndSymbol, K> sym = symbolAndTargets.getKey();
			if(sym.getLabel().getSymbol().equals(symbol)) {
				Set<State<L>> targets = symbolAndTargets.getValue();
				if(targets.equals(symSuccs)) { 
					return true;
				}
			}
		}
		return false;
	}
		

	protected Set<State<L>> addTargetStatesToTransition(Set<State<L>> currentStates, ISymbol<AbstractionAndSymbol,K> symbol, Set<State<L>> someTargetStates) {
		Map<ISymbol<AbstractionAndSymbol, K>, Set<State<L>>> symbolToTargets = transitions.get(currentStates);
		if(symbolToTargets==null) {
			symbolToTargets = new HashMap<ISymbol<AbstractionAndSymbol,K>, Set<State<L>>>();
			transitions.put(currentStates, symbolToTargets);
		}
		Set<State<L>> targets = symbolToTargets.get(symbol);
		if(targets==null) {
			targets = new HashSet<State<L>>();
			symbolToTargets.put(symbol, targets);
		} 
		targets.addAll(someTargetStates);
		return new HashSet<State<L>>(targets);
 	}

	private void createTransitions() {
		for (Entry<Set<State<L>>, Map<ISymbol<AbstractionAndSymbol, K>, Set<State<L>>>> sourceAndMap : transitions.entrySet()) {
			Set<State<L>> source = sourceAndMap.getKey();
			for (Entry<ISymbol<AbstractionAndSymbol, K>, Set<State<L>>> symbolAndTargetStates : sourceAndMap.getValue().entrySet()) {
				ISymbol<AbstractionAndSymbol, K> compoundSymbol = symbolAndTargetStates.getKey();
				Set<State<L>> targetStates = symbolAndTargetStates.getValue();
				stateFor(source).addTransition(compoundSymbol, stateFor(targetStates));
			}			
		}
		transitions.clear(); //free space
	}

	protected abstract A abstraction(Multiset<ISymbol<L, K>> symbols);

	private State<AbstractionAndSymbol> stateFor(Set<State<L>> set) {
		State<AbstractionAndSymbol> compoundState = stateSetToCompoundState.get(set);
		if(compoundState==null) {
			boolean isFinal = true;
			for (State<L> state : set) {
				if(!state.isFinal() ){ 
					isFinal = false;
					break;
				}
			}			
			compoundState = makeState(isFinal);
			System.err.println(compoundState+" - "+set);
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
		//alphabet is instead filled on demand
	}
	
	/**
	 * Maybe processes the event consisting of the symbol and bindings.
	 * Whether or not the event is processed depends on the return value of
	 * the predicate {@link #shouldMonitor(ISymbol, IVariableBinding, Multiset)}.
	 * @param symbolLabel the current event's symbol's label
	 * @param binding the current events's binding
	 */
	public void maybeProcessEvent(L symbolLabel, IVariableBinding<K,V> binding) {
		ISymbol<L, K> symbol = delegate.getAlphabet().getSymbolByLabel(symbolLabel);
		if(shouldMonitor(symbol,binding,skippedSymbols)) {
			if(!didMonitorLastEvent) reenableTime++;
				processEvent(new AbstractionAndSymbol(abstraction(skippedSymbols), symbol), binding);
			skippedSymbols.clear();
			didMonitorLastEvent = true;
		} else {
			skippedSymbols.add(symbol);
			didMonitorLastEvent = false;
		}
	}
	
	/**
	 * Returns the lenght of a sampling period for this monitor template. This size may actually depend on the 
	 * size or structure of the delegate, i.e., the property to be monitored.
	 */
	abstract protected int samplingPeriod();

	/**
	 * This is the rate at which we sample, given as a number from 0 to 1.
	 */
	abstract protected double samplingRate();
	
	/**
	 * Returns the set of critical symbols for the monitored property. Such symbols will always be monitored,
	 * no matter whether or not sampling is enabled.
	 */
	abstract protected Set<ISymbol<L, K>> criticalSymbols();

	/**
	 * Determines whether the current event should be monitored.
	 * @param symbol the current event's symbol
	 * @param binding the current events's binding
	 * @param skippedSymbols the multiset of symbols of events skipped so far
	 * @return 
	 */
	protected boolean shouldMonitor(ISymbol<L, K> symbol, IVariableBinding<K, V> binding, Multiset<ISymbol<L, K>> skippedSymbols) {
		if(phase==0) {
			processEventsInCurrentPeriod = random.nextBoolean();
		}
		int periodLength = processEventsInCurrentPeriod ? samplingPeriod : skipPeriod;
		phase = (phase+1) % periodLength;
		return processEventsInCurrentPeriod || criticalSymbols.contains(symbol);
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
	
	@Override
	public SyncFSMMonitor createMonitorPrototype() {
		return new SyncFSMMonitor(getInitialState());
	}
	
	@Override
	public IVariableBinding<K, V> createEmptyBinding() {
		return delegate.createEmptyBinding();
	}

	public class SyncFSMMonitor extends DefaultFSMMonitor<AbstractionAndSymbol> {

		protected long lastAccess;
		
		public SyncFSMMonitor(State<AbstractionAndSymbol> initialState) {
			super(initialState);
		}
		
		@Override
		public boolean processEvent(ISymbol<AbstractionAndSymbol, ?> s) {
			//as input we get a syncing symbol; now we must check whether we actually need
			//to sync; if not, then we modify the symbol to a non-syncing one
			
			if(reenableTime==lastAccess) {
				//don't sync
				s = getAlphabet().getSymbolByLabel(new AbstractionAndSymbol(abstraction(EMPTY), s.getLabel().getSymbol()));
			} else {
				lastAccess = reenableTime;
			}
			
			return super.processEvent(s);
		}
	}

	public abstract class SymbolMultisetAbstraction {		
		public abstract int hashCode();

		public abstract boolean equals(Object obj);		
		
		protected abstract A add(ISymbol<L, K> sym);
	}

}