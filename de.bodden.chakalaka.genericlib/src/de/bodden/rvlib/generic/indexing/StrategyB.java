package de.bodden.rvlib.generic.indexing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.bodden.rvlib.generic.IEvent;
import de.bodden.rvlib.generic.IIndexingStrategy;
import de.bodden.rvlib.generic.IMonitor;
import de.bodden.rvlib.generic.IMonitorTemplate;
import de.bodden.rvlib.generic.IVariableBinding;
import de.bodden.rvlib.impl.VariableBinding;

/**
 * This strategy is known as Algorithm B in the TACAS paper of Feng Chen at al.
 * For n being the length of the trace and m being the number of all
 * possible parameter combinations, this algorithm works in O(n*m), which
 * is pretty bad. Also, this implementation is not optimized for
 * memory consumption, i.e., it will leak memory.
 */
public class StrategyB<M extends IMonitor<M,L>,L,K,V> implements IIndexingStrategy<L,K,V> {
	
	@SuppressWarnings("rawtypes")
	protected static final IVariableBinding EMPTY_BINDING = new VariableBinding();
	
	private Map<IVariableBinding<K,V>,M> bindingToMonitor;

	private final IMonitorTemplate<M,L,K,V> template;
	
	@SuppressWarnings("unchecked")
	public StrategyB(IMonitorTemplate<M,L,K,V> template) {
		this.template = template;
		bindingToMonitor = new HashMap<IVariableBinding<K,V>, M>();		
		bindingToMonitor.put(EMPTY_BINDING, template.createMonitorPrototype());		
	}

	@Override
	public void processEvent(IEvent<L,K,V> currentEvent) {
		IVariableBinding<K,V> currentVariableBinding = currentEvent.getVariableBinding();
		Set<IVariableBinding<K,V>> joins = new HashSet<IVariableBinding<K,V>>();
		for(IVariableBinding<K,V> storedBinding: bindingToMonitor.keySet()) {
			if(storedBinding.isCompatibleWith(currentVariableBinding)) {
				joins.add(storedBinding.computeJoinWith(currentVariableBinding));
			}
		}
		Set<IVariableBinding<K,V>> keySetCopy = new HashSet<IVariableBinding<K,V>>(bindingToMonitor.keySet());
		for(IVariableBinding<K,V> binding: joins) {
			if(binding.isCompatibleWith(currentVariableBinding)) {
				IVariableBinding<K,V> oldBinding = binding.findMax(keySetCopy);
				M copiedMonitor = bindingToMonitor.get(oldBinding).copy();
				if(copiedMonitor.processEvent(currentEvent.getSymbol())) {
					template.matchCompleted(oldBinding);
				}
				bindingToMonitor.put(binding, copiedMonitor);
			}
		}
	}

}
