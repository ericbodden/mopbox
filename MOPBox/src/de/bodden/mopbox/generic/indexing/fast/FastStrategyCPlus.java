package de.bodden.mopbox.generic.indexing.fast;

import java.lang.ref.WeakReference;
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
import de.bodden.mopbox.generic.indexing.simple.StrategyC;

/**
 * This strategy is known as Algorithm C+ in the ASE paper of Feng Chen at al.
 * Opposed to {@link StrategyC}, it requires the presence of <i>creation symbols</i>.
 * Such symbols need to be explicitly declared.
 *
 * This class obtains thread safety in a naive way, through a single lock that
 * is held when calling {@link #processEvent(ISymbol, IVariableBinding)}.
 */
public class FastStrategyCPlus<M extends IMonitor<M,L>,L,K,V> implements IIndexingStrategy<L,K,V> {
		
	@SuppressWarnings("rawtypes")
	protected static final IVariableBinding EMPTY_BINDING = new VariableBinding();
	
	protected WeakIdentityMultiMap<V, M> bindingToMonitor;
	protected WeakIdentityMultiMap<V, Set<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey>> bindingToDefinedMoreInformativeBindings;

	protected final IMonitorTemplate<M,L,K,V> template;

	protected Set<ISymbol<L,K>> creationSymbols;
	
	protected final List<K> allVariables;
	
	public FastStrategyCPlus(IMonitorTemplate<M,L,K,V> template, Set<ISymbol<L,K>> creationSymbols) {
		this.template = template;
		this.creationSymbols = creationSymbols;
		bindingToMonitor = new WeakIdentityMultiMap<V, M>();		
		bindingToDefinedMoreInformativeBindings = new WeakIdentityMultiMap<V, Set<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey>>();
		allVariables = template.getAlphabet().variables();
	}

	@Override
	public synchronized void processEvent(ISymbol<L,K> symbol, IVariableBinding<K, V> bind){
		
		V[] values = arrayRepresentation(bind);

		M currentMonitor = bindingToMonitor.get(values);
		boolean undefined = (currentMonitor==null);
		if(undefined) { //line 1
			List<IVariableBinding<K,V>> lessInformativeBindings =
				bind.strictlyLessInformativeBindingsOrdered(); //line 2
			IVariableBinding<K,V> foundBinding = null;
			for (IVariableBinding<K,V> bMax : lessInformativeBindings) { 
				if(bindingToMonitor.containsKey(arrayRepresentation(bMax))) { //line 3
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
				HashSet<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey> copy =
					new HashSet<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey>(definedMoreInformativeBindingsFor(bMax));
				for(WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey b: copy) { //line 9
					IVariableBinding<K, V> binding = toBinding(b);
					if(binding.isCompatibleWith(bind)) { //line 9
						IVariableBinding<K,V> bComp = binding;
						IVariableBinding<K,V> join = bComp.computeJoinWith(bind);
						if(!bindingToMonitor.containsKey(arrayRepresentation(join))) { //line 10
							defineTo(join, bComp);
						}
					}
				}
			}
		}
		Set<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey> union = //line 16
			new HashSet<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey>(definedMoreInformativeBindingsFor(bind));
		union.add(toMultiKey(bind)); //line 16
		for (WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey bPrime : union) { //line 16
			M monitor = bindingToMonitor.getByKey(bPrime);			
			if(monitor!=null && monitor.processEvent(symbol)) { //lines 17
				template.matchCompleted(toBinding(bPrime)); //lines 18
			}
		}
	}

	protected <A> IVariableBinding<K,A> toBinding(WeakIdentityMultiMap<A,?>.WeakIdentityMultiKey key) {
		VariableBinding<K, A> bind = new VariableBinding<K, A>();
		for(int i=0;i<allVariables.size();i++) {
			WeakReference<A> weakReference = key.keyRefs[i];
			if(weakReference!=null) {
				A val = weakReference.get();
				if(val!=null) {
					bind.put(allVariables.get(i), val);
				}
			}
		}		
		return bind;
	}
	
	protected V[] arrayRepresentation(IVariableBinding<K, V> bind) {
		@SuppressWarnings("unchecked")
		V[] values = (V[]) new Object[allVariables.size()];
		int i = 0;
		//TODO can this be sped up somehow?
		for(Map.Entry<K,V> singleBinding: bind.entrySet()) {
			int index = allVariables.indexOf(singleBinding.getKey());
			values[index] = singleBinding.getValue();
			i++;
		}
		return values;
	}


	protected void defineTo(IVariableBinding<K,V> b, IVariableBinding<K,V> bPrime) {
		M m = bindingToMonitor.get(arrayRepresentation(bPrime));
		bindingToMonitor.put(m.copy(),arrayRepresentation(b));
		updateChainingTable(b);
	}

	protected void defineNew(IVariableBinding<K,V> b) {
		bindingToMonitor.put(template.createMonitorPrototype(),arrayRepresentation(b));
		updateChainingTable(b);		
	}

	protected void updateChainingTable(IVariableBinding<K,V> b) {
		for(IVariableBinding<K,V> bPrimePrime: b.strictlyLessInformativeBindingsOrdered()) {
			definedMoreInformativeBindingsFor(bPrimePrime).add(toMultiKey(b));
		}
	}

	private WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey toMultiKey(IVariableBinding<K, V> b) {
		return bindingToMonitor.newMultiKey(arrayRepresentation(b));
	}

	protected Set<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey> definedMoreInformativeBindingsFor(IVariableBinding<K,V> binding) {
		Set<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey> set = bindingToDefinedMoreInformativeBindings.get(arrayRepresentation(binding));
		if(set==null) {
			set = new HashSet<WeakIdentityMultiMap<V,M>.WeakIdentityMultiKey>();
			bindingToDefinedMoreInformativeBindings.put(set,arrayRepresentation(binding));
		}
		return set;
	}

}
