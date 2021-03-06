package de.bodden.mopbox.generic.indexing.simple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bodden.mopbox.generic.IIndexingStrategy;
import de.bodden.mopbox.generic.IMonitor;
import de.bodden.mopbox.generic.IMonitorTemplate;
import de.bodden.mopbox.generic.ISymbol;
import de.bodden.mopbox.generic.IVariableBinding;

/**
 * This strategy is known as Algorithm B in the TACAS paper of Feng Chen at al.
 * For n being the length of the trace and m being the number of all
 * possible parameter combinations, this algorithm works in O(n*m), which
 * is pretty bad. Also, this implementation is not optimized for
 * memory consumption, i.e., it will leak memory.
 * 
 * This class obtains thread safety in a naive way, through a single lock that
 * is held when calling {@link #processEvent(ISymbol, IVariableBinding)}.
 */
public class StrategyB<M extends IMonitor<M,L>,L,K,V> implements IIndexingStrategy<L,K,V> {
	
	private Map<IVariableBinding<K,V>,M> bindingToMonitor;

	private final IMonitorTemplate<M,L,K,V> template;
	
	public StrategyB(IMonitorTemplate<M,L,K,V> template) {
		this.template = template;
		bindingToMonitor = new HashMap<IVariableBinding<K,V>, M>();		
		bindingToMonitor.put(template.createEmptyBinding(), template.createMonitorPrototype());		
	}

	@Override
	public synchronized void processEvent(ISymbol<L,K> symbol, IVariableBinding<K, V> bind){
		Set<IVariableBinding<K,V>> joins = new HashSet<IVariableBinding<K,V>>();
		for(IVariableBinding<K,V> storedBinding: bindingToMonitor.keySet()) {
			if(storedBinding.isCompatibleWith(bind)) {
				joins.add(storedBinding.computeJoinWith(bind));
			}
		}
		Set<IVariableBinding<K,V>> keySetCopy = new HashSet<IVariableBinding<K,V>>(bindingToMonitor.keySet());
		for(IVariableBinding<K,V> binding: joins) {
			if(binding.isCompatibleWith(bind)) {
				IVariableBinding<K,V> oldBinding = binding.findMax(keySetCopy);
				M copiedMonitor = bindingToMonitor.get(oldBinding).copy();
				if(copiedMonitor.processEvent(symbol)) {
					template.matchCompleted(oldBinding);
				}
				bindingToMonitor.put(binding, copiedMonitor);
			}
		}
	}

}
