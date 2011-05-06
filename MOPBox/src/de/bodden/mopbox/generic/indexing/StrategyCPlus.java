package de.bodden.mopbox.generic.indexing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;
import de.bodden.mopbox.generic.def.VariableBinding;

/**
 * This strategy is known as Algorithm C+ in the ASE paper of Feng Chen at al.
 * Opposed to {@link StrategyC}, it requires the presence of <i>creation symbols</i>.
 * Such symbols need to be explicitly declared.
 */
public class StrategyCPlus<M extends IMonitor<M,L>,L,K,V> implements IIndexingStrategy<L,K,V> {
	
	private final static boolean DEBUG = false; 
	
	@SuppressWarnings("rawtypes")
	protected static final IVariableBinding EMPTY_BINDING = new VariableBinding();
	
	protected Map<IVariableBinding<K,V>,M> bindingToMonitor;
	protected Map<IVariableBinding<K,V>,Set<IVariableBinding<K,V>>> bindingToDefinedMoreInformativeBindings;

	protected final IMonitorTemplate<M,L,K,V> template;

	protected Set<ISymbol<L>> creationSymbols;
	
	public StrategyCPlus(IMonitorTemplate<M,L,K,V> template, Set<ISymbol<L>> creationSymbols) {
		if(DEBUG) {
			System.err.println();
			System.err.println();
			System.err.println();
		}

		this.template = template;
		this.creationSymbols = creationSymbols;
		bindingToMonitor = new HashMap<IVariableBinding<K,V>, M>();		
		bindingToDefinedMoreInformativeBindings = new HashMap<IVariableBinding<K,V>, Set<IVariableBinding<K,V>>>();
	}

	@Override
	public void processEvent(ISymbol<L> symbol, IVariableBinding<K, V> bind){
		M currentMonitor = bindingToMonitor.get(bind);
		boolean undefined = (currentMonitor==null);
		if(undefined) { //line 1
			List<IVariableBinding<K,V>> lessInformativeBindings =
				bind.strictlyLessInformativeBindingsOrdered(); //line 2
			IVariableBinding<K,V> foundBinding = null;
			for (IVariableBinding<K,V> bMax : lessInformativeBindings) { 
				if(bindingToMonitor.containsKey(bMax)) { //line 3
					foundBinding=bMax;
					break; //line 4
				}
			}
			if(foundBinding!=null) {
				defineTo(bind,foundBinding); //line 5
			} else if(creationSymbols.contains(symbol)){
				defineNew(bind); //line 6
			}
			for (IVariableBinding<K,V> bMax : lessInformativeBindings) { //line 8
				Set<IVariableBinding<K,V>> copy = new HashSet<IVariableBinding<K,V>>(definedMoreInformativeBindingsFor(bMax));
				for(IVariableBinding<K,V> b: copy) { //line 9
					if(b.isCompatibleWith(bind)) { //line 9
						IVariableBinding<K,V> bComp = b;
						IVariableBinding<K,V> join = bComp.computeJoinWith(bind);
						if(!bindingToMonitor.containsKey(join)) { //line 10
							defineTo(join, bComp);
						}
					}
				}
			}
		}
		Set<IVariableBinding<K,V>> union = //line 16
			new HashSet<IVariableBinding<K,V>>(definedMoreInformativeBindingsFor(bind));
		union.add(bind); //line 16
		for (IVariableBinding<K,V> bPrime : union) { //line 16
			M monitor = bindingToMonitor.get(bPrime);			
			if(monitor!=null && monitor.processEvent(symbol)) { //lines 17
				template.matchCompleted(bPrime); //lines 18
			}
		}
		if(DEBUG) {
			System.err.print(symbol);
			System.err.print(": ");
			System.err.println(bind);
			System.err.println(bindingToMonitor);
			System.err.println(bindingToDefinedMoreInformativeBindings);
			System.err.println();
		}
	}

	
	protected void defineTo(IVariableBinding<K,V> b, IVariableBinding<K,V> bPrime) {
		bindingToMonitor.put(b,bindingToMonitor.get(bPrime).copy());
		updateChainingTable(b);
	}

	protected void defineNew(IVariableBinding<K,V> b) {
		bindingToMonitor.put(b,template.createMonitorPrototype());
		updateChainingTable(b);		
	}

	protected void updateChainingTable(IVariableBinding<K,V> b) {
		for(IVariableBinding<K,V> bPrimePrime: b.strictlyLessInformativeBindingsOrdered()) {
			definedMoreInformativeBindingsFor(bPrimePrime).add(b);
		}
	}

	protected Set<IVariableBinding<K,V>> definedMoreInformativeBindingsFor(IVariableBinding<K,V> binding) {
		Set<IVariableBinding<K,V>> set = bindingToDefinedMoreInformativeBindings.get(binding);
		if(set==null) {
			set = new HashSet<IVariableBinding<K,V>>();
			bindingToDefinedMoreInformativeBindings.put(binding,set);
		}
		return set;
	}

}
