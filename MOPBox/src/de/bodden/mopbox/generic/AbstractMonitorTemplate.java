package de.bodden.mopbox.generic;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.bodden.mopbox.generic.def.Alphabet;
import de.bodden.mopbox.generic.def.VariableBinding;


/**
 * This is an abstract template class for {@link IMonitorTemplate}s. It encapsulates common functionality to
 * keep track of the template's alphabet but it makes no assumption of the nature of the monitor template,
 * e.g. finite-state machine vs. something different. 
 *
 * @param <M> The concrete monitor type.
 * @param <L,V> The type of labels that this monitor uses at transitions.
 * @param <K> The type of keys used for the variable bindings.
 * @param <V> The type of values used for the variable bindings.
 */
public abstract class AbstractMonitorTemplate<M extends IMonitor<M,L>,L,K,V> implements IMonitorTemplate<M,L,K,V> {

	private IAlphabet<L,K> alphabet;

	private Set<K> variables = new HashSet<K>();

	private IIndexingStrategy<L,K,V> indexingStrategy;
	
	private boolean initialized = false;
	
	/**
	 * Initializes the template, filling in the alphabet and creating the indexing strategy.
	 */	
	protected void initialize() {
		alphabet = createAlphabet(); 
		fillAlphabet(alphabet);
		indexingStrategy = createIndexingStrategy();
		initialized = true;
	}

	/**
	 * Subclasses may override this method to create a custom kind of alphabet. 
	 */
	protected IAlphabet<L,K> createAlphabet() {
		fillVariables(variables);
		variables = Collections.unmodifiableSet(variables);
		return new Alphabet<L,K>(variables);
	}
	
	/**
	 * Subclasses must override this method to fill in the set of variables
	 * used by symbols of this alphabet. 
	 */
	protected void fillVariables(Set<K> variables) {
		//empty default implementation; no variables
	}

	/**
	 * Creates and returns this template's {@link IIndexingStrategy}. Subclasses
	 * must implement this method, choosing a concrete strategy.
	 */
	protected abstract IIndexingStrategy<L,K,V> createIndexingStrategy();

	/**
	 * Fills the alphabet with {@link ISymbol}s by calling 
	 * {@link IAlphabet#makeNewSymbol(Object)}.
	 * Symbols can then be looked up from the alphabet by using {@link #getSymbolByLabel(Object)}.
	 */
	protected abstract void fillAlphabet(IAlphabet<L,K> alphabet);
	
	public IAlphabet<L,K> getAlphabet() {
		return alphabet;
	}

	/**
	 * TODO Right now the entire method is synchronized. It would be nice to have a more
	 * fine-grained locking scheme.
	 * 
	 * @see de.bodden.mopbox.generic.IMonitorTemplate#processEvent(de.bodden.mopbox.generic.IEvent)
	 */
	@Override
	public synchronized void processEvent(L label, IVariableBinding<K,V> binding){
		assert alphabet.variables().containsAll(binding.keySet()):
			"Event has undefined variables: "+binding+" vs. "+alphabet;
		assert initialized : "not initialized!";
		
		getIndexingStrategy().processEvent(getSymbolByLabel(label), binding);
	}

	/**
	 * A convenience method. A version of {@link #processEvent(Object, IVariableBinding)} in which
	 * variable bindings are ignored.
	 */
	public void processEvent(L label){
		processEvent(label, VariableBinding.<K,V>emptyBinding());
	}
	
	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L,K> getSymbolByLabel(L label) {
		return alphabet.getSymbolByLabel(label);
	}
	
	/**
	 * Returns the variables that the symbols in this monitor template bind. 
	 */	
	public Set<K> getVariables() {
		return variables;
	}
	
	public IIndexingStrategy<L,K,V> getIndexingStrategy() {
		return indexingStrategy;
	}
	
	/**
	 * Resets all state of this template by re-instantiating its indexing strategy. 
	 */
	public void reset() {
		indexingStrategy = createIndexingStrategy();
	}
}
