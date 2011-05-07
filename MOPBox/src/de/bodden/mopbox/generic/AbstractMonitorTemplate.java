package de.bodden.mopbox.generic;

import de.bodden.mopbox.generic.def.Alphabet;


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

	private final IAlphabet<L,K> alphabet;

	private final IIndexingStrategy<L,K,V> indexingStrategy;
	
	protected AbstractMonitorTemplate() {
		alphabet = createAlphabet(); 
		fillAlphabet(alphabet);
		indexingStrategy = createIndexingStrategy();		
	}

	/**
	 * Subclasses may override this method to create a custom kind of alphabet. 
	 */
	protected IAlphabet<L,K> createAlphabet() {
		return new Alphabet<L,K>();
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
	 * TODO Right now the entire method it synchronized. It would be nice to have a more
	 * fine-grained locking scheme.
	 * 
	 * @see de.bodden.mopbox.generic.IMonitorTemplate#processEvent(de.bodden.mopbox.generic.IEvent)
	 */
	@Override
	public void processEvent(L label, IVariableBinding<K,V> binding){
		indexingStrategy.processEvent(getSymbolByLabel(label), binding);
	}
	
	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L,K> getSymbolByLabel(L label) {
		return alphabet.getSymbolByLabel(label);
	}
}
