package de.bodden.rvlib.generic;

import de.bodden.rvlib.generic.def.Symbol;

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
	private int nextSymbolIndex = 0;
	
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
	
	/**
	 * Creates a new symbol for the given label.
	 * This symbol still needs to be added to the alphabet.
	 * TODO simplify this
	 * TODO move this into {@link IAlphabet}?
	 */
	protected ISymbol<L> makeNewSymbol(L label) {
		return new Symbol<L>(label,nextSymbolIndex++);
	}
	
	/**
	 * Retrieves a symbol by its label.
	 */
	public ISymbol<L> getSymbolByLabel(L label) {
		for (ISymbol<L> sym : alphabet) {
			if(sym.getLabel().equals(label)) {
				return sym;
			}
		}
		throw new IllegalArgumentException("Unknown symbol:" +label);
	}
	

	public IAlphabet<L> getAlphabet() {
		return alphabet;
	}

	/**
	 * @see de.bodden.rvlib.generic.IMonitorTemplate#processEvent(de.bodden.rvlib.generic.IEvent)
	 */
	@Override
	public void processEvent(IEvent<L,K,V> e) {
		indexingStrategy.processEvent(e);
	}

}
