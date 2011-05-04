package de.bodden.mopbox.generic;


/**
 * This is an abstract template class for {@link IMonitorTemplate}s. It encapsulates common functionality to
 * keep track of the template's alphabet but it makes no assumption of the nature of the monitor template,
 * e.g. finite-state machine vs. something different. 
 *
 * @param <M> The concrete monitor type.
 * @param <L> The type of labels that this monitor uses at transitions.
 * @param <K> The type of keys used for the variable bindings.
 * @param <V> The type of values used for the variable bindings.
 */
public abstract class AbstractMonitorTemplate<M extends IMonitor<M,L>,L,K,V> implements IMonitorTemplate<M,L,K,V> {

	private final IAlphabet<L> alphabet;

	private final IIndexingStrategy<L,K,V> indexingStrategy;
	
	protected AbstractMonitorTemplate() {
		alphabet = createAlphabet();
		indexingStrategy = createIndexingStrategy();		
	}
	
	/**
	 * Creates and returns this template's {@link IIndexingStrategy}. Subclasses
	 * must implement this method, choosing a concrete strategy.
	 */
	protected abstract IIndexingStrategy<L,K,V> createIndexingStrategy();

	/**
	 * Fills the alphabet with {@link ISymbol}s. The alphabet can be retrieved
	 * using {@link #getAlphabet()}. There also exists the factory method
	 * {@link #makeNewSymbol(Object)} to create new symbols. Symbols can then
	 * be looked up from the alphabet by using {@link #getSymbolByLabel(Object)}.
	 */
	protected abstract IAlphabet<L> createAlphabet();
	
	public IAlphabet<L> getAlphabet() {
		return alphabet;
	}

	/**
	 * @see de.bodden.mopbox.generic.IMonitorTemplate#processEvent(de.bodden.mopbox.generic.IEvent)
	 */
	@Override
	public void processEvent(IEvent<L,K,V> e) {
		indexingStrategy.processEvent(e);
	}
	
	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L> getSymbolByLabel(L label) {
		return alphabet.getSymbolByLabel(label);
	}
}
