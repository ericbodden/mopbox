package de.bodden.rvlib.generic;

import de.bodden.rvlib.impl.Symbol;


public abstract class AbstractMonitorTemplate<M extends IMonitor<M,L>,L,K,V> implements IMonitorTemplate<M,L,K,V> {

	private final IAlphabet<L> alphabet;
	private final IIndexingStrategy<L,K,V> indexingStrategy;
	private int nextSymbolIndex = 0;
	
	protected AbstractMonitorTemplate() {
		alphabet = createAlphabet();
		indexingStrategy = createIndexingStrategy();		
	}
	
	protected abstract IIndexingStrategy<L,K,V> createIndexingStrategy();

	protected abstract IAlphabet<L> createAlphabet();
	
	protected ISymbol<L> makeNewSymbol(L label) {
		return new Symbol<L>(label,nextSymbolIndex++);
	}
	
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

	@Override
	public void processEvent(IEvent<L,K,V> e) {
		indexingStrategy.processEvent(e);
	}

}
