package de.bodden.mopbox.generic.indexing.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.finitestate.DefaultFSMMonitor;
import de.bodden.mopbox.finitestate.OpenFSMMonitorTemplate;
import de.bodden.mopbox.finitestate.State;
import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;

public class SymbolBasedIndexing<L, K, V> implements IIndexingStrategy<L, K, V> {

	//the monitor template being used
	protected final OpenFSMMonitorTemplate<L, K, V> template;

	//set of tracked variable bindings
	protected final Set<IVariableBinding<K,V>> trackedObjs = new HashSet<IVariableBinding<K,V>>();	
	
	//maps a symbol to all monitors associated with that symbol
	protected final Map<ISymbol<L, K>, Set<DefaultFSMMonitor<L>>> symMons = new HashMap<ISymbol<L,K>, Set<DefaultFSMMonitor<L>>>();
	
	//maps a state to all out-transitions of that state, i.e., all transitions for which we do not loop
	protected final Map<State<L>, Set<ISymbol<L, K>>> outTrans = new HashMap<State<L>, Set<ISymbol<L,K>>>();
	
	//here we associate each monitor with the binding it belongs to;
	//this could be saved by requiring a special kind of monitor template that generates
	//subtypes of DefaultFSMMonitor which store binding information in the monitor itself
	protected final Map<DefaultFSMMonitor<L>,IVariableBinding<K, V>> monitorToBinding = new HashMap<DefaultFSMMonitor<L>, IVariableBinding<K, V>>();
	
	public SymbolBasedIndexing(OpenFSMMonitorTemplate<L,K,V> template) {
		this.template = template;
		for(State<L> s: template.getStates()) {
			Set<ISymbol<L,K>> nonLoops = new HashSet<ISymbol<L,K>>();
			for(ISymbol<L, K> sym: template.getAlphabet()) {
				State<L> successor = s.successor(sym);
				//all transitions that do not loop
				//if there is no transition, then we assume that we
				//implicitly move to a "garbage" state, i.e., we do not loop either
				if(successor==null || !successor.equals(s))
					nonLoops.add(sym);
			}
			outTrans.put(s, nonLoops);
		}
		
		
		//initialize 1st layer of maps in symMons
		for(ISymbol<L, K> sym: template.getAlphabet()) {
			symMons.put(sym, new HashSet<DefaultFSMMonitor<L>>());
		}
	}
	
	@Override
	public void processEvent(ISymbol<L, K> b, IVariableBinding<K, V> l) {
		
		//start of line 7
		if(!trackedObjs.contains(l)) {
			boolean alreadyContained = false;
			for(DefaultFSMMonitor<L> m: symMons.get(b)) {
				if(l.isCompatibleWith(monitorToBinding.get(m))) {
					alreadyContained = true;
					break;
				}				
			}
			
			if(!alreadyContained) { //end of line 7
				
				//lines 8,9: create new monitor; already initialized to initial state
				DefaultFSMMonitor<L> m = template.createMonitorPrototype();
				monitorToBinding.put(m, l);
				
				if(outTrans.get(m.getCurrentState()).contains(b)) { //line 10
					Set<DefaultFSMMonitor<L>> mons = symMons.get(b); //line 11
					mons.add(m);
				}
				
				//lines 12 to 14
				trackedObjs.add(l);
				for(IVariableBinding<K, V> lPrime: l.strictlyLessInformativeBindingsOrdered()) {
					trackedObjs.add(lPrime);
				}
			}
		}
		
		//in the next line we are creating a copy to avoid concurrent modifications
		for(DefaultFSMMonitor<L> m: new HashSet<DefaultFSMMonitor<L>>(symMons.get(b))) { //lines 15, 16
			if(monitorToBinding.get(m).isCompatibleWith(l)) { //line 17
				boolean match = m.processEvent(b);	//line 18,19
				if(match) {
					template.matchCompleted(monitorToBinding.get(m));	//line 20
				}
				for(ISymbol<L, K> sigma: template.getAlphabet()) {	//line 21
					Set<DefaultFSMMonitor<L>> mons = symMons.get(sigma);
					if(m.getCurrentState()!=null && //if there is a next state
					   outTrans.get(m.getCurrentState()).contains(sigma)) { //line 22
						mons.add(m);	//line 23
					} else {	//line 24
						mons.remove(m);	//line 25
					}
				}
			}
		}
	}
}